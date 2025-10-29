---
title: "Ethics & Bias in AI"
weight: 90
description: "Practical guide to fairness, bias detection and mitigation, explainability alignment, and regulatory compliance for production ML systems."
draft: false
---

# Ethics & Bias in AI

Production ML systems must be accurate, fair, explainable, and compliant. This guide focuses on actionable practices: how to measure and mitigate bias, align explanations with decisions, and meet regulatory obligations without stalling delivery.

---

## Fairness: what to measure

Choose fairness definitions aligned to your use case and legal context. Common metrics for binary decisions (with protected attribute A ∈ {a, b}):

- Demographic Parity (DP): P(ŷ=1 | A=a) ≈ P(ŷ=1 | A=b). Pros: simple. Cons: ignores ground truth.
- Equal Opportunity (EO): TPR parity across groups: P(ŷ=1 | y=1, A=a) ≈ P(ŷ=1 | y=1, A=b). Aligns with missed-positives sensitivity.
- Equalized Odds (EOdds): TPR and FPR parity across groups. Stronger but harder to achieve.
- Predictive Parity (PPV parity): Precision parity across groups. Conflicts with EO when base rates differ.
- Calibration within groups: P(y=1 | score=s, A=a) ≈ P(y=1 | score=s, A=b).

Guidance:
- Start with EO/EOdds for screening/eligibility problems.
- Use group calibration when downstream decisions consume probabilities.
- Report both group-level metrics and overall performance (e.g., PR-AUC), plus per-slice stability.

---

## Bias detection

Data- and model-driven checks that fit into CI/CD.

- Dataset audits:
  - Representation: class and group proportions; long-tail coverage.
  - Label audits: check label noise and annotator consistency across groups.
  - Feature proxies: correlation between features and protected attributes; causal reasoning to avoid proxy use.
- Validation audits:
  - Slice metrics (per group, intersectional groups): PR-AUC, Recall@Precision, calibration (Brier/ECE).
  - Threshold audits: report operating points per group; beware single global threshold masking disparities.
- Drift audits (production):
  - Feature, prediction, and post-decision outcome drifts per group.
  - Monitor explanation drift (global SHAP importances) to catch shifts in drivers.

Minimal Python sketch (metrics per group):
```python
import numpy as np
from sklearn.metrics import precision_recall_fscore_support, brier_score_loss

def group_report(y_true, y_score, group, thresh=0.5):
    y_pred = (y_score >= thresh).astype(int)
    out = {}
    for g in np.unique(group):
        idx = (group == g)
        p, r, f1, _ = precision_recall_fscore_support(y_true[idx], y_pred[idx], average="binary", zero_division=0)
        brier = brier_score_loss(y_true[idx], y_score[idx])
        out[g] = {"precision": p, "recall": r, "f1": f1, "brier": brier, "n": int(idx.sum())}
    return out
```

---

## Mitigation strategies

Mitigate bias in one or more stages. Prefer minimally invasive controls that preserve task utility.

- Pre-processing
  - Reweighing: adjust sample weights to equalize group-conditional distributions.
  - Learning fair representations: map inputs to embeddings obfuscated from protected attributes (adversarially learned).
  - Data augmentation/curation: collect more data for underrepresented groups; relabel noisy segments.
- In-processing
  - Regularizers/constraints: add fairness penalties (e.g., EO constraints) to the loss.
  - Adversarial debiasing: train predictor while an adversary tries to predict protected attribute from internal reps.
  - Cost-sensitive training: group-aware class weights to balance errors asymmetrically.
- Post-processing
  - Group thresholds: per-group decision thresholds to equalize opportunity/odds under policy.
  - Calibration per group: isotonic/Platt separately by group when probability calibration is required.
  - Reject option classification: modify uncertain predictions to favor disadvantaged group under constraints.

Operational guidance:
- Start with pre-processing reweighing + post-processing thresholds; measure utility loss and fairness gain.
- Escalate to in-processing if required by policy or when simpler methods underperform.
- Always evaluate on a held-out set different from the one used to learn thresholds/weights to avoid optimistic bias.

---

## Explainability alignment

Explainability supports accountability and debugging, but it must align with fairness goals.

- Use SHAP for consistent, aggregatable attributions; LIME for fast local probes. See Explainability guide: Model Explainability with SHAP and LIME.
- Aggregate attributions to slices: compare mean |SHAP| per feature across groups to detect divergent drivers.
- Avoid misinterpretation with correlated features: group features logically and report grouped attributions.
- Redaction policy: explanations can reveal sensitive feature influence. Govern access and log explanation queries.

Example: grouped SHAP summary
```python
import pandas as pd
import numpy as np

# shap_values: (n_samples, n_features), feature_names: list[str], group: array
df = pd.DataFrame(np.abs(shap_values.values), columns=feature_names)
df["group"] = group
grouped = df.groupby("group").mean().T  # mean |SHAP| per feature per group
```

---

## Governance, risk, and compliance

Align controls with your jurisdiction and sector. Key frameworks and obligations:

- EU AI Act (risk-based): transparency, human oversight, data governance, robustness for high-risk systems; record-keeping and conformity assessments.
- GDPR/CCPA and privacy laws: lawful basis, purpose limitation, data minimization; right to explanation/contest (jurisdiction-specific).
- NIST AI Risk Management Framework (RMF): govern, map, measure, manage risks; emphasizes trustworthy AI characteristics.
- ISO/IEC 23894:2023 AI risk management and ISO/IEC 27001 for security controls.
- Sector-specific (e.g., ECOA/FCRA in credit, EEOC in hiring): adverse impact analysis and documentation.

Artifacts to maintain:
- Model Card: purpose, data, metrics (overall + per-slice), limitations, intended use, ethical considerations.
- Datasheet for Datasets: provenance, collection, consent, known issues, recommended uses.
- Decision policy: thresholds, overrides, human-in-the-loop procedures, escalation paths.
- Reproducibility packet: exact data versions, seeds, code, dependency locks, and model artifacts.

---

## Production checklist

- Metrics
  - Define fairness KPI(s) (EO/EOdds/calibration) alongside task KPIs; set guardrails and SLOs.
  - Automate per-slice dashboards and alerts; review worst-slice performance.
- Data
  - Audit for imbalance and proxies; plan data collection to close gaps.
  - Pin and version datasets; track consent/PII lineage; retention and deletion policies.
- Modeling
  - Keep preprocessing inside Pipelines; avoid leakage.
  - Calibrate probabilities; consider per-group thresholds with governance review.
  - Log explanation metadata (model version, baseline, schema).
- Deployment and monitoring
  - Shadow/canary before full rollout; validate fairness in the field.
  - Monitor drift and explanation drift by group; trigger retraining or policy review on breach.
  - Access control, audit logs, and rate limiting for explanation endpoints.

---

## Common pitfalls

- Optimizing a single fairness metric without utility or policy context; trade-offs are inevitable—document them.
- “Solving” fairness by dropping sensitive features while proxies remain; verify via adversarial predictability tests.
- Calibrating on the same split used for tuning thresholds; use separate data or nested procedures.
- Ignoring intersectionality; disparities often show in overlapping attributes (e.g., gender × age × region).

---

## Key takeaways

- Define fairness in your context, measure it per slice, and set explicit guardrails.
- Prefer simple, auditable mitigations first; escalate only as needed.
- Align explanations with fairness monitoring and access control.
- Maintain living governance artifacts (Model Cards, Datasheets) and automate production monitoring for sustained compliance.
