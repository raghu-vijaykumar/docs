---
title: "Performance Metrics"
weight: 10
description: "Production-ready reference for evaluating ML models: classification (Accuracy, Precision, Recall, F1, ROC/AUC), regression (MSE, RMSE, MAE, R²), calibration, and ranking metrics with thresholding and class imbalance guidance."
draft: false
---

# Performance Metrics

Metrics translate model behavior into decisions. Choose metrics that reflect the real objective, are robust to data properties (class imbalance, outliers), and are actionable for model iteration and deployment.

This guide covers:
- Classification: Accuracy, Precision, Recall, F1, ROC/PR Curves, AUC, LogLoss, calibration
- Regression: MSE, RMSE, MAE, MAPE/sMAPE, R²
- Ranking/Recommendation: MAP, MRR, NDCG (quick reference)
- Practical thresholding, averaging strategies, imbalanced data considerations
- Leakage-safe evaluation patterns

---

## Classification metrics

### Confusion matrix primitives

For binary classification (positive class = 1):
- TP: predicted 1, actual 1
- FP: predicted 1, actual 0
- TN: predicted 0, actual 0
- FN: predicted 0, actual 1

Derived metrics:
- Accuracy = (TP + TN) / (TP + TN + FP + FN)
- Precision = TP / (TP + FP)
- Recall (TPR) = TP / (TP + FN)
- Specificity (TNR) = TN / (TN + FP)
- F1 = 2 × Precision × Recall / (Precision + Recall)

Guidance:
- Accuracy is misleading under imbalance.
- Optimize Precision when false positives are costly; Recall when false negatives are costly.
- Use F1 when you need a balance of Precision and Recall.

### Averaging strategies (multiclass/multilabel)

- Micro: global TP/FP/FN across classes (balances by instance; good for imbalance).
- Macro: unweighted average across classes (treats all classes equally).
- Weighted: class-frequency-weighted average (compromise).

Select based on whether rare classes deserve equal weight (macro) or you want instance-level parity (micro).

### Thresholding and curves

Models output scores; metrics depend on threshold θ.

- ROC Curve: TPR vs FPR across θ. AUC-ROC summarizes ranking quality independent of threshold.
- PR Curve: Precision vs Recall across θ. Preferred for heavy class imbalance.

Heuristics:
- Use ROC-AUC for balanced datasets; PR-AUC for rare positives.
- Tune θ on validation data to optimize business metric (e.g., Fβ, cost-sensitive objective).

```python
from sklearn.metrics import roc_auc_score, average_precision_score, precision_recall_curve
import numpy as np

y_true = ...
y_score = ...  # positive class scores/probabilities

roc_auc = roc_auc_score(y_true, y_score)
pr_auc = average_precision_score(y_true, y_score)

prec, rec, thresh = precision_recall_curve(y_true, y_score)
f1 = 2 * (prec * rec) / (prec + rec + 1e-12)
best_idx = np.nanargmax(f1)
best_threshold = thresh[best_idx]
```

### Log Loss (Cross-Entropy) and calibration

- LogLoss penalizes confident wrong predictions; sensitive to calibration.
- Brier score measures mean squared error of probabilities (0–1 bounded; lower is better).

Calibration:
- Reliability matters when decisions depend on absolute probabilities (risk scoring).
- Techniques: Platt scaling (logistic), Isotonic regression (non-parametric) on a validation set.

```python
from sklearn.calibration import CalibratedClassifierCV, calibration_curve

cal = CalibratedClassifierCV(base_estimator=clf, method="isotonic", cv=5)
cal.fit(X_train, y_train)
```

### Additional metrics

- Matthews Correlation Coefficient (MCC): robust single-number summary under imbalance.
- Top-K Accuracy (multiclass): proportion where true label in top-k scored classes.
- Cohen’s Kappa: chance-corrected agreement (useful in annotation tasks).

---

## Regression metrics

