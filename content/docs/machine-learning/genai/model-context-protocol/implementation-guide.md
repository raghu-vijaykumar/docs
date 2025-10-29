---
weight: 3
bookCollapseSection: false
title: "Implementation Guide"
draft: false
---

# Implementing MCP in Practice

This guide provides step-by-step instructions for integrating MCP servers into your AI agent applications. We'll focus on practical implementation using OpenAI Agents SDK as the example framework, with concrete examples of connecting to existing MCP servers.

## Prerequisites

Before getting started, ensure you have:

- **Python 3.8+** installed
- **OpenAI Agents SDK** installed via pip
- **Node.js** (for JavaScript MCP servers) or appropriate runtime for your target servers

## Step 1: Setting up Basic MCP Server Connection

The simplest way to start with MCP is connecting to existing servers. We'll use the `fetch` MCP server as our first example - it provides web scraping capabilities using headless browsers.

```python
import asyncio
from agents import Agent, Runner
from agents.mcp import MCPClient

async def setup_mcp_integration():
    # Create MCP client for the fetch server
    fetch_client = MCPClient.for_stdio_server(
        command="uvx",  # Universal Python package runner
        args=["mcp-server-fetch"],
        timeout=60  # Increased timeout for stability
    )

    # Connect and discover available tools
    await fetch_client.connect()
    available_tools = await fetch_client.list_tools()

    print("Available MCP tools:")
    for tool in available_tools:
        print(f"- {tool.name}: {tool.description}")

    return fetch_client
```

## Step 2: Connecting MCP Tools to an Agent

Once you have MCP server connections established, you can equip your agents with these capabilities:

```python
async def create_agent_with_mcp_tools():
    # Setup MCP clients for multiple servers
    fetch_client = MCPClient.for_stdio_server(
        command="uvx",
        args=["mcp-server-fetch"],
        timeout=60
    )

    filesystem_client = MCPClient.for_stdio_server(
        command="npx",
        args=["-y", "@modelcontextprotocol/server-filesystem", "/tmp/agent-workspace"],
        timeout=30
    )

    playwright_client = MCPClient.for_stdio_server(
        command="npx",
        args=["-y", "@modelcontextprotocol/server-playwright"],
        timeout=60
    )

    # Connect all clients
    await fetch_client.connect()
    await filesystem_client.connect()
    await playwright_client.connect()

    # Get tools from all servers
    all_mcp_tools = []
    all_mcp_tools.extend(await fetch_client.list_tools())
    all_mcp_tools.extend(await filesystem_client.list_tools())
    all_mcp_tools.extend(await playwright_client.list_tools())

    # Create agent with MCP tools
    agent = Agent(
        name="ResearchAssistant",
        instructions="""
        You are a research assistant capable of:
        - Browsing the internet to gather information
        - Reading and writing files
        - Taking screenshots of web pages
        - Clicking buttons and navigating sites

        Be persistent when gathering information and thorough in your research.
        You now have internet access via MCP tools.
        """,
        model="gpt-4",
        tools=all_mcp_tools
    )

    return agent, [fetch_client, filesystem_client, playwright_client]
```

## Step 3: Configuring Sandbox Environments

For security and isolation, most MCP servers accept path restrictions:

```python
def configure_sandboxed_filesystem():
    """Configure filesystem MCP server with sandboxed access"""

    import os
    from pathlib import Path

    # Create sandbox directory
    sandbox_dir = Path("./agent_sandbox")
    sandbox_dir.mkdir(exist_ok=True)

    # MCP server configuration
    filesystem_config = {
        "command": "npx",
        "args": [
            "-y",
            "@modelcontextprotocol/server-filesystem",
            "--allowed-directory", str(sandbox_dir.absolute()),
            "--max-file-size", "10MB"
        ]
    }

    return filesystem_config
```

## Step 4: Error Handling and Reliability

MCP servers can fail or timeout, so implement proper error handling:

```python
from typing import List, Optional
from agents.mcp import MCPClient, MCPTool

async def safe_mcp_connect(
    server_config: dict,
    max_retries: int = 3
) -> Optional[MCPClient]:

    for attempt in range(max_retries):
        try:
            client = MCPClient.for_stdio_server(**server_config)
            await client.connect()
            return client
        except Exception as e:
            print(f"MCP connection attempt {attempt + 1} failed: {e}")
            if attempt < max_retries - 1:
                await asyncio.sleep(2 ** attempt)  # Exponential backoff

    return None

async def setup_reliable_mcp_agent():
    """Setup agent with fallback handling for MCP server failures"""

    configs = [
        {
            "name": "fetch",
            "command": "uvx",
            "args": ["mcp-server-fetch"],
            "timeout": 60
        },
        {
            "name": "filesystem",
            "command": "npx",
            "args": ["-y", "@modelcontextprotocol/server-filesystem", "./sandbox"],
            "timeout": 30
        }
    ]

    clients = []
    all_tools = []

    for config in configs:
        client = await safe_mcp_connect(config)
        if client:
            clients.append(client)
            tools = await client.list_tools()
            all_tools.extend(tools)
            print(f"Successfully loaded {len(tools)} tools from {config['name']}")
        else:
            print(f"Failed to connect to {config['name']} server")

    # Create agent even if some MCP servers failed
    agent = Agent(
        name="FaultTolerantAgent",
        instructions="You have access to various MCP tools. Adapt to what's available.",
        model="gpt-4",
        tools=all_tools
    )

    return agent, clients
```

