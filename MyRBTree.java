// manual rb tree

package structures;

// mention why rb not avl trees

public class MyRBTree<K extends Comparable<? super K>, V> {
    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private static class Node<K, V> {
        K key; 
        V value; 
        Node<K, V> left, right, parent; 

        boolean colour = RED; // makes new nodes RED so that no. blacks doesn't change initially 

        Node(K key, V value, Node<K, V> parent) {
            this.key = key; 
            this.value = value; 
            this.parent = parent;
        }
    }

    private Node<K, V> root; 
    private int size; 

    private Node<K, V> findNode(K key) {
        Node<K, V> curNode = root; 
        while (curNode != null) {
            int cmp = key.compareTo(curNode.key);
            if (cmp < 0) curNode = curNode.left; 
            else if (cmp > 0) curNode = curNode.right; 
            else return curNode;
        }
        return null;
    } 

    public boolean findKey(K key) { 
        return findNode(key) != null;
    } 

    public V getNode(K key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    public void put(K key, V value) {
        if (root == null) {
            root = new Node<>(key, value, null);
            root.colour = BLACK;
            size++;
            return;
        }
        // figure out whether colour related to root node 

        Node<K, V> curNode = root;
        Node<K, V> parent = null; 

        int cmp = 0;

        while (curNode != null) {
            parent = curNode; 
            cmp = key.compareTo(curNode.key); 
            if (cmp < 0) curNode = curNode.left; 
            else if (cmp > 0) curNode = curNode.right; 

            // alters value if node found with same key
            else { 
                curNode.value = value; 
                return; 
            }
        }

        Node<K, V> newNode = new Node<>(key, value, parent);
        if (cmp < 0) parent.left = newNode; 
        else parent.right = newNode;
        size++; 

        fixAfterInsert(newNode);
    } 

    // in-order traversal 
    public K[] inOrderList() {
        @SuppressWarnings("unchecked")
        K[] keys = (K[]) new Comparable[size];
        int index = 0; 
        inOrder(root, keys, index);
        return keys;
    }

    private int inOrder(Node<K, V> node, K[] keys, int index) {
        if (node == null) return index; 
        index = inOrder(node.left, keys, index); 
        keys[index++] = node.key; 
        index = inOrder(node.right, keys, index);
        return index;
    }

    public int size() {
        return size;
    }

    // rotations for RB properties 

    private void rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right; 
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left   = x;
        x.parent = y;
    }

    private void rotateRight(Node<K, V> x) {
        Node<K, V> y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if      (x.parent == null)        root           = y;
        else if (x == x.parent.right)     x.parent.right = y;
        else                              x.parent.left  = y;
        y.right  = x;
        x.parent = y;
    }

    // make function do get all ID's from a certain range 
    // Returns all values whose key is strictly between lo and hi
    public MyArrayList<V> subMap(K lo, K hi) {
        MyArrayList<V> result = new MyArrayList<>();
        inOrderRange(root, lo, hi, result);
        return result;
    }

    private void inOrderRange(Node<K, V> node, K lo, K hi, MyArrayList<V> result) {
        if (node == null) return;
        int cmpLo = lo.compareTo(node.key);
        int cmpHi = hi.compareTo(node.key);

        // Only recurse left if there could be keys > lo in that subtree
        if (cmpLo < 0) inOrderRange(node.left,  lo, hi, result);

        // Visit this node only if strictly inside the range
        if (cmpLo < 0 && cmpHi > 0) result.add(node.value);

        // Only recurse right if there could be keys < hi in that subtree
        if (cmpHi > 0) inOrderRange(node.right, lo, hi, result);
    }

    private void fixAfterInsert(Node<K, V> n) {
        while (n!=root && colourOf(n.parent) == RED) {
            Node<K, V> parent = n.parent; 
            Node<K, V> grandparent = parent.parent;

            if (parent == leftOf(grandparent)) {
                Node<K, V> uncle = rightOf(grandparent); 

                if (colourOf(uncle) == RED) {   // case: uncle is also red -> recolour and move up
                    setColour(parent, BLACK);
                    setColour(uncle, BLACK);
                    setColour(grandparent, RED);
                    n = grandparent;
                }

                else {    // case: uncle black, n is right child -> rotate left
                    if (n == rightOf(parent)) {
                        n = parent; 
                        rotateLeft(n);
                        parent = n.parent; 
                        grandparent = parent.parent;
                    } 

                    // Case: n now parent of initial parent, just need to change colour of parent and gParent
                    setColour(parent, BLACK);
                    setColour(grandparent, RED);
                    rotateRight(grandparent);


                }
            } 

            else {
                Node<K, V> uncle = leftOf(grandparent); 

                if (colourOf(uncle) == RED) {   
                    setColour(parent, BLACK);
                    setColour(uncle, BLACK);
                    setColour(grandparent, RED);
                    n = grandparent;
                }

                else {    
                    if (n == leftOf(parent)) {
                        n = parent; 
                        rotateRight(n);
                        parent = n.parent; 
                        grandparent = parent.parent;
                    } 

                    setColour(parent, BLACK);
                    setColour(grandparent, RED);
                    rotateLeft(grandparent);
                }
            }

        } 

        root.colour = BLACK;
    } 

    private boolean colourOf(Node<K, V> n) { 
        return n == null ? BLACK : n.colour; 
    }

    private void setColour(Node<K, V> n, boolean c) { 
        if (n != null) n.colour = c; 
    }

    private Node<K,V> leftOf(Node<K, V> n) {
        return n == null ? null : n.left;  
    }

    private Node<K,V>  rightOf(Node<K, V> n) {
        return n == null ? null : n.right; 
    }
}
