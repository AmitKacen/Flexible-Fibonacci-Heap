/**
 * Heap
 *
 * An implementation of Fibonacci heap over positive integers
 * with the possibility of not performing lazy melds and
 * the possibility of not performing lazy decrease keys.
 *
 */
public class Heap {
    public final boolean lazyMelds;
    public final boolean lazyDecreaseKeys;
    public HeapItem min;
    public HeapItem head;
    public HeapItem last;
    public int size;
    public int numTrees;
    public int numMarkedNodes;
    public int totalLinks;
    public int totalCuts;
    public int totalHeapifyCosts;

    /**
     * Constructor to initialize an empty heap.
     *
     */
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys) {
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        this.min = null;
        this.head = null;
        this.last = null;
        this.size = 0;
        this.numTrees = 0;
        this.numMarkedNodes = 0;
        this.totalLinks = 0;
        this.totalCuts = 0;
        this.totalHeapifyCosts = 0;
    }

    /**
     * Inserts a new item with the given key and info into the heap and returns the newly generated HeapNode.
     *
     * pre: key > 0
     *
     * Time Complexity (WC): O(1)
     */
    public HeapItem insert(int key, String info) {
        // create new node
        HeapItem nodeitem = new HeapItem(key, info);
        HeapNode node = new HeapNode(nodeitem, null, null, null, null, 0);
        nodeitem.node = node;
        node.next = node;
        node.prev = node;

        // create new heap with the node and meld
        Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
        heap2.min = nodeitem;
        heap2.head = nodeitem;
        heap2.last = nodeitem;
        heap2.size = 1;
        heap2.numTrees = 1;

        this.meld(heap2);
        return nodeitem;

    }

    /**
     * Returns the minimal HeapNode, or null if the heap is empty.
     *
     * Time Complexity (WC): O(1)
     */
    public HeapItem findMin() {
        return min;
    }

    /**
     * Deletes the minimal item from the heap.
     *
     * Time Complexity (WC): O(logn), can be O(n) due to consolidation
     */
    public void deleteMin() { // case 0 : empty heap
        if (head == null) {
            return; // heap is empty
        }
        if (size == 1) {
            head = null;
            last = null;
            min = null;
            size = 0;
            numTrees = 0;
            return;
        }
        HeapNode minNode = min.node;

        // case 1 : only one tree
        if (numTrees == 1) {
            head = null;
            last = null;
        } else { // case 2 : more than one tree
            minNode.prev.next = minNode.next;
            minNode.next.prev = minNode.prev;
            if (head == min) {
                head = minNode.next.item;
            }
            if (last == min) {
                last = minNode.prev.item;
            }
        }

        this.size--;
        this.numTrees--;
        this.min = null;

        // remove marked and parent pointer
        HeapNode child = minNode.child;
        if (child != null) {
            HeapNode current = child;
            do {
                current.parent = null;
                if (current.marked) {
                    current.marked = false;
                    numMarkedNodes--;
                }
                current = current.next;
            } while (current != child);

            // create new heap with children and meld
            Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
            heap2.head = child.item;
            heap2.last = child.prev.item;
            heap2.size = 0; // not adding size in meld
            heap2.min = null;
            heap2.numTrees = minNode.rank;
            meld(heap2);
        }

        succesiveLinking();

        // update min
        HeapNode current = head.node;
        do {
            if (min == null || current.item.key < min.key) {
                min = current.item;
            }
            current = current.next;
        } while (current != head.node);

    }

    /**
     * Decreases the key of x by diff and fixes the heap.
     *
     * pre: 0<=diff<=x.key
     *
     * Time Complexity (WC): O(log n) 
     */
    public void decreaseKey(HeapItem x, int diff) {
        x.key = x.key - diff; // update key
        // update min if needed
        if (x.key < min.key) {
            min = x;
        }
        // if lazy decrease keys is on, do cascading cut
        if (lazyDecreaseKeys) {
            if (x.node.parent != null && x.key < x.node.parent.item.key) {
                cascadingCut(x.node, x.node.parent);
            }
        }
        // else, do heapify up
        else {
            heapifyUp(x.node);
        }
    }

    /**
     * Moves the given node up the tree until the heap property is restored.
     *
     * Time Complexity (WC): O(log n)
     */
    private void heapifyUp(HeapNode node) {
        // while node parent isnt root and node key < parent key do swap
        while (node.parent != null && node.item.key < node.parent.item.key) {
            swapWithParent(node);
            node = node.parent;
        }

        // Update min if needed (maybe not needed here, but just in case)
        if (node.item.key < min.key) {
            min = node.item;
        }
    }

    /**
     * Swaps the given child node with its parent in the heap.
     *
     * Time Complexity (WC): O(1)
     */
    private void swapWithParent(HeapNode child) {
        HeapNode parent = child.parent;

        // nothing to do
        if (parent == null || child.item.key >= parent.item.key) {
            return;
        }

        // swap items
        HeapItem childItem = child.item;
        HeapItem parentItem = parent.item;

        // update heapify costs and swap
        totalHeapifyCosts++; 
        child.item = parentItem;
        parent.item = childItem;

        // update node pointers in items
        child.item.node = child;
        parent.item.node = parent;

        // update head, last, min if needed
        if (this.head == childItem) {
            this.head = parentItem;
        } else if (this.head == parentItem) {
            this.head = childItem;
        }

        if (this.last == childItem) {
            this.last = parentItem;
        } else if (this.last == parentItem) {
            this.last = childItem;
        }

        if (this.min == childItem) {
            this.min = parentItem;
        } else if (this.min == parentItem) {
            this.min = childItem;
        }
    }

    /**
     * Performs a cascading cut operation starting from node x and its parent y.
     *
     * Time Complexity (WC): O(log n)
     */
    private void cascadingCut(HeapNode x, HeapNode y) {
        cut(x, y);
        // if y is not root
        if (y.parent != null) {
            // if unmarked, mark it and stop
            if (!y.marked) {
                y.marked = true;
                numMarkedNodes++;
            }
            // else, continue cutting
            else {
                cascadingCut(y, y.parent);
            }
        }
    }

    /**
     * Cuts the link between node x and its parent y, making x a new root.
     *
     * Time Complexity (WC): O(1)
     */
    private void cut(HeapNode x, HeapNode y) {
        totalCuts++;
        // remove x from child list of y
        x.parent = null;
        if (x.marked) {
            numMarkedNodes--;
        }
        x.marked = false;
        y.rank--;
        if (x.next == x) {
            y.child = null;
        } else {
            y.child = x.next;
            x.prev.next = x.next;
            x.next.prev = x.prev;
        }

        // create new heap with x and meld
        x.next = x;
        x.prev = x;
        Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
        heap2.head = x.item;
        heap2.last = x.item;
        heap2.size = 0; // not adding size in meld
        heap2.min = x.item;
        heap2.numTrees = 1;
        this.meld(heap2);
    }

    /**
     * Delete the x from the heap.
     *
     * Time Complexity (WC): O(n)
     */
    public void delete(HeapItem x) {
        // decrease key to MIN_VALUE and delete min
        decreaseKey(x, Integer.MAX_VALUE);
        deleteMin();
    }

    /**
     * Melds the current heap with heap2.
     *
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     * Time Complexity (WC): O(1) if lazyMelds is true, O(n) otherwise
     */
    public void meld(Heap heap2) {
        if (heap2 == null || heap2.head == null) {
            return; // nothing to meld
        }
        if (this.head == null) {
            // this heap is empty, so just copy heap2's fields
            this.min = heap2.min;
            this.head = heap2.head;
            this.last = heap2.last;
            this.size += heap2.size;
            this.numTrees = heap2.numTrees;
            this.numMarkedNodes += heap2.numMarkedNodes;
            this.totalLinks += heap2.totalLinks;
            this.totalCuts += heap2.totalCuts;
            this.totalHeapifyCosts += heap2.totalHeapifyCosts;
            return;
        }

        // melded heaps
        last.node.next = heap2.head.node;
        heap2.last.node.next = head.node;
        head.node.prev = heap2.last.node;
        heap2.head.node.prev = last.node;
        this.last = heap2.last;
        size += heap2.size;

        // reserve fields history
        numTrees += heap2.numTrees;
        numMarkedNodes += heap2.numMarkedNodes;
        totalLinks += heap2.totalLinks;
        totalCuts += heap2.totalCuts;
        totalHeapifyCosts += heap2.totalHeapifyCosts;

        // update min
        if (heap2.min != null) {
            if (this.min == null || heap2.min.key < this.min.key) {
                this.min = heap2.min;
            }
        }

        // consolidate if not lazy melds
        if (!this.lazyMelds) {
            succesiveLinking();
        }
        return;
    }

    /**
     * Consolidates the heap by linking trees of the same rank.
     *
     * Time Complexity (WC): O(n)
     */
    private void succesiveLinking() {

        if (size <= 1) {
            return; // no need to consolidate
        }

        // Array size based on max possible rank: O(log_phi(n))
        double phi = (1.0 + Math.sqrt(5.0)) / 2.0;
        int arraySize = (int) Math.ceil(Math.log(size) / Math.log(phi)) + 1;
        HeapNode[] bucket = new HeapNode[2 * arraySize];

        // Initialize all buckets to null
        for (int i = 0; i < 2 * arraySize; i++) {
            bucket[i] = null;
        }

        // Consolidate and rebuild root list
        HeapNode x = consolidate(bucket);
        head = x.item;
        last = x.prev.item;
    }

    /**
     * Links two trees of the same rank, making the tree with the smaller key the parent.
     *
     * Time Complexity (WC): O(1)
     */
    private HeapNode link(HeapNode x, HeapNode y) {
        // Links two trees of same rank - smaller key becomes parent

        // Ensure x has smaller key
        if (x.item.key > y.item.key) {
            HeapNode temp = x;
            x = y;
            y = temp;
        }

        // add y as child of x
        if (x.child == null) {
            // y becomes the only child of x
            y.next = y;
            y.prev = y;
        } else {
            // insert y into the child list of x
            y.next = x.child.next;
            y.prev = x.child;
            x.child.next.prev = y;
            x.child.next = y;
        }

        // update pointers and rank
        x.child = y;
        y.parent = x;
        x.rank++;

        return x;
    }

    /**
     * Inserts all root nodes into the bucket array by rank, linking trees of the same rank.
     *
     * Time Complexity (WC): O(n)
     */
    private void toBucket(HeapNode[] bucket) {
        // Insert all roots into buckets by rank, linking trees of same rank
        // Break circularity of root list for traversal
        head.node.prev.next = null;

        HeapNode x = head.node;
        while (x != null) {
            HeapNode y = x;
            x = x.next; // save next before modifying y

            y.parent = null; // roots have no parent
            y.next = y;
            y.prev = y;

            // Link trees of same rank
            while (bucket[y.rank] != null) {
                y = link(y, bucket[y.rank]);
                totalLinks++;
                bucket[y.rank - 1] = null; // B[y.rank - 1] ← null
            }

            bucket[y.rank] = y;
        }
    }

    /**
     * Rebuilds the root list from the bucket array after consolidation.
     *
     * Time Complexity (WC): O(logn)
     */
    private HeapNode fromBucket(HeapNode[] bucket) {
        // Rebuild root list from buckets

        HeapNode x = null; // new root list head
        numTrees = 0;

        for (int i = 0; i < bucket.length; i++) {
            if (bucket[i] != null) {
                numTrees++;
                if (x == null) {
                    // first tree found - initialize root list
                    x = bucket[i];
                    x.next = x;
                    x.prev = x;
                } else {
                    // insert bucket[i] into root list at the end
                    bucket[i].next = x;
                    x.prev.next = bucket[i];
                    bucket[i].prev = x.prev;
                    x.prev = bucket[i];
                }
            }
        }
        return x;
    }

    /**
     * Consolidates the root list into the bucket array and rebuilds the root list.
     *
     * Time Complexity (WC): O(n)
     */
    private HeapNode consolidate(HeapNode[] bucket) {
        // Consolidate the root list into the bucket array
        toBucket(bucket);
        return fromBucket(bucket);
    }

    /**
     * Returns the number of elements in the heap.
     *
     * Time Complexity (WC): O(1)
     */
    public int size() {
        return size;
    }

    /**
     * Returns the number of trees in the heap.
     *
     * Time Complexity (WC): O(1)
     */
    public int numTrees() {
        return numTrees;
    }

    /**
     * Returns the number of marked nodes in the heap.
     *
     * Time Complexity (WC): O(1)
     */
    public int numMarkedNodes() {
        return numMarkedNodes;
    }

    /**
     * Returns the total number of links performed in the heap.
     *
     * Time Complexity (WC): O(1)
     */
    public int totalLinks() {
        return totalLinks;
    }

    /**
     * Returns the total number of cuts performed in the heap.
     *
     * Time Complexity (WC): O(1)
     */
    public int totalCuts() {
        return totalCuts;
    }

    /**
     * Returns the total heapify costs.
     *
     * Time Complexity (WC): O(1)
     */
    public int totalHeapifyCosts() {
        return totalHeapifyCosts;
    }

    /**
     * Class implementing a node in a Heap.
     * 
     */
    public static class HeapNode {
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
        public boolean marked;

        public HeapNode(HeapItem item, HeapNode child, HeapNode next, HeapNode prev, HeapNode parent, int rank) {
            this.item = item;
            this.child = child;
            this.next = next;
            this.prev = prev;
            this.parent = parent;
            this.rank = rank;
            this.marked = false;
        }
    }

    /**
     * Class implementing an item in a Heap.
     * 
     */
    public static class HeapItem {
        public HeapNode node;
        public int key;
        public String info;

        public HeapItem(int key, String info) {
            this.key = key;
            this.info = info;
            this.node = null;
        }
    }

}
