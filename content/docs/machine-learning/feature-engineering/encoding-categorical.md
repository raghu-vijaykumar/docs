---
title: "Encoding Categorical Variables"
weight: 30
description: "Production-ready strategies to convert categorical features into model-consumable representations with leakage controls and scalability in mind."
draft: false
---

# Encoding Categorical Variables

Categorical variables carry semantics that models cannot use directly without encoding. The best encoding depends on:
- Model family (linear vs. tree vs. embedding-based)
- Cardinality (number of unique categories)
- Ordinality (natural order or not)
- Data volume and leakage risk

This guide covers practical encodings, when to use them, and how to implement them safely in pipelines.

---

## Quick Guide

- Low cardinality, nominal (no order): One-Hot Encoding (OHE)
- Ordinal features with true order: Ordinal Encoding
- High cardinality with target signals: Target/Mean Encoding (use out-of-fold to avoid leakage)
- Extreme cardinality or streaming: Hashing
- Tree boosters (LightGBM/XGBoost/CatBoost): Prefer native handling or CatBoost encoding
- Neural/tabular embeddings: Learn embeddings for categories with enough frequency

---

## One-Hot Encoding (OHE)

Expands each category into a binary column.

Pros:
- Simple, robust, interpretable
- Works well with linear models

Cons:
- High dimensionality with many categories
- Sparse features; may hurt tree models when cardinality is large

```python
from sklearn.preprocessing import OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression

cat_cols = ["country", "device_type"]
num_cols = ["age", "income"]

ct = ColumnTransformer([
    ("ohe", OneHotEncoder(handle_unknown="ignore", drop=None), cat_cols)
], remainder="passthrough")

pipe = Pipeline([
    ("ct", ct),
    ("clf", LogisticRegression(max_iter=2000))
])
```

Tips:
- use handle_unknown="ignore" to avoid runtime errors on unseen categories.
- Optionally drop="first" to avoid perfect multicollinearity with linear models (many solvers handle this anyway).

---

## Ordinal Encoding

Map categories to fixed integers, respecting true order (e.g., "low" < "medium" < "high").

Pros:
- Compact and preserves rank information

Cons:
- If order is not meaningful, models might learn spurious order

```python
from sklearn.preprocessing import OrdinalEncoder

ord_enc = OrdinalEncoder(categories=[["low", "medium", "high"]], dtype=int)
X_ord = ord_enc.fit_transform(X[["risk_level"]])
```

Only use when categories have a domain-defined order.

---

## Target Encoding (Mean Encoding)

Replace categories with the mean (or other statistic) of the target within each category.

Pros:
- Powerful for high-cardinality features
- Compact representation

Cons:
- High leakage risk if done improperly
- Can overfit rare categories

Leakage-safe pattern: Out-of-fold encoding with smoothing and noise.

```python
import numpy as np
import pandas as pd
from sklearn.model_selection import KFold

def target_encode_oof(X, y, col, n_splits=5, alpha=10, noise=0.01, random_state=42):
    """
    Out-of-fold target encoding with smoothing:
    enc = (sum_y_cat + alpha * global_mean) / (count_cat + alpha)
    """
    kf = KFold(n_splits=n_splits, shuffle=True, random_state=random_state)
    X = X.copy()
    global_mean = y.mean()
    enc_col = np.zeros(len(X))
    for train_idx, val_idx in kf.split(X):
        stats = (
            pd.DataFrame({col: X.iloc[train_idx][col], "y": y.iloc[train_idx]})
            .groupby(col)["y"]
            .agg(["sum", "count"])
        )
        enc_map = (stats["sum"] + alpha * global_mean) / (stats["count"] + alpha)
        enc_col[val_idx] = X.iloc[val_idx][col].map(enc_map).fillna(global_mean)
    # add small noise to reduce overfitting
    rng = np.random.default_rng(random_state)
    enc_col += rng.normal(0, noise, size=enc_col.shape)
    return enc_col
```

Libraries:
- category_encoders.TargetEncoder provides smoothing and CV integration
- CatBoost uses ordered statistics to avoid leakage internally

Best practices:
- Always out-of-fold for training; fit a final encoder on full training to transform test
- Smooth rare categories toward the global mean
- Add small noise during training to regularize

---

## Leave-One-Out (LOO) Encoding

Variant of target encoding where each row’s category mean excludes the row itself. Reduces bias but is compute-heavy. Most libraries provide this as an option; still requires care to avoid leakage across folds.

---

## Count/Frequency Encoding

Replace category with occurrence count or frequency.

Pros:
- Leakage-safe, fast
- Useful signal for high-cardinality features

Cons:
- Loses target relationship; combine with other encoders

