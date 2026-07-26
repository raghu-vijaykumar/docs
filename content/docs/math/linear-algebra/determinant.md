---
weight: 25
title: "The Determinant"
draft: false
prerequisites:
  - docs/math/linear-algebra/linear-transformations-and-matrices
  - docs/math/linear-algebra/matrix-multiplication-as-composition
---

# The Determinant

## Hook: How Much Does a Transformation Stretch or Squish?

Some linear transformations stretch space out; others squish it in. The **determinant** measures exactly how much a transformation changes areas (in 2D) or volumes (in 3D).

## Intuition: The Unit Square Test

Consider the matrix \(\begin{bmatrix}3 & 0\\0 & 2\end{bmatrix}\). It scales î by 3 and ĵ by 2. The unit square (edges on î and ĵ) becomes a 3×2 rectangle. Its area changed from 1 to 6 -- the transformation scaled area by a factor of **6**.

Compare that to a shear \(\begin{bmatrix}1 & 1\\0 & 1\end{bmatrix}\). The unit square becomes a parallelogram with the same base and height, so its area is still 1 -- the determinant is **1**.

What matters is that knowing how the unit square's area changes tells you how **any** region's area changes. Grid squares all scale by the same factor, and any shape can be approximated by tiny grid squares.

## Determinant in 2D

For a 2×2 matrix \(\begin{bmatrix}a & b\\c & d\end{bmatrix}\):

\[
\det\left(\begin{bmatrix}a & b\\c & d\end{bmatrix}\right) = ad - bc
\]

| Determinant | What it means |
|-------------|---------------|
| 6 | Areas scaled by 6× |
| 1 | Areas unchanged |
| ½ | Areas halved |
| 0 | Space squished onto a line or point (columns linearly dependent) |
| Negative | Orientation flipped; absolute value still gives area scaling |

### Orientation and Negative Determinants

When a transformation flips space over (like turning a sheet of paper over), the determinant is negative. The sign tracks orientation: if ĵ ends up on the opposite side of î from where it started, the orientation has inverted. The absolute value still tells you the area scaling factor.

For example, \(\begin{bmatrix}1 & 2\\1 & -1\end{bmatrix}\) has determinant \(-3\): space flips over and areas scale by 3.

Zero determinant is the boundary -- as î and ĵ get closer together, area shrinks toward zero, passes through zero when they line up, then becomes negative as they pass each other.

## Determinant in 3D

In three dimensions, the determinant measures the scaling factor for **volumes**. Focus on the 1×1×1 cube whose edges rest on î, ĵ, and k̂. After the transformation, it becomes a **parallelepiped**, and the determinant is its volume.

A determinant of 0 means space is squished onto something with zero volume -- a plane, a line, or a point. This happens precisely when the columns of the matrix are linearly dependent.

### Orientation in 3D

Use the right-hand rule: point your forefinger along î, middle finger along ĵ, thumb along k̂. If you can still do this after the transformation, the determinant is positive. If it only works with your left hand, orientation has flipped and the determinant is negative.

## Determinant of a Product

If you multiply two matrices, the determinant of the product equals the product of the determinants:

\[
\det(AB) = \det(A)\,\det(B)
\]

Geometrically, this is immediate: applying transformation B then A scales areas first by \(\det(B)\) then by \(\det(A)\). The combined scaling factor is the product.

## Why It Matters

The determinant is a quick check for whether a transformation squishes space into a lower dimension (\(\det = 0\)), which means the transformation is not invertible. This connects directly to linear systems of equations: a zero determinant means the system either has no solutions or infinitely many.
