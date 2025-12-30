---
weight: 1
bookCollapseSection: true
title: "Math for ML"
draft: false
---

# Mathematics for Machine Learning

Machine learning relies heavily on mathematical foundations that enable algorithms to learn from data, make predictions, and optimize performance. This section covers the core mathematical concepts essential for understanding and implementing ML systems, from basic linear algebra to advanced optimization techniques.

## Linear Algebra

Linear algebra provides the mathematical framework for representing and manipulating data in machine learning. Most ML algorithms involve operations on vectors and matrices, making these concepts fundamental to the field.

### Scalars, Vectors, Matrices, and Tensors

**Scalars**: Single numbers (real or complex)
- Represented as: \(a, b, c \in \mathbb{R}\)

**Vectors**: Ordered arrays of scalars
- Column vector: \(\mathbf{v} = \begin{bmatrix} v_1 \\ v_2 \\ \vdots \\ v_n \end{bmatrix}\)
- Row vector: \(\mathbf{v}^T = [v_1, v_2, \dots, v_n]\)
- Vector operations: addition, scalar multiplication, dot product

**Matrices**: 2D arrays of scalars
- Shape: \(m \times n\) (m rows, n columns)
- Matrix operations: addition, multiplication, transposition
- Special matrices: identity \(\mathbf{I}\), diagonal, symmetric, orthogonal

**Tensors**: Multi-dimensional arrays
- Scalars: 0D tensors
- Vectors: 1D tensors
- Matrices: 2D tensors
- Higher-order tensors: 3D, 4D, etc. (images, video sequences)

### Matrix Operations

**Matrix Addition**: Element-wise operation
\[
\mathbf{C} = \mathbf{A} + \mathbf{B} \implies c_{ij} = a_{ij} + b_{ij}
\]

**Matrix Multiplication**: Row-column dot products
\[
\mathbf{C} = \mathbf{A} \cdot \mathbf{B} \implies c_{ij} = \sum_k a_{ik} b_{kj}
\]

**Matrix Inversion**: Finding \(\mathbf{A}^{-1}\) such that \(\mathbf{A} \cdot \mathbf{A}^{-1} = \mathbf{I}\)
- Only square matrices have inverses (if determinant ≠ 0)
- Computational complexity: O(n³) for naive implementation

### Eigenvalues and Eigenvectors

For a square matrix \(\mathbf{A}\), eigenvectors \(\mathbf{v}\) and eigenvalues \(\lambda\) satisfy:
\[
\mathbf{A} \mathbf{v} = \lambda \mathbf{v}
\]

**Characteristic equation**: \(\det(\mathbf{A} - \lambda \mathbf{I}) = 0\)

**Applications in ML**:
- Principal Component Analysis (PCA)
- Understanding linear transformations
- Stability analysis of dynamical systems

### Singular Value Decomposition (SVD)

Any matrix \(\mathbf{A}\) can be decomposed as:
\[
\mathbf{A} = \mathbf{U} \mathbf{\Sigma} \mathbf{V}^T
\]
- \(\mathbf{U}\): Left singular vectors (orthogonal)
- \(\mathbf{\Sigma}\): Diagonal matrix of singular values
- \(\mathbf{V}\): Right singular vectors (orthogonal)

**Applications**:
- Dimensionality reduction
- Matrix approximation
- Solving linear systems

## Calculus

Calculus provides tools for understanding rates of change and optimization, which are crucial for training machine learning models.

### Differentiation and Gradients

**Derivative**: Rate of change of a function
\[
\frac{d}{dx} f(x) = \lim_{h \to 0} \frac{f(x+h) - f(x)}{h}
\]

**Partial Derivatives**: Derivatives with respect to one variable while holding others constant
\[
\frac{\partial}{\partial x_i} f(x_1, \dots, x_n)
\]

**Gradient**: Vector of all partial derivatives
\[
\nabla f = \begin{bmatrix} \frac{\partial f}{\partial x_1} \\ \vdots \\ \frac{\partial f}{\partial x_n} \end{bmatrix}
\]

### Chain Rule and Backpropagation

**Chain Rule**: For composite functions \(f(g(x))\):
\[
\frac{d}{dx} f(g(x)) = f'(g(x)) \cdot g'(x)
\]

