/*
 * Copyright (c) 2024. Robin Hillyard
 */

 package com.phasmidsoftware.dsaipg.util;

 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.Random;
 import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
 import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue.HeapType;
 
 public class Benchmark {
     private static final int INSERTIONS = 16000;
     private static final int DELETIONS = 4000;
     private static final int NUM_TRIALS = 10;
     private static final String CSV_FILE = "heap_benchmark_results.csv";
 
     public static void main(String[] args) {
         try (FileWriter writer = new FileWriter(CSV_FILE)) {
             writer.write("Heap Type,Trial,Insertion Time (ms),Deletion Time (ms)\n");
 
             // Execute benchmarks for each heap type
             runBenchmark(HeapType.FIBONACCI_HEAP, "Fibonacci Heap", writer);
             runBenchmark(HeapType.FOUR_ARY_FLOYD, "4-ary Heap (Floyd's Trick)", writer);
             runBenchmark(HeapType.FOUR_ARY_HEAP, "4-ary Heap", writer);
             runBenchmark(HeapType.BINARY_HEAP_FLOYD, "Binary Heap (Floyd's Trick)", writer);
             runBenchmark(HeapType.BINARY_HEAP, "Binary Heap", writer);
 
             System.out.println("\nBenchmark results saved to: " + CSV_FILE);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
 
     public static void runBenchmark(HeapType type, String heapName, FileWriter writer) throws IOException {
         System.out.println("\nRunning benchmark for: " + heapName);
 
         for (int i = 1; i <= NUM_TRIALS; i++) {
             Random rand = new Random();
             PriorityQueue<Integer> heap = new PriorityQueue<>(type);
             int maxSpilled = Integer.MIN_VALUE;
 
             // Insert elements
             long startTime = System.nanoTime();
             for (int j = 0; j < INSERTIONS; j++) {
                 int value = rand.nextInt(100000);
                 heap.insert(value);
             }
             long endTime = System.nanoTime();
             double insertTime = (endTime - startTime) / 1e6; // Convert ns to ms
             
             // Remove elements
             startTime = System.nanoTime();
             for (int j = 0; j < DELETIONS; j++) {
                 Integer removed = heap.remove();
                 if (removed != null && removed > maxSpilled) maxSpilled = removed;
             }
             endTime = System.nanoTime();
             double deleteTime = (endTime - startTime) / 1e6; // Convert ns to ms
 
             // Print execution results
             System.out.printf("Trial %d - %s: Insertion Time: %.3f ms | Deletion Time: %.3f ms | Max Spilled: %d%n",
                     i, heapName, insertTime, deleteTime, maxSpilled);
 
             // Write results to CSV file for plotting
             writer.write(heapName + "," + i + "," + insertTime + "," + deleteTime + "\n");
             writer.flush();
         }
         System.out.println("Results written for: " + heapName);
     }
 }
 