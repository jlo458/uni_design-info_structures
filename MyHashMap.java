// manual hashmap data structure

package structures;

public class MyHashMap<K, V> {
    
    private static final int INITIAL_CAPACITY = 16; 
    private static final double LOAD_FACTOR = 0.75; 

    private static class Entry<K, V> {
        K key; 
        V value; 
        Entry<K, V> next; 

        Entry(K key, V value) {
            this.key = key; 
            this.value = value;
        }
    }

    private Entry<K, V>[] table; 
    private int size;
    private int capacity; 

    // @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.capacity = INITIAL_CAPACITY; 
        this.table = new Entry[capacity]; 
    }

    private int hash(K key) {
        if (key == null) return 0; 
        int h = key.hashCode(); 
        h ^= (h>>>16); 
        return Math.abs(h%capacity);
    } 

    public V get(K key) {
        int index = hash(key); 
        Entry<K, V> entry = table[index]; 

        while (entry != null) {
            if (entry.key.equals(key)) return entry.value; 
            entry = entry.next;
        }

        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public void put(K key, V value ) {
        if ((double) size / capacity >= LOAD_FACTOR) resize();

        int index = hash(key); 
        Entry<K, V> entry = table[index];  

        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value; 
                return;
            }

            entry = entry.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value); 
        newEntry.next = table[index]; 
        table[index] = newEntry; 
        size++;
    } 

    public V remove(K key) {
        int index = hash(key); 
        Entry<K, V> entry = table[index];
        Entry<K, V> prev = null;  

        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) table[index] = entry.next; 
                else prev.next = entry.next; 

                size--; 
                return entry.value;
            }

            prev = entry; 
            entry = entry.next;
        }

        return null;
    }

    public int getSize() {
        return size;
    } 

    @SuppressWarnings("unchecked")
    public K[] getKeys() {
        K[] keys = (K[]) new Object[size];
        int index = 0; 
        for (Entry<K, V> bucket : table) {
            Entry<K, V> entry = bucket; 
            while (entry != null) {
                keys[index++] = entry.key;
                entry = entry.next;
            }
        }

        return keys;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= 2;
        Entry<K, V>[] oldTable = table;
        table = new Entry[capacity];
        size  = 0;

        for (Entry<K, V> bucket : oldTable) {
            Entry<K, V> entry = bucket;
            while (entry != null) {
                put(entry.key, entry.value); 
                entry = entry.next;
            }
        }
    }
}