**Backpropagation**: Efficient computation of gradients in neural networks
- Forward pass: compute outputs
- Backward pass: compute gradients using chain rule
- Time complexity: O(number of parameters)

### Jacobian and Hessian Matrices

**Jacobian**: Matrix of all first-order partial derivatives
\[
\mathbf{J} = \begin{bmatrix}
\frac{\partial f_1}{\partial x_1} & \cdots & \frac{\partial f_1}{\partial x_n} \\
\vdots & \ddots & \vdots \\
\frac{\partial f_m}{\partial x_1} & \cdots & \frac{\partial f_m}{\partial x_n}
\end{bmatrix}
\]

**Hessian**: Matrix of second-order partial derivatives
\[
\mathbf{H} = \begin{bmatrix}
\frac{\partial^2 f}{\partial x_1^2} & \cdots & \frac{\partial^2 f}{\partial x_1 \partial x_n} \\
\vdots & \ddots & \vdots \\
\frac{\partial^2 f}{\partial x_n \partial x_1} & \cdots & \frac{\partial^2 f}{\partial x_n^2}
\end{bmatrix}
\]

## Probability and Statistics

Probability theory provides the foundation for dealing with uncertainty in machine learning, while statistics offers tools for inference from data.

### Probability Distributions

**Discrete Distributions**:
- **Bernoulli**: P(X=1) = p, P(X=0) = 1-p
- **Binomial**: Number of successes in n trials
- **Poisson**: Events in fixed interval

**Continuous Distributions**:
- **Gaussian/Normal**: \(\mathcal{N}(\mu, \sigma^2) = \frac{1}{\sigma\sqrt{2\pi}} e^{-\frac{(x-\mu)^2}{2\sigma^2}}\)
- **Exponential**: Memoryless waiting times
- **Beta**: Probabilities and proportions

### Bayes' Theorem

\[
P(A|B) = \frac{P(B|A) P(A)}{P(B)}
\]

**Applications in ML**:
- Naive Bayes classifiers
- Bayesian inference
- Probabilistic graphical models

### Expectation and Variance

**Expectation**: Long-run average of a random variable
\[
\mathbb{E}[X] = \sum x P(x) \quad \text{(discrete)} \quad \mathbb{E}[X] = \int x f(x) dx \quad \text{(continuous)}
\]

**Variance**: Measure of spread around the mean
\[
\mathbb{V}[X] = \mathbb{E}[(X - \mathbb{E}[X])^2]
\]

**Covariance**: Measure of joint variability
\[
\mathbb{C}[X,Y] = \mathbb{E}[(X - \mathbb{E}[X])(Y - \mathbb{E}[Y])]
\]

### Maximum Likelihood Estimation (MLE)

Find parameters that maximize the likelihood of observing the data:
\[
\hat{\theta} = \arg\max_\theta P(\mathbf{x}|\theta)
\]

**Log-likelihood**: Often easier to work with
\[
\ell(\theta) = \log P(\mathbf{x}|\theta)
\]

### Maximum A Posteriori (MAP)

Incorporates prior knowledge:
\[
\hat{\theta} = \arg\max_\theta P(\theta|\mathbf{x}) = \arg\max_\theta P(\mathbf{x}|\theta) P(\theta)
\]

## Optimization Techniques

Optimization forms the core of machine learning training, finding the best model parameters to minimize prediction errors.

### Convex vs Non-Convex Optimization

**Convex Functions**: Bowl-shaped, single global minimum
- Easy to optimize
- Strong theoretical guarantees
- Examples: Linear regression, logistic regression

**Non-Convex Functions**: Multiple local minima/maxima
- Harder to optimize
- No convergence guarantees
- Examples: Neural networks, deep learning

### Loss Functions

**Mean Squared Error (MSE)**: For regression
\[
\mathcal{L} = \frac{1}{n} \sum_{i=1}^n (y_i - \hat{y}_i)^2
\]

**Cross-Entropy**: For classification
\[
\mathcal{L} = -\frac{1}{n} \sum_{i=1}^n \sum_{c=1}^C y_{i,c} \log \hat{y}_{i,c}
\]

