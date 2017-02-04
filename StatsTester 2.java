package hw2;

/**
 * Created by RachelThomas on Mar/16/16.
 */
public class StatsTester {
    public static void main(String[] args) {
        // take 20 samples, from 20x20 grids, then check the values
        PercolationStats percStats = new PercolationStats(20, 20);
        System.out.println("Percstats mean is " + percStats.mean());
        System.out.println("Percstats stdev is " + percStats.stddev());

    }
}
