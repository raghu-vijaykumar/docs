---
title: "Handling Missing Data"
weight: 20
description: "Diagnose missingness mechanisms (MCAR/MAR/MNAR) and apply leakage-safe imputation strategies for robust, production-ready pipelines."
draft: false
---

# Handling Missing Data

Missing values are a signal, not just a nuisance. Treating them correctly improves generalization, avoids leakage, and reveals upstream data quality issues. This guide covers how to diagnose missingness and implement imputation that is statistically sound and operationally safe.

---

## Understand the Missingness Mechanism

- **MCAR (Missing Completely At Random)**: Probability of missingness is independent of observed and unobserved data.  
  - Simple imputations (mean/median) are less biased.
- **MAR (Missing At Random)**: Missingness depends on observed variables.  
  - Model-based imputation leveraging auxiliary features works well.
- **MNAR (Missing Not At Random)**: Missingness depends on the missing value itself.  
  - Hardest case; consider explicit missingness indicators, domain logic, or specialized models.

Practical tip: Treat “missing” as informative. Add a binary missingness indicator per feature before imputing.

---

## Core Strategies

### 1) Drop vs. Impute

- Drop rows: only when missingness is rare and clearly random; risk of bias and data loss.
- Drop columns: only when the feature is mostly missing and low value (e.g., >80% missing and weak importance).
- Prefer imputation for most scenarios.

### 2) Simple Statistical Imputation

- Numeric: median (robust to outliers) or mean (if distribution is symmetric).
- Categorical: most frequent or a new category like "Unknown".

```python
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.linear_model import LogisticRegression

num_cols = ["age", "income", "debt"]
cat_cols = ["segment", "region"]

numeric = Pipeline([
    ("imp", SimpleImputer(strategy="median")),
    ("scaler", StandardScaler())
])

categorical = Pipeline([
    ("imp", SimpleImputer(strategy="most_frequent")),
    ("ohe", OneHotEncoder(handle_unknown="ignore"))
])

pre = ColumnTransformer([
    ("num", numeric, num_cols),
    ("cat", categorical, cat_cols)
])

pipe = Pipeline([
    ("pre", pre),
    ("clf", LogisticRegression(max_iter=2000))
])
```

### 3) Missingness Indicators

Add a boolean flag that marks where a feature is missing. This captures MNAR effects and lets models learn from absence.

```python
import numpy as np

for c in num_cols + cat_cols:
    df[f"{c}__is_missing"] = df[c].isna().astype(np.int8)
```

Note: Add indicators before imputation; put both into the same CV pipeline to avoid leakage.

### 4) KNN / Iterative (MICE) Imputation

- **KNNImputer**: Fills values using nearest neighbors in feature space.  
- **IterativeImputer (MICE)**: Models each feature with missing values as a function of other features.

```python
from sklearn.impute import KNNImputer
from sklearn.experimental import enable_iterative_imputer  # noqa
from sklearn.impute import IterativeImputer

knn_imp = KNNImputer(n_neighbors=5, weights="distance")
iter_imp = IterativeImputer(random_state=42, sample_posterior=False)
```

Caveats:
- Computationally heavier; cross-validate inside a Pipeline.
- Be careful with data leakage: fit imputer only on training folds.

### 5) Domain/Business-Rule Imputation

- Explicit domain defaults (e.g., missing “credit_history_years” → 0 if never opened).
- Time-window aware fills in time series (forward/backward fill within entity).

---

## Algorithms with Native Missing Handling

- **XGBoost/LightGBM**: Learn a default direction for missing values during splits (no imputation required).  
- **CatBoost**: Handles categorical features and missing values with target statistics safely (if configured correctly).

Still consider adding missingness indicators; they often improve tree learners.

```python
import lightgbm as lgb

clf = lgb.LGBMClassifier(
    n_estimators=500,
    learning_rate=0.05,
    num_leaves=64,
    random_state=42
)
# Pass raw data with NaNs; LightGBM handles them.
clf.fit(X_train, y_train, eval_set=[(X_val, y_val)], verbose=False)
```

---

## Categorical Missing Values

- Treat “missing” as its own category when semantically meaningful.  
- For target encoding, ensure leakage-safe schemes (out-of-fold encoding).

```python
from category_encoders.target_encoder import TargetEncoder
from sklearn.model_selection import StratifiedKFold

cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
# Use TargetEncoder within CV folds to avoid leakage
```

---

## Time Series and Panel Data

- Use **entity-aware** imputation: forward-fill/backward-fill within the same entity (user, device), not across entities.
- Respect temporal ordering: never use future values to impute the past.
- Consider stateful models or Kalman smoothing for sensor streams.

```python
# Entity-aware forward-fill within each user
df = df.sort_values(["user_id", "timestamp"])
df["sensor_filled"] = df.groupby("user_id")["sensor"].ffill().bfill()
```

---

## Evaluation and Leakage Control

- Always place imputation inside the CV pipeline.  
- Validate with a model that consumes the imputed data; imputation quality is model-dependent.  
- Use permutation importance to verify that missingness indicators provide signal.  
- Conduct sensitivity analysis: compare multiple imputers and measure metric stability.

```python
from sklearn.model_selection import cross_val_score, StratifiedKFold

cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(pipe, X, y, cv=cv, scoring="roc_auc")
print(f"ROC AUC: {scores.mean():.3f} ± {scores.std():.3f}")
```

---

## Common Pitfalls

- Imputing on the full dataset before splitting → severe leakage.  
- Using mean imputation for skewed/heavy-tailed features → prefer median or log-transform first.  
- Ignoring the information in “missingness” itself → add indicators.  
- Cross-entity fills in panel data → spurious correlations.  
- Overfitting iterative imputers on small data → constrain models and validate.

---

## Key Takeaways

- Diagnose missingness (MCAR/MAR/MNAR) and treat “missing” as informative via indicators.  
- Keep all imputation inside Pipelines to prevent leakage and enable reproducibility.  
- Prefer robust defaults (median for numeric, most frequent or “Unknown” for categorical).  
- Use model-native missing handling when available (LightGBM/XGBoost/CatBoost).  
- For time series/panels, impute within-entity and time-order-aware.
