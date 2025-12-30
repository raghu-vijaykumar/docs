---
title: "Feature Selection & Extraction"
weight: 10
description: "Techniques to reduce dimensionality, remove noise, and improve generalization through targeted selection and transformation of features."
draft: false
---

# Feature Selection & Extraction

Effective feature engineering starts by reducing input complexity to what truly drives signal. This improves generalization, training speed, interpretability, and sometimes even unlocks models that would otherwise fail due to the curse of dimensionality.

This guide covers:
- When to select vs. extract features
- Statistical and model-based selection
- Projection-based extraction (PCA, ICA), manifold learning, and learned representations
- Practical recipes and pitfalls

---

## Selection vs. Extraction

- **Feature Selection** chooses a subset of original features.  
  - Pros: Preserves semantics and interpretability, simplifies pipelines.  
  - Cons: May miss informative combinations of features.

- **Feature Extraction** transforms features into a new space (often lower dimensional).  
  - Pros: Can capture interactions and compress redundant info.  
  - Cons: New features may be less interpretable.

Rule of thumb:
- Start with selection for interpretability and leakage control.
- Add extraction when you see multicollinearity, high redundancy, or need compact representations.

---

## Feature Selection

### Filter Methods (Model-Agnostic, Fast)

Filter methods score each feature independently of a model.

Common criteria:
- Correlation (Pearson/Spearman) for regression
- Mutual Information (MI) for both regression and classification
- Chi-squared (χ²) for categorical targets with non-negative features

```python
from sklearn.feature_selection import SelectKBest, f_classif, mutual_info_classif, chi2

# ANOVA F-test (classification)
selector_f = SelectKBest(score_func=f_classif, k=20).fit(X_train, y_train)
X_train_f = selector_f.transform(X_train)
X_test_f  = selector_f.transform(X_test)

# Mutual Information (works with non-linear dependencies)
selector_mi = SelectKBest(mutual_info_classif, k=20).fit(X_train, y_train)
```

Use when:
- Many features, need a quick pass
- Early-stage screening before model-based refinement

Caveats:
- Ignores feature interactions
- Sensitive to scaling for some tests (e.g., χ² requires non-negative)

### Wrapper Methods (Model-in-the-Loop)

Wrapper methods evaluate subsets using a predictive model.

- **RFE (Recursive Feature Elimination)**: Train model, rank features by importance (e.g., weights), remove weakest, repeat.
- **Sequential Feature Selection**: Greedy forward/backward add/remove features based on cross-validated score.

```python
from sklearn.feature_selection import RFE
from sklearn.linear_model import LogisticRegression

est = LogisticRegression(max_iter=2000)
rfe = RFE(estimator=est, n_features_to_select=30, step=1)
rfe.fit(X_train, y_train)

X_train_sel = rfe.transform(X_train)
X_test_sel  = rfe.transform(X_test)
```

Use when:
- You can afford computational cost
- Interpretability of chosen features matters

Caveats:
- Expensive for high dimensional data
- Can overfit if selection is not nested within CV

### Embedded Methods (Learned Importance)

Selection is embedded in model training via regularization or tree splits.

- **L1 (Lasso)** induces sparsity by zeroing coefficients.
- **Elastic Net** mixes L1 and L2 for stability under collinearity.
- **Tree-based** models (Random Forest, Gradient Boosting) expose impurity-based or permutation importance.

```python
from sklearn.linear_model import LogisticRegression
from sklearn.feature_selection import SelectFromModel

l1 = LogisticRegression(penalty="l1", solver="liblinear", max_iter=1000)
sfm = SelectFromModel(l1, prefit=False).fit(X_train, y_train)

X_train_l1 = sfm.transform(X_train)
X_test_l1  = sfm.transform(X_test)
```

Caveats:
- Coefficient magnitude depends on scaling
- Tree impurity importance can be biased toward continuous/high-cardinality features; prefer permutation importance

---

## Feature Extraction

### PCA (Principal Component Analysis)

Orthogonal linear projection maximizing variance. Great for compressing correlated features and noise reduction.

- Unsupervised; preserves global structure
- Components are linear combos of inputs

```python
from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

pca_pipe = Pipeline([
    ("scaler", StandardScaler()),
    ("pca", PCA(n_components=0.95))  # keep 95% variance
])
X_train_pca = pca_pipe.fit_transform(X_train)
X_test_pca  = pca_pipe.transform(X_test)
```

