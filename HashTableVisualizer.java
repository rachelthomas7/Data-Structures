package hw3.hash;

import java.util.HashSet;
import java.util.Set;

public class HashTableVisualizer {

    public static void main(String[] args) {
        /* scale: StdDraw scale
           N:     number of items
           M:     number of buckets */

        double scale = 1.0;
        int N = 50;
        int M = 10;

        HashTableDrawingUtility.setScale(scale);
        Set<Oomage> oomies = new HashSet<Oomage>();
        for (int i = 0; i < N; i += 1) {
            oomies.add(SimpleOomage.randomSimpleOomage());
        }
        visualize(oomies, M, scale);
    }

    public static void visualize(Set<Oomage> set, int M, double scale) {
        HashTableDrawingUtility.drawLabels(M);
        int[] bucketTracker = new int[M];
        for (int i = 0; i < M; i++) {
            bucketTracker[i] = 0;
        }
        for (Oomage k : set) {
            int bucket = k.hashCode() % 10;
            if (bucket < 0) {
                bucket = M - 1 + bucket;
            }
            double xC = HashTableDrawingUtility.xCoord(bucketTracker[bucket]);
            double yC = HashTableDrawingUtility.yCoord(bucket, M);
            k.draw(xC, yC, scale);
            bucketTracker[bucket] += 1;
//            System.out.println("Current Oomage: ("+xC+", "+yC+")");
//            System.out.println("HC OF CURR: "+k.hashCode());
        }
        /* TODO: Create a visualization of the given hash table. Use
           du.xCoord and du.yCoord to figure out where to draw
           Oomages.
         */

        /* When done with visualizer, be sure to try 
           scale = 0.5, N = 2000, M = 100. */
    }
} 
