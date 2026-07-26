---
weight: 29
title: "Dot Products and Duality"
draft: false
prerequisites:
  - docs/math/linear-algebra/non-square-matrices
  - docs/math/linear-algebra/linear-transformations-and-matrices
---

# Dot Products and Duality

## Hook: What Does a Dot Product Really Mean?

Numerically, the dot product is simple: pair up coordinates, multiply, and add:

\[
\begin{bmatrix}1\\2\end{bmatrix} \cdot \begin{bmatrix}3\\4\end{bmatrix}
= 1(3) + 2(4) = 11
\]

But why does this computation relate to projection? And why is a 1×2 matrix -- a transformation that sends 2D vectors to numbers -- essentially the same thing as a vector?

## Geometric Interpretation

The dot product \(\mathbf v \cdot \mathbf w\) equals the length of the projection of \(\mathbf w\) onto \(\mathbf v\), multiplied by the length of \(\mathbf v\).

- Vectors pointing in the same direction: positive dot product.
- Perpendicular vectors: dot product is zero.
- Opposite directions: negative dot product.

Order does not matter: \(\mathbf v \cdot \mathbf w = \mathbf w \cdot \mathbf v\). Projecting \(\mathbf w\) onto \(\mathbf v\) and scaling by \(|\mathbf v|\) gives the same number as projecting \(\mathbf v\) onto \(\mathbf w\) and scaling by \(|\mathbf w|\). The symmetry becomes clear when both vectors have equal length, and scaling one vector affects both interpretations identically.

## Linear Transformations to 1D

A 1×2 matrix represents a linear transformation from 2D space to the number line. Each basis vector lands on a single number:

\[
\begin{bmatrix} a & b \end{bmatrix}
\]

Applying it to a vector \((x, y)\):

\[
x \cdot a + y \cdot b
\]

This is numerically identical to the dot product of \((x, y)\) with \((a, b)\). A 1×2 matrix is just a vector tipped on its side.

## The Deep Connection: Duality

Consider a diagonal copy of the number line embedded in 2D space, with 0 at the origin and 1 at the tip of a unit vector \(\hat{\mathbf u}\). Projecting any 2D vector onto this line defines a linear transformation to the number line.

Where does î land when projected onto this line? By symmetry, it equals the projection of \(\hat{\mathbf u}\) onto the x-axis, which is simply the x-coordinate of \(\hat{\mathbf u}\). Similarly, ĵ lands at the y-coordinate of \(\hat{\mathbf u}\).

So the 1×2 matrix describing this projection is just the coordinates of \(\hat{\mathbf u}\). Applying the transformation -- multiplying a vector by this matrix -- is computationally identical to taking the dot product with \(\hat{\mathbf u}\).

This means: **the dot product with a unit vector is the same as projecting onto the line spanned by that vector**.

For a non-unit vector scaled by some factor, multiplying the matrix by a vector projects onto the line and then scales by that factor.

## The Principle of Duality

Every linear transformation from a space to 1D corresponds to a unique vector in that space. Applying the transformation is the same as taking a dot product with that vector. The vector is the "dual" of the transformation, and vice versa.

This is why the dot product is deeper than a mechanical computation: it bridges two seemingly separate worlds -- vectors as geometric arrows and linear transformations to the number line.

## Why It Matters

Understanding dot products as linear transformations reveals the hidden structure behind many areas of math and physics. Projection, measuring alignment, and the interplay between vectors and transformations all flow from this duality.
