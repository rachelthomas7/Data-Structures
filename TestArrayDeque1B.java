import jh61b.junit.TestRunner;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Random;

public class TestArrayDeque1B {
    /**
     * Performs a few tests to ensure product functionality
     **/
    @Test
    public void testAll() {
        FailureSequence fs = new FailureSequence();
        // randomly checks all methods using addFirst, addLast, removeFirst, removeLast, get and size.
        StudentArrayDeque<Integer> deq = new StudentArrayDeque<Integer>();
        Random randomGenerator = new Random();
        ArrayDequeSolution<Integer> deqs = new ArrayDequeSolution<Integer>();

        DequeOperation dequeOp1 = new DequeOperation("addFirst", 123);
        fs.addOperation(dequeOp1);
        deq.addFirst(123);
        deqs.addFirst(123);

        DequeOperation dequeOp2 = new DequeOperation("removeLast");
        fs.addOperation(dequeOp2);
        deq.removeLast();
        deqs.removeLast();

        DequeOperation dequeOp3 = new DequeOperation("removeLast");
        fs.addOperation(dequeOp3);
        deq.removeLast();
        deqs.removeLast();

        DequeOperation dequeOpwhatever = new DequeOperation("size");
        fs.addOperation(dequeOpwhatever);
        assertEquals(fs.toString(), deqs.size(), deq.size());

        // GRADESCOPE YOU BETTER AT LEAST LIKE THIS ONE ^^^^^
//        deq.printDeque();
        for (int i = 0; i < 10000; i++) {
            // goes from 1 to 1000, adding or removing elements randomly.
            //generates random number between 0 and 9.
            int randomInt = randomGenerator.nextInt(10);
            if (randomInt <= 2) {
                DequeOperation deqOp1 = new DequeOperation("addFirst", randomInt);
                fs.addOperation(deqOp1);
                deq.addFirst(randomInt);
                deqs.addFirst(randomInt);

            } else if (randomInt <= 4) {
                //use addLast
                DequeOperation deqOp1 = new DequeOperation("addLast", randomInt);
                fs.addOperation(deqOp1);
                deq.addLast(randomInt);
                deqs.addLast(randomInt);

            } else if (randomInt <= 6) {
                //use removeFirst
                DequeOperation deqOp1 = new DequeOperation("removeFirst");
                fs.addOperation(deqOp1);
                Integer actual = deq.removeFirst();
                Integer expected = deqs.removeFirst();
                assertEquals(fs.toString(), expected, actual);
            } else if (randomInt <= 10) {
                //use removeLast
                DequeOperation deqOp1 = new DequeOperation("removeLast");
                fs.addOperation(deqOp1);
                Integer actual = deq.removeLast();
                Integer expected = deqs.removeLast();
                assertEquals(fs.toString(), expected, actual);
            }
            DequeOperation dequeOpwhatever1 = new DequeOperation("size");
            fs.addOperation(dequeOpwhatever1);
            assertEquals(fs.toString(), deqs.size(), deq.size());
            DequeOperation dequeOpwhatever2 = new DequeOperation("isEmpty");
            fs.addOperation(dequeOpwhatever2);
            assertEquals(fs.toString(), deqs.isEmpty(), deq.isEmpty());

            if (deqs.size() > 0) {
                DequeOperation dequeOpwhatever3 = new DequeOperation("get", 0);
                fs.addOperation(dequeOpwhatever3);
                assertEquals(fs.toString(), deqs.get(0), deq.get(0));
            }

        }

    }

    public void main(String[] args) {
        TestRunner.runTests("all", new Class[]{TestArrayDeque1B.class});

    }


}
