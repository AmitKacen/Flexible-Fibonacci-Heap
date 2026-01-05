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
        HeapItem nodeitem = new HeapItem(key, info);
        HeapNode node = new HeapNode(nodeitem, null, null, null, null, 0);
        nodeitem.node = node;
        node.next = node;
        node.prev = node;

        Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
        heap2.min = nodeitem;
        heap2.head = nodeitem;
        heap2.last = nodeitem;
        heap2.size = 1; 

        this.meld(heap2);
        return null; // should be replaced by student code 

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
        HeapNode minNode = min.node;

        // remove minnode from root list
        minNode.next.prev = minNode.prev;
        minNode.prev.next = minNode.next;
        this.size--;


        HeapNode child = minNode.child;
        if (child != null) {
            child.parent = null;
            Heap heap2 = new Heap(this.lazyMelds, this.lazyDecreaseKeys);
            heap2.head = child.item;
            heap2.last = child.prev.item;

            heap2.size = 0; // not adding size in meld

            //finding min in min children O(log n) --> max degree = O(log n)
            HeapNode current = child;
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
            if(lazyMelds) {
                succesiveLinking();
            }
        }
        // update min-> log(n) time beacuse after meld there are o(log n) trees
        HeapNode current = head.node;
        int minKey = head.key;
        min = head;
        current = current.next;
        while (current != child) {
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

        return; // should be replaced by student code
    }

    private void heapifyUp(HeapNode node) {
        if(node.parent == null) {
            return; // node is root
        }
        if(node.item.key < node.parent.item.key) {
            HeapNode temp = new HeapNode(node.item, node.child, node.next, node.prev, node.parent, node.rank); // clone node
            node.child = temp.parent;
            node.parent = temp.parent.parent;
            node.next = temp.parent.next;
            node.prev = temp.parent.prev;
            node.rank = temp.parent.rank;

            
        }
    }
    /**
     * Delete the x from the heap.
     *
     */
    public void delete(HeapItem x) 
    {    
        return; // should be replaced by student code
    }


    /**
     * 
     * Meld the heap with heap2
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     */
    public void meld(Heap heap2)
    {
        // melded heaps
        last.node.next = heap2.head.node;
        heap2.last.node.next = head.node;
        size += heap2.size;

        // update min
        if (heap2.min != null) {
            if (this.min == null || heap2.min.key < this.min.key) {
                this.min = heap2.min;
            }
        }

        // consolidate if not lazy melds
        if (!this.lazyMelds) {
            succesiveLinking();
        } else {
            
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
                bucket[y.rank - 1] = null;  // B[y.rank - 1] ← null
            }
            bucket[y.rank] = y;
        }  
    }
      
    private HeapNode fromBucket(HeapNode[] bucket) {
        // Rebuild root list from buckets
        
        HeapNode x = null;  // new root list head

        for (int i = 0; i < bucket.length; i++) {
            if (bucket[i] != null) {
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
        return size(); // should be replaced by student code
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return 46; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return 46; // should be replaced by student code
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

