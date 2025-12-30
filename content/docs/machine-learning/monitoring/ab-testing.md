---
title: "A/B Testing for ML Systems"
weight: 30
description: "Design, run, and analyze online experiments for ML models with rigorous statistics, guardrails, and production safety."
draft: false
---

# A/B Testing for ML Systems

A/B testing validates that a new model (treatment) improves real business outcomes over the current model (control), under real user and data conditions. Unlike offline metrics, online experiments capture feedback loops, user behavior changes, latency/UX effects, and long-term impact.

![A/B Testing Flow](/images/ab-testing.png)

---

## Why A/B testing for ML

- Ground-truth evaluation on live traffic and real-world distributions
- Detects interactions with systems (caching, ranking, throttling, pricing)
- Measures second-order effects (latency, downstream conversions, churn)
- Acts as a deployment safety net with progressive rollout and easy rollback

---

## Experiment design

### Hypothesis and primary metric
- Define a clear, causal hypothesis: “Model X increases Checkout Conversion by 1% absolute.”
- Choose a single primary metric (e.g., conversion rate, fraud catch rate) and limit secondary metrics to guardrails (latency p95, error rate).

### Unit of randomization
- User-level (sticky assignment; recommended for most product experiments)
- Session-level (acceptable for short-lived tasks; susceptible to user crossover)
- Request-level (ranking/search experiments; risk of interference within user)

Ensure stable IDs and deterministic bucketing to maintain assignment across sessions.

### Split strategy
- 50/50 split maximizes power; smaller treatment shares for risky launches (e.g., 90/10)
- For marketplace or social graphs, avoid interference by isolating clusters (geo, tenant, cohort)

---

## Statistical foundations

### Frequentist (standard A/B)
- Use two-sample tests for proportions/means (e.g., z-test on conversion rates)
- Pre-compute sample size (power analysis) for minimum detectable effect (MDE)
- Avoid continuous peeking with fixed-horizon tests or use alpha spending

### Sequential testing (peeking-safe)
- Group Sequential Tests or alpha-spending (O’Brien–Fleming, Pocock)
- SPRT (Sequential Probability Ratio Test) for fast accept/reject when effects are large

### Bayesian (credible intervals)
- Posterior over effect size; report P(treatment > control) and credible intervals
- Naturally supports monitoring over time; still define decision rules upfront

### Multiple metrics and variants
- Correct for multiplicity (Bonferroni/Holm or hierarchical testing)
- For multi-armed bandits: allocate more traffic to better variants while learning (optimize online reward, not inference)

---

## Guardrail metrics

Add guardrails to catch regressions outside the primary objective.

- Reliability: error rate, timeout rate
- Latency: p50/p90/p95/p99 request durations
- Safety: fraud false positive rate, regulatory constraints
- Business: cancellations/returns, customer support tickets

Decision rule example:
- Ship if: +0.8% to +∞ 95% CI improvement on primary metric; no regression >2% on any guardrail.

---

## ML-specific considerations

### Traffic shaping and shadowing
- Shadow deploy the candidate model: run inference on live traffic but do not serve decisions; compare outputs and latency safely.
- Record predictions, scores, explanations for post-hoc analysis.

### Sticky bucketing and cold-starts
- Keep consistent assignment to prevent bias from users sampling both variants.
- Warm caches and feature stores for both variants to avoid cold-start latency skew.

### Feature and label leakage
- Ensure online features equal offline definitions (no peeking into future).
- If the model affects user behavior (e.g., rankings), measure long-term effects (holdout cohorts, ramp experiments).

### Non-stationarity and drift
- Run experiments across enough time to cover weekly/seasonal patterns.
- Inspect slice metrics (geo, device, segment) to detect heterogeneous treatment effects.

---

## Sample size and power

Minimum required sample size for a binary metric (approximate):
- n per arm ≈ 2 × p̄(1 − p̄) × (z_{1−α/2} + z_{power})² / Δ²  
  where p̄ is baseline conversion, Δ is minimum detectable change.

Rules of thumb:
- Avoid underpowered tests; otherwise results will oscillate with noise.
- Compute MDE aligned with business significance, not only statistical significance.

---

## Analysis workflow

1. Verify randomization balance on pre-treatment covariates (device, geo, referrer).
2. Define analysis windows (burn-in, stable period).
3. Compute primary and guardrail metrics with CIs; use stratified or CUPED adjustment to reduce variance if appropriate.
4. Perform sanity checks (assignment ratios, exposure, traffic anomalies).
5. Decide based on pre-registered rules; document effect sizes, uncertainty, learnings.

CUPED (variance reduction):
- Adjust outcome using correlated pre-experiment covariates to reduce variance and improve power.

---

## Bandits vs. A/B

- A/B: best for causal inference and clear decisions; unbiased effect estimates.
- Bandits: best for online reward optimization; converges to best variant but weaker inference.
- Hybrid: Run short A/B to validate, then switch to bandit for ongoing optimization.

---

## Engineering implementation

### Bucketing
- Deterministic hash(user_id, experiment_id) → percentile → assign arm
- Store assignment in a durable store (cookie/server-side)

### Telemetry
- Log assignment, exposure, feature versions, model version, request/response metadata
- Use immutable event schemas (e.g., “metrics vN”)

### Safety and rollout
- Start with small canary (e.g., 1–5%), monitor guardrails, then ramp (10% → 25% → 50% → 100%)
- Automatic rollback on SLO breach (latency/errors/spikes in negatives)

---

## Common pitfalls

- Peeking and stopping when results “look good” → inflated false positives
- Mismatched features online vs. offline → invalid comparisons
- Assignment instability (changed hash, ID churn) → contamination
- Short tests ignoring day-of-week/seasonality → biased estimates
- Interference between users (social/mktplace spillovers) → cluster randomization required

---

## Tooling

- Experiment platforms: Optimizely, GrowthBook, PlanOut, internal experimentation services
- Stats libraries: statsmodels (frequentist), PyMC/NumPyro (Bayesian)
- Data/observability: ClickHouse, BigQuery, Snowflake + dashboards (Superset, Grafana)

---

## Summary

A/B testing is the definitive check that a model creates real value safely. Define a strong hypothesis and primary metric, randomize correctly, respect statistical discipline, enforce guardrails, and automate rollout/rollback. Combine with shadowing and offline validation to de-risk launches, and always document decisions and learnings for future iterations.
