---
title: "Common Supervised Learning Algorithms"
weight: 5
description: "Production-ready reference for core supervised algorithms: Linear/Logistic Regression, Decision Trees, Random Forests, SVMs, and k-Nearest Neighbors."
draft: false
---

# Common Supervised Learning Algorithms

This guide provides a developer-focused reference for core supervised learning algorithms with practical notes on how they work, when to use them, and implementation patterns that hold up in production.

---

## Linear Regression

Predicts a continuous target as a linear combination of input features.

- Objective: minimize MSE; often with L2 (Ridge) or L1 (Lasso) regularization.
- Assumptions: approximate linearity, low multicollinearity, homoscedastic residuals (optional in practice).
- When to use: strong baseline for tabular regression; interpretable coefficients.

```python
from sklearn.linear_model import LinearRegression, Ridge, Lasso
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

# Baseline
lin = Pipeline([
    ("scaler", StandardScaler(with_mean=False)),  # with_mean=False if sparse
    ("reg", LinearRegression())
])

# With regularization
ridge = Pipeline([
    ("scaler", StandardScaler(with_mean=False)),
    ("reg", Ridge(alpha=1.0))
])

lasso = Pipeline([
    ("scaler", StandardScaler(with_mean=False)),
    ("reg", Lasso(alpha=0.001))
])
```

Notes:
- Use Ridge for multicollinearity; Lasso for feature selection (sparsity).
- Inspect residuals; consider log-transform for skewed targets.

---

## Logistic Regression

Linear classifier that models P(y=1|x) with a sigmoid; extends to multinomial via softmax.

- Objective: cross-entropy loss with L2/L1 regularization.
- Pros: calibrated probabilities (often better with Platt scaling/Isotonic), interpretable.
- When to use: strong baseline for binary/multiclass tabular data.

```python
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

logit = Pipeline([
    ("scaler", StandardScaler(with_mean=False)),
    ("clf", LogisticRegression(
        penalty="l2", C=1.0, solver="lbfgs", max_iter=2000,
        multi_class="auto"  # "multinomial" for softmax if supported by solver
    ))
])
```

Notes:
- Feature scaling helps optimization.
- For extreme imbalance: class_weight="balanced" or focal loss alternatives; threshold tuning post-fit.

---

## Decision Trees

Greedy recursive partitioning that optimizes impurity (Gini/Entropy) or MSE per split.

- Pros: captures non-linearities and interactions; handles mixed feature types; interpretable via rules/plots.
- Cons: high variance; prone to overfitting; unstable to small data changes.

```python
from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor

dt_clf = DecisionTreeClassifier(
    max_depth=8, min_samples_split=10, min_samples_leaf=5, random_state=42
)

dt_reg = DecisionTreeRegressor(
    max_depth=8, min_samples_split=10, min_samples_leaf=5, random_state=42
)
```

Notes:
- Control depth/leaves and use pruning to reduce overfitting.
- Prefer ensembles (Random Forest/GBMs) for accuracy and stability.

---

## Random Forest

Ensemble of bagged decision trees with feature subsampling.

- Pros: robust, good out-of-the-box performance, handles non-linearities, less tuning needed than boosting.
- Cons: larger models; slower inference than a single tree; less performant than tuned GBMs.

```python
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor

rf_clf = RandomForestClassifier(
    n_estimators=300, max_depth=None, min_samples_leaf=2,
    max_features="sqrt", n_jobs=-1, random_state=42
)

rf_reg = RandomForestRegressor(
    n_estimators=300, max_depth=None, min_samples_leaf=2,
    max_features="sqrt", n_jobs=-1, random_state=42
)
```

Notes:
- Tune n_estimators, max_depth, min_samples_leaf, max_features.
- Use permutation importance to avoid impurity-importance bias for high-cardinality categoricals.

---

## Support Vector Machines (SVM)

Finds a maximum-margin hyperplane; can be kernelized for non-linear boundaries.

- Pros: strong classifiers in medium-sized feature spaces; robust margin maximization.
- Cons: sensitive to scaling; kernel SVMs scale poorly with large datasets; parameter tuning required.

```python
from sklearn.svm import SVC, SVR
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

svm_rbf = Pipeline([
    ("scaler", StandardScaler()),
    ("clf", SVC(kernel="rbf", C=1.0, gamma="scale", probability=True))
])

svr_rbf = Pipeline([
    ("scaler", StandardScaler()),
    ("reg", SVR(kernel="rbf", C=1.0, gamma="scale"))
])
```

Notes:
- Always scale features.
- RBF kernel defaults often good; tune C and gamma via log-scale search.

---

## k-Nearest Neighbors (kNN)

Instance-based method classifying/regressing by the majority/average of k nearest neighbors.

- Pros: simple; non-parametric; competitive on well-curated, low-dimensional data.
- Cons: expensive prediction; sensitive to scaling and irrelevant features; curse of dimensionality.

```python
from sklearn.neighbors import KNeighborsClassifier, KNeighborsRegressor
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

knn_clf = Pipeline([
    ("scaler", StandardScaler()),
    ("clf", KNeighborsClassifier(n_neighbors=15, weights="distance", metric="minkowski", p=2))
])

knn_reg = Pipeline([
    ("scaler", StandardScaler()),
    ("reg", KNeighborsRegressor(n_neighbors=15, weights="distance"))
])
```

Notes:
- Use distance weights; tune k on validation; reduce dimensionality (PCA) if many correlated features.

---

## Practical selection guide

- Start simple: Logistic/Linear regression as baselines (with scaling and regularization).
- Add tree ensembles: Random Forests for robust baselines; consider Gradient Boosting (XGBoost/LightGBM/CatBoost) for state-of-the-art on tabular data.
- Try SVM when data size is moderate and boundaries are complex.
- Use kNN for small, low-dimensional problems or as a strong baseline after dimensionality reduction.
- Always wrap preprocessing in Pipelines and align validation with data (stratified/grouped/time series).

---

## Cross-validation pattern

```python
from sklearn.model_selection import StratifiedKFold, cross_val_score

cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(logit, X, y, cv=cv, scoring="roc_auc")
print(f"AUC: {scores.mean():.3f} ± {scores.std():.3f}")
```

Guardrails:
- Use ColumnTransformer for mixed types.
- Address imbalance with class weights or resampling.
- Calibrate probabilities when needed (CalibratedClassifierCV).
