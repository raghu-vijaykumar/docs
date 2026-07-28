---
weight: 30.9
title: "Change of Basis"
draft: false
prerequisites:
  - docs/math/linear-algebra/linear-transformations-and-matrices
  - docs/math/linear-algebra/matrix-multiplication-as-composition
  - docs/math/linear-algebra/inverses-column-space-null-space
---

# Change of Basis

## Different Coordinate Systems, Same Space

The standard basis vectors \(\mathbf î\) and \(\mathbf ĵ\) are not the only choice. Your friend Jennifer might use a different basis -- say \(\mathbf b_1 = (2, 1)\) and \(\mathbf b_2 = (-1, 1)\) in our language. From her perspective, those vectors have coordinates \((1, 0)\) and \((0, 1)\); they define what coordinates mean in her world.

The same geometric vector gets different coordinates in different systems. A vector we describe as \((3, 2)\) might be \((5/3, 1/3)\) in Jennifer's system.

## Translating Vectors: Her Language to Ours

The matrix whose columns are Jennifer's basis vectors (written in our coordinates) converts vectors from her language to ours:

\[
\begin{bmatrix}
2 & -1\\
1 & 1
\end{bmatrix}
\]

If Jennifer describes a vector as \((-1, 2)\), what is it in our system?

\[
(-1) \begin{bmatrix}2\\1\end{bmatrix}
+ 2 \begin{bmatrix}-1\\1\end{bmatrix}
= \begin{bmatrix}-4\\1\end{bmatrix}
\]

This is matrix-vector multiplication: the change of basis matrix times her coordinates gives the vector in our coordinates.

### Geometric Intuition

This matrix transforms our basis vectors (\(\mathbf î, \mathbf ĵ\)) to Jennifer's basis vectors (\(\mathbf b_1, \mathbf b_2\)). So it takes our misconception of what Jennifer means (using her coordinates but in our system) and corrects it to the actual vector she intended.

## Translating Vectors: Ours to Her Language

To go the other way, use the inverse of the change of basis matrix:

\[
\begin{bmatrix}
2 & -1\\
1 & 1
\end{bmatrix}^{-1}
= \begin{bmatrix}
1/3 & 1/3\\
-1/3 & 2/3
\end{bmatrix}
\]

Multiplying our coordinates by this inverse gives the coordinates in her system.

## Translating Transformations

Suppose we have a transformation -- say a 90° rotation -- represented by a matrix \(M\) in our system. How would Jennifer describe the same transformation?

She needs a matrix that:
1. Takes a vector in her language,
2. Applies the rotation,
3. Gives the result in her language.

This is accomplished by the composition:

\[
A^{-1} M A
\]

where \(A\) is the change of basis matrix (her basis vectors as columns).

- \(A\) translates her vector to our language.
- \(M\) applies the rotation.
- \(A^{-1}\) translates the result back to her language.

### Worked Example

With our rotation matrix \(M = \begin{bmatrix}0 & -1\\1 & 0\end{bmatrix}\) and \(A = \begin{bmatrix}2 & -1\\1 & 1\end{bmatrix}\):

\[
A^{-1} M A
= \begin{bmatrix}
1/3 & 1/3\\
-1/3 & 2/3
\end{bmatrix}
\begin{bmatrix}
0 & -1\\
1 & 0
\end{bmatrix}
\begin{bmatrix}
2 & -1\\
1 & 1
\end{bmatrix}
= \begin{bmatrix}
1/3 & -2/3\\
5/3 & -1/3
\end{bmatrix}
\]

Jennifer can multiply this matrix by any vector in her coordinates to get the rotated version in her coordinates.

## The Pattern \(A^{-1} M A\)

Expressions of the form \(A^{-1} M A\) represent a mathematical sort of empathy. The middle matrix \(M\) is a transformation as you see it; the outer matrices shift perspective to how someone else sees it. The full product describes the same transformation but in a different coordinate system.

This pattern is central to eigenvectors and eigenvalues, where the goal is to find a basis in which a transformation becomes a simple scaling -- diagonalization.
