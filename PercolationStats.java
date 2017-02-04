package hw2;

import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdRandom;

public class PercolationStats {

    private double[] ratios; // stores the values of ratios of open sites/ total sites

    public PercolationStats(int N, int T) {
        // perform T independent experiments on an N-by-N grid
        if (N <= 0 || T <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        ratios = new double[T]; // will store the values of the ratios of open sites/total sites
        for (int i = 1; i <= T; i++) {
            Percolation perc = new Percolation(N);
            boolean percolates = false;
            int openSites;
            while (!percolates) {
                int col = StdRandom.uniform(N);
                int row = StdRandom.uniform(N);
                perc.open(row, col);
                percolates = perc.percolates();
            }
            openSites = perc.numberOfOpenSites();
            double multiplied = (N * N);
            double test = (openSites / multiplied);
            ratios[i - 1] = test;
        }
    }

    public double mean() {
        // sample mean of percolation threshold
        return StdStats.mean(ratios);
    }

    public double stddev() {
        // sample standard deviation of percolation threshold
        return StdStats.stddev(ratios);
    }

    public double confidenceLow() {
        // low  endpoint of 95% confidence interval
        double marginOfError = ((1.96 * stddev()) / Math.sqrt(ratios.length));
        return mean() - marginOfError;
    }

    public double confidenceHigh() {
        // high endpoint of 95% confidence interval
        double marginOfError = ((1.96 * stddev()) / Math.sqrt(ratios.length));
        return mean() + marginOfError;
    }
}
