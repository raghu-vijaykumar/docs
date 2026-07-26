---
weight: 30
title: "Orthogonal Matrices"
draft: false
prerequisites:
  - docs/math/linear-algebra/dot-products-and-duality
  - docs/math/linear-algebra/inverses-column-space-null-space
---

# Orthogonal Matrices

## The Transpose

The **transpose** of a matrix \(A\), denoted \(A^T\), interchanges rows and columns. The \((i, j)\) entry of \(A^T\) is the \((j, i)\) entry of \(A\):

\[
\begin{bmatrix}
1 & 2\\
3 & 4
\end{bmatrix}^T
= \begin{bmatrix}
1 & 3\\
2 & 4
\end{bmatrix}
\]

Elements on the leading diagonal stay in place; off-diagonal elements swap across the diagonal.

## Orthonormal Basis Vectors

A set of basis vectors that are all perpendicular (orthogonal) to each other and each of unit length is called an **orthonormal basis**.

For such a set \(\{ \mathbf a_1, \mathbf a_2, \dots, \mathbf a_n \}\):

\[
\mathbf a_i \cdot \mathbf a_j =
\begin{cases}
0 & i \neq j \quad \text{(orthogonal)}\\
1 & i = j \quad \text{(unit length)}
\end{cases}
\]

## Orthogonal Matrices

If the columns of a square matrix \(A\) form an orthonormal basis, then:

\[
A^T A = I
\]

The first row of \(A^T\) is the first column of \(A\) laid sideways, so the \((1,1)\) entry of \(A^T A\) is \(\mathbf a_1 \cdot \mathbf a_1 = 1\). The \((1,2)\) entry is \(\mathbf a_1 \cdot \mathbf a_2 = 0\), and so on. The result is the identity matrix.

This means **the transpose is the inverse**: \(A^T = A^{-1}\). A matrix with this property is called an **orthogonal matrix**.

### Properties

- \(A^T A = I\) and \(A A^T = I\) -- the rows are also orthonormal.
- \(\det(A) = \pm 1\). Since all basis vectors have unit length, the transformation scales volumes by 1. The sign indicates whether the basis preserves or flips orientation.
- Orthogonal matrices represent rotations and reflections -- they preserve lengths and angles.

## Why Orthonormal Bases Are Convenient

Transforming a vector to a new coordinate system is just a dot product with each basis vector -- provided the basis is orthonormal. For an orthonormal basis \(\{\mathbf a_1, \dots, \mathbf a_n\}\), the coordinates of a vector \(\mathbf v\) in that basis are simply \(\mathbf v \cdot \mathbf a_i\).

This means:
- The inverse is trivial to compute (just transpose).
- The transformation does not collapse space (determinant is non-zero).
- Projection is a simple dot product.
- The transformation preserves lengths and angles (it is an isometry).

In data science, wherever possible, data transformations use orthonormal bases so that these nice properties hold.
