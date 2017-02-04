package hw4.puzzle;


public class Board {
    //Your implementation should support all Board methods
    //in time proportional to gridSize^2 (or faster) in the worst case.

    private int gridSize;
    private int manhattanPriority = 999;
    private int hammingPriority = 999;
    private int[][] grid;
//    private int[][] goalGrid;


    //Constructs a board from an gridSize-by-gridSize array of tiles where
    //tiles[i][j] = tile at row i, column j
    public Board(int[][] tiles) {
        //You may assume that the constructor receives an gridSize-by-gridSize array
        //containing the N2 integers between 0 and N2 â 1,
        // where 0 represents the blank square.
        int[][] tilesCopy =
                new int[tiles[0].length][tiles[0].length];
        for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tilesCopy[i][j] = tiles[i][j];
            }
        }
        gridSize = tilesCopy[1].length;
//        System.out.println("GRID SIZE IS "+gridSize);
        grid = tilesCopy;
//        goalGrid = new int[gridSize][gridSize];
//        int count = 1;
//        for (int i = 0; i < gridSize; i++) {
//            for (int j = 0; j < gridSize; j++) {
//                if (i == gridSize - 1 && j == gridSize - 1) {
//                    goalGrid[i][j] = 0;
//                } else {
//                    goalGrid[i][j] = count;
//                }
//                count += 1;
//            }
//        }
    }

    //Returns value of tile at row i, column j (or 0 if blank)
    public int tileAt(int i, int j) {
        //The tileAt() method should throw a
        // java.lang.IndexOutOfBoundsException unless
        // both i or j are between 0 and gridSize â 1.
        if (i >= 0 && i <= gridSize - 1 && j >= 0 && j <= gridSize - 1) {
            // find value
            return grid[i][j];
        } else {
            throw new java.lang.IndexOutOfBoundsException();
        }
    }

    //Returns the board size gridSize
    public int size() {
        return gridSize;
    }

    private int convertCoordinates(int row, int col) {
        // converts the row & column values into an integer index.
        if (row >= 0 && col >= 0 && row < gridSize && col < gridSize) {
            return gridSize * row + col + 1;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private int getCol(int num) {
        //returns the column that the given index should be in.
        if (num > 0 && num < gridSize * gridSize) {
            return gridSize - num % gridSize; // pls work i cannot math
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private int getRow(int num) {
        //returns the row that the given index should be in.
        if (num > 0 && num < gridSize * gridSize) {
            return num / gridSize;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    //Hamming priority function
    public int hamming() {
        /*The number of tiles in the wrong position,
        Intuitively, a search node with a small number of tiles
        in the wrong position is close to the goal,
        and we prefer a search node that have
        been reached using a small number of moves.
        */
        if (hammingPriority == 999) {
            // compute hamming priority
            int hamNum = 0;
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (tileAt(i, j) != convertCoordinates(i, j) && tileAt(i, j) != 0) {
                        hamNum += 1;
                    }
                }
            }
            hammingPriority = hamNum;
        }
        return hammingPriority;
    }

    //Manhattan priority function
    public int manhattan() {
        /*The sum of the Manhattan distances
        (sum of the vertical and horizontal distance)
        from the tiles to their goal positions,
        plus the number of moves made so far to get to the search node.
        */
        if (manhattanPriority == 999) {
            // compute manhattan priority
            int manny = 0;
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    int num = tileAt(i, j);
                    if (num != convertCoordinates(i, j) && num != 0) {
                        manny += Math.abs(getRow(num) - i);
                        manny += Math.abs(getCol(num) - j);
                    }
                }
            }
            manhattanPriority = manny;
        }
        return manhattanPriority;
    }

    //Returns true if is this board the goal board
    public boolean isGoal() {
        return manhattan() == 0;
    }

    //Returns true if this board's tile values are the same
    //position as y's
    public boolean equals(Object y) {
//        System.out.println("ARE "+y+" AND THIS EQUAL?");
        Board boardy = (Board) y;
//        return boardy.grid.equals(grid);
//        boardy.grid
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (boardy.tileAt(i, j) != grid[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return grid.hashCode();
    }

    @Override
    //Returns the string representation of the board.
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
