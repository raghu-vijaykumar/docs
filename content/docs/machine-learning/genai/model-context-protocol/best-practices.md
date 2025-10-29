---
weight: 6
bookCollapseSection: false
title: "Best Practices"
draft: false
---

# MCP Best Practices

Effective MCP deployment requires balancing architectural flexibility with operational reliability. This guide covers the patterns and techniques that separate novice implementations from production-ready systems.

## Architectural Patterns

### Layered Architecture Pattern

```python
# Recommended architectural layering
class LayeredMCAgent:
    """
    Agent architecture with clear MCP integration layers

    Application Layer (Your Logic)
    ├── MCP Orchestration Layer (Server Management)
    │   ├── MCP Transport Layer (Stdio/SSE Logic)
    │   └── MCP Protocol Layer (Message Handling)
    └── External Resources (MCP Servers)
    """

    def __init__(self):
        self.mcp_orchestrator = MCPServerOrchestrator()
        self.tool_registry = MCPToolRegistry()
        self.agent_runtime = AgentExecutionRuntime()

    async def configure_environment(self, environment: str):
        """Configure MCP servers based on deployment environment"""

        config_strategies = {
            "development": self._dev_configuration,
            "staging": self._staging_configuration,
            "production": self._prod_configuration
        }

        strategy = config_strategies.get(environment, self._default_configuration)
        await strategy()

    async def _prod_configuration(self):
        """Production-hardened MCP server configuration"""

        # Sandbox all file operations
        await self.mcp_orchestrator.add_sandboxed_server(
            name="secure-filesystem",
            config=ProductionFilesystemConfig(allowed_paths=["/app/data"])
        )

        # Use authenticated remote services
        await self.mcp_orchestrator.add_authenticated_server(
            name="enterprise-web",
            config=AuthenticatedWebConfig(api_keys=self._get_prod_keys())
        )

        # Enable comprehensive monitoring
        await self.mcp_orchestrator.enable_monitoring(alert_thresholds=PROD_ALERTS)
```

### Circuit Breaker Pattern for MCP Servers

```python
import asyncio
from enum import Enum
from dataclasses import dataclass, field
from typing import Optional, Callable, Awaitable

class CircuitState(Enum):
    CLOSED = "closed"      # Normal operation
    OPEN = "open"         # Failing, requests rejected
    HALF_OPEN = "half_open"  # Testing if failure is resolved

@dataclass
class CircuitBreakerMetrics:
    total_requests: int = 0
    failed_requests: int = 0
    consecutive_failures: int = 0
    last_failure_time: Optional[float] = None
    last_success_time: Optional[float] = None

class MCPCircuitBreaker:
    """Circuit breaker for MCP server reliability"""

    def __init__(
        self,
        failure_threshold: int = 5,
        recovery_timeout: float = 60.0,
        monitoring_period: float = 300.0
    ):
        self.state = CircuitState.CLOSED
        self.failure_threshold = failure_threshold
        self.recovery_timeout = recovery_timeout
        self.monitoring_period = monitoring_period
        self.metrics = CircuitBreakerMetrics()

    async def call_server(self, server_func: Callable[[], Awaitable], *args, **kwargs):
        """Execute MCP server call with circuit breaker protection"""

        if self.state == CircuitState.OPEN:
            if self._should_attempt_reset():
                self.state = CircuitState.HALF_OPEN
            else:
                raise CircuitBreakerOpen("Server temporarily unavailable")

        try:
            result = await server_func(*args, **kwargs)
            self._record_success()
            return result
        except Exception as e:
            self._record_failure()
            raise e

    def _should_attempt_reset(self) -> bool:
        """Check if enough time has passed to retry"""
        if self.metrics.last_failure_time is None:
            return False

        time_since_failure = asyncio.get_event_loop().time() - self.metrics.last_failure_time
        return time_since_failure >= self.recovery_timeout

    def _record_success(self):
        """Record successful server interaction"""
        self.metrics.total_requests += 1
        self.metrics.last_success_time = asyncio.get_event_loop().time()

        if self.state == CircuitState.HALF_OPEN:
            # Successful call in half-open state
            self.state = CircuitState.CLOSED
            self.metrics.consecutive_failures = 0

    def _record_failure(self):
        """Record failed server interaction"""
        self.metrics.total_requests += 1
        self.metrics.failed_requests += 1
        self.metrics.consecutive_failures += 1
        self.metrics.last_failure_time = asyncio.get_event_loop().time()

        if self.metrics.consecutive_failures >= self.failure_threshold:
            self.state = CircuitState.OPEN

class CircuitBreakerOpen(Exception):
    """Raised when circuit breaker is open"""
    pass
```

