// manual hash set, uses MyHashMap underneath

package structures;

// stores just bool value of whether it exists in certain group or not 
// useful for collections/genres etc

public class MyHashSet<T> {
    private MyHashMap<T, Boolean> map = new MyHashMap<>();

    public boolean add(T item)      { 
        if (map.containsKey(item)) return false; 

        map.put(item, true); 
        return true; 
    }
    
    public boolean contains(T item) { return map.containsKey(item); }

    public boolean remove(T item) { return map.remove(item) != null; }

    public int size() { return map.getSize(); }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) { 
        T[] keys = map.getKeys();
        if (array.length >= keys.length) {
            System.arraycopy(keys, 0, array, 0, keys.length);
            if (array.length > keys.length) array[keys.length] = null; // null terminate if array is bigger than keys
            return array;
        }
        else return keys;
    
    }

    public int[] toIntArray() {
        T[] keys = map.getKeys();
        int[] arr = new int[keys.length];
        for (int i = 0; i < keys.length; i++) arr[i] = (Integer) keys[i];
        return arr;
    }
}
