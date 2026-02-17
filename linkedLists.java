// not done yet


public class LinkedList {
    private LinkedListElement head; 

    public LinkedList() {
        // initialise as empty
        head = null;
    }

    public isEmpty() {
        if (head == null) {
            return true;
        } 
        return false;
    } 

    public size() {
        LinkedListElement ptr = head; 
        counter = 0;

        while (ptr != null) {
            ptr = ptr.nextElement;
            counter += 1;
        }

        return counter;
    }



    // adding elements
    public void add(String s) {
        LinkedListElement temp = new LinkedListElement(s);

        if (head != null) {
            temp.setNext(head);
        }

        head = temp;
    }
}

public class LinkedListElement {
    private String value;
    public LinkedListElement nextElement;

    public LinkedListElement(String value) {
        this.value = value;
        this.nextElement = null;

    }

    public void setNext(LinkedListElement next_el) {
        nextElement = next_el;
    }

    
}
