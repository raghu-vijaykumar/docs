---
weight: 7
bookCollapseSection: false
title: "Configuration Examples"
draft: false
---

# Configuration Examples

This section provides complete, copy-paste ready configurations for common MCP deployment scenarios. Each example includes full context and explanations of why specific choices were made.

## Basic Local Development Setup

### Minimal Fetch + Filesystem Configuration

```python
#!/usr/bin/env python3
"""
Basic MCP setup for local development
Installs and runs fetch and filesystem servers
"""

import asyncio
from agents import Agent, Runner
from agents.mcp import MCPClient

async def create_development_agent():
    """Create an agent with basic MCP tools for development"""

    # MCP client configurations
    fetch_client = MCPClient.for_stdio_server(
        command="uvx",
        args=["mcp-server-fetch"],
        timeout=60
    )

    filesystem_client = MCPClient.for_stdio_server(
        command="npx",
        args=[
            "-y",
            "@modelcontextprotocol/server-filesystem",
            "./development_workspace"  # Sandboxed directory
        ],
        timeout=30
    )

    # Connect and discover tools
    await fetch_client.connect()
    await filesystem_client.connect()

    fetch_tools = await fetch_client.list_tools()
    fs_tools = await filesystem_client.list_tools()

    print(f"📚 Loaded {len(fetch_tools)} web tools")
    print(f"💾 Loaded {len(fs_tools)} filesystem tools")

    # Create development-focused agent
    agent = Agent(
        name="DevelopmentAssistant",
        instructions="""
        You are a helpful development assistant. You can:

        - Browse the internet and research technical topics
        - Read and write files in the development workspace
        - Help with coding tasks and documentation

        Always work within the allowed directories and respect internet access guidelines.
        When researching code or solutions, cite your sources.
        """,
        model="gpt-4",
        tools=fetch_tools + fs_tools
    )

    return agent, [fetch_client, filesystem_client]

# Usage example
async def main():
    agent, clients = await create_development_agent()

    # Example task
    result = await Runner.run(
        agent,
        "Research the latest Python async features and save examples to async_examples.py",
        run_config={"max_turns": 5}
    )

    print(f"Task completed: {result.final_output}")

    # Cleanup connections
    for client in clients:
        await client.disconnect()

if __name__ == "__main__":
    asyncio.run(main())
```

### Environment-Based Configuration Switcher

