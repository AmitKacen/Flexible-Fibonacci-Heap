/**
 * Heap
 *
 * An implementation of Fibonacci heap over positive integers 
 * with the possibility of not performing lazy melds and 
 * the possibility of not performing lazy decrease keys.
 *
 */
public class Heap
{
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
     *
     * Constructor to initialize an empty heap.
     *
     */
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys)
    {
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
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapItem insert(int key, String info) 
    {    
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
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapItem findMin()
    {
        return min; 
    }

    /**
     * 
     * Delete the minimal item.
     *
     */
    public void deleteMin()
    {
        if (min == null) {
            return; // heap is empty
        }
        
        HeapNode minNode = min.node;

        // Handle single node case
        if (this.size == 1) {
            this.head = null;
            this.last = null;
            this.min = null;
            this.size = 0;
            this.numTrees = 0;
            return;
        }

        // remove minnode from root list
        minNode.next.prev = minNode.prev;
        minNode.prev.next = minNode.next;

        // Update head/last if needed
        if (this.head.node == minNode) {
            this.head = minNode.next.item;
        }
        if (this.last.node == minNode) {
            this.last = minNode.prev.item;
        }
        
        this.numTrees--; // removing min node decreases numTrees by 1
        int numChildren = minNode.rank; // number of children of min node after which we will update numTrees
        this.size--;


        HeapNode child = minNode.child;
        if (child != null) {
            // Unmark and remove parent from all children
            HeapNode current = child;
            do {
                current.parent = null;
                if (current.marked) {
                    current.marked = false;
                    this.numMarkedNodes--;
                }
                current = current.next;
            } while (current != child);
            
            // create new heap with children and meld
            Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
            heap2.head = child.item;
            heap2.last = child.prev.item;
            heap2.numTrees = numChildren;
            heap2.size = 0;

            //finding min in min children for meld O(log n) --> max degree = O(log n)
            current = child;
            heap2.min = child.item;
            int minKey = child.item.key;
            current = current.next;
            while (current != child) {
                if (current.item.key < minKey) {
                    minKey = current.item.key;
                    heap2.min = current.item;
                }
                current = current.next;   
            }

            this.meld(heap2);
            succesiveLinking();
            
        }

        // update min-> log(n) time beacuse after meld there are o(log n) trees
        HeapNode current = head.node;
        HeapNode start = head.node;  
        min = head;
        int minKey = head.key;
        current = current.next;
        while (current != start) {   
            if (current.item.key < minKey) {
                minKey = current.item.key;
                min = current.item;
            }
            current = current.next;   
        }
    }

    

    /**
     * pre: 0<=diff<=x.key
     * 
     * Decrease the key of x by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapItem x, int diff) 
    {   
        x.key = x.key - diff; // update key
        // update min if needed
        if(x.key < min.key){
            min = x;
        }
        // if lazy decrease keys is on, do cascading cut
        if(lazyDecreaseKeys){
            if(x.node.parent != null && x.key < x.node.parent.item.key){
                cascadingCut(x.node, x.node.parent);
            }
        }
        // else, do heapify up
        else{
            heapifyUp(x.node);
        }
    }

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

    private void swapWithParent(HeapNode child) {
        HeapNode parent = child.parent;
        // if node is root or no need to swap -> stop
        if (parent == null || child.item.key >= parent.item.key) {
            return;  // No swap needed
        }
        
        // Swap the items (keys and info)
        totalHeapifyCosts++;
        HeapItem tempItem = child.item;
        child.item = parent.item;
        parent.item = tempItem;
        
        // Update node references in items
        child.item.node = child;
        parent.item.node = parent;
    }

    private void cascadingCut(HeapNode x, HeapNode y){
        cut(x, y);
        // if y is not root
        if (y.parent != null){
            // if unmarked, mark it and stop
            if (!y.marked){
                y.marked = true;
                numMarkedNodes++;
            }
            // else, continue cutting
            else{
                cascadingCut(y, y.parent);
            }
        }
    }

    private void cut(HeapNode x, HeapNode y){
        totalCuts++;
        // remove x from child list of y
        x.parent = null;
        if (x.marked){
            numMarkedNodes--;
        }
        x.marked = false;
        y.rank--;
        if (x.next == x){
            y.child = null;
        }
        else{
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
     */
    public void delete(HeapItem x) 
    {    
        // decrease key to MIN_VALUE and delete min
        decreaseKey(x, Integer.MAX_VALUE);
        deleteMin();
    }


    /**
     * 
     * Meld the heap with heap2
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     */
    public void meld(Heap heap2)
    {
        if (heap2.size == 0 || heap2 == null) {
            return; // nothing to meld
        }
        if (this.size == 0) {
            // this heap is empty, so just copy heap2's fields
            this.min = heap2.min;
            this.head = heap2.head;
            this.last = heap2.last;
            this.size = heap2.size;
            this.numTrees = heap2.numTrees;
            this.numMarkedNodes = heap2.numMarkedNodes;
            this.totalLinks = heap2.totalLinks;
            this.totalCuts = heap2.totalCuts;
            this.totalHeapifyCosts = heap2.totalHeapifyCosts;
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
    

     private void succesiveLinking() {

        // Array size based on max possible rank: O(log_phi(n))
        int arraySize = (int) (2 * Math.ceil(Math.log(size())));
        HeapNode[] bucket = new HeapNode[arraySize];

        // Initialize all buckets to null
        for (int i = 0; i < arraySize; i++) {
            bucket[i] = null;
        }

        // Consolidate and rebuild root list
        HeapNode x = consolidate(bucket);
        head = x.item;
        last = x.prev.item;
    }


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

    private void toBucket(HeapNode[] bucket) {
        // Insert all roots into buckets by rank, linking trees of same rank

        // Break circularity of root list for traversal
        head.node.prev.next = null;

        HeapNode x = head.node;
        while(x != null) {
            HeapNode y = x;
            x = x.next;    // save next before modifying y
            
            y.parent = null;  // roots have no parent
            
            // Link trees of same rank
            while (bucket[y.rank] != null) {
                y = link(y, bucket[y.rank]);
                totalLinks++;
                bucket[y.rank - 1] = null;  // B[y.rank - 1] ← null
            }
            bucket[y.rank] = y;
        }  
    }
      
    private HeapNode fromBucket(HeapNode[] bucket) {
        // Rebuild root list from buckets
        
        HeapNode x = null;  // new root list head
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

    private HeapNode consolidate(HeapNode[] bucket) {
        // Consolidate the root list into the bucket array
        toBucket(bucket);
        return fromBucket(bucket);
    }
    
    /**
     * 
     * Return the number of elements in the heap
     *   
     */
    public int size()
    {
        return size; // should be replaced by student code
    }


    /**
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return numTrees; // should be replaced by student code
    }
    
    
    /**
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return numMarkedNodes; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return totalLinks; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return totalCuts; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return totalHeapifyCosts; // should be replaced by student code
    }
    
    
    /**
     * Class implementing a node in a Heap.
     *  
     */
    public static class HeapNode{
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
        public boolean marked;

        public HeapNode(HeapItem item, HeapNode child, HeapNode next, HeapNode prev, HeapNode parent, int rank) 
        {
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
    public static class HeapItem{
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