```python
counts = X["merchant"].value_counts()
X["merchant_cnt"] = X["merchant"].map(counts).fillna(0).astype(np.int32)
```

---

## Hashing Trick

Maps categories to a fixed number of bins via a hash function; collisions act as implicit regularization.

Pros:
- O(1) memory for new categories; streaming-friendly
- Avoids storing category dictionaries

Cons:
- Collisions may mix signals; dimension choice matters
- Non-invertible (hard to inspect)

```python
from sklearn.feature_extraction import FeatureHasher

fh = FeatureHasher(n_features=2**18, input_type="string")
X_hashed = fh.transform(X["url_host"].astype(str))
```

Use when:
- Cardinality is huge or unbounded (e.g., URLs, user IDs)
- You need constant memory footprint

---

## Binary Encoding

Encodes categories into binary code digits (log2(K) columns).

Pros:
- Lower dimensionality than OHE for high cardinality
Cons:
- Not strictly leakage-safe if combined with target signals improperly

Available via category_encoders.BinaryEncoder.

---

## Native Handling in Gradient Boosting

- LightGBM: Can accept categorical features as integers with categorical_feature parameter; applies histogram-based splits (with careful preprocessing).
- CatBoost: Preferred for categorical data; supports ordered target statistics to avoid leakage and handles high cardinality well.
- XGBoost: Requires pre-encoding historically; recent versions add limited categorical support but CatBoost is still superior for categories.

CatBoost example:
```python
from catboost import CatBoostClassifier, Pool

cat_features = [0, 2]  # indices of categorical columns
train_pool = Pool(X_train, y_train, cat_features=cat_features)
val_pool = Pool(X_val, y_val, cat_features=cat_features)

model = CatBoostClassifier(
    depth=8, learning_rate=0.05, iterations=1000, loss_function="Logloss",
    eval_metric="AUC", random_state=42, verbose=False
)
model.fit(train_pool, eval_set=val_pool, use_best_model=True)
```

---

## Rare Category Handling

- Bucket rare categories into "Other" based on a minimum frequency threshold.
- Domain-aware grouping (e.g., map country to region).
- For target encoding, heavily smooth rare categories toward global mean.

```python
min_freq = 50
vc = X["merchant"].value_counts()
rare = vc[vc < min_freq].index
X["merchant_bkt"] = X["merchant"].mask(X["merchant"].isin(rare), "OTHER")
```

---

## Pipelines and Cross-Validation

Always put encoders inside Pipelines to avoid leakage and keep transformations reproducible.

```python
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import StratifiedKFold, cross_val_score

num_cols = [...]
low_card_cat = [...]
high_card_cat = [...]

ohe = OneHotEncoder(handle_unknown="ignore")
# For high-card encoders use category_encoders within a Pipeline or custom transformer

pre = ColumnTransformer([
    ("ohe", ohe, low_card_cat)
], remainder="passthrough")

pipe = Pipeline([
    ("pre", pre),
    ("clf", LogisticRegression(max_iter=2000))
])

cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(pipe, X, y, cv=cv, scoring="roc_auc")
print(f"AUC: {scores.mean():.3f} ± {scores.std():.3f}")
```

For target encoding, wrap an out-of-fold encoder as a transformer step that respects CV splits.

---

## Encodings for Neural Tabular Models

- Learn embeddings per category id; dimension ~ min(50, round(1.6 * K**0.56))
- Requires integer mapping and frequency thresholding
- Combine with normalization and MLP/Transformer-based tabular models

```python
import torch.nn as nn

class CatEmbed(nn.Module):
    def __init__(self, cardinals, embed_dims):
        super().__init__()
        self.embeds = nn.ModuleList([nn.Embedding(c, d) for c, d in zip(cardinals, embed_dims)])

    def forward(self, X_cat):
        embs = [emb(X_cat[:, i]) for i, emb in enumerate(self.embeds)]
        return torch.cat(embs, dim=-1)
```

---

## Common Pitfalls

- Encoding before the train/validation split → leakage.
- Using OrdinalEncoder on nominal data for linear models → injects fake order signal.
- OHE with extremely high cardinality → memory blowup; prefer target or hashing.
- Target encoding without smoothing or OOF → severe overfitting.
- Inconsistent encoding dictionaries between train and production → always persist pipeline/encoder artifacts.

---

## Key Takeaways

- Match encoding to model family and cardinality.
- Wrap encoders in Pipelines and use out-of-fold strategies for target encoders.
- Prefer CatBoost for heavy categorical tasks; otherwise combine OHE for low-cardinality and target/hashing for high-cardinality.
- Manage rare categories explicitly and version your encoders for reproducibility.
