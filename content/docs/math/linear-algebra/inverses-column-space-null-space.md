---
weight: 27
title: "Inverse Matrices, Column Space, Rank, and Null Space"
draft: false
prerequisites:
  - docs/math/linear-algebra/determinant
  - docs/math/linear-algebra/matrix-multiplication-as-composition
---

# Inverse Matrices, Column Space, Rank, and Null Space

## Hook: Solving Systems of Equations as Geometric Problems

A system of linear equations -- where each variable is only scaled by constants and added to others -- can be packaged as a single matrix equation:

\[
A \mathbf x = \mathbf v
\]

Here \(A\) is the matrix of coefficients, \(\mathbf x\) is the vector of unknowns, and \(\mathbf v\) is the vector of constants. Geometrically, we are looking for a vector \(\mathbf x\) that, after applying the transformation \(A\), lands on \(\mathbf v\).

## The Inverse: Rewinding the Transformation

When \(A\) has a non-zero determinant, the transformation does not squish space into a lower dimension. Every output \(\mathbf v\) comes from exactly one input \(\mathbf x\). To find \(\mathbf x\), play the transformation in reverse -- apply the **inverse** of \(A\), denoted \(A^{-1}\).

The inverse is the unique transformation with the property:

\[
A^{-1} A = I
\]

where \(I\) is the **identity matrix** (î and ĵ unmoved, ones on the diagonal). Applying \(A\) then \(A^{-1}\) leaves everything unchanged.

The solution to \(A\mathbf x = \mathbf v\) is:

\[
\mathbf x = A^{-1} \mathbf v
\]

Geometrically, follow \(\mathbf v\) backwards through the transformation.

For example, if \(A\) rotates 90° counterclockwise, \(A^{-1}\) rotates 90° clockwise. If \(A\) is a rightward shear, \(A^{-1}\) is a leftward shear.

## Zero Determinant: No Inverse

When \(\det(A) = 0\), space is squished onto a lower dimension -- a line in 2D, or a plane or line in 3D. You cannot unsquish a line back into a plane with a function (a single input cannot map to a whole line of outputs). No inverse exists.

A solution may still exist, but only if \(\mathbf v\) happens to lie in the squished output space.

## The Column Space

The **column space** of \(A\) is the set of all possible outputs \(A\mathbf x\). It is the span of the columns of \(A\) -- where the basis vectors land.

If a 3×3 transformation squishes space onto a plane, the column space is that plane. If it squishes onto a line, the column space is that line. A solution \(A\mathbf x = \mathbf v\) exists exactly when \(\mathbf v\) is in the column space.

## Rank

The **rank** of a matrix is the number of dimensions of its column space:

- Rank 2 (2D): the output fills the plane. The best possible for a 2×2 matrix.
- Rank 3 (3D): the output fills all of 3D space. Full rank.
- Rank 1: the output is a line (e.g., a 3D transformation squishing onto a line).
- Rank 0: the output is just the origin.

When the rank equals the number of columns, the matrix is **full rank** -- the transformation preserves all dimensions and \(\det \neq 0\).

## The Null Space

The **null space** (or **kernel**) of \(A\) is the set of all vectors that land on the zero vector:

\[
A \mathbf x = \mathbf 0
\]

- For a full-rank transformation, only the zero vector lands on zero.
- When space is squished, entire lines or planes of vectors get crushed to the origin.

In 2D: a transformation squishing onto a line sends a whole line of vectors (in a different direction) to zero.
In 3D: squishing onto a plane sends a line to zero; squishing onto a line sends a plane to zero.

The null space gives all solutions to the homogeneous equation \(A\mathbf x = \mathbf 0\). More generally, for any solution to \(A\mathbf x = \mathbf v\), adding any vector from the null space gives another solution.

## Why It Matters

These concepts form the foundation for solving linear systems, which appear everywhere in engineering, physics, computer graphics, and data science. The rank tells you how much information survives a transformation. The null space tells you the ambiguity in your solutions.
