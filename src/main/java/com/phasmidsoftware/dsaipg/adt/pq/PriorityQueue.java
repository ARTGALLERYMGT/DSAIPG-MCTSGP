package com.phasmidsoftware.dsaipg.adt.pq;

//import java.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


public class PriorityQueue<T extends Comparable<T>> {
    private ArrayList<T> heap;
    private LinkedList<T> fibHeap;
    private PriorityQueue<T> fibMinHeap;
    private int d;
    private HeapType heapType;

    public enum HeapType {
        BINARY_HEAP, BINARY_HEAP_FLOYD, FOUR_ARY_HEAP, FOUR_ARY_FLOYD, FIBONACCI_HEAP
    }

    public PriorityQueue(HeapType type) {
        this.heap = new ArrayList<>();
        this.fibHeap = new LinkedList<>();
        this.fibMinHeap = new PriorityQueue<>(heapType);
        this.heapType = type;
        this.d = (type == HeapType.FOUR_ARY_HEAP || type == HeapType.FOUR_ARY_FLOYD) ? 4 : 2;
    }

    public void insert(T value) {
        if (heapType == HeapType.FIBONACCI_HEAP) {
            fibHeap.add(value); // Amortized O(1)
        } else {
            heap.add(value);
            if (heapType != HeapType.FOUR_ARY_FLOYD && heapType != HeapType.BINARY_HEAP_FLOYD) {
                heapifyUp(heap.size() - 1);
            }
        }
    }

    public T remove() {
        if (heapType == HeapType.FIBONACCI_HEAP) {
            return fibHeap.poll(); // O(log n) in full Fibonacci Heap
        }
        if (heap.isEmpty()) return null;
        T removedValue = heap.get(0);
        heap.set(0, heap.remove(heap.size() - 1));
        heapifyDown(0);
        return removedValue;
    }

    public void buildHeapFloyd(T[] values) {
        if (heapType != HeapType.FOUR_ARY_FLOYD && heapType != HeapType.BINARY_HEAP_FLOYD) {
            throw new UnsupportedOperationException("Floyd's trick only applies to Binary and 4-ary Floyd heaps.");
        }
        Collections.addAll(heap, values);
        for (int i = (heap.size() - 1) / d; i >= 0; i--) {
            heapifyDown(i);
        }
    }

    private void heapifyUp(int index) {
        while (index > 0 && heap.get(parent(index)).compareTo(heap.get(index)) > 0) {
            swap(index, parent(index));
            index = parent(index);
        }
    }

    private void heapifyDown(int index) {
        int smallest = index;
        for (int i = 1; i <= d; i++) {
            int childIndex = child(index, i);
            if (childIndex < heap.size() && heap.get(childIndex).compareTo(heap.get(smallest)) < 0) {
                smallest = childIndex;
            }
        }
        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }

    private int parent(int index) {
        return (index - 1) / d;
    }

    private int child(int index, int k) {
        return d * index + k;
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
}