### Connection Pooling Pattern

```python
from typing import List, Dict, Optional, Deque
from collections import deque
import asyncio
import time

class MCPConnectionPool:
    """Pool connections to MCP servers for performance"""

    def __init__(
        self,
        server_config: dict,
        pool_size: int = 5,
        max_idle_time: float = 300.0,  # 5 minutes
        health_check_interval: float = 60.0
    ):
        self.server_config = server_config
        self.pool_size = pool_size
        self.max_idle_time = max_idle_time
        self.health_check_interval = health_check_interval

        # Pool management
        self.available_connections: Deque[MCPClient] = deque()
        self.active_connections: Dict[MCPClient, float] = {}
        self._pool_lock = asyncio.Lock()

        # Health monitoring
        self._health_check_task: Optional[asyncio.Task] = None
        self._stop_health_check = False

    async def get_connection(self) -> MCPClient:
        """Get a healthy connection from the pool"""

        async with self._pool_lock:
            # Try to get available connection
            if self.available_connections:
                client = self.available_connections.pop()
                if await self._is_connection_healthy(client):
                    self.active_connections[client] = time.time()
                    return client

                # Connection unhealthy, create new one
                await client.disconnect() if hasattr(client, 'disconnect') else None

            # Create new connection if under limit
            if len(self.active_connections) < self.pool_size:
                client = MCPClient.for_stdio_server(**self.server_config)
                await client.connect()
                self.active_connections[client] = time.time()
                return client

            # Pool exhausted, wait and retry
            raise Exception("Connection pool exhausted")

    async def return_connection(self, client: MCPClient):
        """Return connection to the pool"""

        async with self._pool_lock:
            if client in self.active_connections:
                del self.active_connections[client]

                # Check if connection is still healthy
                if await self._is_connection_healthy(client):
                    self.available_connections.append(client)
                else:
                    await client.disconnect() if hasattr(client, 'disconnect') else None

    async def _is_connection_healthy(self, client: MCPClient) -> bool:
        """Check if MCP connection is still functional"""

        try:
            # Perform lightweight health check
            await client.ping() if hasattr(client, 'ping') else await client.list_tools()
            return True
        except Exception:
            return False

    async def start_health_monitoring(self):
        """Start background health check task"""

        if self._health_check_task is None:
            self._stop_health_check = False
            self._health_check_task = asyncio.create_task(self._health_monitor())

    async def stop_health_monitoring(self):
        """Stop background health check task"""

        self._stop_health_check = True
        if self._health_check_task:
            self._health_check_task.cancel()
            try:
                await self._health_check_task
            except asyncio.CancelledError:
                pass

    async def _health_monitor(self):
        """Background task to maintain connection health"""

        while not self._stop_health_check:
            try:
                await asyncio.sleep(self.health_check_interval)

                async with self._pool_lock:
                    unhealthy_clients = []

                    # Check available connections
                    for client in list(self.available_connections):
                        if not await self._is_connection_healthy(client):
                            unhealthy_clients.append(client)

                    # Remove unhealthy connections
                    for client in unhealthy_clients:
                        self.available_connections.remove(client)
                        await client.disconnect() if hasattr(client, 'disconnect') else None

            except Exception as e:
                print(f"Health check error: {e}")  # Use proper logging in production
```

## Performance Optimization Patterns

### Adaptive Timeouts Based on Server Characteristics

