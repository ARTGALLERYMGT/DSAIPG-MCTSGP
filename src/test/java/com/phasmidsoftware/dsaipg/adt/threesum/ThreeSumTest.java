package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;
import com.phasmidsoftware.dsaipg.util.Stopwatch;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class ThreeSumTest {

    @Test
    public void testGetTriplesJ0Quadrithmic() {
        int[] ints = new int[]{-2, 0, 2};
        ThreeSum target = new ThreeSumQuadrithmic(ints);
        Triple[] triples = target.getTriples();
        assertEquals(1, triples.length);
    }

    @Test
    public void testGetTriplesJ0() {
        int[] ints = new int[]{-2, 0, 2};
        ThreeSumQuadratic target = new ThreeSumQuadratic(ints);
        List<Triple> triples = target.getTriples(1);
        assertEquals(1, triples.size());
    }

    @Test
    public void testGetTriplesJ1() {
        int[] ints = new int[]{30, -40, -20, -10, 40, 0, 10, 5};
        Arrays.sort(ints);
        ThreeSumQuadratic target = new ThreeSumQuadratic(ints);
        List<Triple> triples = target.getTriples(3);
        assertEquals(2, triples.size());
    }

    @Test
    public void testGetTriplesJ2() {
        Supplier<int[]> intsSupplier = new Source(10, 15, 2L).intsSupplier(10);
        int[] ints = intsSupplier.get();
        ThreeSumQuadratic target = new ThreeSumQuadratic(ints);
        List<Triple> triples = target.getTriples(5);
        assertEquals(1, triples.size());
    }

    @Test
    public void testGetTriples0() {
        int[] ints = new int[]{30, -40, -20, -10, 40, 0, 10, 5};
        Arrays.sort(ints);
        System.out.println("ints: " + Arrays.toString(ints));
        ThreeSum target = new ThreeSumQuadratic(ints);
        Triple[] triples = target.getTriples();
        System.out.println("triples: " + Arrays.toString(triples));
        assertEquals(4, triples.length);
        assertEquals(4, new ThreeSumCubic(ints).getTriples().length);
    }

    @Test
    public void testGetTriples1() {
        Supplier<int[]> intsSupplier = new Source(20, 20, 1L).intsSupplier(10);
        int[] ints = intsSupplier.get();
        ThreeSum target = new ThreeSumQuadratic(ints);
        Triple[] triples = target.getTriples();
        assertEquals(4, triples.length);
        System.out.println(Arrays.toString(triples));
        Triple[] triples2 = new ThreeSumCubic(ints).getTriples();
        System.out.println(Arrays.toString(triples2));
        assertEquals(4, triples2.length);
    }

    @Test
    public void testGetTriples2() {
        Supplier<int[]> intsSupplier = new Source(10, 15, 3L).intsSupplier(10);
        int[] ints = intsSupplier.get();
        ThreeSum target = new ThreeSumQuadratic(ints);
        System.out.println(Arrays.toString(ints));
        Triple[] triples = target.getTriples();
        System.out.println(Arrays.toString(triples));
        assertEquals(1, triples.length);
        assertEquals(1, new ThreeSumCubic(ints).getTriples().length);
    }

    @Test
    public void testGetTriplesE() {
        int[] ints = new int[]{-38, -23, -15, -12, -6, 17, 18, 37, 42, 43};
        ThreeSum target = new ThreeSumQuadratic(ints);
        Triple[] triples = target.getTriples();
        assertEquals(1, triples.length);
    }

    @Test
    public void testGetTriplesC0() {
        int[] ints = new int[]{30, -40, -20, -10, 40, 0, 10, 5};
        Arrays.sort(ints);
        System.out.println("ints: " + Arrays.toString(ints));
        ThreeSum target = new ThreeSumQuadratic(ints);
        Triple[] triples = target.getTriples();
        System.out.println("triples: " + Arrays.toString(triples));
        assertEquals(4, triples.length);
        assertEquals(4, new ThreeSumCubic(ints).getTriples().length);
    }

    private int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(2000) - 1000;
        }
        return array;
    }

    @Test
    public void testThreeSumTiming() {
        // Array sizes for testing
        int[] sizes = {1000, 2000, 4000, 8000, 16000};
        
        System.out.println("\nThreeSum Algorithm Timing Analysis");
        System.out.println("N\tCubic(s)\tQuadratic(s)\tCubic Ratio\tQuadratic Ratio");
        
        double prevCubic = 0;
        double prevQuadratic = 0;
        
        for (int size : sizes) {
            int[] array = generateRandomArray(size);
            double cubicTime = 0;
            double quadraticTime = 0;
            
            // Time cubic algorithm
            try (Stopwatch timer = new Stopwatch()) {
                ThreeSumCubic cubic = new ThreeSumCubic(array.clone());
                cubic.getTriples();
                cubicTime = timer.lap();
            }
            
            // Time quadratic algorithm
            try (Stopwatch timer = new Stopwatch()) {
                ThreeSumQuadratic quadratic = new ThreeSumQuadratic(array.clone());
                quadratic.getTriples();
                quadraticTime = timer.lap();
            }
            
            // Calculate ratios
            String cubicRatio = prevCubic == 0 ? "N/A" : String.format("%.2f", cubicTime / prevCubic);
            String quadRatio = prevQuadratic == 0 ? "N/A" : String.format("%.2f", quadraticTime / prevQuadratic);
            
            // Print results
            System.out.printf("%d\t%.3f\t\t%.3f\t\t%s\t\t%s%n", 
                    size, cubicTime, quadraticTime, cubicRatio, quadRatio);
            
            prevCubic = cubicTime;
            prevQuadratic = quadraticTime;
        }
    }
}