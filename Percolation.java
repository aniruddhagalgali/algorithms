import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final int SITE_OPEN = 4;
    private static final int SITE_OPEN_TOP_CONNECTED = 6;
    private static final int SITE_PERCOLATES = 7;
    private static final boolean CLOSED = false;
    private static final boolean OPEN = true;
    private int matrixSize = 0;
    private boolean[] openSites = null;
    private byte[] sitesStatus = null;
    private WeightedQuickUnionUF weightedQuickUnionUF = null;
    private boolean percolates = false;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("'n' must be > 0");
        }
        matrixSize = n;

        // We need 2 more sites
        // One for virtual top site i.e. Site0
        // Another for virtual bottom site i.e. Site n+1
        int arraySize = n * n;
        weightedQuickUnionUF = new WeightedQuickUnionUF(arraySize);
        openSites = new boolean[arraySize];
        sitesStatus = new byte[arraySize];

        // Initially close all sites
        for (int i = 0; i < openSites.length; i++) {
            openSites[i] = CLOSED;
            sitesStatus[i] = 0;
        }

        // Initially mark all sites in top and bottom rows
        for (int i = 0; i < matrixSize; i++) {
            // Not open | Connected to top virtual site | Not connected to bottom virtual site
            // 010
            sitesStatus[i] = 2;
            // Not open | Not connected to top virtual site | Connected to bottom virtual site
            // 001
            sitesStatus[(matrixSize*matrixSize) - i - 1] = 1;
        }
        // Special case
        if (matrixSize == 1) sitesStatus[0] = 3;
    }

    // converts the 2D array indices to 1D array index
    private int xyTo1D(int row, int col) {
        return ((matrixSize * (row - 1)) + col-1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row <= 0  || col <= 0) throw new IllegalArgumentException("'row' and 'col' must be > 0");
        if (row > matrixSize  || col > matrixSize) throw new IllegalArgumentException("'row' and 'col' must be <= " + matrixSize);

        if (!isOpen(row, col)) {
            // Open this site
            int thisSite = xyTo1D(row, col);
            byte thisSiteStatus = sitesStatus[thisSite];
            sitesStatus[thisSite] = (byte) (thisSiteStatus | SITE_OPEN);
            openSites[thisSite] = OPEN;
            unionWithAdjacentOpenSites(row, col);
        }
    }

    private void unionWithAdjacentOpenSites(int row, int col) {
        int thisSite = xyTo1D(row, col);
        byte newStatus = 0;
        // Site above
        if (row > 1) {
            if (isOpen(row - 1, col)) {
                int aboveSite = xyTo1D(row - 1, col);
                byte aboveSiteStatus = sitesStatus[weightedQuickUnionUF.find(aboveSite)];
                newStatus = (byte) (sitesStatus[thisSite] | aboveSiteStatus);
                sitesStatus[thisSite] = newStatus;
                sitesStatus[aboveSite] = newStatus;
                weightedQuickUnionUF.union(xyTo1D(row - 1, col), thisSite);
            }
        }

        // Site below
        if (row < matrixSize) {
            if (isOpen(row + 1, col)) {
                int belowSite = xyTo1D(row + 1, col);
                byte belowSiteStatus = sitesStatus[weightedQuickUnionUF.find(belowSite)];
                newStatus = (byte) (sitesStatus[thisSite] | belowSiteStatus);
                sitesStatus[thisSite] = newStatus;
                sitesStatus[belowSite] = newStatus;
                weightedQuickUnionUF.union(xyTo1D(row + 1, col), thisSite);
            }
        }

        // Site to left
        if (col > 1) {
            if (isOpen(row, col-1)) {
                int leftSite = xyTo1D(row, col-1);
                byte leftSiteStatus = sitesStatus[weightedQuickUnionUF.find(leftSite)];
                newStatus = (byte) (sitesStatus[thisSite] | leftSiteStatus);
                sitesStatus[thisSite] = newStatus;
                sitesStatus[leftSite] = newStatus;
                weightedQuickUnionUF.union(xyTo1D(row, col - 1), thisSite);
            }
        }

        // Site to right
        if (col < matrixSize) {
            if (isOpen(row, col+1)) {
                int rightSite = xyTo1D(row, col+1);
                byte rightSiteStatus = sitesStatus[weightedQuickUnionUF.find(rightSite)];
                newStatus = (byte) (sitesStatus[thisSite] | rightSiteStatus);
                sitesStatus[thisSite] = newStatus;
                sitesStatus[rightSite] = newStatus;
                weightedQuickUnionUF.union(xyTo1D(row, col + 1), thisSite);
            }
        }
        sitesStatus[weightedQuickUnionUF.find(thisSite)] = newStatus == 0 ? sitesStatus[thisSite] : newStatus;
        if (newStatus >= SITE_PERCOLATES) percolates = true;
        // Special case
        if (matrixSize == 1) percolates = true;
    }

    // test client (optional)
    // public static void main(String[] args)

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row <= 0  || col <= 0) throw new IllegalArgumentException("'row' and 'col' must be > 0");
        if (row > matrixSize  || col > matrixSize) throw new IllegalArgumentException("'row' and 'col' must be <= " + matrixSize);
        //return openSites[xyTo1D(row, col)];
        return sitesStatus[xyTo1D(row, col)] >= SITE_OPEN;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row <= 0  || col <= 0) throw new IllegalArgumentException("'row' and 'col' must be > 0");
        if (row > matrixSize  || col > matrixSize) throw new IllegalArgumentException("'row' and 'col' must be <= " + matrixSize);
        return sitesStatus[weightedQuickUnionUF.find(xyTo1D(row, col))] >= SITE_OPEN_TOP_CONNECTED;
    }

    // Returns the number of open sites
    public int numberOfOpenSites() {
        int openSiteCount = 0;
        for (boolean site : openSites) {
            if (site == OPEN) {
                openSiteCount++;
            }
        }
        return openSiteCount;
    }

    // Does the system percolate?
    public boolean percolates() {
        return percolates;
    }
}
