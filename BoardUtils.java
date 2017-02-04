//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;
import hw4.puzzle.Board;
import org.junit.Assert;

public class BoardUtils {
    private static final int BLANK = 0;

    public BoardUtils() {
    }

    public static void verifyImmutability() {
        byte var0 = 2;
        byte var1 = 2;
        int[][] var2 = new int[var0][var1];
        int var3 = 0;

        for (int var4 = 0; var4 < var0; ++var4) {
            for (int var5 = 0; var5 < var1; ++var5) {
                var2[var4][var5] = var3++;
            }
        }

        Board var6 = new Board(var2);
        Assert.assertEquals("Your Board class is not being initialized with the right values.", 0L, (long) var6.tileAt(0, 0));
        Assert.assertEquals("Your Board class is not being initialized with the right values.", 1L, (long) var6.tileAt(0, 1));
        Assert.assertEquals("Your Board class is not being initialized with the right values.", 2L, (long) var6.tileAt(1, 0));
        Assert.assertEquals("Your Board class is not being initialized with the right values.", 3L, (long) var6.tileAt(1, 1));
        var2[1][1] = 1000;
        Assert.assertEquals("Your Board class is mutable and you should be making a copy of the values in the passed tiles array. Please see the FAQ!", 3L, (long) var6.tileAt(1, 1));
    }

    public static Iterable<Board> neighbors(Board var0) {
        verifyImmutability();
        Queue var1 = new Queue();
        int var2 = var0.size();
        int var3 = -1;
        int var4 = -1;

        int var6;
        for (int var5 = 0; var5 < var2; ++var5) {
            for (var6 = 0; var6 < var2; ++var6) {
                if (var0.tileAt(var5, var6) == 0) {
                    var3 = var5;
                    var4 = var6;
                }
            }
        }

        int[][] var9 = new int[var2][var2];

        int var7;
        for (var6 = 0; var6 < var2; ++var6) {
            for (var7 = 0; var7 < var2; ++var7) {
                var9[var6][var7] = var0.tileAt(var6, var7);
            }
        }

        for (var6 = 0; var6 < var2; ++var6) {
            for (var7 = 0; var7 < var2; ++var7) {
                if (Math.abs(var6 - var3) + Math.abs(var7 - var4) == 1) {
                    var9[var3][var4] = var9[var6][var7];
                    var9[var6][var7] = 0;
                    Board var8 = new Board(var9);
                    var1.enqueue(var8);
                    var9[var6][var7] = var9[var3][var4];
                    var9[var3][var4] = 0;
                }
            }
        }

        return var1;
    }
}
