---
title: "Supervised Learning"
linktitle: "Supervised Learning"
description: "Comprehensive guide to supervised learning algorithms, from classification/regression fundamentals to advanced implementation techniques."
date: 2025-10-28
draft: false
tags: ["supervised learning", "classification", "regression", "machine learning"]
categories: ["Machine Learning"]
weight: 2
toc_enable: true
---

# Supervised Learning

Supervised learning trains algorithms on labeled data to predict outcomes for new inputs. This paradigm forms the backbone of machine learning applications, from recommendation systems to predictive analytics. The learning process involves optimizing model parameters to minimize prediction errors on known examples, enabling generalization to unseen data.

## Core Mechanisms

### Learning Process

Supervised learning follows a systematic optimization approach:

```mermaid
graph LR
    A[Training Data<br/>X: Features<br/>Y: Labels] --> B[Model<br/>Parameters θ]
    B --> C[Predictions<br/>Ŷ = f(X, θ)]
    C --> D[Loss Function<br/>L(Y, Ŷ)]
    D --> E[Optimization<br/>∇θ = ∂L/∂θ]
    E --> B
    C --> F[Test Evaluation]
```

**Key Components:**
- **Loss Function**: Quantifies prediction quality
- **Optimization Algorithm**: Updates parameters to minimize loss
- **Convergence**: Achieves optimal parameter values

### Training Dynamics

Models learn through iterative parameter adjustments:

1. **Forward Pass**: Compute predictions from current parameters
2. **Loss Computation**: Measure prediction quality against true labels
3. **Backward Pass**: Calculate parameter adjustment directions
4. **Parameter Update**: Apply learning rate-scaled adjustments

```python
# Generic supervised learning training loop
def train_model(model, X_train, y_train, epochs=100, learning_rate=0.01):
    for epoch in range(epochs):
        # Forward pass: predictions
        y_pred = model.forward(X_train)

        # Loss computation
        loss = loss_function(y_train, y_pred)

        # Backward pass: gradients
        gradients = model.backward(y_train, y_pred)

        # Parameter update
        model.parameters -= learning_rate * gradients

        # Logging
        if epoch % 10 == 0:
            print(f"Epoch {epoch}: Loss = {loss:.4f}")
```

## Classification vs Regression

### Binary Classification

Predicts one of two mutually exclusive classes. The decision boundary represents a hyperplane separating class regions in feature space.

**Mathematical Formulation:**
- **Decision Function**: d(x) = w^T x + b
- **Classification**: ŷ = sign(d(x))
- **Probability Estimation**: p(y=1|x) = sigmoid(d(x))

**Decision Boundaries:**
```python
import numpy as np
import matplotlib.pyplot as plt

# Linear decision boundary
def plot_decision_boundary(X, y, model, title):
    x_min, x_max = X[:, 0].min() - 1, X[:, 0].max() + 1
    y_min, y_max = X[:, 1].min() - 1, X[:, 1].max() + 1
    xx, yy = np.meshgrid(np.arange(x_min, x_max, 0.01),
                         np.arange(y_min, y_max, 0.01))

    Z = model.predict(np.c_[xx.ravel(), yy.ravel()])
    Z = Z.reshape(xx.shape)

    plt.contourf(xx, yy, Z, alpha=0.8)
    plt.scatter(X[:, 0], X[:, 1], c=y, edgecolor='k')
    plt.title(title)
    plt.show()
```

### Multi-Class Classification

Extends binary classification to multiple categories. Requires strategies like one-versus-rest or softmax normalization.

**One-vs-Rest Approach:**
- Train k binary classifiers for k classes
- Select class with highest confidence score
- **Advantage**: Simple and interpretable
- **Drawback**: May handle class imbalance poorly

**Softmax Approach:**
- Normalize k classification scores into probabilities
- ŷ = argmax(softmax(scores))

### Regression

Predicts continuous numerical values through function approximation. The goal is fitting a curve that minimizes predictive errors.

