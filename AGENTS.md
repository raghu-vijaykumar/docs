---
weight: 1
title: "Content Organization Guide"
draft: false
bookFlatSection: true
---

This is a living document that governs how content is organized in these docs. It is primarily for AI agents building and maintaining the site, but may also be useful for human contributors.

## Teaching philosophy

These docs are designed for **teaching, not reference**. Every page is a lesson that assumes the reader has completed previous lessons. The experience should feel like a textbook: chapters build on chapters, and the order matters.

- **Concrete → Abstract** -- Start with a real-world problem (e.g., "what does one apple cost?"), build intuition, then introduce formal notation.
- **One idea per page** -- Each page teaches one core concept. If a page covers two ideas, split it.
- **Linearity** -- The weight order of pages defines the teaching sequence. New content can move or replace old content to keep the sequence coherent.

## Page ordering (weight system)

Every page uses a `weight` value in front matter. Lower weights appear first.

```yaml
---
weight: 20
title: "Vectors"
---
```

**Convention:**
- Use gaps of 10 between weights (10, 20, 30...) so new pages can be inserted without renumbering everything.
- Section `_index.md` pages use `bookCollapseSection: true` and also get a weight.
- Leaf pages within a section are ordered by weight within that section.
- The `_index.md` markmap must list child pages in teaching order.

## Prerequisites

Every page should specify what the reader must already know:

```yaml
---
weight: 30
title: "Matrix Multiplication"
prerequisites:
  - docs/math/vectors
  - docs/math/dot-product
---
```

When adding new content, check whether it depends on concepts introduced elsewhere. If it does, add those paths to `prerequisites`. If it doesn't, it probably belongs earlier in the sequence.

## Source attribution

Source material (transcripts, excerpts, references) should be absorbed into the content without attribution lines. The content and its organization matter, not the source. If a source is genuinely useful to the reader, integrate it naturally into the text rather than adding a standalone credit line.

## Page template (all subjects)

Every content page follows this structure:

1. **Hook** -- A real-world problem or question that motivates the concept.
2. **Intuition** -- Plain-language explanation, analogy, or diagram.
3. **Formal content** -- Definitions, notation, equations using KaTeX (`\(...\)` for inline, `\[...\]` for display).
4. **Worked example** -- Concrete walkthrough with actual numbers or code.
5. **Why it matters** -- Connect back to the motivating problem and forward to the next concept.

This template is flexible -- some sections may be combined or expanded, but the arc (concrete → abstract → application) should always be visible.

## Diagrams

Use **[draw.io](https://draw.io)** (`.drawio` files) for all diagrams. Export to SVG at 100% scale and commit both the source (`.drawio`) and the exported SVG side by side in an `images/` subdirectory:

```
content/docs/math/vectors/
├── _index.md
├── vectors.md
└── images/
    ├── vector-addition.drawio
    ├── vector-addition.svg
    └── dot-product-geometry.drawio
    └── dot-product-geometry.svg
```

Reference the exported SVG in markdown:

```markdown
![Vector addition diagram](images/vector-addition.svg)
```

**Rules:**
- Every `.drawio` file must have an exported `.svg` committed alongside it.
- Agents creating or editing diagrams should load the `draw-io` skill for color palette, typography, connector styles, layout rules, and export settings.
- Keep diagrams clean -- one concept per diagram, consistent colors, no overlapping boxes.

## Cross-references

Link between pages using the `relref` shortcode so links stay valid across moves:

```markdown
See [Vectors]({{</* relref "docs/math/vectors" */>}}).
```

When moving a page to a new location, add an `aliases` entry in front matter so existing links do not break:

```yaml
---
title: "Vectors"
aliases:
  - /docs/math/vectors-old-path
---
```

## Content lifecycle

These docs are not static. When new content is added, it can alter the existing structure:

1. **Read before you write** -- Before creating a new file, **read every existing file in that section** and its neighbours. You must know what already exists to decide whether to extend, merge, or create.
2. **Extend before create (hard rule)** -- If a concept already exists in any existing page (even briefly), **extend that page** rather than creating a new file. This rule takes priority over the one-idea test. Only create a new file if the concept is entirely absent from all pages in that section. For example: if an existing eigenbasis section already mentions diagonalization and matrix powers, adding diagonalization content means extending that page -- not creating `diagonalization.md`.
3. **Read full content, not titles** -- Reading filenames is not sufficient. Read every existing file in the section end-to-end before deciding. A file named `eigenvectors-deep-dive.md` may already cover eigenbasis and diagonalization.
4. **The one-idea test (secondary)** -- Apply this only *after* deciding to extend. If extending would make the page too long, split into two. But similar/related material belongs together; do not use this rule to justify creating a new file when an existing page already covers the same concept.
5. **Insert, don't append** -- Use the weight system to place new content at the correct teaching position, not at the end. Renumber surrounding pages if needed.
6. **Move if needed** -- If a concept belongs in a different section, move it and add an alias at the old path.
7. **Update markmaps** -- After any structural change (add, move, reorder), update the `_index.md` markmap and quick links to reflect the new order.
8. **Update prerequisites** -- When moving or reordering, verify that prerequisite chains are still correct.

The goal is a single coherent teaching sequence across all sections. If two pages teach overlapping concepts, the older one should yield to the better explanation.

## Front matter convention

All pages use YAML front matter (`---`). Required fields:

| Field | Required | Applies to |
|---|---|---|
| `weight` | Yes | All pages |
| `title` | Yes | All pages |
| `draft` | No (default false) | All pages |
| `bookCollapseSection` | Yes | Section `_index.md` only |
| `bookFlatSection` | Yes | Standalone pages that should not create a sidebar section |
| `prerequisites` | Recommended | Leaf pages |
| `aliases` | When moving | Old paths for redirects |

**Section `_index.md` example:**

```yaml
---
weight: 10
title: "Linear Algebra"
bookCollapseSection: true
draft: false
---
```

**Leaf page example:**

```yaml
---
weight: 20
title: "Vectors"
draft: false
prerequisites:
  - docs/math/why-linear-algebra
---
```

## How agents should use this guide

When given a task to add, edit, or reorganize content:

1. Load this file first.
2. Scan the relevant `_index.md` markmap to understand the current teaching sequence and weights.
3. Identify where the new content fits in the sequence -- what comes before it and what comes after.
4. Create or edit pages following the page template.
5. Use draw.io for any diagrams, referencing the draw-io skill for style.
6. Update weights, prerequisites, aliases, and markmaps to keep the sequence coherent.
7. Verify the build with `hugo --gc --minify`.