- MSE = mean((y − ŷ)²): punishes large errors; smooth gradients.
- RMSE = sqrt(MSE): interpretable in target units; still outlier-sensitive.
- MAE = mean(|y − ŷ|): robust to outliers; L1 optimization target.
- R² = 1 − SS_res/SS_tot: fraction of variance explained (can be negative out-of-distribution).
- Adjusted R²: penalizes complexity for linear models.

Scale-relative errors:
- MAPE = mean(|(y − ŷ)/y|) × 100%: undefined when y≈0; biased toward under-forecasting.
- sMAPE = mean(2|y − ŷ|/(|y| + |ŷ|)) × 100%: symmetric and defined at 0 (still sensitive when both small).

Quantile (Pinball) loss:
- For median/quantile regression and asymmetric cost functions.

```python
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score

mse = mean_squared_error(y_true, y_pred)
rmse = mse ** 0.5
mae = mean_absolute_error(y_true, y_pred)
r2 = r2_score(y_true, y_pred)
```

Guidance:
- Prefer MAE when outliers represent noise; RMSE when large deviations must be penalized.
- Always sanity-check residual plots; consider log-transform targets for heteroskedasticity.

---

## Ranking and recommendation (quick reference)

- MAP@K: average precision across queries; rewards early correct hits.
- MRR: inverse rank of the first relevant item (single-answer tasks).
- NDCG@K: discounted gain normalized by ideal ordering; robust to graded relevance.

These require query grouping and relevance labels; use evaluation that respects per-query distributions.

---

## Practical evaluation patterns

### Leakage-safe validation

- Keep all preprocessing (imputation, scaling, encoding) inside Pipelines.
- Use appropriate CV split:
  - StratifiedKFold for classification
  - GroupKFold/StratifiedGroupKFold for grouped data (users/sessions)
  - TimeSeriesSplit or walk-forward for temporal data
- Nested CV for unbiased model selection when hyperparameter tuning is heavy.

```python
from sklearn.pipeline import Pipeline
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.model_selection import StratifiedKFold, cross_val_score
from sklearn.linear_model import LogisticRegression

pre = ColumnTransformer([
    ("num", StandardScaler(), num_cols),
    ("cat", OneHotEncoder(handle_unknown="ignore"), cat_cols)
])
pipe = Pipeline([("pre", pre), ("clf", LogisticRegression(max_iter=2000))])

cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(pipe, X, y, cv=cv, scoring="roc_auc")
```

### Class imbalance

- Prefer PR-AUC, Recall@fixed-Precision (or Precision@Recall), MCC.
- Re-weight loss (class_weight="balanced") or re-sample (SMOTE) with caution.
- Report metrics at business-relevant operating points (e.g., Recall at 95% Precision).

### Calibration and decisioning

- If decisions require probabilities (e.g., pricing, risk), calibrate and monitor Brier/Expected Calibration Error (ECE).
- Separate model ranking quality (AUC) from calibration quality (Brier/ECE).

---

## Metric selection guide

- Lead scoring/fraud detection: PR-AUC, Recall@Precision≥X, cost-sensitive expected utility.
- Medical screening: Sensitivity (Recall) at specific Specificity; ROC-AUC as secondary.
- Recommenders/ranking: NDCG@K, MAP@K; diversity/novelty metrics if applicable.
- Regression with outliers: MAE/Quantile loss; optionally report RMSE for comparability.
- Forecasting: MAPE/sMAPE with explicit handling of zero/near-zero; pinball loss for quantiles.

---

## Common pitfalls

- Optimizing single-number AUC without checking operating-point metrics.
- Comparing MAPE across datasets with different scale or zero-heavy distributions.
- Using Accuracy on imbalanced data; prefer PR-AUC, F1, MCC.
- Calibrating on the same data used for training/tuning (leakage).
- Ignoring group/time structure in splits leading to optimistic metrics.

---

## Key takeaways

- Match metrics to the business objective, class imbalance, and decision thresholds.
- Use PR-AUC over ROC-AUC for rare positives; report thresholded metrics at actionable points.
- For regression, pick MAE vs RMSE based on outlier policy; consider quantile loss for asymmetric costs.
- Keep preprocessing inside Pipelines and use leakage-safe CV splits. Calibrate when probabilities matter.