## Step 5: Working with SSE-Based Remote Servers

For remote MCP servers using Server-Sent Events:

```python
import aiohttp
from agents.mcp import MCPClient

async def connect_remote_mcp_server():
    """Connect to a remote MCP server over SSE"""

    async with aiohttp.ClientSession() as session:
        client = MCPClient.for_sse_server(
            url="https://api.example.com/mcp",
            headers={
                "Authorization": "Bearer your-api-key",
                "User-Agent": "OpenAI-Agents-SDK/1.0"
            },
            session=session
        )

        await client.connect()
        tools = await client.list_tools()

        return client, tools

# Configuration for production remote servers
remote_server_configs = [
    {
        "name": "weather-service",
        "url": "https://api.weather-provider.com/mcp",
        "headers": {"X-API-Key": os.getenv("WEATHER_API_KEY")}
    },
    {
        "name": "database-tools",
        "url": "https://data-tools.example.com/mcp",
        "headers": {"Authorization": f"Bearer {os.getenv('DB_ACCESS_TOKEN')}"}
    }
]
```

## Step 6: Building Complete Agent Workflows

Here's a complete example combining multiple MCP servers for a research task:

```python
import asyncio
from typing import Dict, Any
from agents import Agent, Runner, RunConfig
from agents.mcp import MCPClient

async def research_and_document_workflow():
    """Complete research workflow using multiple MCP servers"""

    # Setup MCP connections
    fetch_client = MCPClient.for_stdio_server(
        command="uvx", args=["mcp-server-fetch"], timeout=60
    )

    fs_client = MCPClient.for_stdio_server(
        command="npx",
        args=["-y", "@modelcontextprotocol/server-filesystem", "./research_output"],
        timeout=30
    )

    # Connect and get tools
    await fetch_client.connect()
    await fs_client.connect()

    mcp_tools = await fetch_client.list_tools() + await fs_client.list_tools()

    # Configure research agent
    researcher = Agent(
        name="WebResearcher",
        instructions="""
        You are an expert research assistant. Your capabilities include:
        - Searching and browsing the internet for information
        - Reading and analyzing web content
        - Writing research findings to files
        - Summarizing complex topics

        When researching:
        1. Start with broad searches to understand the topic
        2. Dive deep into high-quality sources
        3. Save key findings and summaries to files
        4. Be thorough and cite your sources
        """,
        model="gpt-4-turbo-preview",
        tools=mcp_tools
    )

    # Execute research task
    research_topic = "recent advancements in automated machine learning (AutoML)"

    result = await Runner.run(
        researcher,
        f"Research {research_topic}. Find 3-5 authoritative sources, summarize key advancements, and save your findings to a research_summary.md file.",
        run_config=RunConfig(
            max_turns=10,
            temperature=0.3
        )
    )

    # Cleanup MCP connections
    await fetch_client.disconnect()
    await fs_client.disconnect()

    return result

# Usage
if __name__ == "__main__":
    result = asyncio.run(research_and_document_workflow())
    print("Research completed!")
    print(f"Final result: {result.final_output}")
```

## Step 7: MCP Server Development Basics

If you need to build custom MCP servers, here's the basic structure:

```python
#!/usr/bin/env python3
"""Simple MCP server example that provides weather tools"""

import asyncio
import json
from mcp import Tool, Server
from mcp.server.stdio import stdio_server

# Define your tools
@Tool()
async def get_weather(city: str) -> dict:
    """Get current weather for a city"""
    # Implementation here
    return {"temperature": 72, "condition": "sunny", "city": city}

@Tool()
async def get_forecast(city: str, days: int = 3) -> dict:
    """Get weather forecast for a city"""
    # Implementation here
    return {"forecast": f"{days}-day forecast for {city}"}

async def main():
    """Main server entry point"""
    server = Server("weather-server")

    # Register tools
    server.register_tool(get_weather)
    server.register_tool(get_forecast)

    # Start server
    async with stdio_server() as (read_stream, write_stream):
        await server.run(read_stream, write_stream, server.create_initialization_options())

if __name__ == "__main__":
    asyncio.run(main())
```

## Common Configuration Patterns

### Timeouts and Reliability
```python
# Conservative timeouts based on server capabilities
server_timeouts = {
    "fetch": 60,        # Web scraping can be slow
    "filesystem": 30,   # Local file operations
    "playwright": 45,   # Browser automation
    "default": 30
}
```

### Environment-Specific Configurations
```python
def get_mcp_config_for_environment(env: str) -> List[dict]:
    """Return MCP configurations based on deployment environment"""

    configs = []

    if env == "development":
        configs.extend([
            {
                "name": "local-filesystem",
                "command": "npx", "args": ["-y", "@mcp/server-filesystem", "./dev_sandbox"]
            },
            {
                "name": "debug-tools",
                "command": "python", "args": ["-m", "debug_server"]
            }
        ])

    elif env == "production":
        configs.extend([
            {
                "name": "secure-filesystem",
                "command": "npx", "args": ["@mcp/server-filesystem", "/secure/data"],
                "env": {"NODE_ENV": "production"}
            }
        ])

    return configs
```

This implementation guide provides the foundation for integrating MCP servers into your agent applications. Start with existing MCP servers, gradually adding error handling and advanced configurations as your needs grow.
