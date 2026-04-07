package stores;

import structures.*;

import java.util.Arrays;

import interfaces.ICredits;

public class Credits implements ICredits{
    Stores stores;

    private MyHashMap<Integer, CastCredit[]> filmCast;

    // film ID → crew array (kept sorted by id field)
    private MyHashMap<Integer, CrewCredit[]> filmCrew;

    // person ID → Person object (for getCast/getCrew lookups)
    private MyHashMap<Integer, Person> castPeople;
    private MyHashMap<Integer, Person> crewPeople;

    // person ID → film IDs they appeared in
    private MyHashMap<Integer, MyHashSet<Integer>> castFilms;
    private MyHashMap<Integer, MyHashSet<Integer>> crewFilms;

    // person ID → total credit count (roles, not films)
    private MyHashMap<Integer, Integer> castCreditCount;

    /**
     * The constructor for the Credits data store. This is where you should
     * initialise your data structures.
     * 
     * @param stores An object storing all the different key stores, 
     *               including itself
     */
    public Credits (Stores stores) {
        this.stores          = stores;
        this.filmCast        = new MyHashMap<>();
        this.filmCrew        = new MyHashMap<>();
        this.castPeople      = new MyHashMap<>();
        this.crewPeople      = new MyHashMap<>();
        this.castFilms       = new MyHashMap<>();
        this.crewFilms       = new MyHashMap<>();
        this.castCreditCount = new MyHashMap<>();
    }

    /**
     * Adds data about the people who worked on a given film. The movie ID should be
     * unique
     * 
     * @param cast An array of all cast members that starred in the given film
     * @param crew An array of all crew members that worked on a given film
     * @param id   The (unique) movie ID
     * @return TRUE if the data able to be added, FALSE otherwise
     */
    @Override
    public boolean add(CastCredit[] cast, CrewCredit[] crew, int id) {
        if (filmCast.containsKey(id)) return false;

        Object[] sorted = mergeSort(cast.clone(), (a, b) -> a.getOrder() - b.getOrder());
        CastCredit[] sortedCast = new CastCredit[sorted.length];
        for (int i = 0; i < sorted.length; i++) sortedCast[i] = (CastCredit) sorted[i];


        Object[] sortedC = mergeSort(crew.clone(), (a, b) -> a.getID() - b.getID());
        CrewCredit[] sortedCrew = new CrewCredit[sortedC.length];
        for (int i = 0; i < sortedC.length; i++) sortedCrew[i] = (CrewCredit) sortedC[i];

        filmCast.put(id, sortedCast);
        filmCrew.put(id, sortedCrew);

        for (CastCredit c : cast) {
            if (!castPeople.containsKey(c.getID())) {  // ← correct
                castPeople.put(c.getID(), new Person(c.getID(), c.getName(), c.getProfilePath()));
            }

            if (!castFilms.containsKey(c.getID())) {
                castFilms.put(c.getID(), new MyHashSet<>());
            }

            castFilms.get(c.getID()).add(id);
            int current = castCreditCount.containsKey(c.getID()) ? castCreditCount.get(c.getID()) : 0;
            castCreditCount.put(c.getID(), current + 1);
        }

        for (CrewCredit c : crew) {
            if (!crewPeople.containsKey(c.getID()))  
                crewPeople.put(c.getID(), new Person(c.getID(), c.getName(), c.getProfilePath()));
            if (!crewFilms.containsKey(c.getID()))
                crewFilms.put(c.getID(), new MyHashSet<>());
            crewFilms.get(c.getID()).add(id);
        }

        return true;
    }

    /**
     * Remove a given films data from the data structure
     * 
     * @param id The movie ID
     * @return TRUE if the data was removed, FALSE otherwise
     */
    @Override
    public boolean remove(int id) {
        CastCredit[] cast = filmCast.get(id);
        CrewCredit[] crew = filmCrew.get(id);
        if (cast == null && crew == null) return false;

        if (cast != null) {
            for (CastCredit c : cast) {
                MyHashSet<Integer> films = castFilms.get(c.getID());
                if (films != null) films.remove(id);

                // reduce credit count
                if (castCreditCount.containsKey(c.getID())) {
                    int newCount = castCreditCount.get(c.getID()) - 1;
                    castCreditCount.put(c.getID(), newCount);
                }
            }
        }

        if (crew != null) {
            for (CrewCredit c : crew) {
                MyHashSet<Integer> films = crewFilms.get(c.getID());
                if (films != null) films.remove(id);
            }
        }

        filmCast.remove(id);
        filmCrew.remove(id);
        return true;
    }