```python
from typing import Dict, List
import statistics
import asyncio

class AdaptiveTimeoutManager:
    """Dynamically adjust timeouts based on server performance"""

    def __init__(self):
        self.server_metrics: Dict[str, List[float]] = {}
        self.adjustment_interval = 300  # 5 minutes
        self.min_timeout = 5.0
        self.max_timeout = 300.0
        self.safety_margin = 1.5  # 50% safety margin

    def record_response_time(self, server_name: str, response_time: float):
        """Record response time for performance tracking"""

        if server_name not in self.server_metrics:
            self.server_metrics[server_name] = []

        self.server_metrics[server_name].append(response_time)

        # Keep only recent measurements
        if len(self.server_metrics[server_name]) > 100:
            self.server_metrics[server_name] = self.server_metrics[server_name][-50:]

    def get_optimal_timeout(self, server_name: str) -> float:
        """Calculate optimal timeout for server"""

        metrics = self.server_metrics.get(server_name, [])

        if len(metrics) < 5:
            return 30.0  # Default timeout

        # Use statistical analysis
        mean_time = statistics.mean(metrics)
        stdev_time = statistics.stdev(metrics) if len(metrics) > 1 else 0

        # Set timeout at reasonable percentile with safety margin
        timeout_candidate = (mean_time + 2 * stdev_time) * self.safety_margin
        timeout_candidate = max(self.min_timeout, min(timeout_candidate, self.max_timeout))

        return timeout_candidate

class ImprovedMCPClient(MCPClient):
    """Enhanced MCP client with adaptive timeouts"""

    def __init__(self, timeout_manager: AdaptiveTimeoutManager, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.timeout_manager = timeout_manager
        self.server_name = kwargs.get('server_name', 'default')

    async def call_tool(self, tool_name: str, **kwargs):
        """Call tool with adaptive timeout"""

        optimal_timeout = self.timeout_manager.get_optimal_timeout(self.server_name)

        start_time = asyncio.get_event_loop().time()

        # Apply timeout to the operation
        result = await asyncio.wait_for(
            super().call_tool(tool_name, **kwargs),
            timeout=optimal_timeout
        )

        response_time = asyncio.get_event_loop().time() - start_time
        self.timeout_manager.record_response_time(self.server_name, response_time)

        return result
```

### Request Batching and Parallel Execution

```python
from typing import List, Dict, Any, Optional
import asyncio
from concurrent.futures import ThreadPoolExecutor

class MCPBatchProcessor:
    """Process multiple MCP requests efficiently"""

    def __init__(self, max_concurrent: int = 5):
        self.max_concurrent = max_concurrent
        self.semaphore = asyncio.Semaphore(max_concurrent)

    async def batch_tool_calls(
        self,
        tool_requests: List[Dict[str, Any]],
        server_client: MCPClient
    ) -> List[Any]:
        """Execute multiple tool calls simultaneously"""

        async def execute_single_request(request: Dict[str, Any]) -> Any:
            async with self.semaphore:
                return await server_client.call_tool(
                    tool_name=request["tool_name"],
                    **request.get("parameters", {})
                )

        # Execute all requests concurrently (up to semaphore limit)
        tasks = [
            execute_single_request(request)
            for request in tool_requests
        ]

        results = []
        for result in asyncio.as_completed(tasks):
            results.append(await result)

        return results

    async def parallel_server_calls(
        self,
        server_requests: List[Dict[str, Any]],
        server_pool: dict
    ) -> Dict[str, Any]:
        """Execute calls across multiple MCP servers in parallel"""

        # Group requests by server
        server_task_groups = {}

        for request in server_requests:
            server_name = request["server"]
            if server_name not in server_task_groups:
                server_task_groups[server_name] = []
            server_task_groups[server_name].append(request)

        # Execute requests for each server concurrently
        results = {}
        server_tasks = []

        async def process_server_requests(server_name: str, requests: List[Dict]):
            client = server_pool[server_name]
            batch_processor = MCPBatchProcessor()

            batch_results = await batch_processor.batch_tool_calls(
                requests, client
            )

            return server_name, batch_results

        # Create tasks for each server
        for server_name, requests in server_task_groups.items():
            server_tasks.append(
                process_server_requests(server_name, requests)
            )

        # Execute all server tasks in parallel
        server_results = await asyncio.gather(*server_tasks)

        # Organize results
        for server_name, batch_results in server_results:
            results[server_name] = batch_results

        return results
```

## Error Handling and Resilience Patterns

### Progressive Degradation Strategy

