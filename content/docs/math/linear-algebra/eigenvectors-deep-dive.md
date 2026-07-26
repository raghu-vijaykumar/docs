---
weight: 40
title: "Eigenvectors Deep Dive"
draft: false
prerequisites:
  - docs/math/linear-algebra/eigenvectors-and-eigenvalues
  - docs/math/linear-algebra/linear-transformations-and-matrices
---

# Eigenvectors Deep Dive

Eigenvectors and eigenvalues only really make sense if you have a solid visual understanding for the topics that precede them: matrices as linear transformations, determinants, linear systems of equations, and change of basis.

## Finding Eigenvectors Geometrically

Consider a linear transformation that moves î to (3, 0) and ĵ to (1, 2). Its matrix:

\[
\begin{bmatrix}3 & 1\\0 & 2\end{bmatrix}
\]

Most vectors get knocked off their span. But some special ones remain on their own span -- the transformation only stretches or squishes them, like a scalar.

- **î** stays on the x-axis, stretched by a factor of 3.
- The vector (-1, 1) stays on its diagonal span, stretched by a factor of 2.

These are the **eigenvectors**. The factors 3 and 2 are their **eigenvalues**.

Eigenvalues can be negative (flip + squish) or fractional (squish only). The key property: the direction is unchanged.

## Why Eigenvectors Matter

