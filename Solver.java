package hw4.puzzle;

//import hw4.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Collections;
import java.util.LinkedList;

public class Solver {

    private MinPQ<SearchNode> priorityQueue = new MinPQ<>();
    private int numMoves;
    private SearchNode currNode;
    private LinkedList<Board> boardSequence;
    private Board initialBoard;

    /*Constructor which solves the puzzle, computing
    everything necessary for moves() and solution() to
    not have to solve the problem again. Solves the
    puzzle using the A* algorithm. Assumes a solution exists.*/
    public Solver(Board initial) {
        initialBoard = initial;
        int nM = 0;
        currNode = new SearchNode(initial, nM, null);
        SearchNode item = currNode;
        priorityQueue.insert(item);
        while (!item.board.isGoal()) {
            item = priorityQueue.delMin();
            Iterable<Board> neighbors = BoardUtils.neighbors(item.board);
            for (Board b : neighbors) {
                if (item.numMoves >= 2) {
                    if (!b.equals(item.prevNode.board)
                            && !b.equals(item.prevNode.prevNode.board)) {
                        priorityQueue.insert(new SearchNode(b, item.numMoves + 1, item));
                    }
                } else if (item.numMoves == 0 || !b.equals(item.prevNode.board)) {
                    priorityQueue.insert(new SearchNode(b, item.numMoves + 1, item));
                }
            }
        }
        getFinal(item);
    }

    private void getFinal(SearchNode n) {
        //gets the final value of the SHORTEST PATH
        boardSequence = new LinkedList<>();
        SearchNode curr = n;
        numMoves = n.numMoves;
        while (curr.prevNode != null) {
            boardSequence.add(curr.board);
            curr = curr.prevNode;
        }
        Collections.reverse(boardSequence);
        boardSequence.addFirst(initialBoard);
    }

    private class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private int numMoves;
        private SearchNode prevNode;
        private int hammingy = 999;
        private int manningy = 999;

        public SearchNode(Board b, int nM, SearchNode pN) {
            board = b;
            numMoves = nM;
            prevNode = pN;
        }

        public int hammy() {
            //returns the hamming priority func. of the given board.
            if (hammingy == 999) {
                hammingy = board.hamming() + numMoves;
            }
            return hammingy;
        }

        public int manny() {
            //returns the manhattan priority func. of the given board.
            if (manningy == 999) {
                manningy = board.manhattan() + numMoves;
            }
            return manningy;
        }

        public int priority() {
            //returns the priority of the given board.
            return manny();
        }

        @Override
        public int compareTo(SearchNode o) {
            if (o == null) {
                throw new NullPointerException();
            }
            if (o.getClass() != this.getClass()) {
                throw new ClassCastException();
            }
            return this.priority() - o.priority();
        }
    }

    //Returns the minimum number of moves
    //to solve the initial board.
    public int moves() {
        return numMoves;
    }

    //Returns the sequence of Boards from
    //the initial board to the solution.
    public Iterable<Board> solution() {
        return boardSequence;
    }

    // DO NOT MODIFY MAIN METHOD
    // Uncomment this method once your Solver and Board classes are ready.
    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board initial = new Board(tiles);
        Solver solver = new Solver(initial);
        StdOut.println("Minimum number of moves = " + solver.moves());
        for (Board board : solver.solution()) {
            StdOut.println(board);
        }
    }

}