```python
from typing import Dict, List, Optional
from enum import Enum

class DegradationLevel(Enum):
    FULL_FUNCTIONALITY = "full"
    REDUCED_FUNCTIONALITY = "reduced"
    MINIMAL_FUNCTIONALITY = "minimal"
    EMERGENCY_MODE = "emergency"

class ProgressiveDegradationManager:
    """Manage MCP server degradation gracefully"""

    def __init__(self):
        self.degradation_level = DegradationLevel.FULL_FUNCTIONALITY
        self.degradation_strategies = self._setup_degradation_strategies()

    def _setup_degradation_strategies(self) -> Dict[DegradationLevel, dict]:
        """Define strategies for each degradation level"""

        return {
            DegradationLevel.FULL_FUNCTIONALITY: {
                "allowed_servers": ["fetch", "filesystem", "playwright", "weather"],
                "fallback_behavior": "normal",
                "error_handling": "comprehensive"
            },
            DegradationLevel.REDUCED_FUNCTIONALITY: {
                "allowed_servers": ["filesystem", "fetch"],
                "fallback_behavior": "limited_tools",
                "error_handling": "basic"
            },
            DegradationLevel.MINIMAL_FUNCTIONALITY: {
                "allowed_servers": ["filesystem"],
                "fallback_behavior": "local_only",
                "error_handling": "minimal"
            },
            DegradationLevel.EMERGENCY_MODE: {
                "allowed_servers": [],
                "fallback_behavior": "no_external_tools",
                "error_handling": "emergency_only"
            }
        }

    def assess_system_health(self, server_statuses: Dict[str, bool]) -> DegradationLevel:
        """Determine appropriate degradation level based on system health"""

        working_servers = sum(1 for status in server_statuses.values() if status)
        total_servers = len(server_statuses)

        health_percentage = working_servers / total_servers

        if health_percentage >= 0.8:
            return DegradationLevel.FULL_FUNCTIONALITY
        elif health_percentage >= 0.5:
            return DegradationLevel.REDUCED_FUNCTIONALITY
        elif health_percentage >= 0.2:
            return DegradationLevel.MINIMAL_FUNCTIONALITY
        else:
            return DegradationLevel.EMERGENCY_MODE

    def get_available_servers(self) -> List[str]:
        """Get servers allowed at current degradation level"""

        strategy = self.degradation_strategies[self.degradation_level]
        return strategy["allowed_servers"]

    def handle_tool_request(
        self,
        required_servers: List[str],
        available_servers: List[str]
    ) -> Optional[str]:
        """Determine if tool request can be fulfilled"""

        missing_servers = set(required_servers) - set(available_servers)

        if not missing_servers:
            return "fulfillable"  # All required servers available

        # Check if partial fulfillment is possible
        strategy = self.degradation_strategies[self.degradation_level]

        if strategy["fallback_behavior"] == "limited_tools":
            # Allow request with reduced functionality
            return "degraded_fulfillment"
        elif strategy["fallback_behavior"] == "local_only":
            # Only fulfill if all requirements are local
            return "local_only" if all(s in ["filesystem"] for s in required_servers) else "unfulfillable"

        return "unfulfillable"

    async def degrade_system(self, new_level: DegradationLevel):
        """Gradually degrade system to specified level"""

        print(f"System degrading to: {new_level.value}")

        # Shutdown unnecessary servers
        old_strategy = self.degradation_strategies[self.degradation_level]
        new_strategy = self.degradation_strategies[new_level]

        servers_to_shutdown = set(old_strategy["allowed_servers"]) - set(new_strategy["allowed_servers"])

        for server in servers_to_shutdown:
            print(f"Shutting down server: {server}")
            # Server shutdown logic here

        self.degradation_level = new_level
        print(f"System now operating at {new_level.value} level")
```

## Configuration Management Best Practices

### Environment-Based Configuration

