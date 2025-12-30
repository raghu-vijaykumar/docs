---
title: "Big Data & Scalable ML"
weight: 70
description: "Architectures, frameworks, and patterns for training and serving ML at scale: distributed training, Spark ML, and AutoML."
draft: false
---

# Big Data & Scalable ML

Scaling machine learning is as much about systems design as it is about algorithms. This section covers practical approaches to training and serving models when data, parameters, or throughput exceed a single machine’s capacity.

## What you'll find here

- Distributed Training: Data-parallel and model-parallel strategies with Horovod and TensorFlow Distributed
- ML on Spark: When to use Spark MLlib, how to integrate with popular frameworks, and Databricks patterns
- AutoML & No-Code ML: Strengths, limits, and safe productionization

---

## Scaling dimensions

- Data scale: Terabytes–petabytes; I/O and preprocessing dominate; push compute to data
- Parameter scale: Millions–billions; communication and optimizer behavior matter
- Throughput/latency: Online inference at scale; batching, vectorization, and caching

Common patterns:
- Data-parallel SGD with synchronous allreduce (strong convergence, communication-heavy)
- Asynchronous training with parameter servers (higher throughput, staler gradients)
- Sharded datasets and feature stores co-located with compute to minimize data movement

---

## Distributed training: practical guidance

- Prefer synchronous data-parallel training via collective communication (NCCL/Gloo) for most deep learning workloads
- Use mixed precision (FP16/bfloat16) to improve throughput and reduce bandwidth
- Scale batch size with linear LR scaling and warm-up; watch optimization regime changes
- For tall-tabular + tree ensembles, scale via distributed implementations (XGBoost on Spark/Ray, LightGBM on Dask)

---

## Spark ML at a glance

- Use Spark MLlib for feature pipelines and classical ML at cluster scale (logistic regression, tree ensembles, ALS)
- For deep learning, use Spark primarily for ETL and distributed inference (batch scoring) while training on specialized frameworks
- Persist intermediate features and model artifacts in reliable object stores (Parquet + Delta Lake) for lineage

---

## AutoML in production

- AutoML accelerates baselining and iteration for structured data
- Always export the entire pipeline (preprocessing + model); lock versions, seeds, and transformers
- Validate explainability and fairness; constrain search to meet latency/cost targets

---

## Contents

- [Distributed Training](distributed-training.md)  
- [ML on Spark](spark-ml.md)  
- [AutoML & No-Code ML](automl.md)
