package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Arrays;
import java.util.concurrent.*;

final class ParSort {
    public static int cutoff = 1000;
    private static ForkJoinPool pool = ForkJoinPool.commonPool();  

    public static void setPool(ForkJoinPool newPool) {
        pool = newPool;
    }

    public static void sort(int[] array, int from, int to) {
        if (to - from >= cutoff) {
            int mid = (from + to) / 2;

            ForkJoinTask<int[]> task1 = pool.submit(() -> sortRecursive(array, from, mid));
            ForkJoinTask<int[]> task2 = pool.submit(() -> sortRecursive(array, mid, to));

            try {
                int[] sorted1 = task1.get();
                int[] sorted2 = task2.get();
                int[] merged = doMerge(sorted1, sorted2);
                System.arraycopy(merged, 0, array, from, merged.length);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Arrays.sort(array, from, to);
        }
    }

    static int[] sortRecursive(int[] array, int from, int to) {
        int[] result = Arrays.copyOfRange(array, from, to);
        Arrays.sort(result);
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
}
