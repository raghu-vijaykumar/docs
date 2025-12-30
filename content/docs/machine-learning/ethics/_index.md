---
weight: 9
bookCollapseSection: false
title: "Ethics & Bias in AI"
draft: false
---

# Ethics & Bias in AI

Machine learning systems have profound societal impact, requiring careful consideration of fairness, accountability, and human values. This section explores the technical and philosophical challenges of building ethical AI systems that don't perpetuate harmful biases or inequalities.

## Explainability & Fairness

### Explainable AI (XAI)

**Technical Interpretability**: Ability to understand model decisions through transparent mechanisms.

**Black-box vs White-box Models**:
- **White-box**: Inherently interpretable (decision trees, linear models)
- **Gray-box**: Some interpretability mechanisms available
- **Black-box**: Complex deep learning models requiring special tools

**Post-hoc Explanations**: Adding interpretability to black-box models

### Fairness in Machine Learning

**Defining Fairness**: Multiple mathematical definitions capture different notions of equity

**Demographic Parity**: Equal positive classification rates across protected groups
- P(ŷ = 1 | G = A) = P(ŷ = 1 | G = B)
- Equal opportunity despite different base rates

**Equalized Odds**: Equal true positive and false positive rates
- TPRₐ = TPRᵦ and FPRₐ = FPRᵦ
- Balanced error rates across groups

**Individual Fairness**: Similar individuals treated similarly
- Distance-based metrics using feature similarity

```python
from fairlearn.metrics import demographic_parity_ratio, equalized_odds_ratio
from sklearn.metrics import confusion_matrix

def measure_fairness(y_true, y_pred, sensitive_features):
    """
    Measure fairness metrics across sensitive attribute groups
    """

    # Demographic parity
    dp_ratio = demographic_parity_ratio(y_true, y_pred,
                                       sensitive_features=sensitive_features)

    # Equalized odds
    eo_ratio = equalized_odds_ratio(y_true, y_pred,
                                   sensitive_features=sensitive_features)

    return {"demographic_parity_ratio": dp_ratio,
            "equalized_odds_ratio": eo_ratio}

# Example usage
sensitive_attr = [0, 0, 1, 1, 0, 1, 0, 1]  # Protected group membership
fairness_metrics = measure_fairness(y_true, y_pred, sensitive_attr)
```

### SHAP (Shapley Additive exPlanations)

**Theoretical Foundation**: Based on game theory's Shapley values
- Each feature's contribution to prediction calculated
- Marginal contributions across all feature subsets

**SHAP Values**: Additive feature importance explanations

```python
import shap

# Tree-based models
explainer = shap.TreeExplainer(model)
shap_values = explainer.shap_values(X_test)

# Summary plot - feature importance
shap.summary_plot(shap_values, X_test)

# Waterfall plot - individual prediction explanation
shap.plots.waterfall(explainer.expected_value[1], shap_values[1], X_test.iloc[1])
```

### LIME (Local Interpretable Model-agnostic Explanations)

**Local Explanations**: Explains individual predictions through perturbation

**Algorithm**:
1. Sample points around instance of interest
2. Get model predictions on perturbed samples
3. Learn local surrogate model (usually linear)
4. Use surrogate coefficients as feature importance

```python
from lime.lime_tabular import LimeTabularExplainer

# Create explainer for tabular data
explainer = LimeTabularExplainer(training_data=X_train.values,
                                 feature_names=X_train.columns,
                                 class_names=['Negative', 'Positive'],
                                 mode='classification')

# Explain single prediction
exp = explainer.explain_instance(X_test.iloc[0].values,
                                model.predict_proba,
                                num_features=5)

# Show explanation
exp.show_in_notebook()
print(exp.as_list())
```

## Bias Detection & Mitigation

### Types of Bias

**Selection Bias**: Unrepresentative sampling from population
- **Coverage bias**: Sample miss key subpopulations
- **Non-response bias**: Systematic differences between respondents/non-respondents

**Measurement Bias**: Errors in data collection or labeling
- **Observer bias**: Data collectors influence measurements
- **Instrumentation bias**: Tools introduce systematic errors

**Algorithmic Bias**: Discrimination encoded in training data or procedures
- **Preprocessing bias**: Data transformations amplify disparities
- **Post-processing bias**: Decision rules create unfair outcomes

**Human-Centric Bias**: Subjective judgments contaminate supposedly objective data

### Detection Techniques

**Statistical Parity Tests**: Compare outcome distributions across groups

```python
def statistical_parity(y_pred, sensitive_features, threshold=0.05):
    """
    Test for statistical parity difference
    """
    groups = {}
    for sf in set(sensitive_features):
        mask = sensitive_features == sf
        groups[sf] = y_pred[mask].mean()

    # Max difference between any pair of groups
    parity_diff = max(groups.values()) - min(groups.values())
    return parity_diff > threshold, parity_diff, groups
```

**Disparate Impact Analysis**: Compare selection rates between protected groups

```python
def disparate_impact(y_pred, sensitive_features, reference_group=0):
    """
    Calculate disparate impact ratio
    """
    selection_rates = {}
    for sf in set(sensitive_features):
        mask = sensitive_features == sf
        selection_rates[sf] = y_pred[mask].mean()

    di_ratio = min(selection_rates.values()) / max(selection_rates.values())
    return di_ratio < 0.8, di_ratio  # 80% rule threshold
```

