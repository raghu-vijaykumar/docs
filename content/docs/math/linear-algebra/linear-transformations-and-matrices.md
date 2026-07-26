---
weight: 10
title: "Linear Transformations & Matrices"
draft: false
prerequisites:
  - docs/math/linear-algebra
  - docs/math/linear-algebra/basis-vectors-and-linear-combinations
---

# Linear Transformations & Matrices

---

If I had to choose just one topic that makes all of the others in linear algebra start to click, and which too often goes unlearned the first time a student takes linear algebra, it would be this one: the idea of a linear transformation and its relation to matrices. We start with transformations in two dimensions, then extend the same intuition to three dimensions.

## What Is a Linear Transformation?

A **transformation** is essentially a fancy word for function -- something that takes in inputs and spits out an output for each one. In linear algebra, we think about transformations that take in some vector and spit out another vector.

Why use "transformation" instead of "function"? It suggests a certain way to visualize this input-output relation using movement. If a transformation takes some input vector to some output vector, we imagine that input vector moving over to the output vector. To understand the transformation as a whole, we might imagine watching every possible input vector move to its corresponding output vector.

Since it gets crowded to think about all vectors at once as arrows, a nice trick is to conceptualize each vector not as an arrow but as a single point -- the point where its tip sits. That way, to think about a transformation taking every possible input vector to some output vector, we watch every point in space moving to some other point. For transformations in two dimensions, this is done with all the points on an infinite grid.

## What Makes a Transformation Linear?

Visually speaking, a transformation is **linear** if it has two properties:

1. **All lines must remain lines** (without getting curved).
2. **The origin must remain fixed in place.**

A transformation that curves lines is not linear. A transformation that moves the origin is not linear. Even if grid lines look straight, a diagonal line might still get curved -- that is also not linear.

In general, think of linear transformations as **keeping grid lines parallel and evenly spaced**.

## Describing Transformations with Matrices

How do you describe these transformations numerically? You only need to record where the two basis vectors, **î** and **ĵ**, each land. Everything else follows from that.

Consider a vector **v** with coordinates (-1, 2), meaning **v** = (-1)**î** + (2)**ĵ**. When a transformation is applied, the property that grid lines remain parallel and evenly spaced has a crucial consequence: the place where **v** lands will be (-1) times where **î** landed plus (2) times where **ĵ** landed. In other words, it started as a certain linear combination of **î** and **ĵ**, and it ends up as that same linear combination of where those two vectors landed.

This means you can deduce where any vector must go based only on where **î** and **ĵ** each land.

If **î** lands on coordinates (1, -2) and **ĵ** lands on (3, 0), then the vector **v** = (-1, 2) lands at:

\[
(-1) \begin{bmatrix}1\\-2\end{bmatrix} + (2) \begin{bmatrix}3\\0\end{bmatrix}
= \begin{bmatrix}-1 + 6\\2 + 0\end{bmatrix}
= \begin{bmatrix}5\\2\end{bmatrix}
\]

For a general vector with coordinates (x, y):

\[
x \begin{bmatrix}1\\-2\end{bmatrix} + y \begin{bmatrix}3\\0\end{bmatrix}
= \begin{bmatrix}1x + 3y\\-2x + 0y\end{bmatrix}
\]

So a two-dimensional linear transformation is completely described by just four numbers: the two coordinates for where **î** lands, and the two coordinates for where **ĵ** lands.

## The Matrix

These coordinates are packaged into a 2×2 grid called a **matrix**, where the columns are the two special vectors where **î** and **ĵ** each land:

\[
\begin{bmatrix}1 & 3\\-2 & 0\end{bmatrix}
\]

Given a 2×2 matrix describing a linear transformation and some specific vector, you take the coordinates of the vector, multiply them by the corresponding columns of the matrix, then add the results.

In the most general case, with matrix entries a, b, c, d:

\[
\begin{bmatrix}a & b\\c & d\end{bmatrix}
\]

The first column (a, c) is where the first basis vector lands. The second column (b, d) is where the second basis vector lands. Applying this transformation to vector (x, y):

\[
x \begin{bmatrix}a\\c\end{bmatrix} + y \begin{bmatrix}b\\d\end{bmatrix}
= \begin{bmatrix}ax + by\\cx + dy\end{bmatrix}
\]

## Examples

### Rotation (90° counterclockwise)

**î** lands on (0, 1). **ĵ** lands on (-1, 0).

\[
\begin{bmatrix}0 & -1\\1 & 0\end{bmatrix}
\]

### Shear

**î** remains fixed at (1, 0). **ĵ** moves to (1, 1).

\[
\begin{bmatrix}1 & 1\\0 & 1\end{bmatrix}
\]

### Linear Dependence (Squish)

If the vectors where **î** and **ĵ** land are linearly dependent (one is a scaled version of the other), the transformation squishes all of 2D space onto the line where those two vectors sit.

## Three Dimensions

The same ideas extend seamlessly beyond flatland. Consider a linear transformation with three-dimensional vectors as inputs and three-dimensional vectors as outputs. It smooshes around all the points in 3D space, keeping grid lines parallel and evenly spaced, with the origin fixed.

There are three standard basis vectors: **î** (x-axis), **ĵ** (y-axis), and **k̂** (z-axis). The transformation is completely described by where each of these three lands. Their coordinates become the columns of a **3×3 matrix**:

\[
\begin{bmatrix}
a & b & c\\
d & e & f\\
g & h & i
\end{bmatrix}
\]

The first column \((a, d, g)\) is where **î** lands, the second column \((b, e, h)\) is where **ĵ** lands, and the third column \((c, f, i)\) is where **k̂** lands.

### Example: Rotation Around the Y-Axis

Rotating 90° around the y-axis:
- **î** moves to \((0, 0, -1)\) -- landing on the negative z-axis.
- **ĵ** stays at \((0, 1, 0)\) -- unmoved.
- **k̂** moves to \((1, 0, 0)\) -- landing on the x-axis.

\[
\begin{bmatrix}
0 & 0 & 1\\
0 & 1 & 0\\
-1 & 0 & 0
\end{bmatrix}
\]

### Matrix-Vector Multiplication in 3D

To find where a vector \((x, y, z)\) lands, scale each column by the corresponding coordinate and add:

\[
x \begin{bmatrix}a\\d\\g\end{bmatrix}
+ y \begin{bmatrix}b\\e\\h\end{bmatrix}
+ z \begin{bmatrix}c\\f\\i\end{bmatrix}
= \begin{bmatrix}
ax + by + cz\\
dx + ey + fz\\
gx + hy + iz
\end{bmatrix}
\]

### Composing 3D Transformations

Multiplying two 3×3 matrices works the same way as the 2D case: apply the transformation on the right, then the one on the left. This is especially useful in computer graphics and robotics, where complex 3D rotations are built from simpler ones.

## Summary

- **Linear transformations** move space so that grid lines remain parallel and evenly spaced, and the origin stays fixed.
- These transformations are described using only the coordinates of where each basis vector lands.
- **Matrices** give us a language to describe these transformations; the columns represent those coordinates.
- **Matrix-vector multiplication** is just a way to compute what that transformation does to a given vector.
- The same ideas extend to **three dimensions** with a third basis vector **k̂** and a 3×3 matrix.
