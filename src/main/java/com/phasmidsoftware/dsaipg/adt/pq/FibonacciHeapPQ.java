package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;

public class FibonacciHeapPQ<K extends Comparable<K>> {
    private static class Node<K> {
        K key;
        double priority;
        Node<K> parent, child, left, right;
        int degree;
        boolean mark;

        Node(K key, double priority) {
            this.key = key;
            this.priority = priority;
            this.left = this;
            this.right = this;
        }
    }

    private Node<K> min;
    private int size;
    private K highestPrioritySpilled = null;
    private final int MAX_SIZE = 4095;
    private int removeCounter = 0;

    public FibonacciHeapPQ() {
        this.min = null;
        this.size = 0;
    }

    public void insert(K key, double priority) {
        Node<K> node = new Node<>(key, priority);
        if (min == null) {
            min = node;
        } else {
            insertIntoRootList(node);
            if (priority < min.priority) {
                min = node;
            }
        }
        size++;

        if (size > MAX_SIZE) {
            K removed = lazyRemove();
            if (highestPrioritySpilled == null || (removed != null && removed.compareTo(highestPrioritySpilled) > 0)) {
                highestPrioritySpilled = removed;
            }
        }
    }

    private K lazyRemove() {
        if (min == null) return null;
        Node<K> oldMin = min;

        if (min.child != null) {
            Node<K> child = min.child;
            do {
                Node<K> next = child.right;
                insertIntoRootList(child);
                child.parent = null;
                child = next;
            } while (child != min.child);
        }

        removeFromRootList(min);
        size--;

        if (min == min.right) {
            min = null;
        } else {
            min = min.right;
            removeCounter++;

            // 🚀 Reduce consolidation frequency even further (every 1000 removals)
            if (removeCounter % 1000 == 0) consolidate();
        }

        return oldMin.key;
    }

    private void insertIntoRootList(Node<K> node) {
        if (min == null) {
            min = node;
        } else {
            node.right = min.right;
            node.left = min;
            min.right.left = node;
            min.right = node;
        }
    }

    private void removeFromRootList(Node<K> node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private void consolidate() {
        Map<Integer, Node<K>> degreeTable = new HashMap<>();
        List<Node<K>> rootNodes = new ArrayList<>();
        Node<K> current = min;

        do {
            rootNodes.add(current);
            current = current.right;
        } while (current != min);

        for (Node<K> node : rootNodes) {
            int d = node.degree;
            while (degreeTable.containsKey(d)) {
                Node<K> y = degreeTable.get(d);
                if (node.priority > y.priority) {
                    Node<K> temp = node;
                    node = y;
                    y = temp;
                }
                link(y, node);
                degreeTable.remove(d);
                d++;
            }
            degreeTable.put(d, node);
        }

        min = null;
        for (Node<K> node : degreeTable.values()) {
            if (min == null || node.priority < min.priority) {
                min = node;
            }
        }
    }

    private void link(Node<K> y, Node<K> x) {
        removeFromRootList(y);
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

    public double runBenchmark(int insertions, int deletions, Random rand) {
        long startTime = System.nanoTime();

        for (int i = 0; i < insertions; i++) {
            int value = rand.nextInt(100000);
            double priority = (double) rand.nextInt(100000);
            insert((K) Integer.valueOf(value), priority);
        }

        for (int i = 0; i < deletions; i++) {
            lazyRemove();
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000.0;
    }

    public static void main(String[] args) {
        int M = 4095;
        int insertions = 16000;
        int deletions = 4000;
        Random rand = new Random();

        FibonacciHeapPQ<Integer> fibHeap = new FibonacciHeapPQ<>();
        double elapsedTimeFib = fibHeap.runBenchmark(insertions, deletions, rand);

        System.out.println("Highest Priority Spilled: " + fibHeap.highestPrioritySpilled);
        System.out.println("Fibonacci Heap: " + elapsedTimeFib + " ms");
    }
}