```python
"""
Environment-aware MCP configuration system
Automatically adjusts server configurations based on deployment context
"""

import os
from typing import Dict, Any
from dataclasses import dataclass

@dataclass
class MCPEnvironment:
    name: str
    is_production: bool
    sandbox_strictness: str  # "lax", "moderate", "strict"
    internet_access: bool
    monitoring_enabled: bool

    @classmethod
    def current(cls) -> "MCPEnvironment":
        env_name = os.getenv("APP_ENV", "development")

        environments = {
            "development": cls("development", False, "lax", True, False),
            "staging": cls("staging", False, "moderate", True, True),
            "production": cls("production", True, "strict", True, True)
        }

        return environments.get(env_name, environments["development"])

def get_env_specific_configs() -> Dict[str, Any]:
    """Return MCP configurations tailored to current environment"""

    env = MCPEnvironment.current()

    base_configs = {
        "filesystem": {
            "command": "npx",
            "args": [
                "-y",
                "@modelcontextprotocol/server-filesystem",
                "--allowed-directory",
                "/app/workspace" if env.is_production else "./dev_workspace",
                "--max-file-size", "50MB" if env.is_production else "100MB",
                "--read-only" if env.sandbox_strictness == "strict" else "",
            ].remove(None) if "" in [] else [],  # Remove empty strings
            "timeout": 45 if env.is_production else 30
        },

        "fetch": {
            "command": "uvx",
            "args": ["mcp-server-fetch"],
            "timeout": 120 if env.is_production else 60,
            "env": {
                "HEADLESS": "true",  # Always run headless
                "MAX_CONCURRENT_TABS": "1" if env.is_production else "3"
            }
        } if env.internet_access else None,

        "playwright": {
            "command": "npx",
            "args": ["-y", "@modelcontextprotocol/server-playwright"],
            "timeout": 90 if env.is_production else 60,
            "env": {
                "BROWSER_HEADLESS": "true",
                "MAX_PAGES": "2" if env.is_production else "5"
            }
        } if env.internet_access else None
    }

    # Filter out None values (disabled servers)
    return {k: v for k, v in base_configs.items() if v is not None}

# Usage in agent setup
async def create_environment_aware_agent():
    configs = get_env_specific_configs()

    # Create clients for available configurations
    clients = {}
    all_tools = []

    for server_name, config in configs.items():
        try:
            client = MCPClient.for_stdio_server(**config)
            await client.connect()

            tools = await client.list_tools()
            clients[server_name] = client
            all_tools.extend(tools)

            print(f"✅ Connected to {server_name} ({len(tools)} tools)")

        except Exception as e:
            print(f"⚠️  Failed to connect to {server_name}: {e}")

    # Create agent with available tools
    env = MCPEnvironment.current()

    agent = Agent(
        name=f"{env.name.title()}Assistant",
        instructions=generate_env_specific_instructions(env),
        model="gpt-4-turbo-preview" if env.is_production else "gpt-4",
        tools=all_tools
    )

    return agent, clients

def generate_env_specific_instructions(env: MCPEnvironment) -> str:
    """Generate appropriate instructions based on environment constraints"""

    base_instructions = """
    You are a helpful AI assistant with access to various tools.
    Use tools appropriately and efficiently.
    """

    constraints = []

    if env.sandbox_strictness == "strict":
        constraints.append("- Work only within designated directories")
        constraints.append("- Respect read-only restrictions")

    if not env.internet_access:
        constraints.append("- No internet access available")
        constraints.append("- Focus on local file operations")

    if env.is_production:
        constraints.append("- Optimize for reliability and performance")
        constraints.append("- Be conservative with resource-intensive operations")

    full_instructions = base_instructions
    if constraints:
        full_instructions += "\n\nConstraints:\n" + "\n".join(constraints)

    return full_instructions

# Example usage
async def main():
    agent, clients = await create_environment_aware_agent()

    task = "Read the current directory contents and summarize any Python files"
    result = await Runner.run(agent, task)

    print(f"Result: {result.final_output}")

    # Cleanup
    for client in clients.values():
        await client.disconnect()

if __name__ == "__main__":
    asyncio.run(main())
```

## Production Deployment Configurations

### Containerized MCP Server Stack

```dockerfile
# Dockerfile for containerized MCP server
FROM node:18-alpine AS base

# Install common MCP servers
RUN npm install -g \
    @modelcontextprotocol/server-filesystem \
    @modelcontextprotocol/server-playwright \
    && apk add --no-cache \
    chromium \
    nss \
    freetype \
    freetype-dev \
    harfbuzz \
    ca-certificates \
    ttf-freefont

# Create non-root user
RUN addgroup -g 1001 mcpuser && \
    adduser -D -u 1001 -G mcpuser mcpuser

USER mcpuser
WORKDIR /app

# Default configuration
ENV NODE_ENV=production
ENV MCP_SANDBOX_DIR=/app/sandbox
ENV MCP_TIMEOUT=45

EXPOSE 3000

CMD ["node", "server.js"]
```

```yaml
# docker-compose.yml for MCP server orchestration
version: '3.8'

services:
  mcp-filesystem:
    build: .
    command: ["mcp-server-filesystem", "/app/sandbox"]
    volumes:
      - ./production-data:/app/sandbox:ro
    environment:
      - NODE_ENV=production
      - MCP_TIMEOUT=60
      - MCP_MAX_FILE_SIZE=100MB
    networks:
      - mcp-network
    read_only: true
    tmpfs:
      - /tmp:mcpuser,mode=1700

  mcp-fetch:
    build: .
    command: ["uvx", "mcp-server-fetch"]
    environment:
      - NODE_ENV=production
      - MCP_TIMEOUT=120
      - HEADLESS=true
      - MAX_TABS=2
    networks:
      - mcp-network
    # Resource limits
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '0.5'

  mcp-playwright:
    build: .
    command: ["mcp-server-playwright"]
    environment:
      - NODE_ENV=production
      - MCP_TIMEOUT=90
      - BROWSER_HEADLESS=true
      - MAX_PAGES=3
    networks:
      - mcp-network
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '0.8'

  # Agent application service
  agent-service:
    image: your-agent-app:latest
    depends_on:
      - mcp-filesystem
      - mcp-fetch
      - mcp-playwright
    environment:
      - MCP_FILESYSTEM_URL=http://mcp-filesystem:3000
      - MCP_FETCH_URL=http://mcp-fetch:3000
      - MCP_PLAYWRIGHT_URL=http://mcp-playwright:3000
    networks:
      - mcp-network

networks:
  mcp-network:
    driver: bridge
    internal: true  # Isolate from external network
```

