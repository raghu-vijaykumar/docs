---
title: "AutoML & No-Code ML"
weight: 30
description: "Practical use of AutoML for large-scale tabular, vision, and NLP tasks: strengths, limits, governance, and integration with production pipelines."
draft: false
---

# AutoML & No-Code ML

AutoML accelerates model development by automating feature processing, model selection, and hyperparameter tuning. It is most effective for structured/tabular problems and increasingly capable in vision and NLP. Treat AutoML as a productivity tool, not a replacement for sound problem framing, data governance, and production engineering.

---

## Where AutoML fits

Best fit:
- Tabular classification/regression with moderate-to-large datasets
- Baseline creation and rapid iteration when teams are small or timelines tight
- Systematic search over tree ensembles, linear models, stacking/ensembling
- Time-constrained competitions/benchmarks, A/B candidate generation

Works but requires care:
- Time-series forecasting (need leak-proof folds, calendar effects, hierarchical aggregation)
- Text and image tasks (pretrained backbones + fine-tuning; often GPU/cost heavy)
- Highly imbalanced data (custom metrics, thresholds, stratification required)

Poor fit:
- Custom architectures (LLMs, specialized CV/NLP), non-standard losses/constraints
- Strict latency/memory budgets without search space control
- Data with heavy domain-specific feature semantics that need custom transforms

---

## Capabilities

- Automated preprocessing: missing data strategies, scaling, categorical encodings
- Model search: gradient-boosted trees (XGBoost/LightGBM/CatBoost), linear/logistic, random forests, kNN, SVM (sometimes), neural baselines
- Hyperparameter optimization: random/Bayesian/TPE; early stopping; multi-fidelity schedulers
- Ensembling/stacking: blend top candidates for robust performance
- Cross-validation management: stratified/grouped/time-series folds
- Metric-driven selection: ROC-AUC, F1, logloss, RMSE/MAE, custom metrics
- Exportable artifacts: pipelines + models with versioning

---

## Guardrails and governance

- Data leakage: ensure splits align with reality (time-aware, group-aware); keep all preprocessing inside the CV loop
- Reproducibility: pin seeds; export exact search space and top trials; log versions of data, code, and dependencies
- Metric choice: align with business objectives; define secondary guardrails (calibration, cost, fairness)
- Complexity control: cap model size and depth; constrain feature generation; enforce latency/memory budgets
- Compliance: document features used; track PII handling; preserve explainability (use SHAP on final models)

---

## Popular tools

Open-source:
- Auto-sklearn: Bayesian optimization + meta-learning on scikit-learn
- TPOT: genetic programming to evolve pipelines
- AutoGluon: strong tabular AutoML with multi-layer stacking and easy APIs
- FLAML: lightweight AutoML with cost-aware search
- H2O AutoML: ensembles/stacking across GBMs/GLMs/NNs; robust for tabular

Managed:
- AWS SageMaker Autopilot
- Google Vertex AI AutoML
- Azure AutoML

These add cluster autoscaling, experiment tracking, and endpoint deployment.

---

## Production checklist

- Define metric(s) and constraints up front (latency, memory, inference cost)
- Select validation scheme (StratifiedKFold/GroupKFold/TimeSeriesSplit) and lock it
- Constrain search space with domain bounds (e.g., depth 3–12, learning rate 1e−3–1e−1)
- Enable early stopping; use schedulers (ASHA/HyperBand) for budget efficiency
- Export the winning pipeline (preprocessing + model) and version it
- Run independent holdout evaluation; calibrate probabilities if required
- Profile inference; compress where needed (quantization/pruning/ONNX)
- Add explainability reports (global and per-feature) and fairness checks

---

## Example: AutoGluon (tabular)

```python
# pip install autogluon.tabular
from autogluon.tabular import TabularPredictor

label = "churned"
metric = "roc_auc"  # align with business objective

predictor = TabularPredictor(
    label=label, problem_type="binary", eval_metric=metric
).fit(
    train_data=train_df,
    time_limit=2 * 60 * 60,  # 2 hours
    presets="best_quality",   # or "medium_quality_faster_train"
    num_bag_folds=5,          # stacking/bagging for robustness
    ag_args_fit={"num_gpus": 0}
)

leaderboard = predictor.leaderboard(test_df, silent=True)
print(leaderboard)

# Export artifacts
predictor.persist()               # ensure models kept on disk
predictor.path                    # directory with models + preprocessing
probas = predictor.predict_proba(test_df)
```

Notes:
- presets tune speed/accuracy trade-offs
- num_bag_folds enables ensembling; increase for stronger generalization
- Use `feature_importance` to compute SHAP values for the final ensemble

---

## Example: Auto-sklearn (scikit-learn)

```python
# pip install auto-sklearn
import autosklearn.classification as ask
from sklearn.model_selection import StratifiedKFold
from sklearn.metrics import roc_auc_score

automl = ask.AutoSklearnClassifier(
    time_left_for_this_task=7200,            # 2 hours wall-clock
    per_run_time_limit=600,                  # per model cap
    ensemble_size=50,
    resampling_strategy="cv",
    resampling_strategy_arguments={"folds": 5},
    metric=ask.metrics.roc_auc
)
automl.fit(X_train, y_train)

y_proba = automl.predict_proba(X_valid)[:, 1]
print("AUC:", roc_auc_score(y_valid, y_proba))

# Export best ensemble pipeline
print(automl.show_models())
```

Notes:
- Auto-sklearn uses meta-learning warm-starts from prior tasks
- Ensure CV aligns with your data distributions (grouped/time-aware if needed)

---

## Time-series with AutoML

Key pitfalls:
- Leakage via future-derived features; ensure all transforms are strictly past-only
- Use rolling/walk-forward validation, not random KFold
- Handle seasonality/holidays; include exogenous regressors if causal
- Consider specialized AutoML for forecasting (AutoTS, GluonTS-based, Prophet variants)

Pattern:
- Create feature pipelines with lags/rolling stats in a leakage-safe way
- Use horizon-specific models or direct multi-horizon forecasting
- Evaluate on multiple contiguous time windows and report worst-window error

---

## No-code ML platforms

Strengths:
- Rapid prototyping, accessible to non-ML experts, standardized deployment paths
Limits:
- Opaque pipelines, limited custom logic, potential lock-in, cost opacity

Use when:
- You need quick baselines or dashboards
- The organization prefers managed governance and standard models
Ensure:
- Exportability of artifacts and feature definitions
- Clear cost reporting and monitoring integration

---

## Explainability and fairness

- Compute global and local SHAP values on the final model; export feature impact reports
- Audit for disparate impact across slices (e.g., demographic groups)
- Apply bias mitigation when necessary (reweighing, thresholding, post-hoc calibration)
- Document assumptions, limitations, and monitoring plans

---

## Cost and efficiency

- Cap wall-clock and per-trial budgets; prefer multi-fidelity schedulers (ASHA)
- Downsample for prototyping, then re-train on full data with narrowed search
- Cache feature engineering outputs to avoid repeated ETL cost
- Track training/inference cost per candidate to enforce budgets

---

## Summary

AutoML is a force multiplier for tabular ML and a good starting point for many production problems. Constrain search spaces, enforce leak-proof validation, export full pipelines, and add explainability and fairness checks. Treat AutoML artifacts as first-class production assets with versioning, profiling, and monitors.
