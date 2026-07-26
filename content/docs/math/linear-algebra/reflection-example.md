---
weight: 30.8
title: "Worked Example: Reflecting a Vector Through a Plane"
draft: false
prerequisites:
  - docs/math/linear-algebra/gram-schmidt
  - docs/math/linear-algebra/orthogonal-matrices
  - docs/math/linear-algebra/basis-vectors-and-linear-combinations
---

# Worked Example: Reflecting a Vector Through a Plane

## Problem

We have a mirror whose plane is described by two vectors \(\mathbf v_1 = (1, 1, 1)\) and \(\mathbf v_2 = (2, 0, 1)\), with a third vector \(\mathbf v_3 = (3, 1, -1)\) pointing out of the plane. We want to reflect an arbitrary vector \(\mathbf r = (2, 3, 5)\) through this plane -- like reflecting an image in a mirror at a funny angle.

The trick: construct an orthonormal basis for the plane and its normal, express the reflection simply in that basis, then transform back.

## Step 1: Gram-Schmidt

First, build an orthonormal basis from \(\mathbf v_1, \mathbf v_2, \mathbf v_3\).

### e₁

Normalize \(\mathbf v_1\):

\[
\mathbf e_1 = \frac{\mathbf v_1}{\|\mathbf v_1\|}
= \frac{1}{\sqrt{3}}(1, 1, 1)
\]

### e₂

Subtract the projection of \(\mathbf v_2\) onto \(\mathbf e_1\):

\[
\begin{aligned}
\mathbf u_2 &= \mathbf v_2 - (\mathbf v_2 \cdot \mathbf e_1)\,\mathbf e_1 \\
&= (2, 0, 1) - \frac{1}{3}(2 + 0 + 1)(1, 1, 1) \\
&= (2, 0, 1) - (1, 1, 1) \\
&= (1, -1, 0)
\end{aligned}
\]

Normalize:

\[
\mathbf e_2 = \frac{1}{\sqrt{2}}(1, -1, 0)
\]

### e₃

Subtract projections onto both \(\mathbf e_1\) and \(\mathbf e_2\):

\[
\begin{aligned}
\mathbf u_3 &= \mathbf v_3 - (\mathbf v_3 \cdot \mathbf e_1)\,\mathbf e_1 - (\mathbf v_3 \cdot \mathbf e_2)\,\mathbf e_2 \\
&= (3, 1, -1) - \frac{1}{3}(3+1-1)(1,1,1) - \frac{1}{2}(3-1+0)(1,-1,0) \\
&= (3, 1, -1) - (1, 1, 1) - (1, -1, 0) \\
&= (1, 1, -2)
\end{aligned}
\]

Normalize:

\[
\mathbf e_3 = \frac{1}{\sqrt{6}}(1, 1, -2)
\]

Check orthogonality: \(\mathbf e_1 \cdot \mathbf e_2 = 0\), \(\mathbf e_1 \cdot \mathbf e_3 = 0\), \(\mathbf e_2 \cdot \mathbf e_3 = 0\). All unit length.

## Step 2: The Transformation Matrix E

Arrange the basis vectors as columns:

\[
E = \begin{bmatrix}
\frac{1}{\sqrt{3}} & \frac{1}{\sqrt{2}} & \frac{1}{\sqrt{6}} \\[4pt]
\frac{1}{\sqrt{3}} & -\frac{1}{\sqrt{2}} & \frac{1}{\sqrt{6}} \\[4pt]
\frac{1}{\sqrt{3}} & 0 & -\frac{2}{\sqrt{6}}
\end{bmatrix}
\]

Because this basis is orthonormal, \(E\) is an orthogonal matrix: \(E^{-1} = E^T\).

## Step 3: The Reflection in the Plane's Basis

In the basis \(\{\mathbf e_1, \mathbf e_2, \mathbf e_3\}\):
- Components in the plane (\(\mathbf e_1\) and \(\mathbf e_2\)) stay the same.
- The normal component (\(\mathbf e_3\)) flips sign.

So the reflection transformation in the plane's basis is:

\[
T_E = \begin{bmatrix}
1 & 0 & 0\\
0 & 1 & 0\\
0 & 0 & -1
\end{bmatrix}
\]

## Step 4: The Full Transformation

To reflect any vector \(\mathbf r\):
1. Transform \(\mathbf r\) into the plane's basis: \(E^{-1} \mathbf r = E^T \mathbf r\).
2. Apply the reflection: \(T_E (E^T \mathbf r)\).
3. Transform back to the original basis: \(E (T_E E^T \mathbf r)\).

The total transformation matrix is:

\[
T = E \, T_E \, E^T
\]

Since \(E\) is orthogonal, this is straightforward to compute. The result:

\[
T = \frac{1}{3}\begin{bmatrix}
1 & 2 & 2\\
2 & 1 & 2\\
2 & 2 & -1
\end{bmatrix}
\]

## Step 5: Apply to \(\mathbf r\)

\[
T \begin{bmatrix}2\\3\\5\end{bmatrix}
= \frac{1}{3}\begin{bmatrix}
1 & 2 & 2\\
2 & 1 & 2\\
2 & 2 & -1
\end{bmatrix}
\begin{bmatrix}2\\3\\5\end{bmatrix}
= \frac{1}{3} \begin{bmatrix}11\\14\\5\end{bmatrix}
\]

So the reflection of \((2, 3, 5)\) through the plane is:

\[
\mathbf r' = \left(\frac{11}{3},\ \frac{14}{3},\ \frac{5}{3}\right)
\]

## Why This Works

The key insight: instead of solving a messy trigonometry problem in the original coordinates, we change to a basis where the reflection is trivial (flip the normal component), apply it, and change back. The orthonormal basis makes the inverse cheap (\(E^{-1} = E^T\)) and the whole computation manageable.

This technique -- transform, operate in a convenient basis, transform back -- is a recurring pattern throughout linear algebra and its applications.
