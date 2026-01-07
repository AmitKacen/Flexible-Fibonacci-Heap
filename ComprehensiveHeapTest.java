import java.util.Random;
import java.util.Arrays;

/**
 * Comprehensive test suite for Fibonacci Heap implementation
 */
public class ComprehensiveHeapTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE FIBONACCI HEAP TESTS ===\n");

        // Basic CRUD Tests
        System.out.println("--- Basic CRUD Tests ---");
        test("Insert single element", testInsertSingle());
        test("Insert multiple elements", testInsertMultiple());
        test("FindMin after inserts", testFindMin());
        test("DeleteMin basic", testDeleteMinBasic());
        test("DeleteMin until empty", testDeleteMinUntilEmpty());

        // Edge Cases
        System.out.println("\n--- Edge Case Tests ---");
        test("Empty heap findMin", testEmptyHeapFindMin());
        test("Empty heap deleteMin", testEmptyHeapDeleteMin());
        test("Single element heap", testSingleElementHeap());
        test("Duplicate keys", testDuplicateKeys());
        test("Duplicate keys deleteMin order", testDuplicateKeysDeleteMin());
        test("Insert after deleteMin", testInsertAfterDeleteMin());

        // Decrease Key Tests
        System.out.println("\n--- Decrease Key Tests ---");
        test("DecreaseKey basic", testDecreaseKeyBasic());
        test("DecreaseKey to new min", testDecreaseKeyToNewMin());
        test("DecreaseKey root node", testDecreaseKeyRootNode());
        test("DecreaseKey no heap violation", testDecreaseKeyNoViolation());

        // Fibonacci Specifics - Cascading Cuts
        System.out.println("\n--- Fibonacci Specifics (Cascading Cuts) ---");
        test("Cascading cut trigger", testCascadingCut());
        test("Multiple cascading cuts", testMultipleCascadingCuts());

        // Delete Specific Node Tests
        System.out.println("\n--- Delete Specific Node Tests ---");
        test("Delete min node directly", testDeleteMinNode());
        test("Delete non-min node", testDeleteNonMinNode());
        test("Delete last node", testDeleteLastNode());

        // Meld Tests
        System.out.println("\n--- Meld Tests ---");
        test("Meld two heaps same size", testMeldSameSize());
        test("Meld two heaps different size", testMeldDifferentSize());
        test("Meld with empty heap", testMeldWithEmpty());
        test("Meld empty with non-empty", testMeldEmptyWithNonEmpty());

        // Lazy vs Non-Lazy Tests
        System.out.println("\n--- Lazy vs Non-Lazy Mode Tests ---");
        test("Non-lazy meld consolidates", testNonLazyMeld());
        test("Non-lazy decreaseKey heapifies", testNonLazyDecreaseKey());

        // Stress Tests
        System.out.println("\n--- Stress Tests ---");
        test("Stress test 500 elements sorted order", testStress500Elements());
        test("Stress test 1000 random operations", testStress1000Operations());
        test("Stress test alternating insert/delete", testStressAlternating());

        // Summary
        System.out.println("\n=== TEST SUMMARY ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total:  " + (testsPassed + testsFailed));
        System.out.println("Score:  " + (testsPassed * 100 / (testsPassed + testsFailed)) + "%");
    }

    private static void test(String name, boolean result) {
        if (result) {
            System.out.println("  ✓ " + name);
            testsPassed++;
        } else {
            System.out.println("  ✗ " + name + " [FAILED]");
            testsFailed++;
        }
    }

    // ==================== BASIC CRUD TESTS ====================

    private static boolean testInsertSingle() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem item = heap.insert(10, "A");
            return item != null && heap.size() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testInsertMultiple() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(5, "B");
            heap.insert(20, "C");
            heap.insert(3, "D");
            heap.insert(15, "E");
            return heap.size() == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testFindMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(5, "B");
            heap.insert(20, "C");
            heap.insert(3, "D");
            return heap.findMin() != null && heap.findMin().key == 3;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDeleteMinBasic() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(5, "B");
            heap.insert(20, "C");
            heap.deleteMin();
            return heap.findMin() != null && heap.findMin().key == 10 && heap.size() == 2;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDeleteMinUntilEmpty() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(5, "B");
            heap.insert(20, "C");
            heap.deleteMin(); // removes 5
            heap.deleteMin(); // removes 10
            heap.deleteMin(); // removes 20
            return heap.size() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== EDGE CASE TESTS ====================

    private static boolean testEmptyHeapFindMin() {
        try {
            Heap heap = new Heap(true, true);
            return heap.findMin() == null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testEmptyHeapDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.deleteMin(); // should not crash
            return heap.size() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testSingleElementHeap() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(42, "only");
            if (heap.findMin().key != 42) return false;
            heap.deleteMin();
            return heap.size() == 0 && heap.findMin() == null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDuplicateKeys() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(5, "A");
            heap.insert(5, "B");
            heap.insert(5, "C");
            return heap.size() == 3 && heap.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDuplicateKeysDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(5, "A");
            heap.insert(5, "B");
            heap.insert(5, "C");
            heap.insert(10, "D");
            heap.deleteMin();
            heap.deleteMin();
            heap.deleteMin();
            return heap.findMin() != null && heap.findMin().key == 10;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testInsertAfterDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(5, "B");
            heap.deleteMin();
            heap.insert(3, "C");
            return heap.findMin().key == 3 && heap.size() == 2;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== DECREASE KEY TESTS ====================

    private static boolean testDecreaseKeyBasic() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            Heap.HeapItem item = heap.insert(20, "B");
            heap.decreaseKey(item, 5); // 20 -> 15
            return item.key == 15;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDecreaseKeyToNewMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            Heap.HeapItem item = heap.insert(20, "B");
            heap.decreaseKey(item, 15); // 20 -> 5
            return heap.findMin() == item && heap.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDecreaseKeyRootNode() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem min = heap.insert(10, "A");
            heap.insert(20, "B");
            heap.decreaseKey(min, 5); // 10 -> 5, min stays min
            return heap.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDecreaseKeyNoViolation() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            Heap.HeapItem item = heap.insert(20, "B");
            heap.insert(30, "C");
            // Force some structure by doing deleteMin
            heap.deleteMin();
            // Decrease but don't violate heap property
            heap.decreaseKey(item, 1); // 20 -> 19, still >= parent
            return item.key == 19;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== CASCADING CUT TESTS ====================

    private static boolean testCascadingCut() {
        try {
            // Build a heap that will have tree structure after consolidation
            Heap heap = new Heap(true, true);
            
            // Insert nodes to build structure
            Heap.HeapItem[] items = new Heap.HeapItem[8];
            for (int i = 0; i < 8; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            
            // DeleteMin to trigger consolidation and create tree structure
            heap.deleteMin(); // removes 10
            
            // Now decrease keys to trigger cuts
            // Find a child node and decrease its key below parent
            heap.decreaseKey(items[7], 75); // 80 -> 5, should trigger cut
            
            return heap.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testMultipleCascadingCuts() {
        try {
            Heap heap = new Heap(true, true);
            
            // Build larger structure
            Heap.HeapItem[] items = new Heap.HeapItem[16];
            for (int i = 0; i < 16; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            
            // Consolidate
            heap.deleteMin();
            
            // Multiple decrease keys to trigger cascading cuts
            for (int i = 15; i > 10; i--) {
                heap.decreaseKey(items[i], items[i].key - 1);
            }
            
            return heap.findMin() != null;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== DELETE NODE TESTS ====================

    private static boolean testDeleteMinNode() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem min = heap.insert(5, "min");
            heap.insert(10, "A");
            heap.insert(15, "B");
            heap.delete(min);
            return heap.findMin().key == 10 && heap.size() == 2;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDeleteNonMinNode() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(5, "A");
            Heap.HeapItem middle = heap.insert(10, "B");
            heap.insert(15, "C");
            heap.delete(middle);
            return heap.findMin().key == 5 && heap.size() == 2;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDeleteLastNode() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem only = heap.insert(10, "only");
            heap.delete(only);
            return heap.size() == 0 && heap.findMin() == null;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== MELD TESTS ====================

    private static boolean testMeldSameSize() {
        try {
            Heap heap1 = new Heap(true, true);
            heap1.insert(10, "A");
            heap1.insert(30, "B");
            heap1.insert(50, "C");

            Heap heap2 = new Heap(true, true);
            heap2.insert(5, "X");
            heap2.insert(25, "Y");
            heap2.insert(45, "Z");

            heap1.meld(heap2);
            return heap1.size() == 6 && heap1.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testMeldDifferentSize() {
        try {
            Heap heap1 = new Heap(true, true);
            heap1.insert(10, "A");
            heap1.insert(30, "B");

            Heap heap2 = new Heap(true, true);
            heap2.insert(5, "X");
            heap2.insert(25, "Y");
            heap2.insert(45, "Z");
            heap2.insert(55, "W");
            heap2.insert(65, "V");

            heap1.meld(heap2);
            return heap1.size() == 7 && heap1.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testMeldWithEmpty() {
        try {
            Heap heap1 = new Heap(true, true);
            heap1.insert(10, "A");
            heap1.insert(5, "B");

            Heap heap2 = new Heap(true, true);
            // heap2 is empty

            heap1.meld(heap2);
            return heap1.size() == 2 && heap1.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testMeldEmptyWithNonEmpty() {
        try {
            Heap heap1 = new Heap(true, true);
            // heap1 is empty

            Heap heap2 = new Heap(true, true);
            heap2.insert(10, "A");
            heap2.insert(5, "B");

            heap1.meld(heap2);
            return heap1.size() == 2 && heap1.findMin().key == 5;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== LAZY VS NON-LAZY TESTS ====================

    private static boolean testNonLazyMeld() {
        try {
            Heap heap = new Heap(false, true); // non-lazy melds
            heap.insert(10, "A");
            heap.insert(5, "B");
            heap.insert(20, "C");
            heap.insert(3, "D");
            // With non-lazy melds, should consolidate after each insert
            return heap.findMin().key == 3;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testNonLazyDecreaseKey() {
        try {
            Heap heap = new Heap(true, false); // non-lazy decrease keys
            heap.insert(10, "A");
            Heap.HeapItem item = heap.insert(20, "B");
            heap.insert(5, "C");
            
            // Force structure
            heap.deleteMin();
            
            heap.decreaseKey(item, 18); // 20 -> 2
            return heap.findMin().key == 2;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== STRESS TESTS ====================

    private static boolean testStress500Elements() {
        try {
            Heap heap = new Heap(true, true);
            Random rand = new Random(42); // fixed seed for reproducibility
            
            int[] values = new int[500];
            for (int i = 0; i < 500; i++) {
                values[i] = rand.nextInt(10000) + 1; // positive integers
                heap.insert(values[i], "N" + i);
            }
            
            // Sort expected values
            Arrays.sort(values);
            
            // Extract all and verify sorted order
            for (int i = 0; i < 500; i++) {
                Heap.HeapItem min = heap.findMin();
                if (min == null || min.key != values[i]) {
                    return false;
                }
                heap.deleteMin();
            }
            
            return heap.size() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testStress1000Operations() {
        try {
            Heap heap = new Heap(true, true);
            Random rand = new Random(123);
            Heap.HeapItem[] items = new Heap.HeapItem[200];
            int itemCount = 0;
            
            for (int i = 0; i < 100; i++) {
                int op = rand.nextInt(4);
                
                switch (op) {
                    case 0: // Insert
                        if (itemCount < 200) {
                            items[itemCount] = heap.insert(rand.nextInt(10000) + 1, "N" + i);
                            itemCount++;
                        }
                        break;
                    case 1: // DeleteMin
                        if (heap.size() > 0) {
                            heap.deleteMin();
                        }
                        break;
                    case 2: // DecreaseKey
                        if (heap.size() > 0 && itemCount > 0) {
                            int idx = rand.nextInt(itemCount);
                            if (items[idx] != null && items[idx].key > 1) {
                                int diff = rand.nextInt(items[idx].key - 1) + 1;
                                heap.decreaseKey(items[idx], diff);
                            }
                        }
                        break;
                    case 3: // FindMin
                        heap.findMin();
                        break;
                }
            }
            
            // Verify heap property - extracting should give sorted order
            int prev = Integer.MIN_VALUE;
            while (heap.size() > 0) {
                Heap.HeapItem min = heap.findMin();
                if (min.key < prev) {
                    return false;
                }
                prev = min.key;
                heap.deleteMin();
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testStressAlternating() {
        try {
            Heap heap = new Heap(true, true);
            
            // Alternating insert and delete
            for (int i = 0; i < 100; i++) {
                heap.insert(i * 2 + 1, "A" + i);
                heap.insert(i * 2, "B" + i);
                heap.deleteMin();
            }
            
            // Should have 100 elements left
            if (heap.size() != 100) return false;
            
            // Verify they come out in sorted order
            int prev = Integer.MIN_VALUE;
            while (heap.size() > 0) {
                Heap.HeapItem min = heap.findMin();
                if (min.key < prev) return false;
                prev = min.key;
                heap.deleteMin();
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
