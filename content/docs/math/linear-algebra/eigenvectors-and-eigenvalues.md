---
weight: 30
title: "Eigenvectors & Eigenvalues"
draft: false
prerequisites:
  - docs/math/linear-algebra/matrix-multiplication-as-composition
  - docs/math/linear-algebra/determinant
---

# Eigenvectors & Eigenvalues

The word *eigen* is most usefully translated from the German as meaning *characteristic*. An eigenproblem is about finding the characteristic properties of something.

## The Geometric Idea

Linear transformations (scalings, rotations, shears) affect different vectors differently. Visualize what happens by drawing a square centered at the origin and applying the transformation:

- A vertical scaling turns the square into a rectangle.
- A horizontal shear distorts it into a parallelogram.

Some vectors end up lying on the same line (the same **span**) they started on; others do not.

### Vertical Scaling Example

Take a vertical scaling by a factor of 2. Mark three vectors on the square:

- **Horizontal (green):** unchanged -- same direction, same length.
- **Vertical (pink):** still pointing in the same direction, but its length doubled.
- **Diagonal (orange):** angle changed, length changed.

Only the horizontal and vertical vectors keep their original direction. These are **characteristic** of this particular transform -- they are its **eigenvectors**.

- The horizontal vector's length was unchanged → **eigenvalue 1**.
- The vertical vector doubled in length → **eigenvalue 2**.

In 2D eigenproblems, take a transformation, find the vectors that still lie on their original span, and measure how much their length changed. That is what eigenvectors and eigenvalues are.

### Pure Shear

In a pure shear (no scaling or rotation, area preserved):

- Only the horizontal vector still lies along its original span.
- All other vectors are shifted off their span.

One eigenvector (horizontal), eigenvalue 1.

### Rotation

A rotation changes the direction of every vector. No vectors remain on their original span.

**No eigenvectors.**

## Generalization

The concept is the same in three or more dimensions: find the vectors whose direction is unchanged (up to scaling) by the transformation, and measure the scaling factor.

## Summary

- **Eigenvectors** of a transformation are non-zero vectors that remain on their original span (direction unchanged).
- **Eigenvalues** are the factor by which the eigenvector is scaled.
- Not every transformation has eigenvectors (e.g., rotation).
- A transformation can have one, two, or many eigenvectors depending on its structure.
