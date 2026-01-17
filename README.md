# Flexible Fibonacci Heap Implementation

A versatile Java implementation of a **Fibonacci Heap** that allows for configurable behavior to simulate various heap structures, including standard Binomial Heaps and Lazy Binomial Heaps. This project supports positive integers and provides detailed performance metrics for analysis.

---

## 🚀 Key Features

The `Heap` class is defined by two primary configuration flags, allowing for "laziness" toggling:

| Flag | Description |
| :--- | :--- |
| **`lazyMelds`** | If `true`, insertions and merges are $O(1)$ by deferring tree consolidation. If `false`, it performs `successiveLinking` immediately. |
| **`lazyDecreaseKeys`** | If `true`, uses **Cascading Cuts** (Fibonacci style). If `false`, uses a standard **Heapify-Up** (swap-based) approach. |

### Supported Configurations
By combining these flags, you can simulate different structures:
* **Fibonacci Heap:** `lazyMelds = true`, `lazyDecreaseKeys = true`
* **Lazy Binomial Heap:** `lazyMelds = true`, `lazyDecreaseKeys = false`
* **Binomial Heap with Cuts:** `lazyMelds = false`, `lazyDecreaseKeys = true`
* **Standard Binomial Heap:** `lazyMelds = false`, `lazyDecreaseKeys = false`

---

## 📊 Performance Analysis

The implementation provides $O(1)$ access to the minimum element and efficient amortized costs for core operations.

### Amortized Time Complexity
| Operation | Complexity | Notes |
| :--- | :--- | :--- |
| **Insert** | $O(1)$ | Constant time when `lazyMelds` is enabled. |
| **FindMin** | $O(1)$ | Direct pointer to the minimum node is maintained. |
| **DeleteMin** | $O(\log n)$ | Actual cost can be $O(n)$ during consolidation. |
| **DecreaseKey**| $O(1)$ / $O(\log n)$| $O(1)$ amortized for Fibonacci configuration. |
| **Meld** | $O(1)$ | Constant time when `lazyMelds` is enabled. |

---

## 🏗️ Architecture

The project is structured into three main components:

1.  **`Heap`**: Manages the root list, global minimum, and tracks statistics like `totalLinks`, `totalCuts`, and `totalHeapifyCosts` .
2.  **`HeapNode`**: Represents a node in the tree with pointers for circular doubly linked lists (parent, child, next, prev).
3.  **`HeapItem`**: A user-facing handle for the key-value pair, allowing $O(1)$ node access for `decreaseKey` operations.



---

## 🔬 Experimental Findings

Based on the empirical studies conducted in this project :

* **Lazy Consolidation**: Enabling `lazyMelds` significantly improves `insert` performance by postponing tree unification until a `deleteMin` is required.
* **Cascading Cuts vs. Heapify**: Cascading cuts (`lazyDecreaseKeys = true`) outperformed `heapifyUp` in decrease-key heavy workloads, reducing operations from millions of swaps to hundreds of thousands of cuts.
* **Worst-Case Latency**: Lazy heaps exhibit the highest "maximum cost" per single operation because the first `deleteMin` after a long sequence of insertions must pay the cost of consolidating all trees.

---

## 👥 Authors
* **Amit Kacen** 
* **Dan Remeniuk**