```python
from pydantic import BaseModel, Field
from typing import Optional, List, Dict
import os

class MCPEnvironmentConfig(BaseModel):
    """Environment-specific MCP configuration"""

    environment: str = Field(default="development")

    # Server defaults
    default_timeout: float = Field(default=30.0)
    enable_monitoring: bool = Field(default=False)
    sandbox_all_operations: bool = Field(default=True)

    # Security settings
    require_https: bool = Field(default=False)
    allowed_domains: Optional[List[str]] = None

    # Resource limits
    max_memory_mb: int = Field(default=512)
    max_concurrent_requests: int = Field(default=10)

    # Specific server configurations
    servers: Dict[str, dict] = Field(default_factory=dict)

    @classmethod
    def from_environment(cls) -> "MCPEnvironmentConfig":
        """Load configuration from environment variables"""

        env = os.getenv("APP_ENV", "development")

        base_config = {
            "development": {
                "environment": "development",
                "enable_monitoring": True,
                "require_https": False,
                "servers": {
                    "filesystem": {
                        "command": "npx",
                        "args": ["-y", "@modelcontextprotocol/server-filesystem", "./dev-data"],
                        "timeout": 30
                    },
                    "fetch": {
                        "command": "uvx",
                        "args": ["mcp-server-fetch"]
                    }
                }
            },
            "production": {
                "environment": "production",
                "enable_monitoring": True,
                "require_https": True,
                "allowed_domains": ["api.trusted-service.com", "secure-data.example.com"],
                "max_memory_mb": 2048,
                "sandbox_all_operations": True,
                "servers": {
                    "secure-filesystem": {
                        "command": "docker",
                        "args": ["run", "--rm", "-v", "/secure/data:/app/data", "secure-mcp-fs"],
                        "timeout": 45
                    }
                }
            }
        }

        config_dict = base_config.get(env, base_config["development"])
        return cls(**config_dict)
```

### Configuration Validation and Schema Enforcement

```python
from pydantic import BaseModel, validator, Field
from typing import List, Dict, Any, Optional
import json

class MCPServerSchema(BaseModel):
    """Schema for MCP server configuration"""

    name: str = Field(..., min_length=1, max_length=50)
    command: str = Field(..., min_length=1)
    args: List[str] = Field(default_factory=list)
    timeout: float = Field(default=30.0, ge=1.0, le=300.0)
    environment_variables: Dict[str, str] = Field(default_factory=dict)

    @validator('command')
    def validate_command(cls, v):
        """Ensure command is safe to execute"""

        dangerous_commands = ['rm', 'dd', 'mkfs', 'sudo', 'su']
        cmd_base = v.split()[0].lower()

        if any(dangerous in v.lower() for dangerous in dangerous_commands):
            raise ValueError(f"Potentially dangerous command: {v}")

        return v

class MCPConfigurationValidator:
    """Validate complete MCP configuration"""

    def __init__(self):
        self.validation_errors = []
        self.validation_warnings = []

    def validate_entire_config(self, config: dict) -> bool:
        """Validate complete MCP configuration"""

        self.validation_errors = []
        self.validation_warnings = []

        # Basic structure validation
        if not isinstance(config, dict):
            self.validation_errors.append("Configuration must be a dictionary")
            return False

        # Environment validation
        if "environment" not in config:
            self.validation_warnings.append("No environment specified, using defaults")

        # Server configurations validation
        if "servers" in config:
            self._validate_servers_section(config["servers"])

        # Cross-reference validation
        self._validate_cross_references(config)

        return len(self.validation_errors) == 0

    def _validate_servers_section(self, servers: dict):
        """Validate individual server configurations"""

        for server_name, server_config in servers.items():
            if not isinstance(server_config, dict):
                self.validation_errors.append(f"Server '{server_name}' configuration must be a dictionary")
                continue

            try:
                MCPServerSchema(**server_config)
            except Exception as e:
                self.validation_errors.append(f"Server '{server_name}' configuration invalid: {e}")

    def _validate_cross_references(self, config: dict):
        """Validate references between configuration sections"""

        # Check if referenced domains are in allowed_domains
        allowed_domains = config.get("allowed_domains", [])
        servers = config.get("servers", {})

        for server_name, server_config in servers.items():
            if "args" in server_config:
                for arg in server_config["args"]:
                    # Check if argument contains domains that should be validated
                    if "http" in arg.lower() and not self._is_domain_allowed(arg, allowed_domains):
                        if "require_https" in config and config["require_https"]:
                            self.validation_errors.append(
                                f"Server '{server_name}' references non-allowed domain in HTTPS-required mode"
                            )

    def _is_domain_allowed(self, url_string: str, allowed_domains: List[str]) -> bool:
        """Check if URL domain is in allowed list"""

        from urllib.parse import urlparse

        try:
            parsed = urlparse(url_string)
            if parsed.netloc:
                return any(domain in parsed.netloc for domain in allowed_domains)
        except:
            pass

        return True  # Allow non-URL strings
```

These patterns and practices provide a foundation for building reliable, scalable MCP-based agent applications. Focus on progressive enhancement - start with basic implementations and add sophistication based on production requirements and operational insights.