    /**
     * Gets all the cast members for a given film
     * 
     * @param filmID The movie ID
     * @return An array of CastCredit objects, one for each member of cast that is 
     *         in the given film. The cast members should be in "order" order. If
     *         there is no cast members attached to a film, or the film cannot be 
     *         found in Credits, then return an empty array
     */
    @Override
    public CastCredit[] getFilmCast(int filmID) {
        CastCredit[] cast = filmCast.get(filmID);
        return cast == null ? new CastCredit[0] : cast;
    }

    /**
     * Gets all the crew members for a given film
     * 
     * @param filmID The movie ID
     * @return An array of CrewCredit objects, one for each member of crew that is
     *         in the given film. The crew members should be in "id" order (not "elementID"). If there 
     *         is no crew members attached to a film, or the film cannot be found in Credits, 
     *         then return an empty array
     */
    @Override
    public CrewCredit[] getFilmCrew(int filmID) {
        CrewCredit[] crew = filmCrew.get(filmID);
        return crew == null ? new CrewCredit[0] : crew;
    }

    /**
     * Gets the number of cast that worked on a given film
     * 
     * @param filmID The movie ID
     * @return The number of cast member that worked on a given film. If the film
     *         cannot be found in Credits, then return -1
     */
    @Override
    public int sizeOfCast(int filmID) {
        CastCredit[] cast = filmCast.get(filmID);
        return cast == null ? -1 : cast.length;
    }

    /**
     * Gets the number of crew that worked on a given film
     * 
     * @param filmID The movie ID
     * @return The number of crew member that worked on a given film. If the film
     *         cannot be found in Credits, then return -1
     */
    @Override
    public int sizeOfCrew(int filmID) {
        CrewCredit[] crew = filmCrew.get(filmID);
        return crew == null ? -1 : crew.length;
    }

    /**
     * Gets a list of all unique cast members present in the data structure
     * 
     * @return An array of all unique cast members as Person objects. If there are 
     *         no cast members, then return an empty array
     */
    @Override
    public Person[] getUniqueCast() {
        Object[] keys = castPeople.getKeys();
        Person[] result = new Person[keys.length];
        for (int i = 0; i < keys.length; i++)
            result[i] = castPeople.get((Integer) keys[i]);
        return result;
    }

    /**
     * Gets a list of all unique crew members present in the data structure
     * 
     * @return An array of all unique crew members as Person objects. If there are
     *         no crew members, then return an empty array
     */
    @Override
    public Person[] getUniqueCrew() {
        Object[] keys = crewPeople.getKeys();
        Person[] result = new Person[keys.length];
        for (int i = 0; i < keys.length; i++)
            result[i] = crewPeople.get((Integer) keys[i]);
        return result;
    }

    /**
     * Get all the cast members that have the given string within their name
     * 
     * @param cast The string that needs to be found
     * @return An array of unique Person objects of all cast members that have the 
     *         requested string in their name. If there are no matches, return an 
     *         empty array
     */
    @Override
    public Person[] findCast(String cast) {
        if (cast == null) return new Person[0];
        String lower = cast.toLowerCase();
        MyArrayList<Person> matches = new MyArrayList<>();
        Object[] keys = castPeople.getKeys();
        for (Object k : keys) {
            Person p = castPeople.get((Integer) k);
            if (p.getName() != null && p.getName().toLowerCase().contains(lower))
                matches.add(p);
        }
        Person[] result = new Person[matches.size()];
        for (int i = 0; i < matches.size(); i++) result[i] = matches.get(i);

        //java.util.Arrays.sort(result, (a, b) -> a.getID() - b.getID());

        return result;
    }

