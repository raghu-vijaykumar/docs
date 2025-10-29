---
weight: 2
bookCollapseSection: false
title: "Artificial Neural Networks (ANN)"
draft: false
---

# Artificial Neural Networks (ANN)

Artificial Neural Networks (ANNs), also known as feedforward neural networks, form the foundation of deep learning. These networks process information in one direction—from input to output—making them ideal for many classification and regression tasks. Understanding ANNs requires exploring the individual neurons, how they connect, and the learning algorithms that make them powerful.

## The Building Block: The Neuron

At the heart of every neural network lies the artificial neuron, mathematically inspired by biological neurons but simplified for computational efficiency.

```mermaid
graph LR
    A[Input 1]*w1 --> C((Σ))
    B[Input 2]*w2 --> C
    C --> D[+ bias]
    D --> E[Activation Function]
    E --> F[Output]
```

A neuron receives multiple inputs, each weighted by a learnable parameter. These weighted inputs are summed, optionally with a bias term, and then passed through an activation function. The output becomes input for the next layer.

**Mathematical representation:**
```
output = activation(Σ(weight_i × input_i) + bias)
```

## Network Architecture

ANNs organize neurons into layers, each serving a specific purpose:

- **Input Layer**: Receives the raw data features
- **Hidden Layers**: Extract hierarchical features and patterns
- **Output Layer**: Produces the final prediction

```python
import torch.nn as nn

class SimpleANN(nn.Module):
    def __init__(self, input_size, hidden_size, output_size):
        super(SimpleANN, self).__init__()
        self.layer1 = nn.Linear(input_size, hidden_size)
        self.layer2 = nn.Linear(hidden_size, output_size)

    def forward(self, x):
        x = torch.relu(self.layer1(x))
        x = self.layer2(x)
        return x
```

## Activation Functions

Activation functions introduce non-linearity, enabling networks to learn complex relationships. The choice of activation impacts training stability and expressiveness.

### ReLU (Rectified Linear Unit)

The most popular activation function in deep learning, ReLU maps negative values to zero while preserving positive values unchanged.

**Formula:** f(x) = max(0, x)

**Advantages:**
- Computational efficiency (simple thresholding)
- No vanishing gradient for positive values
- Sparse activation (zero for negative inputs)

**Drawbacks:**
- "Dead neurons" when inputs are always negative
- Not differentiable at zero (though usually fine in practice)

### Sigmoid

Maps inputs to a range between 0 and 1, making it suitable for binary classification output layers.

**Formula:** f(x) = 1 / (1 + e^(-x))

**Use cases:** Binary classification, probabilistic outputs
**Limitations:** Vanishing gradients for large absolute inputs

### Tanh (Hyperbolic Tangent)

Similar to sigmoid but ranges from -1 to 1, centering outputs around zero.

**Formula:** f(x) = (e^x - e^(-x)) / (e^x + e^(-x))

**Benefits:** Zero-centered outputs help gradient flow
**Drawbacks:** Still suffers from vanishing gradients

```python
import torch

def relu_activation(x):
    return torch.max(torch.zeros_like(x), x)

def sigmoid_activation(x):
    return 1 / (1 + torch.exp(-x))

def tanh_activation(x):
    return torch.tanh(x)

# Example usage
x = torch.tensor([-2.0, -1.0, 0.0, 1.0, 2.0])
print(f"ReLU: {relu_activation(x)}")
print(f"Sigmoid: {sigmoid_activation(x)}")
print(f"Tanh: {tanh_activation(x)}")
```

## Loss Functions

Loss functions quantify how well the network predictions match the true labels, guiding the learning process.

### Mean Squared Error (MSE)

Common for regression tasks, measures the squared difference between predictions and targets.

**Formula:** MSE = (1/n) Σ (y_true - y_pred)²

**Use cases:** Continuous value prediction
**Sensitivity:** Penalizes large errors quadratically

### Cross-Entropy Loss

Standard for classification, measures the dissimilarity between predicted probability distributions and true labels.

**Formula for binary:** BCE = -[y * log(p) + (1-y) * log(1-p)]
**Formula for multiclass:** CE = -Σ y_true * log(y_pred)

**Advantages:** Directly optimizes classification accuracy
**Applications:** Most classification problems

```python
import torch.nn.functional as F

# Regression loss
def compute_mse(y_pred, y_true):
    return torch.mean((y_pred - y_true) ** 2)

# Classification loss
def compute_cross_entropy(y_pred, y_true):
    return F.cross_entropy(y_pred, y_true)

# Example
y_pred_reg = torch.tensor([2.5, 1.8, 3.2])
y_true_reg = torch.tensor([3.0, 2.0, 3.0])
mse_loss = compute_mse(y_pred_reg, y_true_reg)

y_pred_class = torch.tensor([[0.1, 0.9], [0.8, 0.2], [0.3, 0.7]])
y_true_class = torch.tensor([1, 0, 1])
ce_loss = compute_cross_entropy(y_pred_class, y_true_class)

print(f"MSE Loss: {mse_loss.item():.4f}")
print(f"Cross-Entropy Loss: {ce_loss.item():.4f}")
```

