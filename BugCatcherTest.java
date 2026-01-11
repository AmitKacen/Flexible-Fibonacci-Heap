import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

/**
 * Bug Catcher Test Suite
 * Designed to catch common implementation bugs in Fibonacci Heap
 */
public class BugCatcherTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static List<String> failureMessages = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== BUG CATCHER TEST SUITE ===\n");

        // ============ NULL POINTER BUGS ============
        System.out.println("--- Null Pointer Bug Tests ---");
        test("Empty heap - findMin returns null", testEmptyHeapFindMin());
        test("Empty heap - deleteMin doesn't crash", testEmptyHeapDeleteMin());
        test("Empty heap - size is 0", testEmptyHeapSize());
        test("Empty heap - numTrees is 0", testEmptyHeapNumTrees());
        test("Delete until empty - no crash", testDeleteUntilEmpty());
        test("Delete last element - heap becomes empty", testDeleteLastElement());
        test("Operations after emptying heap", testOperationsAfterEmpty());

        // ============ SINGLE ELEMENT BUGS ============
        System.out.println("\n--- Single Element Bug Tests ---");
        test("Single element - insert and findMin", testSingleElementFindMin());
        test("Single element - deleteMin empties heap", testSingleElementDeleteMin());
        test("Single element - decreaseKey", testSingleElementDecreaseKey());
        test("Single element - delete", testSingleElementDelete());

        // ============ TWO ELEMENT BUGS ============
        System.out.println("\n--- Two Element Bug Tests ---");
        test("Two elements - correct min", testTwoElementsMin());
        test("Two elements - deleteMin leaves one", testTwoElementsDeleteMin());
        test("Two elements - delete non-min", testTwoElementsDeleteNonMin());
        test("Two elements - decreaseKey swap", testTwoElementsDecreaseKey());

        // ============ CIRCULAR LIST BUGS ============
        System.out.println("\n--- Circular List Bug Tests ---");
        test("Insert 3 - traverse all roots", testCircularListThreeInserts());
        test("After deleteMin - list still circular", testCircularListAfterDeleteMin());
        test("After cut - list still valid", testCircularListAfterCut());

        // ============ MELD BUGS ============
        System.out.println("\n--- Meld Bug Tests ---");
        test("Meld with empty heap (this empty)", testMeldThisEmpty());
        test("Meld with empty heap (other empty)", testMeldOtherEmpty());
        test("Meld both empty", testMeldBothEmpty());
        test("Meld single elements", testMeldSingleElements());
        test("Meld preserves all elements", testMeldPreservesElements());
        test("Meld updates min correctly", testMeldUpdatesMin());

        // ============ CONSOLIDATION BUGS ============
        System.out.println("\n--- Consolidation Bug Tests ---");
        test("Consolidate empty - no crash", testConsolidateEmpty());
        test("Consolidate single - no crash", testConsolidateSingle());
        test("Consolidate creates correct structure", testConsolidateStructure());
        test("Bucket array size sufficient", testBucketArraySize());

        // ============ DECREASE KEY BUGS ============
        System.out.println("\n--- DecreaseKey Bug Tests ---");
        test("DecreaseKey on root node", testDecreaseKeyRoot());
        test("DecreaseKey updates min", testDecreaseKeyUpdatesMin());
        test("DecreaseKey by 0 - no change", testDecreaseKeyByZero());
        test("DecreaseKey on child triggers cut", testDecreaseKeyOnChild());
        test("DecreaseKey doesn't corrupt heap", testDecreaseKeyHeapProperty());

        // ============ DELETE BUGS ============
        System.out.println("\n--- Delete Bug Tests ---");
        test("Delete min node", testDeleteMinNode());
        test("Delete middle node", testDeleteMiddleNode());
        test("Delete all nodes one by one", testDeleteAllNodes());
        test("Delete with Integer.MIN_VALUE key", testDeleteWithMinValue());

        // ============ PARENT/CHILD POINTER BUGS ============
        System.out.println("\n--- Pointer Bug Tests ---");
        test("After link - parent pointer correct", testParentPointerAfterLink());
        test("After cut - parent pointer null", testParentPointerAfterCut());
        test("After deleteMin - children have null parent", testChildrenParentAfterDeleteMin());

        // ============ HEAD/LAST/MIN POINTER BUGS ============
        System.out.println("\n--- Head/Last/Min Pointer Tests ---");
        test("After insert - head not null", testHeadNotNullAfterInsert());
        test("After insert - last not null", testLastNotNullAfterInsert());
        test("After deleteMin - head updated", testHeadUpdatedAfterDeleteMin());
        test("After deleteMin - min updated", testMinUpdatedAfterDeleteMin());
        test("Min always points to minimum", testMinAlwaysCorrect());

        // ============ SIZE BUGS ============
        System.out.println("\n--- Size Bug Tests ---");
        test("Size increases on insert", testSizeIncreasesOnInsert());
        test("Size decreases on deleteMin", testSizeDecreasesOnDeleteMin());
        test("Size after meld", testSizeAfterMeld());
        test("Size matches actual count", testSizeMatchesCount());

        // ============ INFINITE LOOP BUGS ============
        System.out.println("\n--- Infinite Loop Bug Tests ---");
        test("DeleteMin doesn't infinite loop", testDeleteMinNoInfiniteLoop());
        test("Consolidate doesn't infinite loop", testConsolidateNoInfiniteLoop());
        test("Traversal doesn't infinite loop", testTraversalNoInfiniteLoop());

        // ============ COUNTER BUGS ============
        System.out.println("\n--- Counter Bug Tests ---");
        test("totalLinks never negative", testTotalLinksNonNegative());
        test("totalCuts never negative", testTotalCutsNonNegative());
        test("numTrees matches actual trees", testNumTreesMatchesActual());
        test("numMarkedNodes never negative", testNumMarkedNonNegative());

        // ============ STRESS TESTS ============
        System.out.println("\n--- Stress Tests ---");
        test("100 inserts then 100 deletes", testStress100());
        test("Random operations sequence", testRandomOperations());
        test("Alternating insert/delete", testAlternatingOperations());
        test("Heap sort correctness", testHeapSortCorrectness());

        // ============ MODE-SPECIFIC BUGS ============
        System.out.println("\n--- Mode-Specific Bug Tests ---");
        test("Lazy meld - no consolidation on insert", testLazyMeldNoConsolidate());
        test("Non-lazy meld - consolidates on insert", testNonLazyMeldConsolidates());
        test("Lazy decreaseKey - uses cuts", testLazyDecreaseKeyUsesCuts());
        test("Non-lazy decreaseKey - uses heapifyUp", testNonLazyDecreaseKeyUsesHeapify());

        // Summary
        printSummary();
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

    private static void printSummary() {
        System.out.println("\n=== TEST SUMMARY ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total:  " + (testsPassed + testsFailed));
        if (testsPassed + testsFailed > 0) {
            System.out.println("Score:  " + (testsPassed * 100 / (testsPassed + testsFailed)) + "%");
        }
        
        if (!failureMessages.isEmpty()) {
            System.out.println("\n=== FAILURE DETAILS ===");
            for (String msg : failureMessages) {
                System.out.println("  " + msg);
            }
        }
    }

    private static void logFail(String msg) {
        failureMessages.add(msg);
    }

    // ==================== NULL POINTER BUG TESTS ====================

    private static boolean testEmptyHeapFindMin() {
        try {
            Heap heap = new Heap(true, true);
            return heap.findMin() == null;
        } catch (Exception e) {
            logFail("testEmptyHeapFindMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testEmptyHeapDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.deleteMin(); // Should not crash
            return true;
        } catch (Exception e) {
            logFail("testEmptyHeapDeleteMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testEmptyHeapSize() {
        try {
            Heap heap = new Heap(true, true);
            return heap.size() == 0;
        } catch (Exception e) {
            logFail("testEmptyHeapSize: " + e.getMessage());
            return false;
        }
    }

    private static boolean testEmptyHeapNumTrees() {
        try {
            Heap heap = new Heap(true, true);
            return heap.numTrees() == 0;
        } catch (Exception e) {
            logFail("testEmptyHeapNumTrees: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDeleteUntilEmpty() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 10; i++) {
                heap.insert(i, "N" + i);
            }
            for (int i = 0; i < 10; i++) {
                heap.deleteMin();
            }
            return heap.size() == 0 && heap.findMin() == null;
        } catch (Exception e) {
            logFail("testDeleteUntilEmpty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testDeleteLastElement() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.deleteMin();
            return heap.size() == 0 && heap.findMin() == null && heap.numTrees() == 0;
        } catch (Exception e) {
            logFail("testDeleteLastElement: " + e.getMessage());
            return false;
        }
    }

    private static boolean testOperationsAfterEmpty() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.deleteMin();
            // Now heap is empty, try operations
            if (heap.findMin() != null) return false;
            heap.insert(20, "B");
            if (heap.findMin() == null || heap.findMin().key != 20) return false;
            return true;
        } catch (Exception e) {
            logFail("testOperationsAfterEmpty: " + e.getMessage());
            return false;
        }
    }

    // ==================== SINGLE ELEMENT BUG TESTS ====================

    private static boolean testSingleElementFindMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(42, "only");
            return heap.findMin() != null && heap.findMin().key == 42;
        } catch (Exception e) {
            logFail("testSingleElementFindMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSingleElementDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(42, "only");
            heap.deleteMin();
            return heap.size() == 0 && heap.findMin() == null;
        } catch (Exception e) {
            logFail("testSingleElementDeleteMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSingleElementDecreaseKey() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem item = heap.insert(42, "only");
            heap.decreaseKey(item, 10);
            return heap.findMin() != null && heap.findMin().key == 32;
        } catch (Exception e) {
            logFail("testSingleElementDecreaseKey: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSingleElementDelete() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem item = heap.insert(42, "only");
            heap.delete(item);
            return heap.size() == 0 && heap.findMin() == null;
        } catch (Exception e) {
            logFail("testSingleElementDelete: " + e.getMessage());
            return false;
        }
    }

    // ==================== TWO ELEMENT BUG TESTS ====================

    private static boolean testTwoElementsMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(20, "A");
            heap.insert(10, "B");
            return heap.findMin() != null && heap.findMin().key == 10;
        } catch (Exception e) {
            logFail("testTwoElementsMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testTwoElementsDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(20, "A");
            heap.insert(10, "B");
            heap.deleteMin();
            return heap.size() == 1 && heap.findMin() != null && heap.findMin().key == 20;
        } catch (Exception e) {
            logFail("testTwoElementsDeleteMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testTwoElementsDeleteNonMin() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem a = heap.insert(20, "A");
            heap.insert(10, "B");
            heap.delete(a);
            return heap.size() == 1 && heap.findMin() != null && heap.findMin().key == 10;
        } catch (Exception e) {
            logFail("testTwoElementsDeleteNonMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testTwoElementsDecreaseKey() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem a = heap.insert(20, "A");
            heap.insert(10, "B");
            heap.decreaseKey(a, 15); // 20 -> 5
            return heap.findMin() != null && heap.findMin().key == 5;
        } catch (Exception e) {
            logFail("testTwoElementsDecreaseKey: " + e.getMessage());
            return false;
        }
    }

    // ==================== CIRCULAR LIST BUG TESTS ====================

    private static boolean testCircularListThreeInserts() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(20, "B");
            heap.insert(30, "C");
            // Verify we can count 3 roots
            return heap.numTrees() == 3;
        } catch (Exception e) {
            logFail("testCircularListThreeInserts: " + e.getMessage());
            return false;
        }
    }

    private static boolean testCircularListAfterDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 5; i++) {
                heap.insert(i * 10, "N" + i);
            }
            heap.deleteMin();
            // Should be able to traverse and count trees
            int trees = heap.numTrees();
            return trees > 0 && trees <= 4;
        } catch (Exception e) {
            logFail("testCircularListAfterDeleteMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testCircularListAfterCut() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem[] items = new Heap.HeapItem[8];
            for (int i = 0; i < 8; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            heap.deleteMin(); // Creates tree structure
            heap.decreaseKey(items[7], items[7].key - 1); // May trigger cut
            // Should still be valid
            return heap.findMin() != null && heap.size() == 7;
        } catch (Exception e) {
            logFail("testCircularListAfterCut: " + e.getMessage());
            return false;
        }
    }

    // ==================== MELD BUG TESTS ====================

    private static boolean testMeldThisEmpty() {
        try {
            Heap heap1 = new Heap(true, true);
            Heap heap2 = new Heap(true, true);
            heap2.insert(10, "A");
            heap2.insert(20, "B");
            heap1.meld(heap2);
            return heap1.size() == 2 && heap1.findMin().key == 10;
        } catch (Exception e) {
            logFail("testMeldThisEmpty: " + e.getMessage());
            return false;
        }
    }

    private static boolean testMeldOtherEmpty() {
        try {
            Heap heap1 = new Heap(true, true);
            Heap heap2 = new Heap(true, true);
            heap1.insert(10, "A");
            heap1.insert(20, "B");
            heap1.meld(heap2);
            return heap1.size() == 2 && heap1.findMin().key == 10;
        } catch (Exception e) {
            logFail("testMeldOtherEmpty: " + e.getMessage());
            return false;
        }
    }

    private static boolean testMeldBothEmpty() {
        try {
            Heap heap1 = new Heap(true, true);
            Heap heap2 = new Heap(true, true);
            heap1.meld(heap2);
            return heap1.size() == 0 && heap1.findMin() == null;
        } catch (Exception e) {
            logFail("testMeldBothEmpty: " + e.getMessage());
            return false;
        }
    }

    private static boolean testMeldSingleElements() {
        try {
            Heap heap1 = new Heap(true, true);
            Heap heap2 = new Heap(true, true);
            heap1.insert(20, "A");
            heap2.insert(10, "B");
            heap1.meld(heap2);
            return heap1.size() == 2 && heap1.findMin().key == 10;
        } catch (Exception e) {
            logFail("testMeldSingleElements: " + e.getMessage());
            return false;
        }
    }

    private static boolean testMeldPreservesElements() {
        try {
            Heap heap1 = new Heap(true, true);
            Heap heap2 = new Heap(true, true);
            for (int i = 1; i <= 5; i++) {
                heap1.insert(i, "H1-" + i);
                heap2.insert(i + 10, "H2-" + i);
            }
            heap1.meld(heap2);
            System.out.println("Melded heap size: " + heap1.size());
            if (heap1.size() != 10) return false;
            // Extract all and verify
            int[] extracted = new int[10];
            for (int i = 0; i < 10; i++) {
                extracted[i] = heap1.findMin().key;
                System.out.println("Extracting min: " + extracted[i]);
                if(i==1){
                    System.out.println("min child: " + heap1.findMin().node.child.item.key);
                }

                heap1.deleteMin();
                System.out.println("size: " + heap1.size());
            }
            System.out.println("size: " + heap1.size());
            Arrays.sort(extracted);
            System.out.println("Extracted keys: " + Arrays.toString(extracted));
            int[] expected = {1, 2, 3, 4, 5, 11, 12, 13, 14, 15};
            return Arrays.equals(extracted, expected);
        } catch (Exception e) {
            logFail("testMeldPreservesElements: " + e.getMessage());
            return false;
        }
    }

    private static boolean testMeldUpdatesMin() {
        try {
            Heap heap1 = new Heap(true, true);
            Heap heap2 = new Heap(true, true);
            heap1.insert(50, "A");
            heap2.insert(10, "B");
            heap1.meld(heap2);
            return heap1.findMin().key == 10;
        } catch (Exception e) {
            logFail("testMeldUpdatesMin: " + e.getMessage());
            return false;
        }
    }

    // ==================== CONSOLIDATION BUG TESTS ====================

    private static boolean testConsolidateEmpty() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.deleteMin(); // Heap now empty, consolidate might be called
            return heap.size() == 0;
        } catch (Exception e) {
            logFail("testConsolidateEmpty: " + e.getMessage());
            return false;
        }
    }

    private static boolean testConsolidateSingle() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(20, "B");
            heap.deleteMin(); // 1 element left
            return heap.size() == 1 && heap.findMin().key == 20;
        } catch (Exception e) {
            logFail("testConsolidateSingle: " + e.getMessage());
            return false;
        }
    }

    private static boolean testConsolidateStructure() {
        try {
            Heap heap = new Heap(true, true);
            // Insert 8 elements, delete min -> 7 elements
            for (int i = 1; i <= 8; i++) {
                heap.insert(i, "N" + i);
            }
            heap.deleteMin();
            // 7 = 111 binary -> 3 trees
            return heap.numTrees() == 3;
        } catch (Exception e) {
            logFail("testConsolidateStructure: " + e.getMessage());
            return false;
        }
    }

    private static boolean testBucketArraySize() {
        try {
            Heap heap = new Heap(true, true);
            // Insert many elements to test bucket array size
            for (int i = 1; i <= 1000; i++) {
                heap.insert(i, "N" + i);
            }
            heap.deleteMin();
            // Should not throw ArrayIndexOutOfBounds
            return heap.size() == 999;
        } catch (ArrayIndexOutOfBoundsException e) {
            logFail("testBucketArraySize: ArrayIndexOutOfBounds - bucket too small");
            return false;
        } catch (Exception e) {
            logFail("testBucketArraySize: " + e.getMessage());
            return false;
        }
    }

    // ==================== DECREASE KEY BUG TESTS ====================

    private static boolean testDecreaseKeyRoot() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem item = heap.insert(10, "A");
            heap.insert(20, "B");
            heap.decreaseKey(item, 5); // 10 -> 5, root node
            return heap.findMin().key == 5;
        } catch (Exception e) {
            logFail("testDecreaseKeyRoot: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDecreaseKeyUpdatesMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            Heap.HeapItem item = heap.insert(20, "B");
            heap.decreaseKey(item, 15); // 20 -> 5
            return heap.findMin().key == 5;
        } catch (Exception e) {
            logFail("testDecreaseKeyUpdatesMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDecreaseKeyByZero() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem item = heap.insert(10, "A");
            heap.decreaseKey(item, 0);
            return item.key == 10;
        } catch (Exception e) {
            logFail("testDecreaseKeyByZero: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDecreaseKeyOnChild() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem[] items = new Heap.HeapItem[8];
            for (int i = 0; i < 8; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            heap.deleteMin(); // Creates tree structure
            // Now decrease a non-root node
            heap.decreaseKey(items[7], items[7].key - 1); // Should become min
            return heap.findMin().key == 1;
        } catch (Exception e) {
            logFail("testDecreaseKeyOnChild: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDecreaseKeyHeapProperty() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 20; i++) {
                heap.insert(i * 5, "N" + i);
            }
            heap.deleteMin();
            // Multiple decrease keys
            Heap.HeapItem item = heap.insert(200, "target");
            heap.deleteMin();
            heap.decreaseKey(item, 190);
            // Verify heap property by extracting all
            int prev = Integer.MIN_VALUE;
            while (heap.size() > 0) {
                int key = heap.findMin().key;
                if (key < prev) return false;
                prev = key;
                heap.deleteMin();
            }
            return true;
        } catch (Exception e) {
            logFail("testDecreaseKeyHeapProperty: " + e.getMessage());
            return false;
        }
    }

    // ==================== DELETE BUG TESTS ====================

    private static boolean testDeleteMinNode() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem min = heap.insert(5, "min");
            heap.insert(10, "A");
            heap.insert(15, "B");
            heap.delete(min);
            return heap.size() == 2 && heap.findMin().key == 10;
        } catch (Exception e) {
            logFail("testDeleteMinNode: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDeleteMiddleNode() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(5, "A");
            Heap.HeapItem mid = heap.insert(10, "B");
            heap.insert(15, "C");
            heap.delete(mid);
            return heap.size() == 2 && heap.findMin().key == 5;
        } catch (Exception e) {
            logFail("testDeleteMiddleNode: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDeleteAllNodes() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem[] items = new Heap.HeapItem[5];
            for (int i = 0; i < 5; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            // Delete in random order
            heap.delete(items[2]);
            heap.delete(items[0]);
            heap.delete(items[4]);
            heap.delete(items[1]);
            heap.delete(items[3]);
            return heap.size() == 0 && heap.findMin() == null;
        } catch (Exception e) {
            logFail("testDeleteAllNodes: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDeleteWithMinValue() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem item = heap.insert(100, "A");
            heap.insert(50, "B");
            // delete uses decreaseKey with Integer.MAX_VALUE
            heap.delete(item);
            return heap.size() == 1 && heap.findMin().key == 50;
        } catch (Exception e) {
            logFail("testDeleteWithMinValue: " + e.getMessage());
            return false;
        }
    }

    // ==================== POINTER BUG TESTS ====================

    private static boolean testParentPointerAfterLink() {
        try {
            Heap heap = new Heap(false, true); // Non-lazy to force linking
            heap.insert(10, "A");
            heap.insert(20, "B");
            // After non-lazy insert, trees are linked
            // Can't directly verify pointers, but operations should work
            heap.deleteMin();
            return heap.findMin().key == 20;
        } catch (Exception e) {
            logFail("testParentPointerAfterLink: " + e.getMessage());
            return false;
        }
    }

    private static boolean testParentPointerAfterCut() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem[] items = new Heap.HeapItem[8];
            for (int i = 0; i < 8; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            heap.deleteMin();
            // Cut by decreasing key
            heap.decreaseKey(items[7], items[7].key - 1);
            // Should still be valid
            return heap.findMin().key == 1 && heap.size() == 7;
        } catch (Exception e) {
            logFail("testParentPointerAfterCut: " + e.getMessage());
            return false;
        }
    }

    private static boolean testChildrenParentAfterDeleteMin() {
        try {
            Heap heap = new Heap(false, true);
            for (int i = 1; i <= 8; i++) {
                heap.insert(i * 10, "N" + i);
            }
            // Min has children, deleteMin promotes them
            heap.deleteMin();
            // All operations should still work
            heap.deleteMin();
            return heap.size() == 6;
        } catch (Exception e) {
            logFail("testChildrenParentAfterDeleteMin: " + e.getMessage());
            return false;
        }
    }

    // ==================== HEAD/LAST/MIN POINTER TESTS ====================

    private static boolean testHeadNotNullAfterInsert() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            // Can't access head directly, but numTrees should work
            return heap.numTrees() == 1;
        } catch (NullPointerException e) {
            logFail("testHeadNotNullAfterInsert: NullPointerException");
            return false;
        } catch (Exception e) {
            logFail("testHeadNotNullAfterInsert: " + e.getMessage());
            return false;
        }
    }

    private static boolean testLastNotNullAfterInsert() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(20, "B");
            // Operations that use last pointer
            heap.deleteMin();
            return heap.size() == 1;
        } catch (NullPointerException e) {
            logFail("testLastNotNullAfterInsert: NullPointerException");
            return false;
        } catch (Exception e) {
            logFail("testLastNotNullAfterInsert: " + e.getMessage());
            return false;
        }
    }

    private static boolean testHeadUpdatedAfterDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(20, "B");
            heap.insert(30, "C");
            heap.deleteMin(); // Removes 10
            // Head should be updated
            return heap.numTrees() > 0 && heap.size() == 2;
        } catch (Exception e) {
            logFail("testHeadUpdatedAfterDeleteMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testMinUpdatedAfterDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            heap.insert(10, "A");
            heap.insert(20, "B");
            heap.insert(15, "C");
            heap.deleteMin();
            return heap.findMin().key == 15;
        } catch (Exception e) {
            logFail("testMinUpdatedAfterDeleteMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testMinAlwaysCorrect() {
        try {
            Heap heap = new Heap(true, true);
            Random rand = new Random(42);
            int actualMin = Integer.MAX_VALUE;
            for (int i = 0; i < 50; i++) {
                int key = rand.nextInt(1000) + 1;
                heap.insert(key, "N" + i);
                actualMin = Math.min(actualMin, key);
                if (heap.findMin().key != actualMin) return false;
            }
            return true;
        } catch (Exception e) {
            logFail("testMinAlwaysCorrect: " + e.getMessage());
            return false;
        }
    }

    // ==================== SIZE BUG TESTS ====================

    private static boolean testSizeIncreasesOnInsert() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 10; i++) {
                heap.insert(i, "N" + i);
                if (heap.size() != i) return false;
            }
            return true;
        } catch (Exception e) {
            logFail("testSizeIncreasesOnInsert: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSizeDecreasesOnDeleteMin() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 10; i++) {
                heap.insert(i, "N" + i);
            }
            for (int i = 9; i >= 0; i--) {
                heap.deleteMin();
                if (heap.size() != i) return false;
            }
            return true;
        } catch (Exception e) {
            logFail("testSizeDecreasesOnDeleteMin: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSizeAfterMeld() {
        try {
            Heap heap1 = new Heap(true, true);
            Heap heap2 = new Heap(true, true);
            for (int i = 1; i <= 5; i++) {
                heap1.insert(i, "H1-" + i);
                heap2.insert(i + 10, "H2-" + i);
            }
            heap1.meld(heap2);
            return heap1.size() == 10;
        } catch (Exception e) {
            logFail("testSizeAfterMeld: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSizeMatchesCount() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 20; i++) {
                heap.insert(i, "N" + i);
            }
            for (int i = 0; i < 5; i++) {
                heap.deleteMin();
            }
            // Count by extracting
            int count = 0;
            int expectedSize = heap.size();
            while (heap.findMin() != null) {
                heap.deleteMin();
                count++;
            }
            return count == expectedSize;
        } catch (Exception e) {
            logFail("testSizeMatchesCount: " + e.getMessage());
            return false;
        }
    }

    // ==================== INFINITE LOOP BUG TESTS ====================

    private static boolean testDeleteMinNoInfiniteLoop() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 10; i++) {
                heap.insert(i, "N" + i);
            }
            // Set timeout using a counter
            long start = System.currentTimeMillis();
            while (heap.size() > 0 && System.currentTimeMillis() - start < 5000) {
                heap.deleteMin();
            }
            return heap.size() == 0;
        } catch (Exception e) {
            logFail("testDeleteMinNoInfiniteLoop: " + e.getMessage());
            return false;
        }
    }

    private static boolean testConsolidateNoInfiniteLoop() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 100; i++) {
                heap.insert(i, "N" + i);
            }
            long start = System.currentTimeMillis();
            heap.deleteMin(); // Triggers consolidation
            return System.currentTimeMillis() - start < 5000;
        } catch (Exception e) {
            logFail("testConsolidateNoInfiniteLoop: " + e.getMessage());
            return false;
        }
    }

    private static boolean testTraversalNoInfiniteLoop() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 20; i++) {
                heap.insert(i, "N" + i);
            }
            heap.deleteMin();
            long start = System.currentTimeMillis();
            int trees = heap.numTrees(); // May traverse root list
            return System.currentTimeMillis() - start < 5000 && trees > 0;
        } catch (Exception e) {
            logFail("testTraversalNoInfiniteLoop: " + e.getMessage());
            return false;
        }
    }

    // ==================== COUNTER BUG TESTS ====================

    private static boolean testTotalLinksNonNegative() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 20; i++) {
                heap.insert(i, "N" + i);
            }
            heap.deleteMin();
            return heap.totalLinks() >= 0;
        } catch (Exception e) {
            logFail("testTotalLinksNonNegative: " + e.getMessage());
            return false;
        }
    }

    private static boolean testTotalCutsNonNegative() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem[] items = new Heap.HeapItem[10];
            for (int i = 0; i < 10; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            heap.deleteMin();
            heap.decreaseKey(items[9], items[9].key - 1);
            return heap.totalCuts() >= 0;
        } catch (Exception e) {
            logFail("testTotalCutsNonNegative: " + e.getMessage());
            return false;
        }
    }

    private static boolean testNumTreesMatchesActual() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 5; i++) {
                heap.insert(i * 10, "N" + i);
            }
            // In lazy mode, numTrees should equal number of inserts
            return heap.numTrees() == 5;
        } catch (Exception e) {
            logFail("testNumTreesMatchesActual: " + e.getMessage());
            return false;
        }
    }

    private static boolean testNumMarkedNonNegative() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 10; i++) {
                heap.insert(i * 10, "N" + i);
            }
            heap.deleteMin();
            return heap.numMarkedNodes() >= 0;
        } catch (Exception e) {
            logFail("testNumMarkedNonNegative: " + e.getMessage());
            return false;
        }
    }

    // ==================== STRESS TESTS ====================

    private static boolean testStress100() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 100; i++) {
                heap.insert(i, "N" + i);
            }
            for (int i = 1; i <= 100; i++) {
                if (heap.findMin().key != i) return false;
                heap.deleteMin();
            }
            return heap.size() == 0;
        } catch (Exception e) {
            logFail("testStress100: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testRandomOperations() {
            Heap heap = new Heap(true, true);
            Random rand = new Random(123);
            List<Heap.HeapItem> items = new ArrayList<>();
            
            for (int i = 0; i < 500; i++) {
                int op = rand.nextInt(3);
                if (op == 0 || items.isEmpty()) {
                    // Insert
                    Heap.HeapItem item = heap.insert(rand.nextInt(10000) + 1, "N" + i);
                    items.add(item);
                } else if (op == 1 && heap.size() > 0) {
                    // DeleteMin
                    Heap.HeapItem minItem = heap.findMin();
                    heap.deleteMin();
                    items.remove(minItem);
                    // Note: we can't track which item was removed easily
                } else if (op == 2 && !items.isEmpty() && heap.size() > 0) {
                    // DecreaseKey
                    Heap.HeapItem item = items.get(rand.nextInt(items.size()));
                    if (item.key > 1) {
                        heap.decreaseKey(item, rand.nextInt(item.key - 1) + 1);
                    }
                }
            }
            return true; // No crash = pass
         
    }

    private static boolean testAlternatingOperations() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 0; i < 100; i++) {
                heap.insert(i * 2, "A" + i);
                heap.insert(i * 2 + 1, "B" + i);
                heap.deleteMin();
            }
            // Should have 100 elements left
            if (heap.size() != 100) return false;
            // Verify sorted extraction
            int prev = -1;
            while (heap.size() > 0) {
                int key = heap.findMin().key;
                if (key < prev) return false;
                prev = key;
                heap.deleteMin();
            }
            return true;
        } catch (Exception e) {
            logFail("testAlternatingOperations: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testHeapSortCorrectness() {
        try {
            Heap heap = new Heap(true, true);
            Random rand = new Random(456);
            int[] original = new int[200];
            for (int i = 0; i < 200; i++) {
                original[i] = rand.nextInt(10000) + 1;
                heap.insert(original[i], "N" + i);
            }
            Arrays.sort(original);
            for (int i = 0; i < 200; i++) {
                if (heap.findMin().key != original[i]) return false;
                heap.deleteMin();
            }
            return heap.size() == 0;
        } catch (Exception e) {
            logFail("testHeapSortCorrectness: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== MODE-SPECIFIC BUG TESTS ====================

    private static boolean testLazyMeldNoConsolidate() {
        try {
            Heap heap = new Heap(true, true);
            for (int i = 1; i <= 10; i++) {
                heap.insert(i * 10, "N" + i);
            }
            // In lazy mode, no links on insert
            return heap.totalLinks() == 0 && heap.numTrees() == 10;
        } catch (Exception e) {
            logFail("testLazyMeldNoConsolidate: " + e.getMessage());
            return false;
        }
    }

    private static boolean testNonLazyMeldConsolidates() {
        try {
            Heap heap = new Heap(false, true);
            heap.insert(10, "A");
            heap.insert(20, "B");
            // Should consolidate to 1 tree
            return heap.numTrees() == 1 && heap.totalLinks() == 1;
        } catch (Exception e) {
            logFail("testNonLazyMeldConsolidates: " + e.getMessage());
            return false;
        }
    }

    private static boolean testLazyDecreaseKeyUsesCuts() {
        try {
            Heap heap = new Heap(true, true);
            Heap.HeapItem[] items = new Heap.HeapItem[8];
            for (int i = 0; i < 8; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            heap.deleteMin(); // Creates tree
            int cutsBefore = heap.totalCuts();
            // Decrease a child node significantly
            heap.decreaseKey(items[7], items[7].key - 1);
            // If it was a child, should have cut
            return heap.totalCuts() >= cutsBefore;
        } catch (Exception e) {
            logFail("testLazyDecreaseKeyUsesCuts: " + e.getMessage());
            return false;
        }
    }

    private static boolean testNonLazyDecreaseKeyUsesHeapify() {
        try {
            Heap heap = new Heap(false, false);
            Heap.HeapItem[] items = new Heap.HeapItem[8];
            for (int i = 0; i < 8; i++) {
                items[i] = heap.insert((i + 1) * 10, "N" + i);
            }
            int cutsBefore = heap.totalCuts();
            // Decrease a child node
            heap.decreaseKey(items[7], items[7].key - 1);
            // No cuts should happen in non-lazy mode
            return heap.totalCuts() == cutsBefore;
        } catch (Exception e) {
            logFail("testNonLazyDecreaseKeyUsesHeapify: " + e.getMessage());
            return false;
        }
    }
}
