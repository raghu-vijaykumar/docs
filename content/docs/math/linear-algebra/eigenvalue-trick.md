---
weight: 32
title: "Quick Eigenvalues for 2×2 Matrices"
draft: false
prerequisites:
  - docs/math/linear-algebra/eigenvectors-and-eigenvalues
  - docs/math/linear-algebra/determinant
---

# Quick Eigenvalues for 2×2 Matrices

## The Trick

For a 2×2 matrix, the eigenvalues can be read directly from the trace and determinant. If \(m\) is the mean of the diagonal entries and \(p\) is the determinant, then the eigenvalues are:

\[
\lambda = m \pm \sqrt{m^2 - p}
\]

where:
- \(m = \frac{a + d}{2}\) (half the trace)
- \(p = ad - bc\) (the determinant)

## Why It Works

Two key facts about eigenvalues connect them to quantities you can read straight off the matrix:

1. **Trace** = sum of eigenvalues. So the mean of the eigenvalues equals the mean of the diagonal entries.
2. **Determinant** = product of the eigenvalues.

If two numbers have mean \(m\) and product \(p\), they can be written as:

\[
m + d \quad\text{and}\quad m - d
\]

for some distance \(d\). Their product is \((m + d)(m - d) = m^2 - d^2 = p\), so:

\[
d = \sqrt{m^2 - p}
\]

## Examples

### Example 1

\[
\begin{bmatrix}3 & 1\\4 & 1\end{bmatrix}
\]

Mean of diagonals: \((3 + 1)/2 = 2\). Determinant: \(3(1) - 1(4) = -1\).

\[
\lambda = 2 \pm \sqrt{2^2 - (-1)} = 2 \pm \sqrt{5}
\]

### Example 2

\[
\begin{bmatrix}8 & 7\\1 & 2\end{bmatrix}
\]

Mean: \((8 + 2)/2 = 5\). Determinant: \(8(2) - 7(1) = 9\).

\[
\lambda = 5 \pm \sqrt{5^2 - 9} = 5 \pm \sqrt{16} = 5 \pm 4 \implies 9,\ 1
\]

### Example 3: Pauli Spin Matrices

\[
\sigma_x = \begin{bmatrix}0 & 1\\1 & 0\end{bmatrix},\quad
\sigma_y = \begin{bmatrix}0 & -i\\i & 0\end{bmatrix},\quad
\sigma_z = \begin{bmatrix}1 & 0\\0 & -1\end{bmatrix}
\]

All have trace 0 and determinant \(-1\), so eigenvalues are \(\pm 1\) in every case.

For a linear combination \(a\sigma_x + b\sigma_y + c\sigma_z\) with \(a^2 + b^2 + c^2 = 1\), the mean stays 0 and the determinant remains \(-1\), so the eigenvalues are still \(\pm 1\).

## Connection to the Quadratic Formula

This trick is really just the quadratic formula in disguise. The characteristic polynomial of a 2×2 matrix is:

\[
\lambda^2 - (\text{trace})\lambda + \det = 0
\]

The mean of the roots is \(\text{trace}/2\) (which is \(-\frac{1}{2}\) times the linear coefficient of the normalized polynomial), and the product is the constant term. The formula above is the quadratic formula expressed in terms of mean and product rather than polynomial coefficients.

The advantage is that you can write down the eigenvalues directly from looking at the matrix, without the intermediate step of writing the characteristic polynomial.
