---
weight: 20
title: "Matrix Multiplication as Composition"
draft: false
prerequisites:
  - docs/math/linear-algebra/linear-transformations-and-matrices
---

# Matrix Multiplication as Composition

## Recap

Linear transformations are functions with vectors as inputs and vectors as outputs. Visually, they smoosh space such that grid lines stay parallel and evenly spaced, and the origin remains fixed. A linear transformation is completely determined by where it takes the basis vectors (î and ĵ). Any vector with coordinates (x, y) is x times î plus y times ĵ, and after the transformation it lands on x times the transformed î plus y times the transformed ĵ. The coordinates of where î and ĵ land become the columns of a matrix, and matrix-vector multiplication is the sum of the scaled columns.

## Composing Transformations

Oftentimes you want to describe the effect of applying one transformation and then another. For example, first rotate the plane 90° counterclockwise, then apply a shear. The overall effect from start to finish is another linear transformation, called the **composition** of the two separate transformations.

This new transformation can be described with its own matrix by following î and ĵ. In the rotation-then-shear example:

- î ultimately lands at (1, 1) -- first column of the composition matrix
- ĵ ultimately lands at (-1, 0) -- second column of the composition matrix

This new matrix captures the overall effect as a single action rather than two successive ones.

If you take a vector and pump it through the rotation then the shear, the long way is to first multiply by the rotation matrix, then multiply the result by the shear matrix. The result should be the same as multiplying the composition matrix by that vector. This new matrix is naturally called the **product** of the original two matrices.

Always remember: multiplying two matrices has the geometric meaning of applying one transformation then another. The composition is read **right to left** -- you first apply the transformation represented by the matrix on the right, then the one on the left. This stems from function notation.

## Computing the Product

Suppose we have:

\[
M_1 = \begin{bmatrix}1 & -2\\1 & 0\end{bmatrix}, \qquad
M_2 = \begin{bmatrix}0 & 2\\1 & 0\end{bmatrix}
\]

The total effect of applying \(M_1\) then \(M_2\):

1. **Where does î go?** After \(M_1\), î lands at (1, 1) (first column of \(M_1\)). Apply \(M_2\) to that vector:

\[
\begin{bmatrix}0 & 2\\1 & 0\end{bmatrix}
\begin{bmatrix}1\\1\end{bmatrix}
= \begin{bmatrix}2\\1\end{bmatrix}
\]

This becomes the first column of the composition matrix.

2. **Where does ĵ go?** After \(M_1\), ĵ lands at (-2, 0) (second column of \(M_1\)). Apply \(M_2\):

\[
\begin{bmatrix}0 & 2\\1 & 0\end{bmatrix}
\begin{bmatrix}-2\\0\end{bmatrix}
= \begin{bmatrix}0\\-2\end{bmatrix}
\]

This becomes the second column.

In general, with variable entries:

\[
\begin{bmatrix}a & b\\c & d\end{bmatrix}
\begin{bmatrix}e & f\\g & h\end{bmatrix}
\]

The first column of the composition equals the left matrix times the first column of the right matrix:

\[
\begin{bmatrix}a & b\\c & d\end{bmatrix}
\begin{bmatrix}e\\g\end{bmatrix}
= \begin{bmatrix}ae + bg\\ce + dg\end{bmatrix}
\]

The second column equals the left matrix times the second column of the right matrix:

\[
\begin{bmatrix}a & b\\c & d\end{bmatrix}
\begin{bmatrix}f\\h\end{bmatrix}
= \begin{bmatrix}af + bh\\cf + dh\end{bmatrix}
\]

So the composition matrix is:

\[
\begin{bmatrix}ae + bg & af + bh\\ce + dg & cf + dh\end{bmatrix}
\]

## Order Matters

Take a shear (î fixed, ĵ moves right) and a 90° rotation:

- **Shear then rotate**: î ends up at (0, 1), ĵ at (-1, 1) -- both pointing close together.
- **Rotate then shear**: î ends up at (1, 1), ĵ at (-1, 0) -- pointing farther apart.

The overall effect is clearly different. Matrix multiplication is **not commutative**.

## Associativity

If you have three matrices A, B, C, it does not matter whether you compute (AB)C or A(BC). The result is the same.

Thinking in terms of transformations makes this trivial: applying C, then B, then A is the same sequence of operations regardless of how you group the parentheses. There is nothing to prove -- you are just applying the same three things one after another in the same order.

## Summary

- **Matrix multiplication** represents applying one transformation after another.
- The product is read **right to left** (first apply the right matrix, then the left).
- The columns of the product come from multiplying the left matrix by each column of the right matrix.
- **Order matters** -- matrix multiplication is not commutative.
- **Associativity** is trivial when understood as composing transformations.
