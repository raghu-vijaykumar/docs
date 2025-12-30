---
title: "Model Explainability with SHAP and LIME"
weight: 10
description: "Operational guide to local and global model explanations using SHAP and LIME, with production considerations, pitfalls, and code examples."
draft: false
---

# Model Explainability with SHAP and LIME

Explainability bridges model predictions and stakeholder trust. Two widely adopted techniques are:
- SHAP (SHapley Additive exPlanations): game-theoretic, consistent feature attributions
- LIME (Local Interpretable Model-agnostic Explanations): local surrogate models around a prediction

This guide focuses on when to use each method, how to implement them safely, and how to operationalize explanations at scale.

---

## Concepts and trade-offs

- Local vs Global:
  - Local: explain a single prediction (why this user was scored 0.82?)
  - Global: aggregate local attributions to characterize model behavior across the dataset
- Model-agnostic vs Model-specific:
  - LIME, SHAP KernelExplainer: model-agnostic, slower, approximate
  - SHAP TreeExplainer, LinearExplainer, DeepExplainer: model-specific, faster, more accurate
- Baseline/background distribution:
  - SHAP values are defined relative to a baseline (background data). A poor baseline skews attributions.

Guidance:
- Prefer SHAP for consistent, globally aggregatable explanations.
- Use LIME when you need quick, model-agnostic, locally faithful approximations and SHAP is too costly.
- Always document the background dataset and preprocessing assumptions.

---

## SHAP: practical usage

### Tabular tree models (XGBoost/LightGBM/CatBoost)

Fast and faithful via TreeExplainer.

```python
# pip install shap xgboost lightgbm
import shap
import numpy as np
import xgboost as xgb

# Train model
model = xgb.XGBClassifier(
    n_estimators=500, max_depth=6, learning_rate=0.05,
    subsample=0.8, colsample_bytree=0.8, eval_metric="auc", tree_method="hist"
)
model.fit(X_train, y_train)

# SHAP explainer
explainer = shap.TreeExplainer(model)
# Use a small, representative background for speed when needed
X_bg = X_train[np.random.choice(X_train.shape[0], size=min(1000, len(X_train)), replace=False)]
shap_values = explainer(X_val)  # or explainer.shap_values(X_val) for older shap versions

# Local explanation for one row
i = 0
shap.plots.waterfall(shap_values[i])
# Global feature importance (mean |SHAP|)
shap.plots.bar(shap_values, max_display=20)
# Dependence plot (feature effect with interactions)
shap.plots.scatter(shap_values[:, "age"], color=shap_values[:, "income"])
```

Notes:
- For classification, SHAP values can be on the log-odds scale depending on link. Use `explainer(..., output="probability")` (newer APIs) if you need probability-space explanations.
- Aggregate with care on highly correlated features (see Pitfalls).

### Linear models

```python
import shap
import numpy as np
from sklearn.linear_model import LogisticRegression

clf = LogisticRegression(max_iter=5000).fit(X_train, y_train)
explainer = shap.LinearExplainer(clf, X_train, feature_perturbation="interventional")
sv = explainer(X_val)
shap.plots.bar(sv)
```

### Model-agnostic KernelExplainer

Works for arbitrary predict() functions; slower O(N × M) where N=background size, M=features.

```python
import shap
import numpy as np

f = model.predict_proba  # returns probabilities
X_bg = shap.sample(X_train, 200)  # small representative background
explainer = shap.KernelExplainer(f, X_bg)
sv = explainer.shap_values(X_val[:50], nsamples="auto")  # subset for latency
```

Production tips:
- Cache explanations for frequent queries.
- Precompute global summaries offline; compute local explanations on-demand with time budgets and fallbacks (e.g., top-K features only).

---

## LIME: practical usage

LIME learns a locally faithful linear surrogate around a specific prediction by perturbing inputs.

```python
# pip install lime
import numpy as np
from lime.lime_tabular import LimeTabularExplainer

# Feature names and categorical metadata help readability
explainer = LimeTabularExplainer(
    training_data=X_train,
    feature_names=feature_names,
    class_names=["no", "yes"],
    discretize_continuous=True,
    mode="classification"
)

row = X_val[0]
exp = explainer.explain_instance(
    data_row=row,
    predict_fn=model.predict_proba,
    num_features=10,
    top_labels=1
)
exp.show_in_notebook(show_table=True)
# Or export as list of (feature, weight)
explanation_weights = exp.as_list(label=1)
```

