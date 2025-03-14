package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

final class ParSort {
    public static int cutoff = 1000; // Default cutoff

    public static void sort(int[] array, int from, int to) {
        if (to - from >= cutoff) {
            int mid = (from + to) / 2;

            // Initialize the CompletableFuture objects
            CompletableFuture<int[]> completableFuture1 = asyncSort(array, from, mid);
            CompletableFuture<int[]> completableFuture2 = asyncSort(array, mid, to);

            // Combine results from two recursive sorts
            CompletableFuture<int[]> completableFuture = completableFuture1
                    .thenCombine(completableFuture2, ParSort::doMerge);

            // Copy sorted result back to the original array
            int[] sortedArray = completableFuture.join(); // Ensure completion
            System.arraycopy(sortedArray, 0, array, from, sortedArray.length);
        } else {
            Arrays.sort(array, from, to); // Base case: Use sequential sort
        }
    }

    static int[] sortRecursive(int[] array, int from, int to) {
        int[] result = Arrays.copyOfRange(array, from, to); // Copy portion to result array
        Arrays.sort(result); // Sort the copied portion
        return result;
    }

    static int[] doMerge(int[] xs1, int[] xs2) {
        int[] result = new int[xs1.length + xs2.length];
        int i = 0, j = 0;
        for (int k = 0; k < result.length; k++) {
            if (i >= xs1.length) result[k] = xs2[j++];
            else if (j >= xs2.length) result[k] = xs1[i++];
            else if (xs2[j] < xs1[i]) result[k] = xs2[j++];
            else result[k] = xs1[i++];
        }
        return result;
    }

    static CompletableFuture<int[]> asyncSort(int[] array, int from, int to) {
        return CompletableFuture.supplyAsync(() -> sortRecursive(array, from, to));
    }
}
