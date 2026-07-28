---
weight: 27.5
title: "Cramer's Rule"
draft: false
prerequisites:
  - docs/math/linear-algebra/determinant
  - docs/math/linear-algebra/inverses-column-space-null-space
---

# Cramer's Rule

## Hook: Solving Systems with Determinants

Given a linear system \(A\mathbf x = \mathbf v\) with a non-zero determinant, there is a clever formula for each coordinate of \(\mathbf x\) using determinants alone. It is not the fastest method (Gaussian elimination wins there), but it reveals a beautiful geometric connection between determinants and linear systems.

## Coordinates as Areas

For a 2D vector \(\mathbf x = (x, y)\), the coordinates can be interpreted as signed areas:

- The parallelogram spanned by \(\mathbf î\) and \(\mathbf x\) has area \(y\) (base 1, height = y-coordinate).
- The parallelogram spanned by \(\mathbf x\) and \(\mathbf ĵ\) has area \(x\).

In 3D, the parallelepiped spanned by \(\mathbf î\), \(\mathbf ĵ\), and \(\mathbf x\) has volume \(z\). Similarly for the other coordinates using the other pairs of basis vectors.

## What Happens After a Transformation?

Under a linear transformation \(A\), every area scales by \(\det(A)\). So:

- The transformed parallelogram spanned by \(A\mathbf î\) (first column of \(A\)) and \(A\mathbf x = \mathbf v\) has area \(\det(A) \cdot y\).

But we can compute this area directly from the known outputs: it is the determinant of the matrix whose first column is \(A\mathbf î\) and second column is \(\mathbf v\).

## Cramer's Rule for 2×2

For a 2×2 system:

\[
\begin{bmatrix}a & b\\c & d\end{bmatrix}
\begin{bmatrix}x\\y\end{bmatrix}
= \begin{bmatrix}v_1\\v_2\end{bmatrix}
\]

\[
x = \frac{\det\begin{bmatrix}v_1 & b\\v_2 & d\end{bmatrix}}{\det\begin{bmatrix}a & b\\c & d\end{bmatrix}}
\qquad
y = \frac{\det\begin{bmatrix}a & v_1\\c & v_2\end{bmatrix}}{\det\begin{bmatrix}a & b\\c & d\end{bmatrix}}
\]

The numerator replaces the column corresponding to the target variable with the output vector \(\mathbf v\), then takes the determinant. The denominator is the determinant of \(A\).

### Example

\[
\begin{bmatrix}1 & 2\\1 & -1\end{bmatrix}
\begin{bmatrix}x\\y\end{bmatrix}
= \begin{bmatrix}4\\-2\end{bmatrix}
\]

\[
\det(A) = 1(-1) - 2(1) = -3
\]

\[
x = \frac{\det\begin{bmatrix}4 & 2\\-2 & -1\end{bmatrix}}{-3}
= \frac{4(-1) - 2(-2)}{-3}
= \frac{-4 + 4}{-3} = 0
\]

\[
y = \frac{\det\begin{bmatrix}1 & 4\\1 & -2\end{bmatrix}}{-3}
= \frac{1(-2) - 4(1)}{-3}
= \frac{-2 - 4}{-3} = 2
\]

## General Case

For an \(n \times n\) system \(A\mathbf x = \mathbf v\), the \(k\)-th coordinate of \(\mathbf x\) is:

\[
x_k = \frac{\det(A_k)}{\det(A)}
\]

where \(A_k\) is the matrix formed by replacing the \(k\)-th column of \(A\) with \(\mathbf v\).

## Why It Works Geometrically

The mystery input vector \(\mathbf x\) forms a parallelepiped with all basis vectors except the \(k\)-th one. The (signed) volume of that shape is exactly \(x_k\). After the transformation \(A\), that same shape becomes the parallelepiped spanned by the columns of \(A_k\) (all columns of \(A\) except the \(k\)-th one replaced by \(\mathbf v\)). Its volume equals \(\det(A_k)\). Since all volumes scale by \(\det(A)\), we have:

\[
\det(A_k) = \det(A) \cdot x_k
\]

Hence \(x_k = \det(A_k) / \det(A)\).

## A Special Case: Orthonormal Matrices

If \(A\) is orthonormal (rotations, reflections), \(\det(A) = \pm 1\) and dot products are preserved. Then Cramer's rule reduces to simply taking the dot product of \(\mathbf v\) with each column of \(A\) -- since replacing a column with \(\mathbf v\) and taking the determinant is equivalent to computing that dot product when the columns are orthonormal.
