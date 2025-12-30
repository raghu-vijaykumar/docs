---
title: "Model Serving: APIs, TF Serving, TorchServe, and Cloud"
weight: 20
description: "Patterns and tooling to productionize ML inference: FastAPI/Flask services, TensorFlow Serving, TorchServe, KServe, Triton, and cloud deployment strategies."
draft: false
---

# Model Serving: APIs, TF Serving, TorchServe, and Cloud

Serving makes models useful. The production goal is predictable latency, high availability, safe rollouts, and reproducible behavior from the same artifacts you validated offline. This guide covers practical serving options, from lightweight HTTP APIs to industrial-grade inference servers and cloud platforms.

---

## Serving architecture choices

Pick based on team expertise, latency/throughput needs, and model/runtime:

- Lightweight HTTP service (FastAPI/Flask): Full control, language/runtime freedom, simple to extend. Ideal for low–medium throughput and heterogeneous models.
- Framework-native servers:
  - TensorFlow Serving (TF models, SavedModel)
  - TorchServe (PyTorch, custom handlers)
  - NVIDIA Triton (multi-framework, GPU batching)
- Kubernetes-native: KServe, Seldon Core (versioning, canary, autoscaling, request/response contracts)
- Cloud managed: SageMaker, Vertex AI, Azure ML (autoscaling, A/B, monitoring, minimal ops)

Common requirements:
- Stable model artifact format
- Feature parity offline vs online
- Observability (metrics, logs, traces)
- Versioning and safe rollout paths
- Security (authn/z, secrets, input validation)

---

## Option 1: FastAPI/Flask microservice

Pros: Flexible, simple, fast developer loop.  
Cons: You own infra, scaling, and GPU scheduling.

FastAPI example (sklearn/xgboost/pytorch CPU):

```python
# app.py
import joblib
import numpy as np
from pydantic import BaseModel, conlist
from fastapi import FastAPI, HTTPException
from starlette.middleware.cors import CORSMiddleware
import uvicorn

class PredictRequest(BaseModel):
    features: conlist(float, min_items=1)  # single row
    model_version: str | None = None

class PredictBatchRequest(BaseModel):
    features: list[conlist(float, min_items=1)]  # batch rows
    model_version: str | None = None

app = FastAPI(title="ML Inference API", version="1.0.0")
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"])

# Load artifacts at startup
MODEL_PATH = "artifacts/model.joblib"
SCALER_PATH = "artifacts/scaler.joblib"
model = joblib.load(MODEL_PATH)
scaler = joblib.load(SCALER_PATH)

@app.post("/v1/predict")
def predict(req: PredictRequest):
    try:
        X = np.array(req.features, dtype=float).reshape(1, -1)
        X = scaler.transform(X)
        y = model.predict_proba(X)[:, 1].tolist()
        return {"predictions": y, "model_version": req.model_version or "1.0.0"}
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/v1/predict_batch")
def predict_batch(req: PredictBatchRequest):
    X = np.array(req.features, dtype=float)
    X = scaler.transform(X)
    y = model.predict_proba(X)[:, 1].tolist()
    return {"predictions": y, "n": len(y), "model_version": req.model_version or "1.0.0"}

if __name__ == "__main__":
    uvicorn.run("app:app", host="0.0.0.0", port=8080, workers=2)
```

Packaging:
- Model store (S3/GCS/Azure) + checksum/version file
- Dockerfile with slim Python base; pin exact dependency versions
- Health endpoints (/healthz, /readyz)
- Structured logging (JSON) with request_id and latency
- Prometheus metrics (latency p50/p95/p99, QPS, error rate)

Performance tips:
- Preload artifacts, avoid global interpreter locks with uvicorn workers or gunicorn+uvicorn workers
- Use numpy/pandas vectorization; avoid per-row Python loops
- Consider ONNX or TorchScript for CPU/GPU acceleration

---

## Option 2: TensorFlow Serving

Optimized server for SavedModel format with gRPC/HTTP interfaces.

Artifacts:
- Export as SavedModel (contains graph, weights, signatures)

```python
import tensorflow as tf
# model: tf.keras.Model
tf.saved_model.save(model, "export/1")  # version folder
```

Run TF Serving (Docker):
```bash
docker run -p 8501:8501 \
  -v "$PWD/export:/models/my_model" \
  -e MODEL_NAME=my_model \
  tensorflow/serving:latest
```

Query (HTTP):
```bash
curl -X POST http://localhost:8501/v1/models/my_model:predict \
  -H "Content-Type: application/json" \
  -d '{"instances": [[0.5, 1.2, -0.3, 2.1]]}'
```

Features:
- Native batching and model versioning
- gRPC for low-latency clients
- Dynamic reloading on new version directories