**Huber Loss**: Robust to outliers
\[
\mathcal{L} = \begin{cases}
\frac{1}{2} (y - \hat{y})^2 & |y - \hat{y}| \leq \delta \\
\delta |y - \hat{y}| - \frac{1}{2} \delta^2 & |y - \hat{y}| > \delta
\end{cases}
\]

### Gradient Descent Variants

**Batch Gradient Descent**: Use entire dataset
\[
\theta_{t+1} = \theta_t - \eta \nabla \mathcal{L}(\theta_t)
\]

**Stochastic Gradient Descent (SGD)**: Use single example
\[
\theta_{t+1} = \theta_t - \eta \nabla \mathcal{L}_i(\theta_t)
\]

**Mini-batch Gradient Descent**: Use small batches
- Balances efficiency and stability
- Most commonly used in practice

### Advanced Optimizers

**Momentum**: Accelerates convergence
\[
v_t = \gamma v_{t-1} + \eta \nabla \mathcal{L}(\theta_t) \\
\theta_{t+1} = \theta_t - v_t
\]

**Adam**: Adaptive moment estimation
\[
m_t = \beta_1 m_{t-1} + (1-\beta_1) g_t \\
v_t = \beta_2 v_{t-1} + (1-\beta_2) g_t^2 \\
\hat{m}_t = \frac{m_t}{1-\beta_1^t}, \quad \hat{v}_t = \frac{v_t}{1-\beta_2^t} \\
\theta_{t+1} = \theta_t - \eta \frac{\hat{m}_t}{\sqrt{\hat{v}_t} + \epsilon}
\]

## Information Theory

Information theory quantifies the amount of information in data and is fundamental to understanding compression and communication.

### Entropy

**Shannon Entropy**: Average information content
\[
H(X) = -\sum_{x} P(x) \log_2 P(x)
\]

**Joint Entropy**: Entropy of joint distribution
\[
H(X,Y) = -\sum_{x,y} P(x,y) \log_2 P(x,y)
\]

**Conditional Entropy**: Entropy remaining after knowing Y
\[
H(X|Y) = H(X,Y) - H(Y)
\]

### Kullback-Leibler Divergence

Measure of difference between two distributions:
\[
D_{KL}(P||Q) = \sum_x P(x) \log_2 \frac{P(x)}{Q(x)}
\]

**Properties**:
- Non-negative: D_KL(P||Q) ≥ 0
- Not symmetric: D_KL(P||Q) ≠ D_KL(Q||P)
- Used in variational inference and model comparison

### Mutual Information

Amount of information shared between variables:
\[
I(X;Y) = H(X) + H(Y) - H(X,Y)
\]

**Relationship to KL divergence**:
\[
I(X;Y) = D_{KL}(P(X,Y) || P(X)P(Y))
\]

## Applications in Machine Learning

### Dimensionality Reduction

**Principal Component Analysis (PCA)**:
- Find directions of maximum variance
- Project data onto lower-dimensional subspace
- Preserves as much variance as possible

**t-SNE and UMAP**:
- Preserve local structure in high dimensions
- Useful for visualization and clustering

### Bayesian Networks

**Probabilistic Graphical Models**:
- Represent conditional dependencies
- Enable efficient inference
- Applications: diagnosis, recommendation systems

### Gaussian Processes

**Non-parametric regression**:
- Functions drawn from GP prior
- Uncertainty quantification
- Hyperparameter optimization

## Advanced Topics

### Manifold Learning

**Manifold Hypothesis**: High-dimensional data lies on low-dimensional manifold

**Isomap, LLE, Laplacian Eigenmaps**: Nonlinear dimensionality reduction

### Reinforcement Learning Mathematics

**Bellman Equations**:
- State value: V(s) = E[R + γV(s')]
- Action value: Q(s,a) = E[R + γ max_a' Q(s',a')]

**Policy Gradients**: Optimize policies directly
\[
\nabla_\theta J(\theta) = \mathbb{E}_\pi [\nabla_\theta \log \pi_\theta(a|s) Q^\pi(s,a)]
\]

### Optimal Transport

**Wasserstein Distance**: Earth mover's distance between distributions

**Applications**: Domain adaptation, generative modeling

This mathematical foundation enables the development of sophisticated machine learning algorithms and provides the theoretical understanding necessary for advancing the field. Each concept builds upon the others, creating a cohesive framework for solving complex real-world problems.
