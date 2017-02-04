package hw3.hash;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;

public class TestSimpleOomage {

    @Test
    public void testHashCodeDeterministic() {
        SimpleOomage so = SimpleOomage.randomSimpleOomage();
        int hashCode = so.hashCode();
        for (int i = 0; i < 100; i += 1) {
            assertEquals(hashCode, so.hashCode());
        }
    }

    @Test
    public void testHashCodePerfect() {
        SimpleOomage o1 = new SimpleOomage(50, 10, 20);
//        System.out.println("50, 10, 20 hc = "+o1.hashCode());
        SimpleOomage o2 = new SimpleOomage(10, 50, 20);
//        System.out.println("10, 50, 20 hc = "+o2.hashCode());
        SimpleOomage o3 = new SimpleOomage(50, 50, 50);
//        System.out.println("50, 50, 50 hc = "+o3.hashCode());
        SimpleOomage o4 = new SimpleOomage(50, 50, 50);
//        System.out.println("50, 50, 50 hc = "+o4.hashCode());
        assertNotEquals(o1.hashCode(), o2.hashCode());
        assertEquals(o3.hashCode(), o4.hashCode());
        /*Creating 5 Oomages and visualizing them using your visualizer for M = 3.
            * Oomage 0 has hash code: -2
            * Oomage 1 has hash code: -1
            * Oomage 2 has hash code: 0
            * Oomage 3 has hash code: 1
            * Oomage 4 has hash code: 2*/
        //java.awt.Color[r=181,g=64,b=191] and java.awt.Color[r=181,g=164,b=19] have the same hash code!
        SimpleOomage a = new SimpleOomage(181, 64, 191);
        SimpleOomage b = new SimpleOomage(181, 164, 19);
//        System.out.println("181, 64, 191 hc = "+a.hashCode());
//        System.out.println("181, 164, 19 hc = "+b.hashCode());
        assertNotEquals(a.hashCode(), b.hashCode());


    }

    @Test
    public void testEquals() {
        SimpleOomage ooA = new SimpleOomage(5, 10, 20);
        SimpleOomage ooA2 = new SimpleOomage(5, 10, 20);
        SimpleOomage ooB = new SimpleOomage(50, 50, 50);
        assertEquals(ooA, ooA2);
        assertNotEquals(ooA, ooB);
        assertNotEquals(ooA2, ooB);
        assertNotEquals(ooA, "ketchup");
    }

    @Test
    public void testHashCodeAndEqualsConsistency() {
        SimpleOomage ooA = new SimpleOomage(5, 10, 20);
        SimpleOomage ooA2 = new SimpleOomage(5, 10, 20);
        HashSet<SimpleOomage> hashSet = new HashSet<SimpleOomage>();
        hashSet.add(ooA);
        assertTrue(hashSet.contains(ooA2));
    }

    /**
     * Calls tests for SimpleOomage.
     */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestSimpleOomage.class);
    }
}