    /**
     * Get all the crew members that have the given string within their name
     * 
     * @param crew The string that needs to be found
     * @return An array of unique Person objects of all crew members that have the 
     *         requested string in their name. If there are no matches, return an 
     *         empty array
     */
    @Override
    public Person[] findCrew(String crew) {
        if (crew == null) return new Person[0];

        String lower = crew.trim().toLowerCase();
        if (lower.isEmpty()) return new Person[0];

        MyArrayList<Person> matches = new MyArrayList<>();

        Object[] keys = crewFilms.getKeys();

        for (Object k : keys) {
            Person p = crewPeople.get((Integer) k);

            if (p.getName() != null &&
                p.getName().toLowerCase().contains(lower)) {
                matches.add(p);
            }
        }

        Person[] result = new Person[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            result[i] = matches.get(i);
        }

        // sort by name (important for other test)
        Arrays.sort(result, (a, b) -> a.getName().compareTo(b.getName()));

        return result; 
    }

    /**
     * Gets the Person object corresponding to the cast ID
     * 
     * @param castID The cast ID of the person to be found
     * @return The Person object corresponding to the cast ID provided. 
     *         If a person cannot be found, then return null
     */
    @Override
    public Person getCast(int castID) {
        return castPeople.get(castID);
    }

    /**
     * Gets the Person object corresponding to the crew ID
     * 
     * @param crewID The crew ID of the person to be found
     * @return The Person object corresponding to the crew ID provided. 
     *         If a person cannot be found, then return null
     */
    @Override
    public Person getCrew(int crewID){
        return crewPeople.get(crewID);
    }

    
    /**
     * Get an array of film IDs where the cast member has starred in
     * 
     * @param castID The cast ID of the person
     * @return An array of all the films the member of cast has starred
     *         in. If there are no films attached to the cast member, 
     *         then return an empty array
     */
    @Override
    public int[] getCastFilms(int castID){
        MyHashSet<Integer> films = castFilms.get(castID);
        return films == null ? new int[0] : films.toIntArray();
    }

    /**
     * Get an array of film IDs where the crew member has starred in
     * 
     * @param crewID The crew ID of the person
     * @return An array of all the films the member of crew has starred
     *         in. If there are no films attached to the crew member, 
     *         then return an empty array
     */
    @Override
    public int[] getCrewFilms(int crewID) {
        MyHashSet<Integer> films = crewFilms.get(crewID);
        return films == null ? new int[0] : films.toIntArray();
    }

    /**
     * Get the films that this cast member stars in (in the top 3 cast
     * members/top 3 billing). This is determined by the order field in
     * the CastCredit class
     * 
     * @param castID The cast ID of the cast member to be searched for
     * @return An array of film IDs where the the cast member stars in.
     *         If there are no films where the cast member has starred in,
     *         or the cast member does not exist, return an empty array
     */
    @Override
    public int[] getCastStarsInFilms(int castID){
        MyHashSet<Integer> allFilms = castFilms.get(castID);
        if (allFilms == null) return new int[0];

        MyArrayList<Integer> starred = new MyArrayList<>();
        int[] filmIDs = allFilms.toIntArray();
        for (int filmID : filmIDs) {
            CastCredit[] cast = filmCast.get(filmID);
            if (cast == null) continue;

            for (int i = 0; i < Math.min(3, cast.length); i++) {
                if (cast[i].getID() == castID) {
                    starred.add(filmID);
                    break;
                }
            }
        }

        int[] result = new int[starred.size()];
        for (int i = 0; i < starred.size(); i++) result[i] = starred.get(i);
        return result;
    }
    
    /**
     * Get Person objects for cast members who have appeared in the most
     * films. If the cast member has multiple roles within the film, then
     * they would get a credit per role played. For example, if a cast
     * member performed as 2 roles in the same film, then this would count
     * as 2 credits. The list should be ordered by the highest to lowest number of credits.
     * 
     * @param numResults The maximum number of elements that should be returned
     * @return An array of Person objects corresponding to the cast members
     *         with the most credits, ordered by the highest number of credits.
     *         If there are less cast members that the number required, then the
     *         list should be the same number of cast members found.
     */

