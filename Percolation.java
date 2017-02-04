package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int gridSize; //holds the size of the percolation grid.
    private boolean[][] grid;
    private WeightedQuickUnionUF connections;
    private WeightedQuickUnionUF backwashHelper;
    private int numOpenSites;
    int topPointer = 0;
    int connectionsSize = 0;
    int bottomPointer = -456;
    /* all methods should take constant time plus a constant number of
     calls to the union-find methods union(), find(), connected(), and count()*/

    public Percolation(int N) {
        // create N-by-N grid, with all sites initially blocked
        if (N <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        gridSize = N;
        grid = new boolean[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = false; // a value of zero equates to being blocked.
            }
        }
        connectionsSize = gridSize * gridSize;
        bottomPointer = connectionsSize + 1;
        connections = new WeightedQuickUnionUF(connectionsSize + 2);
        backwashHelper = new WeightedQuickUnionUF(connectionsSize + 1);
    }

    private int convertCoordinates(int row, int col) {
        // converts the row & column values into an integer index.
        if (row >= 0 && col >= 0 && row < gridSize && col < gridSize) {
            return gridSize * row + col + 1;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void open(int row, int col) {
        // open the site (row, col) if it is not open already
        if (row < 0 || col < 0 || row >= gridSize || col >= gridSize) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        boolean oldVal = grid[row][col];
//        System.out.println("Oldval is " + oldVal);
        grid[row][col] = true; // this is the default "open value". will be changed if full.
        int ind = convertCoordinates(row, col);
        int rowAbove = row - 1;
        int columnLeft = col - 1;
        int rowBelow = row + 1;
        int columnRight = col + 1;
        if (rowAbove >= 0) {
            // check position directly above index
            if (grid[rowAbove][col]) {
                // open or full area found adjacent to row,column in grid
                connections.union(ind, convertCoordinates(rowAbove, col));
                backwashHelper.union(ind, convertCoordinates(rowAbove, col));
            }
        }
        if (columnRight <= gridSize - 1) {
            // check position to right of index
            if (grid[row][columnRight]) {
                // open or full area found adjacent to row,column in grid
                connections.union(ind, convertCoordinates(row, columnRight));
                backwashHelper.union(ind, convertCoordinates(row, columnRight));
            }
        }
        if (rowBelow <= gridSize - 1) {
            // check position directly below index
            if (grid[rowBelow][col]) {
                // open or full area found adjacent to row,column in grid
                connections.union(ind, convertCoordinates(rowBelow, col));
                backwashHelper.union(ind, convertCoordinates(rowBelow, col));
            }
        }
        if (columnLeft >= 0) {
            // check position to left of index
            if (grid[row][columnLeft]) {
                // open or full area found adjacent to row,column in grid
                connections.union(ind, convertCoordinates(row, columnLeft));
                backwashHelper.union(ind, convertCoordinates(row, columnLeft));
            }
        }
        // now check whether row, col in grid is connected to water.
        if (row == 0) {
            // automatic water connection
            connections.union(topPointer, ind);
            backwashHelper.union(topPointer, ind);
        }
        if (row == gridSize - 1) {
            // automatic bottom connection.
            connections.union(ind, bottomPointer);
        }
        if (!oldVal) {
            numOpenSites += 1;
        }
    }

    public boolean isOpen(int row, int col) {
        // is the site (row, col) open?
        if (row < 0 || col < 0 || row >= gridSize || col >= gridSize) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        return grid[row][col];
    }

    public boolean isFull(int row, int col) {
        // is the site (row, col) full?
        if (row < 0 || col < 0 || row >= gridSize || col >= gridSize) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        return connections.connected(topPointer, convertCoordinates(row, col))
                && (backwashHelper.connected(topPointer, convertCoordinates(row, col)));
    }

    public int numberOfOpenSites() {
        // number of open sites
        return numOpenSites;
    }

    public boolean percolates() {
        // does the system percolate?
        return connections.connected(topPointer, bottomPointer);
    }

//    private static void printGrid(boolean[][] grid) {
//        // for debugging purposes
//        System.out.println("PRINTING GRID.");
//        for (int i = 0; i < grid.length; i++) {
//            for (int j = 0; j < grid.length; j++) {
//                if (grid[i][j]) {
//                    System.out.print(" O");
//                } else {
//                    System.out.print(" -");
//                }
//            }
//            System.out.println();
//        }
//
//    }

    public static void main(String[] args) {

    }

}                       