Guidance:
- LIME is sensitive to the perturbation distribution. Set `discretize_continuous` and categorical masks to mimic realistic variations.
- LIME explanations vary between runs; average multiple runs for stability if needed.

---

## Pipelines and preprocessing

Explanations must align with the features actually consumed by the model.

- If you trained with a Pipeline/ColumnTransformer, either:
  - Explain in model-input space (post-transform). Keep track of transformed feature names (e.g., OHE columns).
  - Or wrap the model with a function that applies the same preprocessing inside the explainer to ensure parity.

Example for scikit-learn Pipeline:

```python
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression
import shap

pre = ColumnTransformer([
    ("num", StandardScaler(), num_cols),
    ("cat", OneHotEncoder(handle_unknown="ignore"), cat_cols)
])

pipe = Pipeline([("pre", pre), ("clf", LogisticRegression(max_iter=2000))]).fit(X_train_df, y_train)
# SHAP on transformed inputs
X_trans = pipe.named_steps["pre"].transform(X_val_df)
explainer = shap.LinearExplainer(pipe.named_steps["clf"], pipe.named_steps["pre"].transform(X_train_df))
sv = explainer(X_trans)
```

For readability, map back transformed columns to original feature groups (e.g., sum |SHAP| over all OHE dummies of a base feature).

---

## Aggregating to global insights

- Mean absolute SHAP: global feature importance ranked by contribution magnitude.
- SHAP dependence plots: feature effect vs value; color by interacting feature to reveal interactions.
- Partial dependence (PDP) and ICE:
  - PDP: average predicted response as a function of one feature (assumes feature independence)
  - ICE: per-instance curves; reveal heterogeneity across data

Use both SHAP dependence and PDP/ICE to understand non-linearities and interactions.

---

## Monitoring and governance

- Track explanation drift: compare global SHAP importances and distributions over time. Large shifts may indicate feature drift or model changes.
- Persist explanation metadata: model version, baseline/background sample, feature schema, explainer parameters.
- Access control: explanations may leak sensitive feature influence. Gate via IAM and redact sensitive attributions when required.

---

## Performance and cost

- For TreeExplainer, explanations scale well; still cap per-request rows.
- For KernelExplainer/LIME, limit:
  - Number of features (top-K)
  - Background sample size
  - Number of perturbations/samples
- Batch local explanations asynchronously; cache results.

---

## Common pitfalls and remedies

- Correlated features:
  - SHAP distribution of credit among correlated features can be unstable. Prefer “interventional” SHAP settings, group correlated features, and report grouped importances.
- Poor background:
  - Choose a representative baseline (stratified sample of training data). Document selection; avoid using a single zero vector unless meaningful.
- Preprocessing mismatch:
  - Ensure the exact training-time transforms are applied. Fix with Pipelines or feature stores.
- Categorical explosion:
  - One-hot expands features; aggregate attributions back to the base feature for readability.
- Leakage in explanations:
  - If features inadvertently encode targets (leakage), SHAP/LIME will highlight them—fix the pipeline, not the explanation.

---

## Minimal end-to-end example (tree model)

```python
import shap, numpy as np, xgboost as xgb
from sklearn.model_selection import train_test_split
from sklearn.metrics import roc_auc_score

X_train, X_val, y_train, y_val = train_test_split(X, y, stratify=y, test_size=0.2, random_state=42)
model = xgb.XGBClassifier(n_estimators=800, learning_rate=0.05, max_depth=6, subsample=0.8, colsample_bytree=0.8, eval_metric="auc", tree_method="hist")
model.fit(X_train, y_train)
y_score = model.predict_proba(X_val)[:, 1]
print("AUC:", roc_auc_score(y_val, y_score))

explainer = shap.TreeExplainer(model)
sv = explainer(X_val)

# Global
shap.plots.bar(sv, max_display=15)
# Local: first row
shap.plots.waterfall(sv[0])
```

---

## Key takeaways

- Use SHAP for consistent, aggregatable attributions; TreeExplainer for tree ensembles, Linear/Deep/KernelExplainer otherwise.
- Use LIME for quick, model-agnostic local explanations when SHAP is too heavy.
- Keep preprocessing inside the explanation path; choose and document a representative background distribution.
- Aggregate to global insights; monitor explanation drift in production; cache and budget expensive explanations.