**Loss Functions:**
- **Mean Squared Error**: (1/n) Σ(yᵢ - ŷᵢ)²
- **Mean Absolute Error**: (1/n) Σ|yᵢ - ŷᵢ|
- **Huber Loss**: Combines MSE and MAE benefits

## Probabilistic Perspective

Supervised learning can be framed probabilistically:

```mermaid
graph TD
    A[Joint Distribution<br/>P(X, Y)] --> B[Conditional Likelihood<br/>P(Y|X)]
    B --> C{Frequentist Estimation<br/>Maximum Likelihood}
    B --> D{Bayesian Estimation<br/>Posterior Inference}

    C --> E[Point Estimates<br/>θ̂ = argmax P(D|θ)]
    D --> F[Distribution Estimation<br/>P(θ|D) ∝ P(D|θ)P(θ)]

    E --> G[Deterministic Predictions]
    F --> H[Probabilistic Predictions]
```

### Frequentist vs Bayesian

**Frequentist Approach:**
- Treats parameters as fixed but unknown
- Estimates parameters via maximum likelihood
- Provides point estimates and confidence intervals

**Bayesian Approach:**
- Treats parameters as random variables
- Updates parameter beliefs with new data
- Provides full posterior distributions

### Probabilistic Classification

Bayesian decision theory provides optimal classification rules:

**Minimum Risk Classification:**
- Compute posterior probabilities P(ωⱼ|x)
- Choose class minimizing expected loss
- **Risk R(ωⱼ|x)** = Σᵢ λ(ωⱼ|ωᵢ) P(ωᵢ|x)

**Naive Bayes Classifier:**
- Assumes feature independence
- P(ωⱼ|x) ∝ P(ωⱼ) ∏ P(xᵢ|ωⱼ)
- Computationally efficient despite independence assumption

## Feature Learning & Representations

Supervised learning can exploit feature architectures:

### Linear Representations

Simplest form: linear combination of input features

**Learned Transformation:**
- φ(τ) = ⟨τ, μ⟩ + b (inner product)
- τ ∈ H (Hilbert space), μ ∈ H (moment)
- Learned linear manifold subspaces

### Kernel Methods

Implicitly map features to higher dimensions for non-linear decision boundaries:

**Kernel Definition:**
- k(x, x') = ⟨φ(x), φ(x')⟩
- Avoid explicit φ computation through dual formulation
- Enable infinite-dimensional feature spaces

```python
from sklearn.svm import SVC

# Radial Basis Function Kernel
svm_rbf = SVC(kernel='rbf', gamma='scale')
svm_rbf.fit(X_train, y_train)

# Custom kernel implementation
def polynomial_kernel(X, Y, degree=3):
    return (X @ Y.T + 1) ** degree
```

### Representation Learning

Deep architectures learn hierarchical feature representations:

**Feature Hierarchies:**
- Lower layers: Basic patterns and edges
- Middle layers: Parts and components
- Upper layers: Abstract categories and semantics

**End-to-End Learning:**
- Feature engineering becomes automated
- Gradient-based optimization throughout
- Potentially more effective than hand-crafted features

## Algorithm Families

### Linear Models

Learn linear relationships between features and targets. Mathematically tractable but may underfit complex relationships.

**Generalized Linear Models:**
- Assumptions about target distribution
- Link functions connecting linear terms to targets
- Examples: Gaussian (regression), Bernoulli (binary), Multinomial (multiclass)

### Tree-Based Models

Create hierarchical decision rules through recursive partitioning. Handle complex interactions and heterogeneous data types.

**Decision Trees:**
- Information gain-based splitting
- Greedy optimization strategy
- Natural handling of categorical features

**Ensemble Methods:**
- Bagging: Bootstrap aggregation (Random Forest)
- Boosting: Sequential error correction (Gradient Boosting)
- Voting mechanisms combining multiple learners

### Neural Networks

Learn arbitrary function approximators through layered computation. Provide expressive capacity for complex patterns.

**Architectural Components:**
- Hidden layers with activation functions
- Forward-backward propagation algorithms
- Universal approximation capabilities

### Instance-Based Learning