    // CHANGE COMMENTS
    @Override
    public Person[] getMostCastCredits(int numResults) {
        Object[] keys = castCreditCount.getKeys();
        int n = keys.length;
        if (n == 0 || numResults == 0) return new Person[0];

        int capacity = Math.min(numResults, n);

        // initialise heap array
        heap     = new HeapEntry[capacity];
        heapSize = 0;

        // push every person through the heap
        // heap keeps only the top K credits at any time
        for (Object k : keys) {
            int    credits = castCreditCount.get((Integer) k);
            Person p       = castPeople.get((Integer) k);
            heapPush(new HeapEntry(credits, p), capacity);
        }

        // extract from heap in reverse — min-heap gives ascending order
        // so filling result array backwards gives descending (highest first)
        Person[] result = new Person[heapSize];
        for (int i = heapSize - 1; i >= 0; i--) {
            result[i] = heap[0].person;   // root is always the minimum
            heap[0]   = heap[--heapSize]; // move last element to root
            bubbleDown(0);                // restore heap property
        }
        return result;
    }

    /**
     * Get the number of credits for a given cast member. If the cast member has
     * multiple roles within the film, then they would get a credit per role
     * played. For example, if a cast member performed as 2 roles in the same film,
     * then this would count as 2 credits.
     * 
     * @param castID A cast ID representing the cast member to be found
     * @return The number of credits the given cast member has. If the cast member
     *         cannot be found, return -1
     */
    @Override
    public int getNumCastCredits(int castID) {
        if (!castCreditCount.containsKey(castID)) return -1;
        return castCreditCount.get(castID);
    }

    /**
     * Gets the number of films stored in this data structure
     * 
     * @return The number of films in the data structure
     */
    @Override
    public int size() {
        return filmCast.getSize();
    } 

    // Additional sort functions 

    // merge sort for CastCredit and CrewCredit arrays, sorted by id field
    private <T> T[] mergeSort(T[] arr, java.util.Comparator<T> cmp) {
        if (arr.length <= 1) return arr;

        int mid = arr.length / 2;

        @SuppressWarnings("unchecked")
        T[] left  = (T[]) new Object[mid];

        @SuppressWarnings("unchecked")
        T[] right = (T[]) new Object[arr.length - mid];

        for (int i = 0;   i < mid;          i++) left[i]        = arr[i];
        for (int i = mid; i < arr.length;   i++) right[i - mid] = arr[i];

        left  = mergeSort(left,  cmp);
        right = mergeSort(right, cmp);

        return merge(left, right, cmp);
    }

    private <T> T[] merge(T[] left, T[] right, java.util.Comparator<T> cmp) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[left.length + right.length];

        int i = 0, j = 0, k = 0;

        while (i < left.length && j < right.length) {
            if (cmp.compare(left[i], right[j]) <= 0) result[k++] = left[i++];
            else                                      result[k++] = right[j++];
        }

        while (i < left.length)  result[k++] = left[i++];
        while (j < right.length) result[k++] = right[j++];

        return result;
    } 


    // Min-heap implementation for getMostCastCredits
    private static class HeapEntry {
        int credits;
        Person person;
        HeapEntry(int credits, Person person) {
            this.credits = credits;
            this.person  = person;
        }
    }

    private HeapEntry[] heap;
    private int heapSize;

    private void heapPush(HeapEntry e, int capacity) {
        if (heapSize < capacity) {
            heap[heapSize] = e;
            bubbleUp(heapSize);
            heapSize++;
        } else if (e.credits > heap[0].credits) {
            heap[0] = e;
            bubbleDown(0);
        }
    }

    private void bubbleUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (heap[parent].credits <= heap[i].credits) break;
            HeapEntry tmp  = heap[parent];
            heap[parent]   = heap[i];
            heap[i]        = tmp;
            i = parent;
        }
    }

    private void bubbleDown(int i) {
        while (true) {
            int left     = 2 * i + 1;
            int right    = 2 * i + 2;
            int smallest = i;
            if (left  < heapSize && heap[left].credits  < heap[smallest].credits) smallest = left;
            if (right < heapSize && heap[right].credits < heap[smallest].credits) smallest = right;
            if (smallest == i) break;
            HeapEntry tmp    = heap[smallest];
            heap[smallest]   = heap[i];
            heap[i]          = tmp;
            i = smallest;
        }
    }

}