When to use:
- TensorFlow-only stacks
- Need stable, high-performance server out of the box
- Prefer explicit graph signature and shape validation

---

## Option 3: TorchServe

Serving for PyTorch with customizable handlers.

Artifacts:
- TorchScript or eager model + handler
- .mar archive packaged with model + handler + requirements

Example handler:
```python
# handler.py
import torch
from ts.torch_handler.base_handler import BaseHandler

class MyHandler(BaseHandler):
    def preprocess(self, data):
        # data: list of {body: bytes/json}
        import json, numpy as np
        arr = np.array(json.loads(data[0]["body"])["features"], dtype="float32")
        return torch.from_numpy(arr).unsqueeze(0)

    def postprocess(self, preds):
        return [preds.detach().cpu().numpy().tolist()]
```

Packaging and run:
```bash
torch-model-archiver --model-name mymodel --version 1.0 \
  --serialized-file model.pt --handler handler.py --extra-files "scaler.pkl"
mkdir model_store && mv mymodel.mar model_store/
torchserve --start --model-store model_store --models mymodel=mymodel.mar --ncs
```

Features:
- Multi-model management, batch inference, scaling via workers
- Metrics endpoint, management API
- Good default for PyTorch teams

---

## Option 4: NVIDIA Triton Inference Server

Multi-framework server (TF, PyTorch, ONNX, XGBoost, Python backend) tuned for GPUs and dynamic batching.

Highlights:
- Automatic GPU/CPU batching, concurrent execution, ensemble pipelines
- Supports gRPC/HTTP, Prometheus metrics
- Python backend for arbitrary pre/post-processing

When to use:
- High-throughput GPU inference
- Latency targets benefit from dynamic batching
- Mixed model ecosystems

---

## Kubernetes-native: KServe

Declarative model serving on Kubernetes. Abstractions for predictors, canary, autoscaling.

Example InferenceService (KServe):
```yaml
apiVersion: serving.kserve.io/v1beta1
kind: InferenceService
metadata:
  name: churn-prob
spec:
  predictor:
    sklearn:
      storageUri: s3://ml-artifacts/churn/1/
      resources:
        requests: {cpu: "500m", memory: "1Gi"}
        limits:   {cpu: "2", memory: "2Gi"}
```

Features:
- Canary routing (e.g., 10% → 50% → 100%)
- Autoscaling (KPA/HPA), scale-to-zero
- Request/response schema standardization
- Explainers (alibi), transformers, and outlier detectors

---

## Cloud-managed platforms

- AWS SageMaker: real-time endpoints, multi-model endpoints, batch transform, A/B deploy, data capture
- Google Vertex AI: endpoints, model monitoring, batch prediction, explainability
- Azure ML: managed online/batch endpoints, versioned deployments

Benefits:
- Elastic scaling, observability integrations, security and IAM
- Built-in blue/green, canary, shadow
- Pay-as-you-go; lock-in considerations

---

## Feature parity: offline vs online

Most production regressions stem from train–serve skew:
- Use the same feature code in both paths (shared libs, feature store)
- Manage time semantics (no peeking into the future)
- Version everything: model, preprocessing transformers, encoders, scalers
- Add request validation and schema contracts (pydantic/JSON schema/protobuf)

---

## Observability and safety

- Metrics: QPS, latency (p50/p95/p99), error rate, timeouts, queue depth, GPU utilization
- Structured logs: request_id, model_version, input hash, latency
- Tracing: propagate correlation IDs; instrument handlers
- Drift monitors: log feature stats and prediction distribution for offline checks
- Rollouts: shadow → canary → full; auto rollback on SLO breach
- Security: input validation, authN/Z (mTLS/JWT), PII handling, rate limiting

---

## Inference optimization

- Quantization: INT8/FP16 (ONNX Runtime, TensorRT)
- Compilation: TorchScript, TorchDynamo/Inductor, XLA, TF-TensorRT, OpenVINO
- Batching: accumulate small requests within a deadline
- Caching: memoize model outputs for idempotent requests
- Threading/affinity: pin BLAS threads; avoid oversubscription

---

## Reference decision guide

- CPU-bound, simple ops, full control → FastAPI + ONNX Runtime
- PyTorch GPU w/ batching → TorchServe or Triton
- TensorFlow graph models → TF Serving
- Mixed frameworks, aggressive batching, GPUs → Triton
- K8s native, multi-team governance → KServe/Seldon
- Minimal ops, integrated governance → Cloud managed endpoints

---

## Checklist

- Deterministic artifact packaging (hashes, immutability)
- Schema-validated requests and safe defaults
- Health/readiness probes and graceful shutdowns
- Versioned rollouts and traffic shaping
- Metrics/logs/traces wired; dashboards and alerts
- Security posture: secrets management, least-privilege IAM, network policy
