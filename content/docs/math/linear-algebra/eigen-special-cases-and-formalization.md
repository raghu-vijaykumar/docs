---
weight: 35
title: "Eigen Special Cases & Formalization"
draft: false
prerequisites:
  - docs/math/linear-algebra/eigenvectors-and-eigenvalues
---

# Eigen Special Cases & Formalization

## Three Special Cases

### 1. Uniform Scaling

Scaling by the same amount in every direction. Every vector is an eigenvector, all with the same eigenvalue.

### 2. 180° Rotation

A non-zero pure rotation that *does* have eigenvectors. Every vector stays on its own span but points in the opposite direction. All vectors are eigenvectors with eigenvalue \(-1\).

### 3. Horizontal Shear + Vertical Scaling

More subtle. The horizontal vector is an eigenvector (eigenvalue 1). But there is a second eigenvector -- harder to spot visually. This shows that eigenvectors are not always obvious, even in 2D.

## Extending to 3D

In 3D, scaling and shear work much the same as in 2D. But rotation takes on a new meaning: the eigenvector of a 3D rotation is the **axis of rotation**.

## Algebraic Formalization

For a transformation matrix \(A\), an eigenvector \(\mathbf x\) satisfies:

\[
A \mathbf x = \lambda \mathbf x
\]

The left side applies the transformation; the right side scales \(\mathbf x\) by \(\lambda\). Rearranging:

\[
(A - \lambda I) \mathbf x = \mathbf 0
\]

The identity matrix \(I\) is needed because subtracting a scalar from a matrix is not defined. For a non-trivial solution (\(\mathbf x \neq \mathbf 0\)), the matrix \(A - \lambda I\) must be singular:

\[
\det(A - \lambda I) = 0
\]

### The Characteristic Polynomial

For a general 2×2 matrix \(A = \begin{bmatrix}a & b\\c & d\end{bmatrix}\):

\[
\det\left(\begin{bmatrix}a-\lambda & b\\c & d-\lambda\end{bmatrix}\right)
= \lambda^2 - (a+d)\lambda + (ad - bc) = 0
\]

The eigenvalues are the roots of this polynomial.

### Example: Vertical Scaling by 2

\[
A = \begin{bmatrix}1 & 0\\0 & 2\end{bmatrix}
\]

\[
\det\left(\begin{bmatrix}1-\lambda & 0\\0 & 2-\lambda\end{bmatrix}\right)
= (1-\lambda)(2-\lambda) = 0
\]

Eigenvalues: \(\lambda = 1\) and \(\lambda = 2\).

For \(\lambda = 1\):

\[
\begin{bmatrix}0 & 0\\0 & 1\end{bmatrix}
\begin{bmatrix}x_1\\x_2\end{bmatrix}
= \begin{bmatrix}0\\0\end{bmatrix}
\Rightarrow x_1 = t,\; x_2 = 0
\]

Eigenvectors: any vector along the horizontal axis.

For \(\lambda = 2\):

\[
\begin{bmatrix}-1 & 0\\0 & 0\end{bmatrix}
\begin{bmatrix}x_1\\x_2\end{bmatrix}
= \begin{bmatrix}0\\0\end{bmatrix}
\Rightarrow x_1 = 0,\; x_2 = t
\]

Eigenvectors: any vector along the vertical axis.

### Example: 90° Rotation (No Real Eigenvectors)

\[
A = \begin{bmatrix}0 & -1\\1 & 0\end{bmatrix}
\]

\[
\det\left(\begin{bmatrix}-\lambda & -1\\1 & -\lambda\end{bmatrix}\right)
= \lambda^2 + 1 = 0
\]

No real solutions \(\rightarrow\) no real eigenvectors.

## Why Computers Do This

Finding eigenvalues means finding roots of a polynomial of degree \(n\) (the dimension of the matrix). For large \(n\), this is not feasible by hand. Computers use iterative numerical methods. A strong conceptual understanding is far more valuable than manual calculation skill.

## Summary

- Special cases (uniform scaling, 180° rotation, shear+scaling) build intuition.
- In 3D, eigenvectors of a rotation reveal the axis of rotation.
- The formal expression \(A\mathbf x = \lambda \mathbf x\) leads to \(\det(A - \lambda I) = 0\).
- The characteristic polynomial gives the eigenvalues; plugging back yields eigenvectors.
- For high-dimensional problems, computers handle the computation.
