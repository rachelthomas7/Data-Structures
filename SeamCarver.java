/**
 * Created by RachelThomas on Apr/24/16.
 */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;


public class SeamCarver {
    private Picture pic;
    private SeamRemover seamRemover;

    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        seamRemover = new SeamRemover();
    }

    // current picture
    public Picture picture() {
        return new Picture(pic);
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        double picWidth = pic.width(), picHeight = pic.height();
        if (picWidth == 1 && picHeight == 1) {
            if (x != 0 || y != 0) {
                throw new IndexOutOfBoundsException();
            }
            return 0;
        } else if (picWidth == 1) { // cannot search left or right
            if (x != 0 || y < 0 || y > picHeight - 1) {
                throw new IndexOutOfBoundsException();
            }
            int nextY = y + 1, prevY = y - 1;
            if (y == 0) {
                prevY = (int) picHeight - 1;
            } else if (y == picHeight - 1) {
                nextY = 0;
            }
            Color cUp = pic.get(x, nextY);
            Color cDown = pic.get(x, prevY);
            double bY = Math.abs(cUp.getBlue() - cDown.getBlue());
            double rY = Math.abs(cUp.getRed() - cDown.getRed());
            double gY = Math.abs(cUp.getGreen() - cDown.getGreen());
            return rY * rY + gY * gY + bY * bY;
        } else if (picHeight == 1) { // cannot search up or down
            if (x < 0 || x > picWidth - 1 || y != 0) {
                throw new IndexOutOfBoundsException();
            }
            int nextX = x + 1, prevX = x - 1, nextY = y + 1, prevY = y - 1;
            if (x == 0) {
                prevX = (int) picWidth - 1;
            } else if (x == picWidth - 1) {
                nextX = 0;
            }
            Color cLeft = pic.get(prevX, y);
            Color cRight = pic.get(nextX, y);
            double bX = Math.abs(cLeft.getBlue() - cRight.getBlue());
            double rX = Math.abs(cLeft.getRed() - cRight.getRed());
            double gX = Math.abs(cLeft.getGreen() - cRight.getGreen());
            return rX * rX + gX * gX + bX * bX;
        }
        int nextX = x + 1, prevX = x - 1, nextY = y + 1, prevY = y - 1;
        if (x == 0) {
            prevX = (int) picWidth - 1;
        } else if (x == picWidth - 1) {
            nextX = 0;
        }
        if (y == 0) {
            prevY = (int) picHeight - 1;
        } else if (y == picHeight - 1) {
            nextY = 0;
        }
        Color cLeft = pic.get(prevX, y);
        Color cRight = pic.get(nextX, y);
        Color cUp = pic.get(x, nextY);
        Color cDown = pic.get(x, prevY);
        double bX = Math.abs(cLeft.getBlue() - cRight.getBlue());
        double rX = Math.abs(cLeft.getRed() - cRight.getRed());
        double gX = Math.abs(cLeft.getGreen() - cRight.getGreen());
        double deltaTwoX = rX * rX + gX * gX + bX * bX;
        double bY = Math.abs(cUp.getBlue() - cDown.getBlue());
        double rY = Math.abs(cUp.getRed() - cDown.getRed());
        double gY = Math.abs(cUp.getGreen() - cDown.getGreen());
        double deltaTwoY = rY * rY + gY * gY + bY * bY;
        return deltaTwoX + deltaTwoY;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        pic = transpose(pic);
        int[] path = findVerticalSeam();
        pic = transpose(pic);
        return path;

    }

    private Picture transpose(Picture p) {
        int width = p.width();
        int height = p.height();
        Picture newPic = new Picture(height, width);
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                Color c = p.get(row, col);
                newPic.set(col, row, c);
            }
        }
        return newPic;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int mindex = 0;
        int[] path = new int[height()];
        if (width() == 1) {
            for (int i = 0; i < height(); i++) {
                path[i] = 0;
            }
        } else if (height() == 1) {
            double mineng = Double.MAX_VALUE;
            for (int i = 0; i < width(); i++) {
                if (mineng > energy(i, 0)) {
                    mindex = i;
                    mineng = energy(i, 0);
                }
            }
            path[0] = mindex;
        } else {
            double[][] costOfMinCostPath = new double[height()][width()]; //indices: row, col
            double[][] energyCost = new double[height()][width()]; //indices: row, col
            for (int col = 0; col < energyCost[0].length; col++) {
                energyCost[0][col] = energy(col, 0);
                costOfMinCostPath[0][col] = energyCost[0][col];
            }
            for (int row = 1; row < height(); row++) {
                for (int col = 0; col < width(); col++) {
                    energyCost[row][col] = energy(col, row);
                    if (col == 0) { //cannot search left
                        costOfMinCostPath[row][col] = energyCost[row][col]
                                + Math.min(costOfMinCostPath[row - 1][col],
                                costOfMinCostPath[row - 1][col + 1]);
                    } else if (col == width() - 1) { //cannot search right
                        costOfMinCostPath[row][col] = energyCost[row][col]
                                + Math.min(costOfMinCostPath[row - 1][col],
                                costOfMinCostPath[row - 1][col - 1]);
                    } else {
                        costOfMinCostPath[row][col] = energyCost[row][col]
                                + Math.min(Math.min(costOfMinCostPath[row - 1][col],
                                costOfMinCostPath[row - 1][col + 1]),
                                costOfMinCostPath[row - 1][col - 1]);
                    }
                } //iterate through last row and find smallest one,
            } //then look back up through its "parents" to get the path
            Double min = Double.MAX_VALUE;
            for (int i = 0; i < width(); i++) {
                if (costOfMinCostPath[height() - 1][i] < min) {
                    // found lowest so far
                    min = costOfMinCostPath[height() - 1][i];
                    mindex = i;
                } //found col in last row with shortest val
            } //now, look back up through mindex's parent
            path[height() - 1] = mindex;
            for (int row = height() - 2; row >= 0; row--) {
                if (mindex == 0) { //cannot search left
                    double b = costOfMinCostPath[row][mindex];
                    double c = costOfMinCostPath[row][mindex + 1];
                    if (c <= b) { //right is smallest
                        mindex += 1;
                    }
                } else if (mindex == width() - 1) { //cannot search right
                    double a = costOfMinCostPath[row][mindex - 1];
                    double b = costOfMinCostPath[row][mindex];
                    if (a <= b) { //left is smallest
                        mindex -= 1;
                    }
                } else { //can search left & right
                    double a = costOfMinCostPath[row][mindex - 1];
                    double b = costOfMinCostPath[row][mindex];
                    double c = costOfMinCostPath[row][mindex + 1];
                    if (a <= b && a <= c) {
                        mindex -= 1; //left is smallest
                    } else if (c <= b && c <= a) {
                        mindex += 1; //right is smallest
                    }
                }
                path[row] = mindex;
            }
        }
        return path;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam.length != width()) {
            throw new IllegalArgumentException();
        }
        seamRemover.removeHorizontalSeam(picture(), seam);
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        if (seam.length != height()) {
            throw new IllegalArgumentException();
        }
        seamRemover.removeVerticalSeam(picture(), seam);
    }
}
