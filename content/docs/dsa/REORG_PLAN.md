---
title: "DSA Concept-first Reorganization Plan"
weight: 0
draft: false
---

# DSA Concept-first Reorganization Plan

Goals:
- Unify algorithms, data structures, and utilities under concept-centric sections (e.g., Trees, Graphs, Arrays & Strings).
- Keep URLs stable via `aliases` during/after migration.
- Use Hugo sections with `_index.md` for browsable list pages and clean sidebar ordering.
- Update the DSA landing page Markmap to reflect the new IA.

## Proposed Information Architecture (under `content/docs/dsa`)

- fundamentals/
  - complexity.md (Big-O, time/space tradeoffs)
  - patterns.md (two pointers, sliding window, prefix sums, difference arrays)
  - recursion.md (migrate `utilities/recursion.md`)
  - comparator-iterator.md (merge `utilities/comparator.md` + `utilities/iterator.md`, or keep separate)
- arrays-strings/
  - arrays.md
  - strings-overview.md
  - string-algorithms/ (from `algorithms/string/*`)
  - trie.md (from `data-structures/trie.md`)
- linked-lists/
  - overview.md
  - singly.md, doubly.md, cycle-detection.md (from `data-structures/linked-list/*`)
- stacks-queues/
  - stack.md (from `data-structures/stack.md`)
  - queue.md (from `data-structures/queue.md`)
  - deque.md, monotonic-stack.md (optional later)
- hashing/
  - hashtable.md (from `data-structures/hashtable.md`)
  - hashing-techniques.md
- heaps-priority-queues/
  - heap.md (from `data-structures/heap.md`)
  - priority-queue-usage.md
- trees/
  - overview.md
  - bst.md, avl.md, segment-tree.md, fenwick-tree.md (from `data-structures/tree/*`)
  - tree-traversals.md
- graphs/
  - overview.md
  - representations.md (adjacency list/matrix)
  - traversal.md (BFS/DFS)
  - shortest-paths.md (Dijkstra/Bellman-Ford/Floyd–Warshall)
  - mst.md (Kruskal/Prim)
  - disjoint-set-union.md (from `data-structures/disjoint-set.md`)
- searching/
  - binary-search.md (from `algorithms/search/*`)
  - search-on-answer.md (binary search patterns)
- sorting/
  - comparison-sorts.md (from `algorithms/sort/*`)
  - non-comparison-sorts.md
- intervals-scheduling/
  - intervals.md (from `data-structures/intervals.md`)
  - scheduling-greedy.md (merge interval-greedy problems)
- greedy/
  - overview.md (from `algorithms/greedy/*`)
  - classical-problems.md
- dynamic-programming/
  - overview.md (from `algorithms/dynamic-programming.md`)
  - patterns.md (knapsack, LIS, partition, digit DP, etc.)
- backtracking/
  - overview.md (from `algorithms/backtracking.md`)
  - classical-problems.md
- bit-manipulation/
  - overview.md (from `algorithms/bit-manipulation/*`)
  - tricks.md
- math-number-theory/
  - overview.md (from `algorithms/math/*`)
  - gcd-lcm.md, combinatorics.md, modular-arithmetic.md
- problems-and-recipes/
  - templates.md (boilerplate, common snippets)
  - problem-cheatsheets.md

## File Mapping (current → target)

- `data-structures/stack.md` → `stacks-queues/stack.md`
- `data-structures/queue.md` → `stacks-queues/queue.md`
- `data-structures/hashtable.md` → `hashing/hashtable.md`
- `data-structures/heap.md` → `heaps-priority-queues/heap.md`
- `data-structures/trie.md` → `arrays-strings/trie.md`
- `data-structures/intervals.md` → `intervals-scheduling/intervals.md`
- `data-structures/disjoint-set.md` → `graphs/disjoint-set-union.md`
- `data-structures/tree/*` → `trees/*`
- `data-structures/linked-list/*` → `linked-lists/*`
- `algorithms/dynamic-programming.md` → `dynamic-programming/overview.md`
- `algorithms/backtracking.md` → `backtracking/overview.md`
- `algorithms/graph/*` → `graphs/*` (group as traversal, shortest-paths, mst, etc.)
- `algorithms/sort/*` → `sorting/*`
- `algorithms/search/*` → `searching/*`
- `algorithms/string/*` → `arrays-strings/string-algorithms/*`
- `algorithms/bit-manipulation/*` → `bit-manipulation/*`
- `algorithms/math/*` → `math-number-theory/*`
- `algorithms/greedy/*` → `greedy/*`
- `utilities/recursion.md` → `fundamentals/recursion.md`
- `utilities/comparator.md` → `fundamentals/comparator-iterator.md` (or `fundamentals/comparator.md`)
- `utilities/iterator.md` → `fundamentals/comparator-iterator.md` (or `fundamentals/iterator.md`)

## Hugo Conventions and Redirects

- Each top-level concept directory should have an `_index.md` with `title`, `weight`, and a short description.
- Preserve old URLs using `aliases` in the front matter for moved pages. Example:

```yaml
---
title: "Stacks"
aliases: ["/docs/dsa/data-structures/stack/"]
weight: 10
---
```

- Use `weight` on each `_index.md` to control sidebar order.

## Phased Implementation

1. Phase 1 (low risk)
   - Create concept directories with `_index.md` placeholders.
   - Update DSA landing page Markmap to concept-first (links still point to current paths).
2. Phase 2 (migration)
   - Move files into concept directories.
   - Add `aliases` in moved pages.
   - Update `relref` link targets across the repo to the new paths.
3. Phase 3 (cleanup)
   - Remove old empty sections (`algorithms/`, `data-structures/`, `utilities/`) once all references are updated.

## Bulk Update Guidance

- Search and update internal references:
  - Hugo relrefs: `{{</* relref "docs/dsa/..." */>}}`
  - Markdown links: `](/docs/dsa/...)`
- Validate with `hugo` build and fix any broken references reported.

This plan keeps DS and algorithms unified per concept, improves discoverability, and preserves external links via aliases during the migration.
