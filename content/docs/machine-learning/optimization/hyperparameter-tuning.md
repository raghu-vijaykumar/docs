---
title: "Hyperparameter Tuning: Grid, Random, Bayesian"
weight: 20
description: "Production-grade tuning strategies with leakage-safe validation, resource-aware scheduling, and practical tooling (scikit-learn, Optuna, Ray Tune)."
draft: false
---

# Hyperparameter Tuning: Grid, Random, Bayesian

Hyperparameter optimization (HPO) aligns model capacity and regularization with the data. The goal is not just to find the global best score, but to find a robust configuration that generalizes and is economical to train and operate.

This guide provides practical recipes for tuning across model families, selecting search strategies, managing compute budgets, and avoiding leakage.

---

## Core principles

- Treat HPO as an experiment with constraints (time, budget, compute).  
- Define a primary metric and stability guardrails (std across folds, worst-fold performance).  
- Align validation strategy with data realities (stratified, grouped, time series).  
- Prefer fewer, high-quality trials over exhaustive but leaky or mis-specified searches.  
- Reproducibility matters: version code, seeds, datasets, and search spaces.

---

## Validation schemes (avoid leakage)

- IID tabular: StratifiedKFold for classification, KFold for regression.  
- Grouped data (users/sessions/patients): GroupKFold/StratifiedGroupKFold.  
- Time series: TimeSeriesSplit; walk-forward CV (never peek into the future).  
- Put all preprocessing (imputation/encoding/scaling) inside Pipelines so they’re fit only on training folds.

```python
from sklearn.model_selection import StratifiedKFold
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression

pipe = Pipeline([
    ("scaler", StandardScaler()),
    ("clf", LogisticRegression(max_iter=2000))
])
cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
```

---

## Search strategies

### Grid Search (deterministic, exhaustive on a grid)
- Pros: Simple, parallelizable, good when few discrete hyperparameters.  
- Cons: Exponential blowup with dimensionality; wastes budget on unimportant dims.

```python
from sklearn.model_selection import GridSearchCV

param_grid = {
    "clf__C": [0.01, 0.1, 1.0, 10],
    "clf__penalty": ["l2"],
    "clf__solver": ["lbfgs"]
}
gs = GridSearchCV(pipe, param_grid, scoring="roc_auc", cv=cv, n_jobs=-1, refit=True)
gs.fit(X, y)
print(gs.best_params_, gs.best_score_)
```

### Random Search (stochastic, budget-friendly)
- Pros: Efficient in high dimensions; better anytime performance than grid.  
- Cons: No model of the objective; may miss narrow optima.

```python
from sklearn.model_selection import RandomizedSearchCV
from scipy.stats import loguniform, randint

param_dist = {
    "clf__C": loguniform(1e-3, 1e2),
    "clf__max_iter": randint(500, 4000)
}
rs = RandomizedSearchCV(pipe, param_distributions=param_dist, n_iter=50, scoring="roc_auc", cv=cv, n_jobs=-1, random_state=42)
rs.fit(X, y)
print(rs.best_params_, rs.best_score_)
```

### Bayesian Optimization (model-based, sample-efficient)
Builds a surrogate model (e.g., TPE or Gaussian Process) to select promising configurations.

- Pros: Sample-efficient; good when training is expensive.  
- Cons: More complex; sensitivity to noisy objectives and search space design.

Optuna example:
```python
import optuna
from sklearn.metrics import roc_auc_score

def objective(trial):
    C = trial.suggest_float("C", 1e-3, 1e2, log=True)
    penalty = trial.suggest_categorical("penalty", ["l2"])
    solver = "lbfgs"
    model = Pipeline([
        ("scaler", StandardScaler()),
        ("clf", LogisticRegression(C=C, penalty=penalty, solver=solver, max_iter=2000))
    ])
    scores = []
    for tr, va in cv.split(X, y):
        model.fit(X[tr], y[tr])
        p = model.predict_proba(X[va])[:, 1]
        scores.append(roc_auc_score(y[va], p))
    return sum(scores) / len(scores)

study = optuna.create_study(direction="maximize")
study.optimize(objective, n_trials=50)
print(study.best_trial.params, study.best_value)
```

