// switch file name to main
// tests my linked list data structure

public class Main {

    public static void main(String[] args) {

        LinkedList myLinkedList = new LinkedList();

        System.out.println("Is empty? " + myLinkedList.isEmpty());

        // Add to head
        myLinkedList.add("A");
        myLinkedList.add("B");
        myLinkedList.add("C");

        System.out.println("Size after adding 3 to head: " + myLinkedList.size());
        printList(myLinkedList);

        // Add to tail
        myLinkedList.addToTail(new LinkedListElement("D"));
        myLinkedList.addToTail(new LinkedListElement("E"));

        System.out.println("Size after adding 2 to tail: " + myLinkedList.size());
        printList(myLinkedList);

        // Remove from head
        LinkedListElement removedHead = myLinkedList.removeFromHead();
        System.out.println("Removed from head: " + removedHead.getValue());
        printList(myLinkedList);

        // Remove from tail
        LinkedListElement removedTail = myLinkedList.removeFromTail();
        System.out.println("Removed from tail: " + removedTail.getValue());
        printList(myLinkedList);

        System.out.println("Final size: " + myLinkedList.size());
        System.out.println("Is empty? " + myLinkedList.isEmpty());
    }

    // Helper method to print list
    public static void printList(LinkedList list) {

        System.out.print("List: ");

        LinkedListElement ptr = list.removeFromHead();
        LinkedListElement tempHead = ptr;

        // We remove and re-add elements just for printing
        while (ptr != null) {
            System.out.print(ptr.getValue() + " ");
            ptr = list.removeFromHead();
        }

        // Restore list
        while (tempHead != null) {
            list.add(tempHead.getValue());
            tempHead = tempHead.getNext();
        }

        System.out.println();
    }
}

