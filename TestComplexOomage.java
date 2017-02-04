package hw3.hash;

import edu.princeton.cs.algs4.Interval1D;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

import edu.princeton.cs.algs4.StdRandom;


public class TestComplexOomage {

    @Test
    public void testHashCodeDeterministic() {
        ComplexOomage so = ComplexOomage.randomComplexOomage();
        int hashCode = so.hashCode();
        for (int i = 0; i < 100; i += 1) {
            assertEquals(hashCode, so.hashCode());
        }
    }

    public boolean haveNiceHashCodeSpread(Set<ComplexOomage> oomages) {
        /* hashCodes that would distribute them fairly evenly across
         * buckets To do this, mod each's hashCode by M = 10,
         * and ensure that no bucket has fewer than N / 50
         * Oomages and no bucket has more than N / 2.5 Oomages.
         */
        boolean boo = true;
        int[] buckets = new int[10];
        int size = oomages.size();
        for (int i = 0; i < 10; i++) {
            buckets[i] = 0;
        }
        for (Oomage k : oomages) {
            int bucket = k.hashCode() % 10;
            if (bucket < 0) {
                bucket = 10 + bucket;
            }
            buckets[bucket] += 1;
        }
//        System.out.print("BUCKETS: [");
        for (int i = 0; i < 10; i++) {
//            System.out.print(buckets[i] + ", ");
            if (buckets[i] < size / 50 || buckets[i] > size / 2.5) {
                boo = false;
            }
        }
//        System.out.println("]");
        return boo;
    }


    @Test
    public void testRandomItemsHashCodeSpread() {
        HashSet<ComplexOomage> oomages = new HashSet<ComplexOomage>();
        int N = 10000;

        for (int i = 0; i < N; i += 1) {
            oomages.add(ComplexOomage.randomComplexOomage());
        }

        assertTrue(haveNiceHashCodeSpread(oomages));
    }

    @Test
    public void testWithDeadlyParams() {
        /* TODO: Create a Set that shows the flaw in the hashCode function.
         */
        HashSet<ComplexOomage> oomages = new HashSet<ComplexOomage>();
        for (int i = 0; i < 100; i++) {
            List<Integer> params = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                params.add(255);
            }
//            System.out.print("PARAMS IS ");
            for (int k = 0; k < params.size(); k++) {
//                System.out.print(params.get(k) + ", ");
            }
//            System.out.println();
            oomages.add(new ComplexOomage(params));
        }
        assertTrue(haveNiceHashCodeSpread(oomages));
    }

    /**
     * Calls tests for SimpleOomage.
     */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestComplexOomage.class);
    }
}
