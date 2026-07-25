---
weight: 1
title: "Linear Algebra Notation"
draft: false
---

# Linear Algebra Notation Cheat Sheet

Linear algebra introduces a lot of notation all at once. Here is a quick reference.

## Why linear algebra?

Suppose you go shopping twice. First trip: 2 apples + 3 bananas = €8. Second trip: 10 apples + 1 banana = €13. What does one apple cost?

\[
\begin{aligned}
2a + 3b &= 8 \\
10a + 1b &= 13
\end{aligned}
\]

This is a system of simultaneous equations. Write it as a matrix multiplying a vector:

\[
\begin{bmatrix}2&3\\10&1\end{bmatrix}
\begin{bmatrix}a\\b\end{bmatrix}
=
\begin{bmatrix}8\\13\end{bmatrix}.
\]

The numbers 2, 3, 10, 1 are the coefficients. The unknowns \((a,b)\) are the prices. Linear algebra gives us a systematic way to solve this -- and much bigger versions of the same problem -- with algorithms that work for any number of items and shopping trips.

Many problems reduce to the same pattern: fitting a curve to data, Google's PageRank, image compression, recommendation systems. The objects and operations you learn here are the common language they all share.

## 1. Vectors

### Geometric view

A vector is an arrow that moves you through space. In coordinates:

\[
\mathbf r = \begin{bmatrix} r_1 \\ r_2 \\ r_3 \end{bmatrix}
\]

- \(\mathbf r\) = the whole vector
- \(r_1, r_2, r_3\) = its components

### Data-science view

A vector is just a list of attributes. For example, a house can be described by its features:

\[
\text{house} = \begin{bmatrix}120\;\text{m}^2 \\ 2\;\text{bedrooms} \\ 1\;\text{bathroom} \\ €150{,}000\end{bmatrix}
\]

A car by its specs:

\[
\text{car} = \begin{bmatrix}€35{,}000 \\ 120\;\text{g CO}_2/\text{km} \\ 5\;\text{stars} \\ 200\;\text{km/h}\end{bmatrix}
\]

In metallurgy, an alloy is a vector of its component concentrations. In relativity, space-time is a 4D vector \((x, y, z, t)\). The key idea: **anything you can write as an ordered list is a vector**.

### Parametric view

When fitting a model, the parameters themselves form a space. If a Gaussian distribution has parameters \(\mu\) (center) and \(\sigma\) (width), then the pair \((\mu, \sigma)\) is a point in a 2D parameter space. A change to the parameters is a vector in that space. Finding the best fit means moving through this space toward the minimum of a "badness" surface -- that is gradient descent.

### Vector operations

A vector is defined by two operations:

**Addition** -- place one vector after another:

\[
\mathbf r + \mathbf s
\]

**Scalar multiplication** -- stretch or shrink a vector:

\[
\alpha \mathbf r
\]

These are the only rules you need. Everything else (dot products, cross products, linear combinations) builds on these two.

### In code

```text
r = [r1, r2, r3]
```

## 2. Unit (basis) vectors

\[
\mathbf e_1 = \begin{bmatrix}1\\0\\0\end{bmatrix},\qquad
\mathbf e_2 = \begin{bmatrix}0\\1\\0\end{bmatrix},\qquad
\mathbf e_3 = \begin{bmatrix}0\\0\\1\end{bmatrix}
\]

Each one points along one coordinate axis.

## 3. Components of basis vectors

The expression \((e_3)_j\) means "the \(j\)-th component of \(\mathbf e_3\)." Since

\[
\mathbf e_3 = \begin{bmatrix}0\\0\\1\end{bmatrix},
\]

| \(j\) | \((e_3)_j\) |
|------|-------------|
| 1    | 0           |
| 2    | 0           |
| 3    | 1           |

## 4. Dot product

Matrix notation writes the dot product \(\mathbf a\cdot\mathbf b\) as

\[
\boxed{\mathbf a^T\mathbf b}.
\]

For example,

\[
\mathbf a = \begin{bmatrix}1\\2\\3\end{bmatrix},\qquad
\mathbf b = \begin{bmatrix}4\\5\\6\end{bmatrix},
\qquad
\mathbf a\cdot\mathbf b = 1(4)+2(5)+3(6) = 32.
\]

## 5. Transpose

Transpose turns a column vector into a row vector:

\[
\begin{bmatrix}1\\2\\3\end{bmatrix}^T = \begin{bmatrix}1&2&3\end{bmatrix}.
\]

## 6. Einstein summation notation

**If an index appears twice, sum over it.** For example,

\[
a_j b_j
\]

means \(a_1 b_1 + a_2 b_2 + a_3 b_3\). The index \(j\) disappears after summation.

## 7. Free index vs dummy index

### Free index

Appears once on each side. In

\[
r'_i = r_i - s_i \frac{r_3}{s_3},
\]

\(i\) is free. When \(i=1\) you get the first equation, \(i=2\) the second, etc. This single equation actually represents three equations.

### Dummy index

Appears twice and is summed over. In \((e_3)_j r_j\), \(j\) is a dummy index. You can rename it freely:

\[
(e_3)_j r_j = (e_3)_k r_k = (e_3)_m r_m.
\]

