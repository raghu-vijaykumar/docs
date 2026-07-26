---
weight: 28
title: "Non-Square Matrices as Transformations Between Dimensions"
draft: false
prerequisites:
  - docs/math/linear-algebra/inverses-column-space-null-space
---

# Non-Square Matrices as Transformations Between Dimensions

## Hook: What About Transformations That Change Dimension?

So far every transformation has been square -- 2D to 2D, 3D to 3D. But matrices can have different numbers of rows and columns, representing transformations between spaces of different dimensions. A 3×2 matrix maps 2D vectors to 3D vectors; a 2×3 matrix maps 3D vectors to 2D vectors.

## 2D to 3D: A 3×2 Matrix

A 3×2 matrix has three rows and two columns. The two columns tell you where the two input basis vectors land, and each landing spot requires three coordinates (the three rows).

\[
\begin{bmatrix}
2 & 0\\
-1 & 1\\
-2 & 1
\end{bmatrix}
\]

Here î lands at \((2, -1, -2)\) and ĵ lands at \((0, 1, 1)\). The output lives in 3D space, but the column space is a 2D plane through the origin -- the span of those two vectors. Since the column space has the same number of dimensions as the input space, this matrix is **full rank**.

Geometrically: a flat 2D grid maps onto a tilted plane slicing through 3D space.

To apply the transformation to a 2D vector \((x, y)\), scale each column by the corresponding coordinate and add as usual:

\[
x \begin{bmatrix}2\\-1\\-2\end{bmatrix}
+ y \begin{bmatrix}0\\1\\1\end{bmatrix}
= \begin{bmatrix}2x + 0y\\-1x + 1y\\-2x + 1y\end{bmatrix}
\]

## 3D to 2D: A 2×3 Matrix

A 2×3 matrix has two rows and three columns. The three columns indicate a 3D input space; the two rows mean each landing spot is described with two coordinates -- the output is 2D.

\[
\begin{bmatrix}
a & b & c\\
d & e & f
\end{bmatrix}
\]

Three basis vectors (î, ĵ, k̂) each land somewhere in 2D space. The transformation squishes all of 3D space onto a plane. This is always a lossy compression -- the determinant is not defined for non-square matrices, but the rank tells you how many output dimensions survive.

## 2D to 1D: A 1×2 Matrix

A 1×2 matrix has one row and two columns. It maps 2D vectors onto the number line (1D space):

\[
\begin{bmatrix} a & b \end{bmatrix}
\]

Each column has just a single entry -- the number that basis vector lands at. For a vector \((x, y)\):

\[
x \cdot a + y \cdot b
\]

This is a **linear functional**: it takes a 2D vector and produces a single number. It has a close relationship with the dot product, which will be explored next.

Geometrically, evenly spaced dots on a line in the input remain evenly spaced when mapped onto the number line.

## Key Intuition

- **Columns** = number of input dimensions (basis vectors).
- **Rows** = number of output dimensions (coordinates per landing spot).
- The **column space** lives in the output space; its dimension is the rank.
- Full rank means the column space dimension equals the input dimension.
