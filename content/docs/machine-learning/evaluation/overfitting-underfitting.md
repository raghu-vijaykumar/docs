---
title: "Overfitting & Underfitting"
weight: 40
description: "Diagnose and correct capacity–data mismatches with learning curves, regularization, early stopping, and data-centric fixes across model families."
draft: false
---

# Overfitting & Underfitting

Generalization failures come in two patterns:
- Underfitting: model is too simple or under-trained to capture signal (high bias).
- Overfitting: model memorizes idiosyncrasies in training data and fails to generalize (high variance).

This guide provides practical diagnostics and fixes that are safe to use in production pipelines.

---

## Symptoms and diagnostics

### Learning curves (train vs validation)
- Underfitting: both train and validation errors are high and close; more training data doesn’t help much.
- Overfitting: training error is much lower than validation error; the gap often widens with training.

Action: always plot learning curves early to identify the regime.

### Cross-validation spread
- Large std across folds and weak worst-fold scores indicate variance/instability.
- Stable, uniformly poor scores indicate bias or feature issues.

### Error analysis and slices
- Overfitting often appears first on tail slices (rare segments). Track per-slice metrics and worst-slice performance.

### Training dynamics
- Overfitting: validation metric peaks then declines while training loss continues to improve → enable early stopping.
- Underfitting: both training and validation losses plateau high → add capacity/features or train longer.

---

## Fixes by failure mode

### If underfitting (high bias)
- Increase capacity: deeper trees/nets, more features, interaction terms, nonlinear bases (splines).
- Reduce regularization: lower L2/L1 strength, less dropout, relax constraints.
- Train longer or with better schedule: refine learning-rate schedules; ensure no optimizer pathologies.
- Improve features: domain-engineered features or learned embeddings.

### If overfitting (high variance)
- Add regularization: weight decay (L2), L1/Elastic Net, dropout, label smoothing.
- Early stopping: monitor validation and restore best weights.
- Data/augmentation: more data, stronger augmentation, deduplicate near-duplicates.
- Simplify model: shallower trees, fewer parameters, limit interactions.
- Robust validation: Stratified/Group/Time-based splits; tune using mean ± std and worst-fold.

---

## Model-family playbook

### Linear/logistic
- Underfit: add interactions, polynomial/spline features; reduce regularization.
- Overfit: increase Ridge/Lasso; feature selection; constrain coefficients; cross-validated regularization.

### Trees/GBMs
- Underfit: increase depth/leaves; increase estimators; raise learning rate mildly; ensure adequate features.
- Overfit: reduce depth/leaves, increase min_samples_leaf/min_child_weight; lower learning rate + more trees with early stopping; subsample rows/cols.

### Neural networks
- Underfit: widen/deepen network; train longer; use better architecture/activations; improve optimization.
- Overfit: weight decay, dropout, data augmentation (mixup/cutmix), early stopping, moderate batch sizes, label smoothing.

---

## Safe patterns and code

### Early stopping for GBMs
```python
from xgboost import XGBClassifier
from sklearn.metrics import roc_auc_score

model = XGBClassifier(
    n_estimators=5000, max_depth=6, learning_rate=0.03,
    subsample=0.8, colsample_bytree=0.8, eval_metric="auc", tree_method="hist"
)
model.fit(
    X_train, y_train,
    eval_set=[(X_val, y_val)],
    verbose=False,
    early_stopping_rounds=100  # restores best_iteration
)

y_val_proba = model.predict_proba(X_val)[:, 1]
print("AUC:", roc_auc_score(y_val, y_val_proba))
```

LightGBM:
```python
import lightgbm as lgb

train_ds = lgb.Dataset(X_train, label=y_train)
val_ds = lgb.Dataset(X_val, label=y_val, reference=train_ds)

params = dict(objective="binary", metric="auc", learning_rate=0.03, num_leaves=63,
              feature_fraction=0.8, bagging_fraction=0.8)
model = lgb.train(params, train_ds, num_boost_round=5000, valid_sets=[val_ds],
                  callbacks=[lgb.early_stopping(stopping_rounds=100), lgb.log_evaluation(0)])
```

### Early stopping for Keras
```python
import tensorflow as tf

model = tf.keras.Sequential([...])
model.compile(optimizer=tf.keras.optimizers.AdamW(3e-4, weight_decay=1e-2),
              loss="binary_crossentropy", metrics=["AUC"])

early = tf.keras.callbacks.EarlyStopping(monitor="val_auc", mode="max",
                                         patience=5, restore_best_weights=True)
model.fit(train_ds, validation_data=val_ds, epochs=100, callbacks=[early])
```

### Regularization (sklearn)
```python
from sklearn.linear_model import LogisticRegressionCV

logit = LogisticRegressionCV(
    Cs=10, cv=5, penalty="l2", solver="lbfgs", max_iter=3000, scoring="roc_auc"
)
logit.fit(X_train, y_train)
```

---

## Data-centric controls

- More and better data beats complex regularization:
  - Collect more examples, especially of tail segments and difficult cases.
  - Improve label quality; remove duplicates and leakage-prone features.
  - Align train/serve features; enforce time-order in temporal problems.

- Augmentation (vision/NLP/tabular):
  - Vision: flips/crops/color jitter/mixup.
  - NLP: back-translation, masking, synonym swaps (mind semantics).
  - Tabular: noise injection, mixup/cutmix variants; use with care to avoid distribution shift.

---

## Monitoring in production

Overfitting can be silent until distributions shift:
- Track live metrics: calibration (Brier/ECE), operating-point metrics (Recall@Precision), per-slice dashboards.
- Drift: feature, prediction, and target distribution when labels arrive; use shadow/holdout cohorts.
- Alert on gap growth: widening train–serve or offline–online metric gaps suggest overfitting or skew.

---

## Common pitfalls

- Tuning on leaky validation splits → fake “generalization”.
- Using Accuracy on imbalanced data; prefer PR-AUC and thresholded metrics.
- Over-regularizing to fight label noise instead of fixing labels.
- Ignoring worst-fold/slice: average looks fine, tails suffer.

---

## Key takeaways

- Identify regime via learning curves and CV spread.
- Fix underfitting by adding capacity/features/training; fix overfitting with regularization, early stopping, data, and simplification.
- Use leakage-safe validation, monitor per-slice metrics, and keep a production-minded feedback loop (drift + calibration).