**Intersectional Analysis**: Examine bias at subgroup intersections

```python
from itertools import product

def intersectional_analysis(y_pred, sensitive_features_list):
    """
    Analyze fairness across multiple sensitive attributes
    """
    intersections = list(product(*[set(sf) for sf in sensitive_features_list]))

    intersection_rates = {}
    for intersection in intersections:
        mask = np.all([sf == val for sf, val in zip(sensitive_features_list, intersection)], axis=0)
        if mask.sum() > 0:  # Avoid empty groups
            intersection_rates[intersection] = y_pred[mask].mean()

    return intersection_rates
```

### Mitigation Strategies

#### Pre-processing Techniques

**Reweighing**: Adjust sample weights to balance representation

```python
from aif360.datasets import BinaryLabelDataset
from aif360.algorithms.preprocessing import Reweighing

# Convert to AIF360 dataset format
dataset = BinaryLabelDataset(df=X.join(y),
                           label_names=['target'],
                           protected_attribute_names=['sensitive_attr'])

# Apply reweighing
RW = Reweighing(unprivileged_groups=[{'sensitive_attr': 0}],
                privileged_groups=[{'sensitive_attr': 1}])
dataset_transf = RW.fit_transform(dataset)
```

**Massaging**: Modify labels to achieve fairness constraints

**Sampling**: Oversample underrepresented or undersample overrepresented groups

#### In-processing Techniques

**Fairness-aware Algorithms**: Optimize both accuracy and fairness

**Adversarial Debiasing**: Use adversarial training to remove protected attribute information

```python
from aif360.algorithms.inprocessing import AdversarialDebiasing

# Fair classification through adversarial training
adv_debiaser = AdversarialDebiasing(privileged_groups=[{'sensitive_attr': 1}],
                                   unprivileged_groups=[{'sensitive_attr': 0}],
                                   scope_name='debiased_classifier')
model = adv_debiaser.fit(dataset)
```

**Constraint-based Methods**: Add fairness constraints to optimization

#### Post-processing Techniques

**Threshold Adjustment**: Modify decision boundaries for fairness

```python
from aif360.algorithms.postprocessing import CalibratedEqOddsPostprocessing

# Calibrate predictions for equalized odds
calib_eq_odds = CalibratedEqOddsPostprocessing(
    privileged_groups=[{'sensitive_attr': 1}],
    unprivileged_groups=[{'sensitive_attr': 0}],
    cost_constraint='fnr'  # Minimize false negative rate difference
)
calib_eq_odds = calib_eq_odds.fit(dataset_orig, dataset_pred)
dataset_transf = calib_eq_odds.predict(dataset_pred)
```

### Model Cards & Documentation

Comprehensive model documentation including fairness considerations

**Essential Components**:
- Intended use and limitations
- Training data demographics and potential biases
- Performance metrics including fairness measures
- Mitigation strategies employed

## AI Regulations & Compliance

### Regulatory Landscape

**GDPR (EU)**: Right to explanation, automated decision-making transparency
- **Article 22**: Right to not be subject to automated decision-making
- **Article 15**: Right to meaningful information about logic involved

**CCPA/CPRA (California)**: Similar to GDPR with California-specific requirements

**Algorithmic Accountability**: Emerging state and local regulations

**Fair Lending Laws**: ECOA and CRA require fair lending practices

### Technical Compliance

**Audit Trails**: Comprehensive logging of model decisions and reasoning

**Appeal Mechanisms**: Human-in-the-loop processes for automated decisions

**Conformity Assessment**: Regular validation of system alignment with requirements

### Practical Implementation

**Responsible AI Frameworks**:
- Microsoft's Responsible AI principles
- Google's AI principles
- IBM's Fairness 360 toolkit
- Open-source fairness toolkits

**Continuous Monitoring**:
- Ongoing bias audits
- Performance degradation alerts
- Fairness metric dashboards

```python
def create_fairness_monitoring_pipeline(model, test_data, sensitive_features):
    """
    Setup continuous fairness monitoring
    """
    monitoring_metrics = [
        'demographic_parity_ratio',
        'equalized_odds_ratio',
        'disparate_impact_ratio'
    ]

    # Define alert thresholds
    alert_thresholds = {
        'demographic_parity_ratio': 0.8,
        'equalized_odds_ratio': 0.8,
        'disparate_impact_ratio': 0.8
    }

    pipeline = ModelMonitoringPipeline(
        model=model,
        metrics=monitoring_metrics,
        thresholds=alert_thresholds,
        evaluation_data=test_data,
        sensitive_features=sensitive_features,
        schedule='daily'  # or 'realtime'
    )

    return pipeline
```

## Getting Started

1. **Assessment**: Audit existing models for bias and explainability gaps
2. **Infrastructure**: Implement monitoring and explainability capabilities
3. **People**: Train teams on ethical AI practices and regulatory requirements
4. **Processes**: Establish review workflows for high-risk AI systems

Building ethical AI requires ongoing commitment to fairness, transparency, and accountability. Technical tools provide necessary capabilities, but organizational culture and processes determine ultimate success.
