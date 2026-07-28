---
weight: 60
title: "Abstract Vector Spaces"
draft: false
prerequisites:
  - docs/math/linear-algebra/eigenvectors-deep-dive
  - docs/math/linear-algebra/linear-transformations-and-matrices
---

# Abstract Vector Spaces

## What Are Vectors, Really?

Are vectors fundamentally arrows in space described by coordinates for convenience? Or pairs of numbers conveniently visualized as arrows? Both are just manifestations of something deeper.

Core concepts like determinants and eigenvectors are indifferent to coordinate choices. The determinant measures area scaling regardless of basis. Eigenvectors are vectors that stay on their span regardless of how you describe them. These properties are inherently **spatial**, not numerical.

## Functions as Vectors

Functions behave like vectors: they can be added and scaled.

- **Adding functions** \((f + g)(x) = f(x) + g(x)\) -- like adding vectors coordinate by coordinate, but with infinitely many coordinates.
- **Scaling functions** \((c \cdot f)(x) = c \cdot f(x)\) -- like scaling a vector.

Since vectors are defined by what you can do with them (add and scale), functions qualify.

## Linear Transformations of Functions

The formal definition of a linear transformation uses two properties:

1. **Additivity**: \(T(\mathbf v + \mathbf w) = T(\mathbf v) + T(\mathbf w)\)
2. **Scaling**: \(T(c \mathbf v) = c \, T(\mathbf v)\)

These properties apply beyond arrows in space. The derivative from calculus, for example, is a linear transformation on functions:

\[
\frac{d}{dx}(f + g) = \frac{df}{dx} + \frac{dg}{dx}, \qquad
\frac{d}{dx}(c f) = c \, \frac{df}{dx}
\]

### The Derivative as a Matrix

Restrict to polynomials with basis \(\{1, x, x^2, x^3, \dots\}\). A polynomial like \(x^3 + 5x^2 + 4x + 5\) has coordinates \((5, 4, 5, 1, 0, 0, \dots)\).

The derivative is an infinite matrix:

\[
\frac{d}{dx} \begin{pmatrix}5\\4\\5\\1\\0\\\vdots\end{pmatrix}
= \begin{bmatrix}
0 & 1 & 0 & 0 & \cdots\\
0 & 0 & 2 & 0 & \cdots\\
0 & 0 & 0 & 3 & \cdots\\
0 & 0 & 0 & 0 & \cdots\\
\vdots & \vdots & \vdots & \vdots & \ddots
\end{bmatrix}
\begin{pmatrix}5\\4\\5\\1\\0\\\vdots\end{pmatrix}
= \begin{pmatrix}4\\10\\3\\0\\\vdots\end{pmatrix}
\]

The result \((4, 10, 3, 0, \dots)\) corresponds to \(4 + 10x + 3x^2\), which is the derivative.

Each column is found by taking the derivative of the corresponding basis function and recording its coordinates. This works because the derivative is linear.

## Vector Spaces and Axioms

A **vector space** is any set where addition and scalar multiplication obey eight axioms (closure, associativity, commutativity, identity elements, inverses, distributivity). These axioms form an interface: prove your results using only the axioms, and they apply to every vector space -- arrows, lists of numbers, functions, or anything else that satisfies the rules.

Concepts like dot products, eigenvectors, and linear transformations all have analogs in function spaces, often under different names (inner products, eigenfunctions).

## Why It Matters

Linear algebra's power lies in its generality. The same tools that describe rotations in 3D space also describe derivatives of polynomials, quantum mechanical operators, and countless other systems. Understanding the abstract scaffolding lets you recognize linear structure wherever it appears and apply the full machinery of the subject.
