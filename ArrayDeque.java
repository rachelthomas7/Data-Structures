/**
 * Created by RachelThomas and FrancesGrimaldo on Feb/1/16.
 */
public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private static int rFactor = 2;
    private int front; //front gives the integer of the index where the front of the array is located.
    private int back; //back gives the integer of the index where the back of the array is located.

    public ArrayDeque() {
        //constructor for ArrayDeque class
        size = 0;
        items = (Item[]) new Object[8];
        front = 0;
        back = 0;
    }

    public void addFirst(Item item) {
        //adds element item onto front of list
        if (isEmpty()) {
            /*for (int i = 0; i < size; i++) {
                items[i] = item;
            }*/
            items[0] = item;
            front = 0;
            back = 0;

        } else { // if not empty
            if (isFull()) {
                //must create a new array with a larger size to hold more values.
                Item[] itemsCopy = (Item[]) new Object[back * rFactor];
                itemsCopy[0] = item;
                front = 0;
                back = back + 1;
                for (int i = 1; i <= back; i++) {
                    itemsCopy[i] = items[i - 1];
                }

                items = itemsCopy;
            } else {
                // if neither empty nor full
                if (front != 0) {
                    //if there is an empty index before the active area
                    items[front - 1] = item;
                    front = front - 1;
                } else {
                    // if there is not an empty index before the active area, shift all elements over by 1
                    for (int i = back; i >= front; i--) {
                        items[i + 1] = items[i];
                    }
                    // add item to front
                    back = back + 1;
                    items[front] = item;
                }
            }
        }
        size += 1;
    }

    public void addLast(Item item) {
        //adds element item onto back of list
        if (isEmpty()) {
            // if list is empty, add onto the front (active area is always in the front)
//            for (int i = 0; i < items.length; i++) { //make sure this doesn't cause an error : usage of built ins
//                items[i] = item;
//            }
            items[0] = item;
            front = 0;
            back = 0;
        } else {
            //if not empty
            if (isFull()) {
                //must create a new array with a larger size to hold more values
                Item[] itemsCopy = (Item[]) new Object[back * rFactor];
                front = 0;
                back = back + 1;
                itemsCopy[back] = item;
                for (int i = 0; i < back; i++) {
                    itemsCopy[i] = items[i];
                }
                items = itemsCopy;
            } else {
                // if neither empty nor full, add to the back of the list
                if (back < size - front) {
                    back = back + 1;
                    items[back] = item;
                } else {
                    // shift all items over by 1
                }
            }
        }
        size += 1;
    }

    public boolean isEmpty() {
        //checks whether the ArrayDeque is empty or not.
        return (size == 0);
    }

    private Boolean isFull() {
        //checks whether the ArrayDeque is full or not.
        return (back == items.length - 1 && front == 0);

    }

    public int size() {
        //returns the size of the ArrayDeque. Takes constant time to execute.
        return size;
    }

    public void printDeque() {
        //prints elements of ArrayDeque from first to last.
        for (int i = front; i <= back; i++) {
            System.out.print(items[i] + " ");
            if (i != back) {
                System.out.print("-->");
            }
        }
        System.out.println();
    }

    public Item removeFirst() {
        //removes the first item in the ArrayDeque. Shifts remaining entries over so that they are now in front.
        if (isEmpty()) {
            return null;
        }
        Item itemToRemove = items[0];
        for (int i = front; i < back; i++) {
            items[i] = items[i + 1];
        }
        back -= 1;
        size -= 1;
        resize();
        return itemToRemove;
        //should work????
    }

    public Item removeLast() {
        //removes the last item in the ArrayDeque.
        if (isEmpty()) {
            return null;
        } else {
            Item itemToRemove = items[back];
//            items[back] = nu;
            back -= 1;
            size -= 1;
            resize();
            return itemToRemove;
        }//should work (pls pls pls)
    }

    private void resize() {
        //resizes the ArrayDeque so that the array used is only large enough to hold the items inside.
        // if you have an int, you can multiply by 100 so it does not round down to 0.
        if (((size * 100) / items.length) < 25 && items.length >= 16) {
            Item[] itemsCopy = (Item[]) new Object[back - front + 1];
            // fill new array with elements in items.
            int count = 0;
            for (int i = front; i <= back; i++) {
                itemsCopy[count] = items[i];
                count += 1;
            }
            front = 0;
            back = itemsCopy.length - 1;
            items = itemsCopy;
        }
    }

    public Item get(int index) {
        //returns the item at given index in the ArrayDeque. Takes constant time to execute.
        if (index >= size) {
            return null;
        } else {
            return items[index + front];
        }
    } // should work. please let us have at least this, O naked gods of cs.
}