Interpretation:
- Examine explained_variance_ratio_ to choose components
- Components are axis-rotations; not sparse by default

### ICA (Independent Component Analysis)

Separates statistically independent sources, useful for signal separation (e.g., EEG, audio).

```python
from sklearn.decomposition import FastICA
ica = FastICA(n_components=20, random_state=42)
X_train_ica = ica.fit_transform(X_train)
X_test_ica  = ica.transform(X_test)
```

Use when sources are non-Gaussian and independence assumptions are plausible.

### Manifold Learning (Non-Linear)

- **t-SNE/UMAP**: Non-linear embeddings for visualization and clustering structure.  
  - Best for exploration, not as stable feature inputs for downstream supervised tasks without care.

```python
# Visualization only (do not leak test data)
from sklearn.manifold import TSNE
X_vis = TSNE(n_components=2, perplexity=30, learning_rate="auto").fit_transform(X_sample)
```

### Learned Representations

- **Autoencoders**: Neural networks trained to reconstruct inputs; bottleneck activations are compact representations.
- **Pretrained embeddings**: NLP (BERT) or CV (ResNet) features reused across tasks.

```python
import torch.nn as nn

class AE(nn.Module):
    def __init__(self, d):
        super().__init__()
        self.enc = nn.Sequential(nn.Linear(d, 128), nn.ReLU(), nn.Linear(128, 32))
        self.dec = nn.Sequential(nn.Linear(32, 128), nn.ReLU(), nn.Linear(128, d))

    def forward(self, x):
        z = self.enc(x)
        x_hat = self.dec(z)
        return x_hat, z
```

---

## Practical Recipes

### 1) Tabular Classification Baseline
- Scale numeric, one-hot encode categoricals
- Filter select top 50 using mutual information
- Train simple baseline (logreg/xgboost) and collect permutation importances
- Optionally compress via PCA if multicollinearity is high

### 2) High-Dimensional Sparse Text
- Feature hashing or TF-IDF with n-grams
- L1-regularized linear model for embedded selection
- Optionally SVD (TruncatedSVD) for low-rank compression

```python
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.decomposition import TruncatedSVD
from sklearn.pipeline import make_pipeline

tfidf = TfidfVectorizer(ngram_range=(1,2), min_df=3, max_df=0.9)
svd = TruncatedSVD(n_components=300, random_state=42)
pipe = make_pipeline(tfidf, svd)
X_text = pipe.fit_transform(corpus)
```

### 3) Time Series with Many Sensors
- Aggregate windowed statistics per sensor
- Remove near-constant and highly collinear features
- Use tree-based permutation importance to prune noisy sensors
- Consider autoencoder for compact multivariate encodings

---

## Evaluation and Leakage Control

- Always wrap selection/extraction within cross-validation using Pipelines; never fit on full data before splitting.
- Use permutation importance on a held-out set to validate that selected/extracted features actually improve generalization.
- For fairness-sensitive tasks, ensure selected features are not proxies for protected attributes.

```python
from sklearn.model_selection import StratifiedKFold, cross_val_score
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.feature_selection import SelectKBest, mutual_info_classif

pipe = Pipeline([
    ("scaler", StandardScaler(with_mean=False)),  # e.g., for sparse avoid centering
    ("select", SelectKBest(mutual_info_classif, k=50)),
    ("clf", LogisticRegression(max_iter=2000))
])

cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(pipe, X, y, cv=cv, scoring="roc_auc")
print(scores.mean(), scores.std())
```

---

## Common Pitfalls

- Selecting features on the full dataset before CV → data leakage and inflated scores.
- Interpreting PCA components as original features → they are mixtures; use loadings carefully.
- Assuming importance equals causality → validate with domain knowledge and, where possible, causal analysis.
- Using impurity-based importance with high-cardinality categoricals → switch to permutation importance or target encoding.

---

## Key Takeaways

- Start simple: filter methods + regularized linear or tree-based models give strong baselines.
- Use Pipelines and nested CV to avoid leakage during selection/extraction.
- Choose extraction (PCA/SVD/AE) when redundancy is high or compact representations help downstream models.
- Validate with permutation importance and stability checks across folds to ensure robustness.
