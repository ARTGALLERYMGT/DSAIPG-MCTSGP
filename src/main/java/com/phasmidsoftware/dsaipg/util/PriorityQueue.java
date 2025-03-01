package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;

public class PriorityQueue<K extends Comparable<K>> {
    private final int maxCapacity;
    private final Comparator<K> comparator;
    private final int heapType;
    private int last;
    private K highestPrioritySpilled = null;
    private final boolean floyd;
    private final K[] binHeap;

    // Fibonacci Heap Members
    private FibonacciNode<K> fibMin;
    private int fibSize;
    private final Map<K, FibonacciNode<K>> fibNodes;

    private static class FibonacciNode<K> {
        K key;
        double priority;
        FibonacciNode<K> parent, child, left, right;
        int degree;
        boolean mark;

        FibonacciNode(K key, double priority) {
            this.key = key;
            this.priority = priority;
            this.left = this;
            this.right = this;
        }
    }

    @SuppressWarnings("unchecked")
    public PriorityQueue(int maxCapacity, int heapType, Comparator<K> comparator) {
        this.maxCapacity = maxCapacity;
        this.comparator = comparator;
        this.heapType = heapType;
        this.binHeap = heapType == 5 ? null : (K[]) new Comparable[maxCapacity + 1];
        this.last = 0;
        this.floyd = (heapType == 2 || heapType == 4);
        this.fibMin = null;
        this.fibSize = 0;
        this.fibNodes = heapType == 5 ? new HashMap<>() : null;
    }

    public void insert(K key) {
        if (heapType == 5) {
            insertFibonacci(key, Math.random());
        } else {
            if (last >= maxCapacity) {
                K removed = remove();
                if (highestPrioritySpilled == null || comparator.compare(removed, highestPrioritySpilled) > 0) {
                    highestPrioritySpilled = removed;
                }
            }
            binHeap[++last] = key;
        }
    }

    public K remove() {
        if (heapType == 5) {
            return removeFibonacci();
        } else {
            if (last == 0) throw new RuntimeException("Heap is empty");
            K root = binHeap[1];
            binHeap[1] = binHeap[last--];
            return root;
        }
    }

    // 🚀 Optimized Fibonacci Heap Insert
    private void insertFibonacci(K key, double priority) {
        FibonacciNode<K> node = new FibonacciNode<>(key, priority);
        fibNodes.put(key, node);
        if (fibMin == null) {
            fibMin = node;
        } else {
            insertIntoFibRootList(node);
            if (priority < fibMin.priority) {
                fibMin = node;
            }
        }
        fibSize++;

        // 🚀 If exceeding max size, remove element
        if (fibSize > maxCapacity) {
            K removed = removeFibonacci();
            if (highestPrioritySpilled == null || comparator.compare(removed, highestPrioritySpilled) > 0) {
                highestPrioritySpilled = removed;
            }
        }
    }

    // 🚀 Optimized Fibonacci Heap Remove
    private K removeFibonacci() {
        if (fibMin == null) return null;
        FibonacciNode<K> oldMin = fibMin;
        if (fibMin.child != null) {
            FibonacciNode<K> child = fibMin.child;
            do {
                FibonacciNode<K> next = child.right;
                insertIntoFibRootList(child);
                child.parent = null;
                child = next;
            } while (child != fibMin.child);
        }

        removeFromFibRootList(fibMin);
        fibNodes.remove(fibMin.key);
        fibSize--;

        if (fibMin == fibMin.right) {
            fibMin = null;
        } else {
            fibMin = fibMin.right;
            consolidateFibonacci();
        }

        return oldMin.key;
    }

    private void insertIntoFibRootList(FibonacciNode<K> node) {
        if (fibMin == null) {
            fibMin = node;
        } else {
            node.right = fibMin.right;
            node.left = fibMin;
            fibMin.right.left = node;
            fibMin.right = node;
        }
    }

    private void removeFromFibRootList(FibonacciNode<K> node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private void consolidateFibonacci() {
        Map<Integer, FibonacciNode<K>> degreeTable = new HashMap<>();
        List<FibonacciNode<K>> rootNodes = new ArrayList<>();
        FibonacciNode<K> current = fibMin;

        do {
            rootNodes.add(current);
            current = current.right;
        } while (current != fibMin);

        for (FibonacciNode<K> node : rootNodes) {
            int d = node.degree;
            while (degreeTable.containsKey(d)) {
                FibonacciNode<K> y = degreeTable.get(d);
                if (node.priority > y.priority) {
                    FibonacciNode<K> temp = node;
                    node = y;
                    y = temp;
                }
                linkFibonacci(y, node);
                degreeTable.remove(d);
                d++;
            }
            degreeTable.put(d, node);
        }

        fibMin = null;
        for (FibonacciNode<K> node : degreeTable.values()) {
            if (fibMin == null || node.priority < fibMin.priority) {
                fibMin = node;
            }
        }
    }

    private void linkFibonacci(FibonacciNode<K> y, FibonacciNode<K> x) {
        removeFromFibRootList(y);
        y.left = y.right = y;
        y.parent = x;
        if (x.child == null) {
            x.child = y;
        } else {
            y.right = x.child.right;
            y.left = x.child;
            x.child.right.left = y;
            x.child.right = y;
        }
        x.degree++;
        y.mark = false;
    }

    public double runBenchmark(int insertions, int deletions, Random rand, int trials) {
        double totalTime = 0;
        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            for (int j = 0; j < insertions; j++) {
                insert((K) Integer.valueOf(rand.nextInt(100000)));
            }
            for (int j = 0; j < deletions; j++) {
                remove();
            }
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime) / 1_000_000.0;
        }
        return totalTime / trials;
    }

    public static void main(String[] args) {
        int M = 4095;
        int insertions = 16000;
        int deletions = 4000;
        int trials = 5;
        Random rand = new Random();

        System.out.println("Fibonacci Heap: " + new PriorityQueue<>(M, 5, Integer::compare).runBenchmark(insertions, deletions, rand, trials) + " ms");
        System.out.println("4-ary Heap Floyd: " + new PriorityQueue<>(M, 4, Integer::compare).runBenchmark(insertions, deletions, rand, trials) + " ms");
        System.out.println("4-ary Heap: " + new PriorityQueue<>(M, 3, Integer::compare).runBenchmark(insertions, deletions, rand, trials) + " ms");
        System.out.println("Binary Heap Floyd: " + new PriorityQueue<>(M, 2, Integer::compare).runBenchmark(insertions, deletions, rand, trials) + " ms");
        System.out.println("Binary Heap: " + new PriorityQueue<>(M, 1, Integer::compare).runBenchmark(insertions, deletions, rand, trials) + " ms");
    }
}
