public class ArrayDequeTest {
    /* Utility method for printing out empty checks. */
    public static boolean checkEmpty(boolean expected, boolean actual) {
        if (expected != actual) {
            System.out.println("isEmpty() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }
    /* Utility method for printing out empty checks. */
    public static boolean checkSize(int expected, int actual) {
        if (expected != actual) {
            System.out.println("size() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }
    /* Prints a nice message based on whether a test passed.
     * The \n means newline. */
    public static void printTestStatus(boolean passed) {
        if (passed) {
            System.out.println("Test passed!\n");
        } else {
            System.out.println("Test failed!\n");
        }
    }
    /**
     * Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     */
    public static void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");
        ArrayDeque<String> adele = new ArrayDeque<String>();
        boolean passed = checkEmpty(true, adele.isEmpty());
        adele.addFirst("front");
        passed = checkSize(1, adele.size()) && passed;
        passed = checkEmpty(false, adele.isEmpty()) && passed;
        adele.addLast("middle");
        passed = checkSize(2, adele.size()) && passed;
        adele.addLast("back");
        passed = checkSize(3, adele.size()) && passed;
        System.out.println("Printing out deque: ");
        adele.printDeque();
        printTestStatus(passed);
    }
    /**
     * Adds an item, then removes an item, and ensures that dll is empty afterwards.
     */
    public static void addRemoveTest() {
        System.out.println("Running add/remove test.");
        ArrayDeque<Integer> hello = new ArrayDeque<Integer>();
        // should be empty
        boolean passed = checkEmpty(true, hello.isEmpty());
        hello.addFirst(10);
        // should not be empty
        passed = checkEmpty(false, hello.isEmpty()) && passed;
        hello.removeFirst();
        // should be empty
        passed = checkEmpty(true, hello.isEmpty()) && passed;
        printTestStatus(passed);
    }
    public static void randomTests() {
        System.out.println("creating a new list with elements 5, 6, and 7.");
        ArrayDeque<Integer> itsMe = new ArrayDeque<Integer>();
        itsMe.addLast(5);
        itsMe.addLast(6);
        itsMe.addLast(7);
        itsMe.printDeque();
        System.out.println("adding 4.");
        itsMe.addLast(4);
        itsMe.printDeque();
        System.out.println("removing 4");
        itsMe.removeLast();
        itsMe.printDeque();
        System.out.println("Getting items at indices 0, 1, and 2.");
        System.out.println(itsMe.get(0) + ", " + itsMe.get(1) + ", " + itsMe.get(2));
    }
    public static void main(String[] args) {
        System.out.println("Running tests for ArrayDeque.java.\n");
        addIsEmptySizeTest();
        addRemoveTest();
        randomTests();
        getTested();
    }

    public static void getTested() {
        ArrayDeque<Integer> narp = new ArrayDeque<Integer>();
        narp.addLast(0);
        narp.addFirst(1);
        System.out.print(narp.get(1));
    }
}