---

## Early stopping and budget allocation

- Use early stopping when supported (XGBoost/LightGBM/GBMs/NNs) to terminate weak trials quickly.  
- Asynchronous Successive Halving (ASHA)/HyperBand allocate more budget to promising trials.  
- Multi-fidelity strategies: treat epochs, subset of data, or resolution as a fidelity axis.

Ray Tune (ASHA) example:
```python
# pip install ray[tune] xgboost
from ray import tune
from xgboost import XGBClassifier
from sklearn.metrics import roc_auc_score

def trainable(config):
    model = XGBClassifier(
        n_estimators=config["n_estimators"],
        max_depth=config["max_depth"],
        learning_rate=config["lr"],
        subsample=config["subsample"],
        colsample_bytree=config["colsample"],
        tree_method="hist",
        eval_metric="auc",
        n_jobs=1
    )
    # Split once or use CV per iteration if budget allows
    model.fit(X_train, y_train,
              eval_set=[(X_val, y_val)],
              verbose=False)
    preds = model.predict_proba(X_val)[:, 1]
    tune.report(auc=roc_auc_score(y_val, preds))

search_space = {
    "n_estimators": tune.randint(100, 1500),
    "max_depth": tune.randint(3, 12),
    "lr": tune.loguniform(1e-3, 2e-1),
    "subsample": tune.uniform(0.5, 1.0),
    "colsample": tune.uniform(0.5, 1.0)
}

tuner = tune.Tuner(
    tune.with_resources(trainable, resources={"cpu": 2}),
    tune_config=tune.TuneConfig(
        metric="auc",
        mode="max",
        num_samples=100,
        scheduler=tune.schedulers.ASHAScheduler(max_t=100, grace_period=10)
    ),
    param_space=search_space
)
results = tuner.fit()
```

---

## Search space design

- Use log-scales for multiplicative parameters (learning rate, regularization strength).  
- Constrain ranges with domain knowledge (e.g., depth 3–12 for trees, LR 1e−4–1e−1 for Adam).  
- Tie parameters logically (e.g., max_depth with min_child_weight in XGB).  
- Define categorical vs. continuous carefully; coarse-to-fine refinement is effective.

Examples:
- Tree ensembles: n_estimators, max_depth, min_child_weight, subsample, colsample_bytree, reg_alpha/reg_lambda, learning_rate.  
- Linear models: regularization strength (C or alpha), penalty type, feature scaling choices.  
- Neural nets: LR, weight decay, batch size, dropout rates, hidden sizes, schedule type.

---

## Stability and reporting

- Report mean ± std across folds; show worst-fold score.  
- Re-run top-5 trials with different CV seeds to verify stability.  
- Keep a holdout test set for final evaluation after HPO.  
- Log configs and results (CSV/MLflow/W&B); capture random seeds and commit hashes.

---

## Common anti-patterns

- Tuning on a leaky validation split (pipeline steps fit on full data).  
- Huge grids with unimportant parameters; focus on impactful knobs first.  
- Over-optimizing the metric with noisy CV (too few folds/samples).  
- Ignoring training time and latency constraints of chosen hyperparameters.  
- Not freezing pre-processing learned params when exporting to production.

---

## Quick recipes

- Tabular + XGBoost/LightGBM: Random/Bayesian search over depth, learning_rate, n_estimators, regularization, subsample/colsample; use early stopping on a valid set; 200–500 trials often enough.  
- Linear/logistic: Random search over C/alpha on log-scale; StandardScaler; solver-dependent constraints.  
- Neural nets: Start AdamW LR sweep (log-scale) with cosine/one-cycle schedule; tune weight decay and dropout; scale batch size for throughput.

---

## Final checklist

- Search strategy chosen based on budget and objective noise.  
- Validation scheme matches data (stratified/grouped/time).  
- All preprocessing inside Pipeline; no leakage.  
- Early stopping enabled where applicable; scheduler for budget allocation.  
- Results logged with reproducible seeds; top configs re-validated.
