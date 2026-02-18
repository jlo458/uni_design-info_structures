public class LinkedList {

    private LinkedListElement head;

    public LinkedList() {
        head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        LinkedListElement ptr = head;
        int counter = 0;

        while (ptr != null) {
            counter++;
            ptr = ptr.getNext();
        }

        return counter;
    }

    public void addToTail(LinkedListElement val) {

        if (head == null) {        // empty list case
            head = val;
            return;
        }

        LinkedListElement ptr = head;

        while (ptr.getNext() != null) {   // move to last node
            ptr = ptr.getNext();
        }

        ptr.setNext(val);
    }

    public LinkedListElement removeFromTail() {

        if (head == null) return null;   // empty list

        if (head.getNext() == null) {    // only one element
            LinkedListElement temp = head;
            head = null;
            return temp;
        }

        LinkedListElement ptr = head;

        // stop at second-to-last node
        while (ptr.getNext().getNext() != null) {
            ptr = ptr.getNext();
        }

        LinkedListElement temp = ptr.getNext();
        ptr.setNext(null);
        return temp;
    }

    public LinkedListElement removeFromHead() {

        if (head == null) return null;

        LinkedListElement temp = head;
        head = head.getNext();
        return temp;
    }

    // Add to head (stack-style)
    public void add(String s) {
        LinkedListElement temp = new LinkedListElement(s);
        temp.setNext(head);
        head = temp;
    }
}
