---
weight: 30.5
title: "Gram-Schmidt Process"
draft: false
prerequisites:
  - docs/math/linear-algebra/orthogonal-matrices
  - docs/math/linear-algebra/dot-products-and-duality
---

# Gram-Schmidt Process

## Problem: How to Build an Orthonormal Basis

Orthonormal bases make life easy -- the inverse is the transpose, projection is a dot product, and lengths are preserved. But given a set of linearly independent vectors that span a space, how do you actually construct one?

## The Process

Start with a set of linearly independent vectors \(\mathbf v_1, \mathbf v_2, \dots, \mathbf v_n\) that span the space. The Gram-Schmidt process constructs an orthonormal basis \(\mathbf e_1, \mathbf e_2, \dots, \mathbf e_n\) one vector at a time.

### Step 1: First Vector

Keep the first vector, but normalize it to unit length:

\[
\mathbf e_1 = \frac{\mathbf v_1}{\|\mathbf v_1\|}
\]

### Step 2: Second Vector

Decompose \(\mathbf v_2\) into two parts: a component parallel to \(\mathbf e_1\) and a component perpendicular to it.

The parallel component is the projection of \(\mathbf v_2\) onto \(\mathbf e_1\):

\[
(\mathbf v_2 \cdot \mathbf e_1)\,\mathbf e_1
\]

Subtract this from \(\mathbf v_2\) to get the perpendicular component \(\mathbf u_2\):

\[
\mathbf u_2 = \mathbf v_2 - (\mathbf v_2 \cdot \mathbf e_1)\,\mathbf e_1
\]

Then normalize:

\[
\mathbf e_2 = \frac{\mathbf u_2}{\|\mathbf u_2\|}
\]

Now \(\mathbf e_2\) is orthogonal to \(\mathbf e_1\) and has unit length.

### Step 3: Third Vector

For \(\mathbf v_3\), subtract its projections onto both \(\mathbf e_1\) and \(\mathbf e_2\):

\[
\mathbf u_3 = \mathbf v_3 - (\mathbf v_3 \cdot \mathbf e_1)\,\mathbf e_1 - (\mathbf v_3 \cdot \mathbf e_2)\,\mathbf e_2
\]

Then normalize:

\[
\mathbf e_3 = \frac{\mathbf u_3}{\|\mathbf u_3\|}
\]

Now \(\mathbf e_3\) is orthogonal to both \(\mathbf e_1\) and \(\mathbf e_2\).

### General Step

For each subsequent vector \(\mathbf v_k\):

\[
\mathbf u_k = \mathbf v_k - \sum_{i=1}^{k-1} (\mathbf v_k \cdot \mathbf e_i)\,\mathbf e_i
\]

\[
\mathbf e_k = \frac{\mathbf u_k}{\|\mathbf u_k\|}
\]

## What It Achieves

Starting from any set of linearly independent vectors, Gram-Schmidt produces an orthonormal basis for the same space. The resulting basis vectors are all unit length and mutually orthogonal.

This means the transformation matrix built from these vectors is orthogonal (\(A^T = A^{-1}\)), making inversions trivial, projections simple, and lengths preserved.