Consider a 3D rotation. If you find an eigenvector -- a vector that remains on its own span -- you have found the **axis of rotation**. The corresponding eigenvalue is 1 (rotations don't stretch). The full 3×3 matrix reduces to an axis and an angle.

## The Symbolic Definition

\[
A \mathbf v = \lambda \mathbf v
\]

Where \(A\) is the matrix (transformation), \(\mathbf v\) is the eigenvector, and \(\lambda\) is the eigenvalue.

To solve, rewrite the right side as matrix multiplication:

\[
A \mathbf v = (\lambda I) \mathbf v
\]

Then subtract:

\[
(A - \lambda I) \mathbf v = \mathbf 0
\]

For a non-zero eigenvector \(\mathbf v\) to exist, the matrix \(A - \lambda I\) must squish space into a lower dimension, meaning its determinant must be zero:

\[
\det(A - \lambda I) = 0
\]

## Characteristic Polynomial

For \(A = \begin{bmatrix}3 & 1\\0 & 2\end{bmatrix}\):

\[
\det\left(\begin{bmatrix}3-\lambda & 1\\0 & 2-\lambda\end{bmatrix}\right)
= (3-\lambda)(2-\lambda) = 0
\]

Eigenvalues: \(\lambda = 2\) and \(\lambda = 3\).

For \(\lambda = 2\), solve \((A - 2I)\mathbf v = \mathbf 0\):

\[
\begin{bmatrix}1 & 1\\0 & 0\end{bmatrix} \mathbf v = \mathbf 0
\]

The solutions are all vectors on the diagonal line spanned by (-1, 1) -- the eigenvectors for \(\lambda = 2\).

## Cases With No Eigenvectors

A 90° rotation matrix \(\begin{bmatrix}0 & -1\\1 & 0\end{bmatrix}\):

\[
\det\left(\begin{bmatrix}-\lambda & -1\\1 & -\lambda\end{bmatrix}\right)
= \lambda^2 + 1 = 0
\]

The roots are \(i\) and \(-i\). No real eigenvalues → no real eigenvectors.

## Shear Example

A shear matrix \(\begin{bmatrix}1 & 1\\0 & 1\end{bmatrix}\):

\[
\det\left(\begin{bmatrix}1-\lambda & 1\\0 & 1-\lambda\end{bmatrix}\right)
= (1-\lambda)^2 = 0
\]

Only eigenvalue \(\lambda = 1\). Only vectors on the x-axis are eigenvectors.

## Scaling Example

A uniform scaling \(\begin{bmatrix}2 & 0\\0 & 2\end{bmatrix}\) has \(\lambda = 2\), and **every vector** is an eigenvector.

## Diagonalization

### The Problem: Repeated Matrix Powers

Imagine a transformation matrix \(T\) representing the change in position of a particle after one time step. After \(n\) steps:

\[
\mathbf v_n = T^{\,n} \mathbf v_0
\]

If \(n \approx 1.2\) million (e.g., two weeks of 1-second steps), applying matrix multiplication 1.2 million times is expensive.

### Diagonal Matrices Are Easy

A **diagonal matrix** has non-zero entries only on the leading diagonal:

\[
D = \begin{bmatrix}a & 0 & 0\\0 & b & 0\\0 & 0 & c\end{bmatrix}
\]

Raising it to a power is trivial -- raise each diagonal entry:

\[
D^{\,n} = \begin{bmatrix}a^n & 0 & 0\\0 & b^n & 0\\0 & 0 & c^{\,n}\end{bmatrix}
\]

### The Eigenbasis Solution

If a transformation has enough eigenvectors to span the space, arrange them as columns of a matrix \(C\) (the change-of-basis matrix to the eigenbasis). In that coordinate system, the transformation becomes a pure scaling -- a diagonal matrix \(D\) with eigenvalues on the diagonal:

\[
T = C D C^{-1}
\]

Now raise \(T\) to the \(n\)-th power:

\[
\begin{aligned}
T^{\,n} &= (C D C^{-1})(C D C^{-1})\cdots(C D C^{-1}) \\
&= C D (C^{-1} C) D (C^{-1} C) \cdots D C^{-1} \\
&= C D^{\,n} C^{-1}
\end{aligned}
\]

The \(C^{-1} C\) pairs cancel, leaving:

\[
\boxed{T^{\,n} = C D^{\,n} C^{-1}}
\]

### What It Buys You

For large \(n\), the cost shifts from repeated matrix multiplication to:
1. Find eigenvalues/eigenvectors (done once).
2. Raise diagonal entries to \(n\) (trivial).
3. Multiply \(C D^{\,n} C^{-1}\) (three matrix multiplications).

### Worked Example

Let \(T = \begin{bmatrix}1 & 1\\0 & 2\end{bmatrix}\). Its eigenpairs:

- \(\lambda = 1\) with eigenvector \((1, 0)\)
- \(\lambda = 2\) with eigenvector \((1, 1)\)

Build \(C = \begin{bmatrix}1 & 1\\0 & 1\end{bmatrix}\), \(C^{-1} = \begin{bmatrix}1 & -1\\0 & 1\end{bmatrix}\), \(D = \begin{bmatrix}1 & 0\\0 & 2\end{bmatrix}\).

Compute \(T^2\) via diagonalization:

\[
\begin{aligned}
T^2 &= C D^2 C^{-1} \\
&= \begin{bmatrix}1 & 1\\0 & 1\end{bmatrix}
\begin{bmatrix}1 & 0\\0 & 4\end{bmatrix}
\begin{bmatrix}1 & -1\\0 & 1\end{bmatrix} \\
&= \begin{bmatrix}1 & 1\\0 & 1\end{bmatrix}
\begin{bmatrix}1 & -1\\0 & 4\end{bmatrix} \\
&= \begin{bmatrix}1 & 3\\0 & 4\end{bmatrix}
\end{aligned}
\]

Applying directly: \(T^2 = \begin{bmatrix}1 & 1\\0 & 2\end{bmatrix}^2 = \begin{bmatrix}1 & 3\\0 & 4\end{bmatrix}\). Both approaches agree.

For this small 2D case the direct method is just as fast. The advantage of diagonalization grows with matrix size and exponent size.

### Limitations

Not all matrices are diagonalizable -- only those with a full set of linearly independent eigenvectors. A shear, for example, does not have enough eigenvectors to span the space.

## Summary

- Eigenvectors remain on their span; eigenvalues measure the scaling factor.
- Solve \(\det(A - \lambda I) = 0\) to find eigenvalues.
- Not all transformations have (real) eigenvectors.
- An **eigenbasis** diagonalizes a matrix via \(T = C D C^{-1}\), simplifying computations like powers of a matrix.