Store training examples and learn from similar instances. Natural approach requiring no explicit modeling assumptions.

**K-Nearest Neighbors:**
- Distance-based similarity computation
- Majority voting or distance-weighted averaging
- Adaptable to arbitrary target distributions

## Optimization Landscapes

### Convex vs Non-Convex Optimization

**Convex Problems:**
- Single global optimum
- Guaranteed convergence to optimal solution
- Efficient optimization algorithms available

**Non-Convex Problems:**
- Multiple local optima and saddle points
- No convergence guarantees
- Challenging optimization landscapes

### Gradient Descent Variants

**Batch Gradient Descent:**
- Updates parameters using full dataset
- Stable convergence but computationally expensive
- θ = θ - η ∇J(θ)

**Stochastic Gradient Descent (SGD):**
- Updates parameters using single examples
- Noisy updates but computationally efficient
- θ = θ - η ∇Jᵢ(θ)

**Mini-Batch Gradient Descent:**
- Compromise between batch and stochastic
- Vectorized implementations possible
- Balanced convergence properties

### Modern Optimization

**Adaptive Methods:**
- **AdaGrad**: Adapts learning rates per parameter
- **RMSProp**: Establishes running averages of gradients
- **Adam**: Combines momentum and adaptive learning rates

```python
import torch.optim as optim

# Example Adam optimizer usage
optimizer = optim.Adam(model.parameters(), lr=0.001, betas=(0.9, 0.999))
optimizer.zero_grad()
loss.backward()
optimizer.step()
```

## Handling Real-World Challenges

### Imbalanced Datasets

Many datasets exhibit class distribution imbalances causing learning bias:

**Strategies:**
- **Resampling**: Oversample minority or undersample majority classes
- **Cost-Sensitive Learning**: Attribute higher costs to minority class errors
- **Synthetic Generation**: SMOTE algorithm for creating synthetic minority samples

### Missing Data

Real datasets often contain missing values requiring imputation:

**Imputation Approaches:**
- **Mean/Median Imputation**: Simple statistical substitutions
- **KNN Imputation**: Leveraging similarity-based estimates
- **Model-Based Imputation**: Using supervised models to predict missing values

### Categorical Variables

Non-numeric variables require encoding for mathematical algorithms:

**Encoding Strategies:**
- **One-Hot Encoding**: Binary indicator variables per category
- **Ordinal Encoding**: Preserving categorical ordering if meaningful
- **Target Encoding**: Using target statistics for encoding

### Feature Scaling

Algorithms sensitive to feature scales require normalization:

**Standardization:**
- z = (x - μ) / σ
- Centers to zero mean, scales to unit variance
- Appropriate for algorithms assuming normally distributed features

**Min-Max Scaling:**
- x' = (x - min_x) / (max_x - min_x)
- Scales to [0,1] interval
- Preserves zero entries for sparse matrices

## Advanced Topics

### Transfer Learning

Leverages knowledge from related tasks to improve performance on target tasks. Particularly valuable when target data is limited.

**Approaches:**
- **Fine-Tuning**: Adjust pre-trained model parameters on target task
- **Feature Extraction**: Use pre-trained features as inputs to new classifiers
- **Domain Adaptation**: Account for distribution differences between source and target

### Active Learning

Iteratively selects most informative samples for labeling, reducing annotation costs.

**Query Strategies:**
- **Uncertainty Sampling**: Query instances with highest prediction uncertainty
- **Query-by-Committee**: Disagreement between different model hypotheses
- **Expected Model Change**: Instances promising largest parameter updates

### Semi-Supervised Learning

Incorporates both labeled and unlabeled data for more robust learning.

**Regularization Approaches:**
- **Consistency Regularization**: Similar inputs should produce similar outputs
- **Entropy Minimization**: Encourage confident predictions on unlabeled data
- **Pseudo-Labeling**: Use model predictions as labels for unlabeled instances

Supervised learning provides the foundation for predictive modeling across domains. Algorithm selection requires balancing expressiveness, interpretability, and computational efficiency while considering specific problem characteristics and data constraints.
