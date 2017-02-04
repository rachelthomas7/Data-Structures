public class LinkedListDeque<Item> {
    private class Node {
        public Node prev;
        public Item item;
        public Node next;
        public Node(Node p, Item i, Node n) {
            prev = p;
            item = i;
            next = n;
        }
    }
    private LinkedListDeque recursiveHelper = null;
    /* Constructors: */
    private Node sentinelF;
    private Node sentinelB;
    private int size;
    public LinkedListDeque() {
        size = 0;
        sentinelF = new Node(null, null, null);
        sentinelB = new Node(null, null, null);
        sentinelF.next = sentinelB;
        sentinelB.prev = sentinelF;
        recursiveHelper = this;
    }
    /* Methods: */
    public void addFirst(Item item) {
    /* Adds an item to the front of the Deque. */
        if (!isEmpty()) {
            Node oldFrontNode = sentinelF.next;
            Node newNode = new Node(sentinelF, item, oldFrontNode);
            sentinelF.next = newNode;
            oldFrontNode.prev = newNode;
        } else {
            Node newNode = new Node(sentinelF, item, sentinelB);
            sentinelF.next = newNode;
            sentinelB.prev = newNode;
        }
        size += 1;
        recursiveHelper = this;
    }
    public void addLast(Item item) {
    /* Adds an item to the back of the Deque. */
        if (!isEmpty()) {
            Node oldBackNode = sentinelB.prev;
            Node newNode = new Node(oldBackNode, item, sentinelB);
            sentinelB.prev = newNode;
            oldBackNode.next = newNode;
        } else {
            Node newNode = new Node(sentinelF, item, sentinelB);
            sentinelF.next = newNode;
            sentinelB.prev = newNode;
        }
        size += 1;
        recursiveHelper = this;
    }
    public boolean isEmpty() {
    /* Returns true if deque is empty, false otherwise. */
        if (sentinelF.next == sentinelB) {
            return true;
        } else {
            Node curr = sentinelF.next;
            while (curr != sentinelB) {
                if (curr.item != null) {
                    return false;
                }
            }
            return true;
        }
    }
    public int size() {
    /* Returns the number of items in the Deque. */
        return size;
    }
    public void printDeque() {
    /* Prints the items in the Deque from first to last, separated by a space. */
        Node curr = sentinelF.next;
        while (curr != sentinelB) {
            System.out.print(curr.item + " ");
            if (curr != sentinelB.prev) {
                System.out.print("--> ");
            }
            curr = curr.next;
        }
        System.out.println();
    }
    public Item removeFirst() {
    /* Removes and returns the item at the front of the Deque.
    If no such item exists, returns null. */
        if (isEmpty()) {
            return null;
        } else {
            Node itemToRemove = sentinelF.next;
            sentinelF.next = sentinelF.next.next;
            sentinelF.next.prev = sentinelF;
            recursiveHelper = this;
            size -= 1;
            return itemToRemove.item;
        }
    }
    public Item removeLast() {
    /* Removes and returns the item at the back of the Deque.
    * If no such item exists, returns null. */
        if (isEmpty()) {
            return null;
        } else {
            Node itemToRemove = sentinelB.prev;
            sentinelB.prev = sentinelB.prev.prev;
            sentinelB.prev.next = sentinelB;
            recursiveHelper = this;
            size -= 1;
            return itemToRemove.item;
        }
    }
    public Item get(int index) {
    /* Gets the item at the given index, where 0 is the front,
    * 1 is the next item and so forth.
    * If no such item exists, returns null. Must not alter the deque. */
        if (isEmpty()) {
            return null;
        } else {
            int count = 0;
            Node curr = sentinelF.next;
            while (curr != sentinelB) {
                if (count == index) {
                    return curr.item;
                }
                curr = curr.next;
                count += 1;
            }
            return null;
        }
    }
    public Item getRecursive(int index) {
        //take in a node, an index check if COUNTER FXN HELPER THINGS =
        return getRecursiveHelper(sentinelF.next, index).item;
    }
    private Node getRecursiveHelper(Node curr, int index) {
        int count = 0;
        while (curr != sentinelB) {
            if (count == index) {
                return curr;
            } else {
                curr = curr.next;
                count += 1;
            }
        }
        if (curr == sentinelB) {
            return null; //this indicates that this is not the item you seek
        }
        return null;
    }
}