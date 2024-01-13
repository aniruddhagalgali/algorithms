import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private int numOfTrials = 0;
    private double[] percolationThresholds = null;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        // Validate input
        if (n <= 0) {
            throw new IllegalArgumentException("'n' must be > 0");
        }
        if (trials <= 0) {
            throw new IllegalArgumentException("'trials' must be > 0");
        }
        numOfTrials = trials;
        percolationThresholds = new double[numOfTrials];
        for (int trialCounter = 0; trialCounter < numOfTrials; trialCounter++) {
            int readRover = 0;
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                // Open a random site if not already open
                int row = StdRandom.uniformInt(1, n+1);
                int col = StdRandom.uniformInt(1, n+1);
                if (!percolation.isOpen(row, col)) {
                    percolation.open(row, col);
                }
            }
            percolationThresholds[trialCounter] = (double) percolation.numberOfOpenSites() /(n*n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(percolationThresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(percolationThresholds);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return (StdStats.mean(percolationThresholds) - ((CONFIDENCE_95 * StdStats.stddev(percolationThresholds))/ Math.sqrt(numOfTrials)));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return (StdStats.mean(percolationThresholds) + ((CONFIDENCE_95 * StdStats.stddev(percolationThresholds))/ Math.sqrt(numOfTrials)));
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats percolationStats = new PercolationStats(n, t);
        System.out.println("mean = " + percolationStats.mean());
        System.out.println("stddev = " + percolationStats.stddev());
        System.out.println("95% confidence interval = [" + percolationStats.confidenceLo() + ", " + percolationStats.confidenceHi()+"]");
    }
}
