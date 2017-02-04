package hw3.hash;

import java.awt.Color;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdDraw;

import javax.sound.midi.SysexMessage;


public class SimpleOomage implements Oomage {
    protected int red;
    protected int green;
    protected int blue;

    private static final double WIDTH = 0.01;
    private static final boolean USE_PERFECT_HASH = true;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() != this.getClass() || (o == null)) {
            return false;
        }
        SimpleOomage k = (SimpleOomage) o;
        return ((k.red == this.red) && (k.blue == this.blue) && (k.green == this.green));
    }

    @Override
    public int hashCode() {
        if (!USE_PERFECT_HASH) {
            return red + green + blue;
        } else {
//            String hc = "";
            int result = 5;
//            hc = hc + red;
            result += red * 7;
            result += (blue * blue) + (blue / 2);
            result += green * 3;
//            System.out.print("RED = "+red);
//            hc = hc + "";
//            hc = hc + green;
//            System.out.print("BLUE = "+blue);
//            hc = hc + "8";
//            hc = hc + blue;
//            System.out.println("GREEN = "+green);
//            System.out.println("hc is "+hc);
            return 37 * result;
        }
    }

    public SimpleOomage(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException();
        }
        red = r;
        green = g;
        blue = b;
    }

    @Override
    public void draw(double x, double y, double scalingFactor) {
        StdDraw.setPenColor(new Color(red, green, blue));
        StdDraw.filledSquare(x, y, WIDTH * scalingFactor);
    }

    public static SimpleOomage randomSimpleOomage() {
        int red = StdRandom.uniform(0, 256);
        int green = StdRandom.uniform(0, 256);
        int blue = StdRandom.uniform(0, 256);

        return new SimpleOomage(red, green, blue);
    }

    public static void main(String[] args) {
        System.out.println("Drawing 4 random simple Oomages.");
        randomSimpleOomage().draw(0.25, 0.25);
        randomSimpleOomage().draw(0.75, 0.75);
        randomSimpleOomage().draw(0.25, 0.75);
        randomSimpleOomage().draw(0.75, 0.25);
    }

    public String toString() {
        return "R: " + red + ", G: " + green + ", B: " + blue;
    }
} 
