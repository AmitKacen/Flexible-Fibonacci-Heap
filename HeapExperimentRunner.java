import java.util.Random;

/**
 * Experimental Runner for Heap implementations
 * Tests four heap types: Regular Binomial, Lazy Binomial, Fibonacci, Binomial with Cuts
 */
public class HeapExperimentRunner {

    // Experimental constants
    private static final int N = 464646;
    private static final int NUM_ITERATIONS = 20;

    // Heap type configurations
    private static final boolean[][] HEAP_CONFIGS = {
        {false, false},  // Regular Binomial
        {true, false},   // Lazy Binomial
        {true, true},    // Fibonacci
        {false, true}    // Binomial with Cuts
    };

    private static final String[] HEAP_NAMES = {
        "Regular Binomial",
        "Lazy Binomial",
        "Fibonacci",
        "Binomial with Cuts"
    };

    /**
     * Generates a random permutation of integers 1 to n
     */
    private static int[] generateRandomPermutation(int n, Random rand) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i + 1;
        }
        // Fisher-Yates shuffle
        for (int i = n - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    /**
     * Result class to store metrics from each run
     */
    private static class ExperimentResult {
        long executionTime;
        int finalSize;
        int numTrees;
        int totalLinks;
        int totalCuts;
        int totalHeapifyCosts;
        int maxOperationCost;

        public ExperimentResult() {
            this.executionTime = 0;
            this.finalSize = 0;
            this.numTrees = 0;
            this.totalLinks = 0;
            this.totalCuts = 0;
            this.totalHeapifyCosts = 0;
            this.maxOperationCost = 0;
        }

        public void add(ExperimentResult other) {
            this.executionTime += other.executionTime;
            this.finalSize += other.finalSize;
            this.numTrees += other.numTrees;
            this.totalLinks += other.totalLinks;
            this.totalCuts += other.totalCuts;
            this.totalHeapifyCosts += other.totalHeapifyCosts;
            this.maxOperationCost += other.maxOperationCost;
        }

        public void divideBy(int divisor) {
            this.executionTime /= divisor;
            this.finalSize /= divisor;
            this.numTrees /= divisor;
            this.totalLinks /= divisor;
            this.totalCuts /= divisor;
            this.totalHeapifyCosts /= divisor;
            this.maxOperationCost /= divisor;
        }
    }

    /**
     * Helper method to calculate the cost of an operation
     * Cost = links + cuts + heapifyCosts performed during the operation
     */
    private static int getOperationCost(int linksBefore, int cutsBefore, int heapifyBefore,
                                         int linksAfter, int cutsAfter, int heapifyAfter) {
        return (linksAfter - linksBefore) + (cutsAfter - cutsBefore) + (heapifyAfter - heapifyBefore);
    }

    /**
     * Experiment 1: Insert n elements in random order, then perform deleteMin() once
     */
    private static ExperimentResult runExperiment1(boolean lazyMelds, boolean lazyDecreaseKeys, int[] permutation) {
        ExperimentResult result = new ExperimentResult();
        
        long startTime = System.currentTimeMillis();
        
        Heap heap = new Heap(lazyMelds, lazyDecreaseKeys);
        @SuppressWarnings("unused")
        Heap.HeapItem[] nodes = new Heap.HeapItem[N + 1]; // nodes[i] points to node with key i
        
        int maxCost = 0;
        
        // Insert n elements
        for (int i = 0; i < N; i++) {
            int linksBefore = heap.totalLinks;
            int cutsBefore = heap.totalCuts;
            int heapifyBefore = heap.totalHeapifyCosts;
            
            nodes[permutation[i]] = heap.insert(permutation[i], "");
            
            int cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                        heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
            maxCost = Math.max(maxCost, cost);
        }
        
        // Perform deleteMin once
        int linksBefore = heap.totalLinks;
        int cutsBefore = heap.totalCuts;
        int heapifyBefore = heap.totalHeapifyCosts;
        
        heap.deleteMin();
        
        int cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                    heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
        maxCost = Math.max(maxCost, cost);
        
        long endTime = System.currentTimeMillis();
        
        result.executionTime = endTime - startTime;
        result.finalSize = heap.size();
        result.numTrees = heap.numTrees();
        result.totalLinks = heap.totalLinks();
        result.totalCuts = heap.totalCuts();
        result.totalHeapifyCosts = heap.totalHeapifyCosts();
        result.maxOperationCost = maxCost;
        
        return result;
    }

    /**
     * Experiment 2: Insert n elements, deleteMin, then delete max keys until 46 elements remain
     */
    private static ExperimentResult runExperiment2(boolean lazyMelds, boolean lazyDecreaseKeys, int[] permutation) {
        ExperimentResult result = new ExperimentResult();
        
        long startTime = System.currentTimeMillis();
        
        Heap heap = new Heap(lazyMelds, lazyDecreaseKeys);
        Heap.HeapItem[] nodes = new Heap.HeapItem[N + 1]; // nodes[i] points to node with key i
        
        int maxCost = 0;
        
        // Insert n elements
        for (int i = 0; i < N; i++) {
            int linksBefore = heap.totalLinks;
            int cutsBefore = heap.totalCuts;
            int heapifyBefore = heap.totalHeapifyCosts;
            
            nodes[permutation[i]] = heap.insert(permutation[i], "");
            
            int cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                        heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
            maxCost = Math.max(maxCost, cost);
        }
        
        // Perform deleteMin
        int linksBefore = heap.totalLinks;
        int cutsBefore = heap.totalCuts;
        int heapifyBefore = heap.totalHeapifyCosts;
        
        heap.deleteMin();
        
        int cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                    heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
        maxCost = Math.max(maxCost, cost);
        
        // Delete maximum keys until only 46 elements remain
        // After deleteMin, the minimum element (1) is gone, so we start from N
        int currentKey = N;
        while (heap.size() > 46) {
            // Skip if this key was already deleted (key 1 was deleted by deleteMin)
            if (nodes[currentKey] != null) {
                linksBefore = heap.totalLinks;
                cutsBefore = heap.totalCuts;
                heapifyBefore = heap.totalHeapifyCosts;
                
                try {
                    heap.delete(nodes[currentKey]);
                } catch (Exception e) {
                    System.err.println("=== CRASH IN delete ===");
                    System.err.println("lazyMelds=" + lazyMelds + ", lazyDecreaseKeys=" + lazyDecreaseKeys);
                    System.err.println("heap.size=" + heap.size() + ", heap.numTrees=" + heap.numTrees);
                    System.err.println("currentKey=" + currentKey);
                    throw e;
                }
                nodes[currentKey] = null;
                
                cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                        heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
                maxCost = Math.max(maxCost, cost);
            }
            currentKey--;
        }
        
        long endTime = System.currentTimeMillis();
        
        result.executionTime = endTime - startTime;
        result.finalSize = heap.size();
        result.numTrees = heap.numTrees();
        result.totalLinks = heap.totalLinks();
        result.totalCuts = heap.totalCuts();
        result.totalHeapifyCosts = heap.totalHeapifyCosts();
        result.maxOperationCost = maxCost;
        
        return result;
    }

    /**
     * Experiment 3: Insert n elements, deleteMin, decreaseKey for 10% largest to 0, deleteMin again
     */
    private static ExperimentResult runExperiment3(boolean lazyMelds, boolean lazyDecreaseKeys, int[] permutation) {
        ExperimentResult result = new ExperimentResult();
        
        long startTime = System.currentTimeMillis();
        
        Heap heap = new Heap(lazyMelds, lazyDecreaseKeys);
        Heap.HeapItem[] nodes = new Heap.HeapItem[N + 1]; // nodes[i] points to node with key i
        
        int maxCost = 0;
        
        // Insert n elements
        for (int i = 0; i < N; i++) {
            int linksBefore = heap.totalLinks;
            int cutsBefore = heap.totalCuts;
            int heapifyBefore = heap.totalHeapifyCosts;
            
            nodes[permutation[i]] = heap.insert(permutation[i], "");
            
            int cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                        heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
            maxCost = Math.max(maxCost, cost);
        }
        
        // Perform first deleteMin
        int linksBefore = heap.totalLinks;
        int cutsBefore = heap.totalCuts;
        int heapifyBefore = heap.totalHeapifyCosts;
        
        heap.deleteMin();
        nodes[1] = null; // Key 1 was deleted
        
        int cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                    heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
        maxCost = Math.max(maxCost, cost);
        
        // Decrease key to 0 for the 10% largest elements
        // 10% of N = N/10 elements, these are keys from (N - N/10 + 1) to N
        int tenPercent = N / 10;
        int startKey = N - tenPercent + 1;
        
        for (int key = startKey; key <= N; key++) {
            if (nodes[key] != null) {
                linksBefore = heap.totalLinks;
                cutsBefore = heap.totalCuts;
                heapifyBefore = heap.totalHeapifyCosts;
                
                // Decrease key to 0 (decrease by the current key value)
                heap.decreaseKey(nodes[key], nodes[key].key);
                
                cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                        heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
                maxCost = Math.max(maxCost, cost);
            }
        }
        
        // Perform second deleteMin
        linksBefore = heap.totalLinks;
        cutsBefore = heap.totalCuts;
        heapifyBefore = heap.totalHeapifyCosts;
        
        heap.deleteMin();
        
        cost = getOperationCost(linksBefore, cutsBefore, heapifyBefore,
                                heap.totalLinks, heap.totalCuts, heap.totalHeapifyCosts);
        maxCost = Math.max(maxCost, cost);
        
        long endTime = System.currentTimeMillis();
        
        result.executionTime = endTime - startTime;
        result.finalSize = heap.size();
        result.numTrees = heap.numTrees();
        result.totalLinks = heap.totalLinks();
        result.totalCuts = heap.totalCuts();
        result.totalHeapifyCosts = heap.totalHeapifyCosts();
        result.maxOperationCost = maxCost;
        
        return result;
    }

    /**
     * Print formatted table header
     */
    private static void printTableHeader() {
        System.out.println("+" + "-".repeat(22) + "+" + "-".repeat(14) + "+" + "-".repeat(12) + "+" 
                         + "-".repeat(12) + "+" + "-".repeat(14) + "+" + "-".repeat(14) + "+" 
                         + "-".repeat(18) + "+" + "-".repeat(16) + "+");
        System.out.printf("| %-20s | %-12s | %-10s | %-10s | %-12s | %-12s | %-16s | %-14s |%n",
                         "Heap Type", "Time (ms)", "Final Size", "Num Trees", 
                         "Total Links", "Total Cuts", "Total Heapify", "Max Op Cost");
        System.out.println("+" + "-".repeat(22) + "+" + "-".repeat(14) + "+" + "-".repeat(12) + "+" 
                         + "-".repeat(12) + "+" + "-".repeat(14) + "+" + "-".repeat(14) + "+" 
                         + "-".repeat(18) + "+" + "-".repeat(16) + "+");
    }

    /**
     * Print a result row
     */
    private static void printResultRow(String heapName, ExperimentResult result) {
        System.out.printf("| %-20s | %12d | %10d | %10d | %12d | %12d | %16d | %14d |%n",
                         heapName, result.executionTime, result.finalSize, result.numTrees,
                         result.totalLinks, result.totalCuts, result.totalHeapifyCosts, 
                         result.maxOperationCost);
    }

    /**
     * Print table footer
     */
    private static void printTableFooter() {
        System.out.println("+" + "-".repeat(22) + "+" + "-".repeat(14) + "+" + "-".repeat(12) + "+" 
                         + "-".repeat(12) + "+" + "-".repeat(14) + "+" + "-".repeat(14) + "+" 
                         + "-".repeat(18) + "+" + "-".repeat(16) + "+");
    }

    public static void main(String[] args) {
        Random rand = new Random(); // Random seed for different results each run
        
        System.out.println("=".repeat(140));
        System.out.println("HEAP EXPERIMENT RUNNER");
        System.out.println("n = " + N + ", Iterations = " + NUM_ITERATIONS);
        System.out.println("=".repeat(140));
        
        // Run all experiments
        for (int exp = 1; exp <= 3; exp++) {
            System.out.println();
            System.out.println("*".repeat(140));
            System.out.print("EXPERIMENT " + exp + ": ");
            switch (exp) {
                case 1:
                    System.out.println("Insert n elements in random order, then deleteMin() once");
                    break;
                case 2:
                    System.out.println("Insert n elements, deleteMin(), delete max keys until 46 elements remain");
                    break;
                case 3:
                    System.out.println("Insert n elements, deleteMin(), decreaseKey to 0 for 10% largest, deleteMin() again");
                    break;
            }
            System.out.println("*".repeat(140));
            
            ExperimentResult[] avgResults = new ExperimentResult[4];
            for (int i = 0; i < 4; i++) {
                avgResults[i] = new ExperimentResult();
            }
            
            // Run NUM_ITERATIONS iterations and accumulate results
            for (int iter = 0; iter < NUM_ITERATIONS; iter++) {
                int[] permutation = generateRandomPermutation(N, rand);
                
                for (int heapType = 0; heapType < 4; heapType++) {
                    boolean lazyMelds = HEAP_CONFIGS[heapType][0];
                    boolean lazyDecreaseKeys = HEAP_CONFIGS[heapType][1];
                    
                    ExperimentResult result;
                    switch (exp) {
                        case 1:
                            result = runExperiment1(lazyMelds, lazyDecreaseKeys, permutation);
                            break;
                        case 2:
                            result = runExperiment2(lazyMelds, lazyDecreaseKeys, permutation);
                            break;
                        case 3:
                            result = runExperiment3(lazyMelds, lazyDecreaseKeys, permutation);
                            break;
                        default:
                            result = new ExperimentResult();
                    }
                    avgResults[heapType].add(result);
                }
                
                // Progress indicator
                if ((iter + 1) % 5 == 0) {
                    System.out.println("  Completed iteration " + (iter + 1) + "/" + NUM_ITERATIONS);
                }
            }
            
            // Calculate averages
            for (int i = 0; i < 4; i++) {
                avgResults[i].divideBy(NUM_ITERATIONS);
            }
            
            // Print results table
            System.out.println();
            System.out.println("Results (Averaged over " + NUM_ITERATIONS + " iterations):");
            printTableHeader();
            for (int heapType = 0; heapType < 4; heapType++) {
                printResultRow(HEAP_NAMES[heapType], avgResults[heapType]);
            }
            printTableFooter();
        }
        
        System.out.println();
        System.out.println("=".repeat(140));
        System.out.println("All experiments completed!");
        System.out.println("=".repeat(140));
    }
}
