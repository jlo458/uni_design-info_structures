public class LinkedListElement {

    private String value;
    private LinkedListElement nextElement;

    public LinkedListElement(String value) {
        this.value = value;
        this.nextElement = null;
    }

    public String getValue() {
        return value;
    }

    public LinkedListElement getNext() {
        return nextElement;
    }

    public void setNext(LinkedListElement next_el) {
        nextElement = next_el;
    }
}
