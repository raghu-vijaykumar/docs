---
title: "ML on Spark (MLlib, Databricks)"
weight: 20
description: "When and how to use Spark for ML: feature pipelines, classical algorithms at scale, distributed XGBoost/LightGBM, and Databricks patterns."
draft: false
---

# ML on Spark (MLlib, Databricks)

Spark is a strong fit when feature engineering and data preparation dominate and the dataset is too large for a single machine. Use Spark for scalable ETL, feature pipelines, and classical ML; for deep learning training prefer specialized frameworks and use Spark for data prep and batch inference.

---

## When to use Spark for ML

Use Spark MLlib when:
- You need to preprocess and join very large datasets (terabytes) with cluster-scale compute.
- Your modeling needs are classical ML (logistic regression, trees/ensembles, ALS).
- You need distributed cross-validation and hyperparameter search integrated with the same feature pipeline.
- You plan to run distributed batch scoring or maintain feature pipelines in production (Delta/Parquet lineage).

Prefer non-Spark training when:
- Training deep learning models (use PyTorch/TensorFlow with distributed strategies).
- You require GPU-optimized training loops or custom model architectures.
- You can fit the data and pipeline into a single machine (scikit-learn + joblib may be faster/simpler).

---

## Spark ML pipelines

Pipelines let you build leakage-safe, reproducible preprocessing + model flows.

```python
# PySpark ML pipeline example (classification)
from pyspark.sql import SparkSession
from pyspark.ml import Pipeline
from pyspark.ml.feature import StringIndexer, OneHotEncoder, VectorAssembler, StandardScaler
from pyspark.ml.classification import GBTClassifier
from pyspark.ml.evaluation import BinaryClassificationEvaluator
from pyspark.ml.tuning import CrossValidator, ParamGridBuilder

spark = SparkSession.builder.getOrCreate()

df = spark.read.parquet("s3://bucket/data/training/")
cat_cols = ["segment", "region"]
num_cols = ["age", "income", "debt"]

indexers = [StringIndexer(inputCol=c, outputCol=f"{c}_idx", handleInvalid="keep") for c in cat_cols]
encoders = [OneHotEncoder(inputCols=[f"{c}_idx"], outputCols=[f"{c}_oh"]) for c in cat_cols]
assembler = VectorAssembler(
    inputCols=[f"{c}_oh" for c in cat_cols] + num_cols, outputCol="features_raw"
)
scaler = StandardScaler(inputCol="features_raw", outputCol="features", withMean=False, withStd=True)

gbt = GBTClassifier(featuresCol="features", labelCol="label", maxDepth=6, maxIter=100)

pipe = Pipeline(stages=indexers + encoders + [assembler, scaler, gbt])

evaluator = BinaryClassificationEvaluator(labelCol="label", rawPredictionCol="rawPrediction", metricName="areaUnderROC")

param_grid = (ParamGridBuilder()
              .addGrid(gbt.maxDepth, [4, 6, 8])
              .addGrid(gbt.maxIter, [50, 100])
              .build())

cv = CrossValidator(estimator=pipe, estimatorParamMaps=param_grid, evaluator=evaluator, numFolds=5, parallelism=4)

model = cv.fit(df)
print("Best AUC:", model.avgMetrics[model.avgMetrics.index(max(model.avgMetrics))])

model.bestModel.write().overwrite().save("s3://bucket/models/churn_gbt")
```

Notes:
- handleInvalid="keep" prevents failures on unseen categories.
- Use VectorAssembler to create a single vector column; many MLlib models expect it.
- Keep feature creation inside the pipeline to avoid leakage.

---

## Distributed XGBoost and LightGBM

Leverage distributed tree ensembles with Spark for tabular data.

### XGBoost4J-Spark
- Provides Spark Estimator/Transformer APIs.
- Uses its own distributed training backend; supports checkpoints, early stopping.

```python
from xgboost.spark import SparkXGBClassifier

xgb = SparkXGBClassifier(
    features_col="features", label_col="label",
    max_depth=8, n_estimators=500, learning_rate=0.05,
    subsample=0.8, colsample_bytree=0.8, tree_method="hist",
    early_stopping_rounds=50, eval_metric="auc"
)
pipe = Pipeline(stages=[assembler, xgb])
model = pipe.fit(train_df)
```

### LightGBM on Spark (mmlspark)
- Gradient-based one-side sampling, histogram-based; often faster than XGBoost.
- Good for high-cardinality categoricals (if using native categorical support in LightGBM — ensure correct encoding pipeline).

```python
from synapse.ml.lightgbm import LightGBMClassifier

lgbm = LightGBMClassifier(
    featuresCol="features", labelCol="label",
    numLeaves=63, numIterations=1000, learningRate=0.05,
    baggingFraction=0.8, featureFraction=0.8, objective="binary",
    earlyStoppingRound=50
)
pipe = Pipeline(stages=[assembler, lgbm])
model = pipe.fit(train_df)
```

Operational tips:
- Persist training data in memory (cache) after expensive feature steps.
- Use checkpointing for long-running jobs.
- Ensure consistent random seeds and partitions for reproducibility.

---

## Reproducible data and features with Delta Lake

- Store training data and intermediate features in Delta tables (ACID on data lakes).
- Use time travel to pin exact versions used for training/evaluation.
- Maintain feature definitions as code; consider a feature store for online/offline parity.

Databricks pattern:
- Bronze (raw) → Silver (cleaned) → Gold (features)
- Training reads Gold at a version; scoring uses the same transformations in Jobs or Model Serving.

---

## Batch scoring and streaming inference

- Batch prediction: use the same pipeline to transform and score large datasets.
- Streaming: apply the pipeline to Structured Streaming data (consider latency and model size).
- Save models with MLflow or pipeline.write(); package with dependencies.

```python
scored = model.transform(new_df).select("id", "prediction", "probability")
scored.write.mode("overwrite").parquet("s3://bucket/scores/dt=2025-10-29/")
```

---

## Databricks and MLflow

- Track runs, parameters, metrics, and artifacts with MLflow.
- Register models in the Model Registry; manage stages (Staging/Production), approvals, and rollbacks.
- Use Databricks Jobs for scheduled training/scoring; Model Serving for low-latency endpoints (for supported runtimes).

---

## Performance and cost tips

- Prune the pipeline: push down heavy joins/filters early; cache after expensive transforms.
- Use columnar formats (Parquet/Delta) and predicate pushdown.
- Balance partitions: avoid skew (salting for hot keys), coalesce/repartition for downstream stages.
- Prefer tree ensembles for accuracy/speed on tabular; reserve Spark MLlib deep learning for simple use cases.

---

## Limitations and gotchas

- MLlib algorithms are limited compared to scikit-learn/XGBoost/LightGBM ecosystems.
- Python UDFs can be slow; use Pandas UDFs or native Spark SQL functions where possible.
- Training on GPUs via Spark is limited; for deep learning, orchestrate ETL with Spark and train with distributed DL frameworks.

---

## Summary

Use Spark to scale data processing and classical ML with end-to-end pipelines, distributed CV, and robust batch/stream inference. For deep learning training, rely on specialized frameworks and keep Spark for data engineering, orchestration, and serving batch predictions with strong lineage and governance.
