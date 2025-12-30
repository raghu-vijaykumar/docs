---
title: "Scaling & Normalization"
weight: 40
description: "Practical guidance on when and how to scale features: Standardization, Min-Max, Robust scaling, and vector normalizations with leakage-safe pipelines."
draft: false
---

# Scaling & Normalization

Many algorithms assume features are comparable in scale or are sensitive to the distribution of inputs. Proper scaling improves optimization stability, speeds up convergence, and can meaningfully impact accuracy—especially for distance-based, margin-based, and gradient-based models.

This guide explains:
- When scaling matters (and when it doesn’t)
- The differences between standardization, min-max, robust scaling, and vector normalization
- How to implement scaling inside leakage-safe pipelines
- Practical tips for sparse, skewed, and time-series data

---

## When scaling matters

Strongly scale-sensitive:
- kNN, k-Means, DBSCAN, hierarchical clustering (distance-based)
- SVM (especially with RBF/poly kernels)
- Linear/Logistic Regression with regularization (L1/L2 magnitudes depend on scale)
- Neural networks (faster, more stable training)
- PCA/ICA/TruncatedSVD (variance-based projections)
- Gradient boosting with linear base learners or mixed feature types

Less sensitive:
- Tree-based models (Decision Trees, Random Forests, Gradient Boosted Trees) are mostly scale-invariant, but mixed feature scales can still affect split heuristics and interpretability.

---

## Standardization (Z-score)

Centers to zero mean and unit variance per feature.

- Formula: z = (x - μ) / σ
- Good default for many models (linear/logistic regression, SVM, neural nets, PCA).
- Assumes roughly Gaussian-like distribution; outliers can skew μ, σ.

```python
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression

pipe = Pipeline([
    ("scaler", StandardScaler(with_mean=True)),
    ("clf", LogisticRegression(max_iter=2000))
])
```

Tips:
- For sparse matrices, set with_mean=False to avoid densification.

---

## Min–Max Scaling (Normalization to Range)

Scales each feature to a specified range (default [0, 1]).

- Formula: x' = (x - min) / (max - min)
- Preserves zero entries; commonly used for bounded inputs or when a specific range is required.
- Very sensitive to outliers; a single extreme value stretches the scale.

```python
from sklearn.preprocessing import MinMaxScaler
mm = MinMaxScaler(feature_range=(0, 1))
X_scaled = mm.fit_transform(X)
```

Use when:
- Features are bounded by definition
- Algorithms expect or benefit from [0, 1] inputs (some neural layers/activations)

---

## Robust Scaling (Median and IQR)

Centers by median and scales by interquartile range. Robust to outliers and heavy-tailed features.

- Formula: x' = (x - median) / IQR
- Useful for skewed distributions and outlier-prone data.

```python
from sklearn.preprocessing import RobustScaler
robust = RobustScaler(with_centering=True, with_scaling=True, quantile_range=(25.0, 75.0))
X_robust = robust.fit_transform(X)
```

Use when:
- Outliers are real and informative but you still need stable scaling
- You want a compromise between Min–Max and StandardScaler for skewed features

---

## Vector Normalization (L1/L2)

Rescales each sample vector to unit L1 or L2 norm. Common in text and embedding pipelines.

- L2 normalization is typical for cosine-similarity-based methods.
- Works orthogonally to feature scaling (this normalizes rows, not columns).

```python
from sklearn.preprocessing import Normalizer

norm = Normalizer(norm="l2")   # or "l1"
X_unit = norm.fit_transform(X) # fit is a no-op; transform normalizes each row
```

Use when:
- Comparing direction of vectors rather than magnitude (TF-IDF, embeddings)
- Feeding to kNN/cosine metrics or clustering in cosine space

---

## Choosing a strategy

- Default: StandardScaler for most ML baselines.
- Heavy outliers/skew: RobustScaler or log-transform then Standard/Robust.
- Strict bounds or required range: MinMaxScaler.
- Cosine or unit-length requirements: Normalizer (row-wise), optionally after per-feature scaling.

Consider combining:
- Log1p transform for long-tailed positive features → StandardScaler.
- Box–Cox/Yeo–Johnson transforms for near-Gaussian behavior (PowerTransformer).

```python
from sklearn.preprocessing import PowerTransformer

pt = PowerTransformer(method="yeo-johnson", standardize=True)
X_pt = pt.fit_transform(X)   # auto-learns monotonic transforms per feature
```

---

## Leakage-safe pipelines

Never fit scalers on the full dataset before cross-validation. Put all preprocessing in a Pipeline so that the scaler is fit only on the training folds.

```python
from sklearn.model_selection import StratifiedKFold, cross_val_score
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC

pipe = Pipeline([
    ("scaler", StandardScaler()),
    ("clf", SVC(kernel="rbf", C=1.0, gamma="scale"))
])

cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(pipe, X, y, cv=cv, scoring="roc_auc")
print(f"AUC: {scores.mean():.3f} ± {scores.std():.3f}")
```

For mixed types:
- Use ColumnTransformer to scale numeric features and pass through encoded categoricals.

```python
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.linear_model import Ridge
from sklearn.pipeline import Pipeline

num_cols = [...]
cat_cols = [...]

pre = ColumnTransformer([
    ("num", StandardScaler(with_mean=False), num_cols),  # with_mean=False for sparse safety
    ("cat", OneHotEncoder(handle_unknown="ignore"), cat_cols)
])

pipe = Pipeline([
    ("pre", pre),
    ("reg", Ridge(alpha=1.0))
])
```

---

## Sparse data and scaling

- StandardScaler(with_mean=False) preserves sparsity (no centering).
- MinMaxScaler typically densifies; avoid on huge sparse matrices.
- Prefer TF-IDF for text; optionally L2-normalize rows for cosine similarity.

---

## Time series and online settings

- Compute scaling parameters using only historical data (no peeking into the future).
- Use rolling or expanding window scalers for streaming pipelines.
- Persist learned scaler parameters with the model for consistent production behavior.

---

## Diagnostics and stability

- Inspect distributions pre/post scaling (histograms, Q–Q plots).
- Check per-feature means and variances after scaling to verify expectations.
- For PCA, confirm explained variance ratio improves stability after scaling.

---

## Common pitfalls

- Fitting scaler on entire dataset before split → leakage and inflated scores.
- Min–Max in presence of outliers → range collapse; prefer Robust or clip outliers first.
- Centering sparse matrices → memory blowups; disable centering or densification.
- Applying vector normalization where absolute magnitude matters (e.g., counts) → unintended loss of scale information.

---

## Key takeaways

- Choose StandardScaler by default; switch to Robust for outliers, Min–Max for bounded ranges, and Normalizer for unit vectors.
- Wrap scaling in Pipelines/ColumnTransformers to prevent leakage.
- Respect sparsity and time-order constraints.
- Validate the impact of scaling with cross-validation and sanity checks on distributions.
