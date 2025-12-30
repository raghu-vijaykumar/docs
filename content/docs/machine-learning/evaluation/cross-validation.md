---
title: "Cross-Validation"
weight: 20
description: "Leakage-safe validation strategies: K-Fold, Stratified, Grouped, Leave-One-Out, and Time Series splits with practical patterns and pitfalls."
draft: false
---

# Cross-Validation

Cross-validation (CV) estimates generalization by training on multiple train/validation partitions. The goal is a low-bias, low-variance estimate that mirrors production data reality, without leakage.

This guide covers standard CV strategies, when to use each, and how to implement them safely inside preprocessing pipelines.

---

## Why cross-validation

- Reduces variance of a single holdout split
- Uses data more efficiently when labeled data is scarce
- Enables robust model comparison and hyperparameter tuning
- Surfaces instability (report mean ± std across folds, and worst fold)

Always ensure preprocessing is fit only on the training fold. Never pre-fit scalers/encoders on the full dataset.

---

## K-Fold Cross-Validation

Split data into K equal folds; cycle each fold as validation while training on the remaining K−1 folds.

- Use when: IID data (independent and identically distributed), moderate dataset sizes
- Typical K: 5 or 10
- Report: mean ± std; optionally worst-fold performance

```python
from sklearn.model_selection import KFold, cross_val_score
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import Ridge

X, y = ...
pipe = Pipeline([("scaler", StandardScaler()), ("reg", Ridge(alpha=1.0))])

kf = KFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(pipe, X, y, cv=kf, scoring="r2")
print(f"R2: {scores.mean():.3f} ± {scores.std():.3f}")
```

---

## Stratified K-Fold (classification)

Maintains class label proportions in each fold.

- Use when: classification with class imbalance or many classes
- Benefit: more stable estimates vs plain KFold in imbalanced datasets

```python
from sklearn.model_selection import StratifiedKFold

skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(pipe, X, y, cv=skf, scoring="roc_auc")
```

---

## Group K-Fold / Stratified Group K-Fold

Ensure that all samples from the same group (e.g., user/session/patient) appear in only one fold to avoid identity leakage.

- Use when: multiple rows share a natural group identifier
- Example: user-level predictions where multiple sessions belong to a user

```python
from sklearn.model_selection import GroupKFold

groups = df["user_id"].to_numpy()
gkf = GroupKFold(n_splits=5)
scores = cross_val_score(pipe, X, y, groups=groups, cv=gkf, scoring="roc_auc")
```

StratifiedGroupKFold (available in sklearn ≥1.1) balances class proportions while respecting groups.

---

## Leave-One-Out (LOO) CV

Each sample is used once as validation; the rest are training.

- Pros: nearly unbiased estimate for small datasets
- Cons: extremely expensive (N fits), high variance per split
- Use when: very small datasets and simple models; otherwise prefer KFold

```python
from sklearn.model_selection import LeaveOneOut

loo = LeaveOneOut()
scores = cross_val_score(pipe, X, y, cv=loo, scoring="neg_mean_squared_error")
```

---

## Time Series Cross-Validation

Never randomize across time. Use walk-forward (expanding window) or sliding window splits that respect chronology.

- Use when: temporal dependencies; avoid peeking into the future
- Patterns:
  - Expanding window: train on [t1..ti], validate on (ti..ti+1]
  - Sliding window: fixed-size rolling train window, then validate next chunk

```python
from sklearn.model_selection import TimeSeriesSplit

tscv = TimeSeriesSplit(n_splits=5, test_size=None, gap=0)
for train_idx, val_idx in tscv.split(X):
    X_tr, X_val = X[train_idx], X[val_idx]
    y_tr, y_val = y[train_idx], y[val_idx]
    pipe.fit(X_tr, y_tr)
    # evaluate on (X_val, y_val)
```

Practical tips:
- Consider a gap between train and validation to respect data latency
- Aggregate metrics across windows and report worst-window performance

---

## Nested Cross-Validation (for model selection)

When hyperparameter tuning is extensive, use nested CV to avoid optimistic bias.

- Outer CV: estimates generalization
- Inner CV: hyperparameter tuning on the training fold of the outer split

Costly but provides an unbiased estimate of selection/tuning pipelines.

---

## Leakage controls

- Preprocessing inside Pipelines: scalers, encoders, imputers, target encoders with OOF logic
- Target leakage: ensure no features are derived from future or validation parts
- Group/time-aware splits: use GroupKFold/TimeSeriesSplit when relevant
- Feature selection: compute feature importance/selection within each fold only

---

## Reporting and stability

- Always report mean ± std across folds; include worst-fold metric
- Re-run CV with different random seeds for stratification/shuffling
- Compare models with paired tests on fold-wise metrics (e.g., Wilcoxon signed-rank)

---

## Common pitfalls

- Random KFold on grouped/time data → leakage and inflated metrics
- Fitting preprocessors (scaler, encoder) outside the CV loops
- Using the validation splits from CV to also calibrate thresholds (double dipping)
- Tuning on the test set; keep a final untouched holdout if possible

---

## Key takeaways

- Match the split strategy to data generating process (IID, grouped, time-based).
- Keep all preprocessing and feature selection inside the CV folds.
- Prefer StratifiedKFold for classification; Group-aware or TimeSeries splits when needed.
- Use nested CV for heavy HPO if you need an unbiased performance estimate.
