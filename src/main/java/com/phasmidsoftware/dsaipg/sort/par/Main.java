package com.phasmidsoftware.dsaipg.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        int[] cutoffValues = {1000, 5000, 10000, 20000, 50000, 100000}; // Different cutoffs
        int[] threadCounts = {1, 2, 4, 8, 16, 32}; // Different thread counts

        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("cutoff_results.csv")))) {
            bw.write("Threads,Cutoff,Avg Sorting Time (ms)\n");

            for (int threads : threadCounts) {
                ForkJoinPool pool = new ForkJoinPool(threads);
                ParSort.setPool(pool);

                for (int cutoff : cutoffValues) {
                    ParSort.cutoff = cutoff;
                    long totalTime = 0;
                    int numTests = 5; // Run 5 times to get an average

                    for (int i = 0; i < numTests; i++) {
                        int[] array = new int[2000000]; // Large dataset
                        Random random = new Random();
                        for (int j = 0; j < array.length; j++) {
                            array[j] = random.nextInt(10000000);
                        }

                        long startTime = System.currentTimeMillis();
                        ParSort.sort(array, 0, array.length);
                        long endTime = System.currentTimeMillis();
                        totalTime += (endTime - startTime);
                    }

                    long avgTime = totalTime / numTests; // Average time
                    System.out.println("Threads: " + threads + " | Cutoff: " + cutoff + " | Avg Time: " + avgTime + "ms");

                    // Save results to CSV
                    bw.write(threads + "," + cutoff + "," + avgTime + "\n");
                    bw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
