/**
 * Performs some basic linked list tests.
 */
public class LinkedListDequeTest {
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
	 * <p>
	 * && is the "and" operation.
	 */
	public static void addIsEmptySizeTest() {
		System.out.println("Running add/isEmpty/Size test.");
//    System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
		LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
		boolean passed = checkEmpty(true, lld1.isEmpty());
		lld1.addFirst("front");
		passed = checkSize(1, lld1.size()) && passed;
		passed = checkEmpty(false, lld1.isEmpty()) && passed;
		lld1.addLast("middle");
		passed = checkSize(2, lld1.size()) && passed;
		lld1.addLast("back");
		passed = checkSize(3, lld1.size()) && passed;
		System.out.println("Printing out deque: ");
		lld1.printDeque();
		printTestStatus(passed);
	}
	/**
	 * Adds an item, then removes an item, and ensures that dll is empty afterwards.
	 */
	public static void addRemoveTest() {
		System.out.println("Running add/remove test.");
//    System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
		LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		boolean passed = checkEmpty(true, lld1.isEmpty());
		lld1.addFirst(10);
		// should not be empty
		passed = checkEmpty(false, lld1.isEmpty()) && passed;
		lld1.removeFirst();
		// should be empty
		passed = checkEmpty(true, lld1.isEmpty()) && passed;
		printTestStatus(passed);
	}
	public static void randomTests() {
		System.out.println("creating a new list with elements 5, 6, and 7.");
		LinkedListDeque<Integer> listy = new LinkedListDeque<Integer>();
		listy.addLast(5);
		listy.addLast(6);
		listy.addLast(7);
		listy.printDeque();
		System.out.println("adding 4.");
		listy.addLast(4);
		listy.printDeque();
		System.out.println("removing 4");
		listy.removeLast();
		listy.printDeque();
		System.out.println("Iteratively getting items at indices 0, 1, and 2.");
		System.out.println(listy.get(0) + ", " + listy.get(1) + ", " + listy.get(2));
		System.out.println("Recursively getting items at indices 0, 1 and 2.");
		System.out.println(listy.getRecursive(0));
		System.out.println(listy.getRecursive(1));
		System.out.println(listy.getRecursive(2));
	}
	public static void main(String[] args) {
		System.out.println("Running tests.\n");
		addIsEmptySizeTest();
		addRemoveTest();
		randomTests();
	}
}