### Production Agent Configuration

```python
"""
Production-hardened agent configuration
Includes monitoring, security, and resilience features
"""

import asyncio
import logging
from typing import Dict, List, Optional
from agents import Agent, Runner, RunConfig
from agents.mcp import MCPClient

# Configure logging for production
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('/var/log/agent.log'),
        logging.StreamHandler()
    ]
)

class ProductionAgentManager:
    """Manages MCP-connected agents in production"""

    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.active_clients: Dict[str, MCPClient] = {}
        self.metrics = {"requests": 0, "errors": 0}

    async def initialize_production_servers(self) -> Dict[str, MCPClient]:
        """Initialize all MCP servers with production settings"""

        server_configs = {
            "filesystem": {
                "url": "http://mcp-filesystem:3000/mcp",  # Container network
                "headers": {"Authorization": f"Bearer {os.getenv('MCP_API_KEY')}"}
            },
            "fetch": {
                "url": "http://mcp-fetch:3000/mcp",
                "headers": {"Authorization": f"Bearer {os.getenv('MCP_API_KEY')}"}
            },
            "playwright": {
                "url": "http://mcp-playwright:3000/mcp",
                "headers": {"Authorization": f"Bearer {os.getenv('MCP_API_KEY')}"}
            }
        }

        connected_clients = {}

        for server_name, config in server_configs.items():
            try:
                client = MCPClient.for_sse_server(**config)
                await client.connect()

                # Verify server health
                tools = await client.list_tools()
                self.logger.info(f"Connected to {server_name} with {len(tools)} tools")

                connected_clients[server_name] = client

            except Exception as e:
                self.logger.error(f"Failed to connect to {server_name}: {e}")
                self.metrics["errors"] += 1

        self.active_clients = connected_clients
        return connected_clients

    async def create_production_agent(self) -> Agent:
        """Create agent with production-grade settings"""

        # Get all available tools
        all_tools = []
        failed_servers = []

        for server_name, client in self.active_clients.items():
            try:
                tools = await client.list_tools()
                all_tools.extend(tools)
                self.logger.info(f"Loaded {len(tools)} tools from {server_name}")
            except Exception as e:
                self.logger.error(f"Failed to get tools from {server_name}: {e}")
                failed_servers.append(server_name)
                self.metrics["errors"] += 1

        if failed_servers:
            self.logger.warning(f"Servers failed to load: {failed_servers}")

        # Production agent configuration
        agent = Agent(
            name="ProductionAssistant",
            instructions="""
            You are a production AI assistant with access to enterprise tools.

            CRITICAL GUIDELINES:
            - Respect security boundaries and access controls
            - Be conservative with resource-intensive operations
            - Log important actions for audit purposes
            - Work within approved directories and domains
            - Handle errors gracefully and inform users of limitations

            When tools are unavailable, explain clearly and suggest alternatives.
            Never attempt to work around security restrictions.
            """,
            model="gpt-4-turbo-2024-04-09",  # Use stable production model
            tools=all_tools,
            # Additional production settings
            temperature=0.7,  # Balance creativity with consistency
            max_tokens=4000,  # Reasonable response size
        )

        return agent

    async def execute_task_with_fallback(self, task: str) -> Dict[str, Any]:
        """Execute task with comprehensive error handling and fallback"""

        self.metrics["requests"] += 1

        try:
            agent = await self.create_production_agent()

            config = RunConfig(
                max_turns=15,  # Allow complex multi-step tasks
                temperature=0.3,  # Lower temperature for reliability
                # Add production run settings here
                monitoring=True,
                log_level="INFO"
            )

            result = await Runner.run(agent, task, run_config=config)

            # Log successful completion
            self.logger.info(f"Task completed successfully: {task[:50]}...")

            return {
                "success": True,
                "output": result.final_output,
                "stats": result.run_stats
            }

        except Exception as e:
            # Log error with context
            self.logger.error(f"Task failed: {task[:50]}... | Error: {e}")
            self.metrics["errors"] += 1

            # Fallback: try with minimal toolset
            try:
                minimal_tools = await self._get_minimal_toolset()
                fallback_agent = Agent(
                    name="FallbackAssistant",
                    instructions="You have limited tools available. Work with what you can.",
                    model="gpt-4",
                    tools=minimal_tools
                )

                fallback_result = await Runner.run(fallback_agent, task)

                return {
                    "success": True,
                    "output": f"[Partial result - some tools unavailable]: {fallback_result.final_output}",
                    "fallback_used": True
                }

            except Exception as fallback_error:
                self.logger.error(f"Fallback also failed: {fallback_error}")

                return {
                    "success": False,
                    "error": str(e),
                    "message": "Unable to complete task due to technical issues"
                }

    async def _get_minimal_toolset(self) -> List:
        """Get minimal reliable tools for fallback operations"""

        minimal_tools = []

        # Try to get just filesystem tools (usually most reliable)
        if "filesystem" in self.active_clients:
            try:
                tools = await self.active_clients["filesystem"].list_tools()
                minimal_tools.extend(tools)
            except Exception:
                pass

        return minimal_tools

    async def get_health_status(self) -> Dict[str, Any]:
        """Get comprehensive health status"""

        server_health = {}

        for server_name, client in self.active_clients.items():
            try:
                # Quick health check
                await asyncio.wait_for(
                    client.ping() if hasattr(client, 'ping') else client.list_tools(),
                    timeout=5.0
                )
                server_health[server_name] = "healthy"
            except Exception as e:
                server_health[server_name] = f"unhealthy: {str(e)[:50]}"

        return {
            "timestamp": asyncio.get_event_loop().time(),
            "metrics": self.metrics,
            "server_health": server_health,
            "active_servers": len(self.active_clients),
            "total_tools": sum(
                len(await client.list_tools())
                for client in self.active_clients.values()
                if server_health.get(str(client), "").startswith("healthy")
            )
        }

    async def graceful_shutdown(self):
        """Clean shutdown of all MCP connections"""

        self.logger.info("Initiating graceful shutdown...")

        shutdown_tasks = []
        for server_name, client in self.active_clients.items():
            shutdown_tasks.append(self._safe_disconnect(client, server_name))

        if shutdown_tasks:
            await asyncio.gather(*shutdown_tasks, return_exceptions=True)

        self.logger.info("Shutdown complete")

    async def _safe_disconnect(self, client: MCPClient, server_name: str):
        """Safely disconnect a client with error handling"""

        try:
            if hasattr(client, 'disconnect'):
                await client.disconnect()
            self.logger.info(f"Disconnected from {server_name}")
        except Exception as e:
            self.logger.error(f"Error disconnecting from {server_name}: {e}")

# Production application entry point
async def main():
    """Main production application"""

    manager = ProductionAgentManager()

    try:
        # Initialize MCP servers
        await manager.initialize_production_servers()

        # Example task execution
        result = await manager.execute_task_with_fallback(
            "Analyze the current workspace and create a summary report"
        )

        print(f"Task result: {result}")

    except KeyboardInterrupt:
        print("Received shutdown signal...")
    finally:
        await manager.graceful_shutdown()

if __name__ == "__main__":
    asyncio.run(main())
```

## Summary

These configuration examples demonstrate how to:

1. **Start Simple**: Begin with basic local configurations and gradually add complexity
2. **Environment Awareness**: Automatically adjust settings based on deployment context
3. **Production Hardening**: Add monitoring, error handling, and security measures
4. **Containerization**: Use Docker for isolation, reproducibility, and scaling
5. **Graceful Degradation**: Handle failures gracefully while maintaining core functionality

Each configuration includes comments explaining the rationale behind specific choices, making it easy to adapt to your specific requirements while maintaining production-ready reliability.
