---
title: "Distributed Training"
weight: 10
description: "Data-parallel and model-parallel training patterns with Horovod and TensorFlow Distributed, including communication strategies, scaling rules, and failure handling."
draft: false
---

# Distributed Training

When a single machine cannot keep up with data volume or model size, you distribute training. The key is to scale compute while maintaining convergence and correctness.

This guide focuses on data-parallel synchronous training (the default for most workloads), when to consider model/pipeline parallelism, and the mechanics of efficient communication.

---

## Parallelism patterns

- Data parallelism (most common):
  - Each worker holds a full model replica, processes a different data shard, and synchronizes gradients/weights.
  - Synchronous (all-reduce) vs asynchronous (parameter server) updates.

- Model parallelism:
  - Split model layers or tensors across devices/nodes when a single device doesn’t fit the model.
  - Used in very large models (LLMs, large CV models).

- Pipeline parallelism:
  - Partition layers into stages and stream micro-batches through the pipeline.
  - Increases utilization for deep networks with sequential stages.

Combine as needed (e.g., tensor parallel + pipeline parallel + data parallel for very large models).

---

## Communication strategies

- Collective all-reduce (synchronous):
  - NCCL (NVIDIA), Gloo (CPU/GPU), MPI backends.
  - Ring all-reduce, hierarchical all-reduce to minimize bandwidth bottlenecks.
  - Strong convergence and reproducibility.

- Parameter server (asynchronous):
  - Workers communicate gradients to PS; weights update asynchronously.
  - Better throughput, but staleness can harm convergence/accuracy.

Heuristics:
- Prefer synchronous all-reduce for most deep learning.
- Use fp16/bfloat16 gradients (mixed precision) to reduce bandwidth.
- Enable gradient bucketing/fusion to amortize communication overhead.

---

## Scaling rules of thumb

- Linear scaling rule: if you multiply batch size by k, scale learning rate by k and add warm-up.
- Keep an eye on the optimization regime: very large batches may change generalization; try LARS/LAMB optimizers where applicable.
- Start with gradient accumulation before multi-node: test larger effective batch sizes on a single node.

---

## Horovod (PyTorch/TensorFlow)

Horovod simplifies data-parallel training with ring all-reduce.

```python
# pip install horovod[pytorch]
import horovod.torch as hvd
import torch
from torch import nn, optim
from torch.utils.data import DataLoader, DistributedSampler

hvd.init()
torch.cuda.set_device(hvd.local_rank())

model = MyModel().cuda()
optimizer = optim.SGD(model.parameters(), lr=0.1 * hvd.size(), momentum=0.9)

# Broadcast initial state from rank 0
hvd.broadcast_parameters(model.state_dict(), root_rank=0)
hvd.broadcast_optimizer_state(optimizer, root_rank=0)

# Wrap optimizer for distributed allreduce
optimizer = hvd.DistributedOptimizer(
    optimizer, named_parameters=model.named_parameters(), op=hvd.Average
)

dataset = MyDataset(...)
sampler = DistributedSampler(dataset, num_replicas=hvd.size(), rank=hvd.rank(), shuffle=True)
loader = DataLoader(dataset, batch_size=64, sampler=sampler, pin_memory=True, num_workers=4)

scaler = torch.cuda.amp.GradScaler()

for epoch in range(num_epochs):
    sampler.set_epoch(epoch)
    model.train()
    for x, y in loader:
        x, y = x.cuda(non_blocking=True), y.cuda(non_blocking=True)
        optimizer.zero_grad(set_to_none=True)
        with torch.cuda.amp.autocast():
            loss = nn.functional.cross_entropy(model(x), y)
        scaler.scale(loss).backward()
        scaler.step(optimizer)
        scaler.update()
```

Launch:
```bash
horovodrun -np 8 -H host1:4,host2:4 python train.py
# Or using mpirun, or via cluster schedulers (K8s, SLURM)
```

---

## TensorFlow Distributed (tf.distribute)

Use `MultiWorkerMirroredStrategy` for multi-host synchronous training.

```python
import tensorflow as tf
strategy = tf.distribute.MultiWorkerMirroredStrategy()

global_batch_size = 256
with strategy.scope():
    model = make_model()
    opt = tf.keras.optimizers.Adam(learning_rate=3e-4)
    model.compile(optimizer=opt, loss="sparse_categorical_crossentropy", metrics=["accuracy"])

# Use a sharded dataset (e.g., TFRecords with file-level sharding)
model.fit(train_ds, validation_data=val_ds, epochs=30)
```

Cluster spec via TF environment variables (TF_CONFIG) or K8s operator.

---

## Failure handling and determinism

- Elastic training: allow workers to join/leave (Horovod Elastic, TorchElastic). Useful on preemptible instances.
- Checkpointing:
  - Save model + optimizer + scheduler states frequently.
  - Store in distributed object storage (S3/GCS/Azure Blob).
- Determinism:
  - Fix seeds across frameworks and data loaders.
  - Be aware that some CUDA ops are non-deterministic; accept small variance.

---

## Data pipelines and sharding

- Shard datasets by worker rank; avoid duplicate samples per epoch.
- Preprocessing:
  - Move heavy ETL to Spark/Beam or tf.data with interleave/prefetch/cache.
  - Use record formats that support parallel reads (TFRecord, Parquet).
- Ensure identical preprocessing between training and serving (export full pipeline where possible).

---

## Performance tuning checklist

- Mixed precision (AMP) enabled; loss scaling for stability.
- Gradient accumulation to reach target effective batch size.
- Pin memory, set num_workers appropriately, prefetch and cache data.
- Profile: identify data-bound vs compute-bound; overlap compute/communication.
- Set environment:
  - NCCL/GPU affinity, interconnect bandwidth (NVLink, Infiniband), thread pools.
- Monitor:
  - Step time, communication time vs compute time, GPU utilization, host I/O.

---

## When to use model/pipeline parallelism

- Model does not fit a single device memory even with activation checkpointing.
- Very deep sequential models benefit from pipeline stages (reduce bubbles with balanced stage times and micro-batches).
- Use libraries/tooling:
  - PyTorch: torch.distributed, DeepSpeed (ZeRO stages), Megatron-LM.
  - TensorFlow: model partitioning, GSPMD.

---

## Summary

Favor synchronous data parallelism with mixed precision and strong I/O pipelines. Scale learning rate with batch size (with warm-up), checkpoint robustly, and profile to resolve compute/communication bottlenecks. Move to tensor/pipeline parallel only when model size forces it or to push utilization on very deep models.
