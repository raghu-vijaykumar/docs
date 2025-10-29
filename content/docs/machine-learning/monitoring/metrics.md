---
title: "Monitoring Metrics"
---

# Monitoring Metrics

Effective monitoring requires tracking multiple categories of metrics to detect drift, performance issues, and system health.

## Dashboard Setup

- Track metrics that indicate potential problems
- Set thresholds and alarms for alerting
- Include both software and ML-specific metrics
- Monitor trends over time, not just snapshots

## Software Metrics

Core infrastructure health indicators:
- **Memory usage**: RAM consumption levels
- **CPU utilization**: Compute resource usage
- **Latency**: Response time in milliseconds
- **Throughput**: Requests per second (QPS)
- **Server load**: Overall system load averages
- **Availability**: Uptime percentages

## Implementation: Basic ML Metrics Collection

Here's a Python example using Prometheus client to track essential ML API metrics:

```python
from prometheus_client import Counter, Histogram, Gauge, CollectorRegistry
import time

# Create registry for metrics
registry = CollectorRegistry()

# Define metrics
PREDICTION_COUNT = Counter(
    'ml_predictions_total',
    'Total number of ML predictions made',
    ['model_version', 'endpoint'],
    registry=registry
)

PREDICTION_LATENCY = Histogram(
    'ml_prediction_latency_seconds',
    'Prediction latency in seconds',
    ['model_version'],
    buckets=[0.01, 0.1, 0.5, 1.0, 2.0, 5.0],
    registry=registry
)

MODEL_MEMORY_USAGE = Gauge(
    'ml_model_memory_mb',
    'Model memory usage in MB',
    ['model_name'],
    registry=registry
)

ERROR_COUNT = Counter(
    'ml_errors_total',
    'Total ML prediction errors',
    ['error_type', 'model_version'],
    registry=registry
)

def predict_with_monitoring(input_data, model, model_version="v1.0"):
    start_time = time.time()

    try:
        # Make prediction
        result = model.predict(input_data)

        # Record successful prediction
        PREDICTION_COUNT.labels(model_version=model_version, endpoint="predict").inc()
        PREDICTION_LATENCY.labels(model_version=model_version).observe(time.time() - start_time)

        return result

    except Exception as e:
        # Record error
        ERROR_COUNT.labels(error_type=type(e).__name__, model_version=model_version).inc()
        raise e

# Usage example
from sklearn.ensemble import RandomForestClassifier
model = RandomForestClassifier()
# ... model training ...
MODEL_MEMORY_USAGE.labels(model_name="rf_classifier").set(50.5)  # Update memory usage

# Serve predictions with monitoring
predictions = predict_with_monitoring(test_data, model)
```

## Input Metrics

Track changes in input data distribution:
- **Input length**: Audio duration, text length, image size
- **Missing values**: Percentage of null/empty inputs
- **Volume levels**: Audio amplitude, image brightness
- **Data ranges**: Expected value distributions

### Data Drift Detection
Compare current inputs against baseline:
- Audio: Changed microphone characteristics, accents
- Visual: Lighting conditions, camera upgrades
- Text: New terminology, user behavior shifts

## Output Metrics

Monitor model predictions and end-user impact:
- **Null responses**: Prediction failures (speech system thinks no speech)
- **Failed predictions**: Error rates, confidence scores
- **User behavior**: Search retries, app switches, abandonment rates
- **Click-through rate**: Engagement metrics for recommenders

## Implementation Strategy

### Brainstorm Everything That Can Go Wrong
Systematically identify failure modes:
- Hardware failures, network issues
- Data quality degradation
- Model performance drops
- External factor changes (lighting, user behavior)

### Start Comprehensive, Refine Over Time
Initial monitoring includes many metrics, then:
- Remove irrelevant ones
- Add newly discovered issues
- Adjust thresholds based on observed patterns

### Set Alert Thresholds
- Define acceptable ranges for key metrics
- Example: Server load > 0.91 triggers alert
- Example: Null output fraction > 5% requires investigation

## Iterative Process

Monitoring setup is never finished:
- Deploy initial metrics suite
- Run system and observe real performance
- Add metrics for unexpected failure modes
- Refine thresholds based on false positives/negatives
- Remove metrics that never provide useful signals

## Monitoring Strategies & Tools

### Why Monitoring Matters

Failures in monitoring can have catastrophic consequences:
- **Silent model failures**: Systems appear working but don't add value
- **Uncaught bugs**: Subtle issues accumulate over time
- **Regulatory violations**: Especially in healthcare/finance
- **Business losses**: Lost revenue from degraded performance

### Monitoring Challenges

ML systems add complexity beyond traditional software:
- **Data dependencies**: Models sensitive to input changes
- **Configuration sensitivity**: Small changes cause big behavioral shifts
- **Entanglement**: Feature changes affect all model components
- **Team coordination**: Data scientists, engineers, DevOps need alignment

### Operational Monitoring

Track system health using standard SRE tools:
- **Metrics**: Latency, CPU/memory, throughput
- **Logs**: Event records with context
- **Traces**: Distributed system call flows

### Data Science Monitoring

Track model-specific aspects:
- **Input distributions**: Feature values, missing data rates
- **Output distributions**: Prediction statistics, confidence scores
- **Data quality**: Drift detection, skew identification

## Tooling Examples

### Prometheus & Grafana

Set up metrics collection and visualization:
- **Scrape ML API endpoints**: Latency, prediction counts
- **Track system resources**: CPU, memory during inference
- **Create dashboards**: Trend analysis, anomaly alerts

```yaml
# Example Prometheus configuration
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'ml-api'
    static_configs:
      - targets: ['ml-api:8080']
```

### Expanded Configuration: Alert Rules

```yaml
# Prometheus alert rules for ML services
groups:
  - name: ml_service_alerts
    rules:
      - alert: HighPredictionErrorRate
        expr: rate(ml_errors_total[5m]) / rate(ml_predictions_total[5m]) > 0.05
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High ML prediction error rate"

      - alert: PredictionLatencyAnomaly
        expr: histogram_quantile(0.95, rate(ml_prediction_latency_seconds_bucket[10m])) > 3
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "ML prediction latency is elevated"

      - alert: MemoryUsageSpike
        expr: ml_model_memory_mb > 1000
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "ML model memory usage critically high"
```

### Grafana Dashboard Example

```json
{
  "dashboard": {
    "title": "ML Service Health",
    "panels": [
      {
        "title": "Prediction Latency",
        "type": "graph",
        "targets": [{
          "expr": "histogram_quantile(0.95, rate(ml_prediction_latency_seconds_bucket[5m]))",
          "legendFormat": "p95 latency"
        }]
      },
      {
        "title": "Error Rate",
        "type": "graph",
        "targets": [{
          "expr": "rate(ml_errors_total[5m]) / rate(ml_predictions_total[5m])",
          "legendFormat": "error rate"
        }]
      }
    ]
  }
}
```

### Logging with ELK Stack

Capture detailed events for debugging:
- **Input validation**: Feature distributions, missing values
- **Prediction details**: Confidence scores, decision paths
- **Error contexts**: Stack traces, input samples

## Risk Management Spectrum

From no monitoring to heavy oversight:
- **No validation**: High risk, easy changes
- **Basic alerting**: Reduced risk, minimal overhead
- **Full observability**: Lowest risk, most restrictive

## Related Topics

- [Concept Drift](../drift)
- [Pipeline Monitoring](../pipelines)
- [Deployment Patterns](../../deployment/patterns)