## 8. Why \(A_{ij}r_j\) and not \(A_{ij}r_i\)?

This is the most common confusion in Einstein notation.

### Ordinary matrix multiplication

\[
A = \begin{bmatrix}1&2\\3&4\end{bmatrix},\qquad
r = \begin{bmatrix}r_1\\r_2\end{bmatrix}.
\]

The first component of \(Ar\) is \(1r_1 + 2r_2\); the second is \(3r_1 + 4r_2\). The **row** changes and the **column** is summed over.

In index notation:

\[
(Ar)_i = A_{ij}r_j.
\]

Here \(i\) selects which output component to compute, and \(j\) is summed (columns). The two indices have different jobs, so they need different names.

### What if you write \(A_{ij}r_i\)?

Now the repeated index is \(i\), so you sum over **rows** instead of columns:

\[
A_{ij}r_i = \sum_i A_{ij}r_i.
\]

This gives something completely different -- it is not matrix multiplication.

### The identity matrix case

\[
r_i = I_{ij}r_j \quad\text{(correct)}
\]

Here \(j\) is summed (columns) and \(i\) picks the output component. You **cannot** write \(r_i = I_{ij}r_i\) because \(i\) would appear **three times** -- once on the left, once in \(I_{ij}\), once in \(r_i\). Einstein notation forbids this: a free index appears once per side, a dummy index appears exactly twice.

### Programming analogy

\[
I_{ij}r_j \quad\equiv\quad
\begin{array}{l}
\texttt{for each i:} \\
\quad\texttt{answer[i] = 0} \\
\quad\texttt{for each j:} \\
\quad\quad\texttt{answer[i] += I[i][j] * r[j]}
\end{array}
\]

Using \(i\) for both roles would be like using the same loop variable for both the outer and inner loop.

**Rule of thumb:** In \(A_{ij}x_j\), \(i\) = "which answer am I computing?" and \(j\) = "which entries am I adding together?"

## 9. Why does \((e_3)_j r_j = r_3\)?

Expand:

\[
(e_3)_1 r_1 + (e_3)_2 r_2 + (e_3)_3 r_3.
\]

Substitute \(\mathbf e_3 = [0\;0\;1]^T\):

\[
0 r_1 + 0 r_2 + 1 r_3 = r_3.
\]

## 10. Identity matrix

\[
I = \begin{bmatrix}1&0&0\\0&1&0\\0&0&1\end{bmatrix}
\]

It leaves vectors unchanged: \(I\mathbf r = \mathbf r\).

## 11. \(I_{ij}\)

\(I_{ij}\) means the entry in row \(i\), column \(j\). For example \(I_{23}=0\) because row 2, column 3 is 0.

## 12. \(I_{3j}\)

\(I_{3j}\) means the third row of the identity matrix: \([0\;0\;1]\). This is exactly the same as \((e_3)_j\), so

\[
\boxed{I_{3j} = (e_3)_j}.
\]

This is why two answer choices can be identical.

## 13. Outer product

The product \(\mathbf s \mathbf e_3^T\) has dimensions \((3\times 1)(1\times 3) = 3\times 3\):

\[
\begin{bmatrix}s_1\\s_2\\s_3\end{bmatrix}
\begin{bmatrix}0&0&1\end{bmatrix}
= \begin{bmatrix}0&0&s_1\\0&0&s_2\\0&0&s_3\end{bmatrix}.
\]

This is called an **outer product**.

## 14. Matrix multiplication

\(A_{ij} r_j\) simply means \(A\mathbf r\). The repeated \(j\) tells you to sum across the columns of \(A\).

## 15. The key identity

Everything boils down to this:

\[
\boxed{(\mathbf e_3\cdot\mathbf r)\,\mathbf s = (\mathbf s\mathbf e_3^T)\,\mathbf r}.
\]

Read it step by step:

1. Dot product: \(\mathbf e_3\cdot\mathbf r = \mathbf e_3^T\mathbf r\).
2. Since matrix multiplication is associative,
   \[
   \mathbf s(\mathbf e_3^T\mathbf r) = (\mathbf s\mathbf e_3^T)\mathbf r.
   \]

That is the trick used to factor out \(\mathbf r\).

## Quick reference table

| Notation | Meaning |
|----------|---------|
| \(\mathbf r\) | Entire vector |
| \(r_i\) | \(i\)-th component of a vector |
| \(\mathbf e_i\) | \(i\)-th basis vector |
| \((e_3)_j\) | \(j\)-th component of the third basis vector |
| \(\mathbf a\cdot\mathbf b\) | Dot product |
| \(\mathbf a^T\mathbf b\) | Matrix form of the dot product |
| \(I\) | Identity matrix |
| \(I_{ij}\) | Entry at row \(i\), column \(j\) of \(I\) |
| \(I_{3j}\) | Third row of the identity matrix |
| Repeated index (e.g. \(j\)) | Sum over that index (Einstein notation) |
| \(A_{ij} r_j\) | Matrix multiplication (\(A\mathbf r\)) |
| \(\mathbf s\mathbf e_3^T\) | Outer product (forms a matrix) |