## Backpropagation & Gradient Descent

The training process involves two key phases: forward propagation (computing predictions) and backward propagation (computing gradients to update parameters).

### Forward Propagation

Data flows from input to output layer:
1. Input layer receives features
2. Each neuron computes weighted sum + bias
3. Activation function transforms the result
4. Process repeats for each layer

### Backward Propagation

Gradients are computed from output back to input:
1. Calculate loss between prediction and target
2. Compute derivative of loss w.r.t. each parameter
3. Use chain rule to propagate gradients backward
4. Update parameters using gradient descent

```python
def train_step(model, x, y, optimizer, criterion):
    # Forward pass
    y_pred = model(x)
    loss = criterion(y_pred, y)

    # Backward pass
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()

    return loss.item()
```

### Gradient Descent Variants

Different optimization algorithms modify parameters differently:

- **Batch Gradient Descent**: Updates after processing entire dataset (slow, stable)
- **Stochastic Gradient Descent (SGD)**: Updates after each sample (fast, noisy)
- **Mini-batch Gradient Descent**: Compromise between batch and SGD

```python
# PyTorch optimizers
import torch.optim as optim

# Basic SGD
optimizer_sgd = optim.SGD(model.parameters(), lr=0.01)

# SGD with momentum
optimizer_momentum = optim.SGD(model.parameters(), lr=0.01, momentum=0.9)

# Adam optimizer (most common)
optimizer_adam = optim.Adam(model.parameters(), lr=0.001)
```

## Practical Training Considerations

### Initialization

Network parameters must be initialized to break symmetry. Popular methods:
- Xavier/Glorot initialization for tanh/sigmoid
- He initialization for ReLU

### Normalization

Standardization of inputs and intermediate activations:
- **Batch Normalization**: Normalizes layer inputs during training
- **Layer Normalization**: Normalizes neurons within a layer

### Regularization

Prevent overfitting through:
- **Dropout**: Randomly zeros neuron outputs during training
- **L2 Weight Decay**: Penalizes large weights

```python
import torch.nn as nn

class RegularizedANN(nn.Module):
    def __init__(self):
        super().__init__()
        self.layer1 = nn.Linear(784, 128)
        self.bn1 = nn.BatchNorm1d(128)  # Batch normalization
        self.dropout = nn.Dropout(0.2)  # Dropout regularization
        self.layer2 = nn.Linear(128, 10)

    def forward(self, x):
        x = torch.relu(self.bn1(self.layer1(x)))
        x = self.dropout(x)
        x = self.layer2(x)
        return x
```

## Common Architectures & Applications

### Multilayer Perceptron (MLP)

Basic ANN with multiple hidden layers, used for tabular data classification/regression.

```python
class MLP(nn.Module):
    def __init__(self, layers):
        super().__init__()
        self.layers = nn.ModuleList()
        for i in range(len(layers) - 1):
            self.layers.append(nn.Linear(layers[i], layers[i+1]))

    def forward(self, x):
        for i, layer in enumerate(self.layers[:-1]):
            x = torch.relu(layer(x))
        x = self.layers[-1](x)
        return x

# Usage for MNIST-like dataset
model = MLP([784, 256, 128, 10])
```

### Autoencoders

Unsupervised networks that learn efficient data representations.

**Applications:**
- Dimensionality reduction
- Image denoising
- Anomaly detection
- Generative modeling

## Training Best Practices

### Learning Rate Scheduling

Dynamically adjust learning rate during training:
- Exponential decay
- Step decay
- Cosine annealing

```python
scheduler = torch.optim.lr_scheduler.StepLR(optimizer, step_size=30, gamma=0.1)
# Call scheduler.step() each epoch
```

### Early Stopping

Monitor validation loss and stop training when it starts increasing to prevent overfitting.

```python
class EarlyStopping:
    def __init__(self, patience=5, min_delta=0):
        self.patience = patience
        self.min_delta = min_delta
        self.counter = 0
        self.best_loss = None

    def __call__(self, val_loss):
        if self.best_loss is None or val_loss < self.best_loss - self.min_delta:
            self.best_loss = val_loss
            self.counter = 0
        else:
            self.counter += 1
            if self.counter >= self.patience:
                return True
        return False
```

### Performance Monitoring

Track both training and validation metrics:
- Loss curves
- Accuracy trends
- Gradient norms
- Learning rate values

## Troubleshooting Training Issues

### Vanishing Gradients

Symptoms: Parameters stop updating, loss plateaus
Solutions: Use ReLU activation, proper initialization, batch normalization

### Exploding Gradients

Symptoms: Parameters become extremely large, NaN losses
Solutions: Gradient clipping, careful initialization

### Overfitting

Symptoms: High training accuracy, low validation accuracy
Solutions: Regularization, early stopping, more data, simpler models

### Underfitting

Symptoms: Poor performance on both training and validation
Solutions: Larger models, longer training, lower regularization

ANNs provide the foundational understanding needed for all deep learning architectures. The concepts here—neurons, layers, activations, loss functions, and optimization—appear throughout neural network design, making this knowledge essential for working with more complex models like CNNs and transformers.
