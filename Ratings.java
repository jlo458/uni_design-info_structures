package stores;

import java.time.LocalDateTime;
import java.util.Arrays;

import interfaces.IRatings;
import structures.*;

public class Ratings implements IRatings {
    Stores stores;

    private MyHashMap<Integer, MyHashMap<Integer, float[]>> userRatings;
    private MyHashMap<Integer, MyHashMap<Integer, float[]>> movieRatings;

    private int totalSize;


    /**
     * The constructor for the Ratings data store. This is where you should
     * initialise your data structures.
     * @param stores An object storing all the different key stores,
     *               including itself
     */
    
    public Ratings(Stores stores) {
        this.stores      = stores;
        this.userRatings  = new MyHashMap<>();
        this.movieRatings = new MyHashMap<>();
        this.totalSize    = 0;
    }

    /**
     * Adds a rating to the data structure. The rating is made unique by its user ID
     * and its movie ID
     * 
     * @param userID    The user ID
     * @param movieID   The movie ID
     * @param rating    The rating gave to the film by this user (between 0 and 5
     *                  inclusive)
     * @param timestamp The time at which the rating was made
     * @return TRUE if the data able to be added, FALSE otherwise
     */
    @Override
    public boolean add(int userid, int movieid, float rating, LocalDateTime timestamp) {
        // unique key is (userid, movieid) pair — reject if already exists
        if (userRatings.containsKey(userid) &&
            userRatings.get(userid).containsKey(movieid)) return false;

        // store [rating, timestamp] — float[] of size 2
        float[] entry = { rating, timestamp.toEpochSecond(java.time.ZoneOffset.UTC) };

        if (!userRatings.containsKey(userid))
            userRatings.put(userid, new MyHashMap<>());
        userRatings.get(userid).put(movieid, entry);

        if (!movieRatings.containsKey(movieid))
            movieRatings.put(movieid, new MyHashMap<>());
        movieRatings.get(movieid).put(userid, entry);

        totalSize++;
        return true;
    }

    /**
     * Removes a given rating, using the user ID and the movie ID as the unique
     * identifier
     * 
     * @param userID  The user ID
     * @param movieID The movie ID
     * @return TRUE if the data was removed successfully, FALSE otherwise
     */
    @Override
    public boolean remove(int userid, int movieid) {
        if (!userRatings.containsKey(userid)) return false;
        if (!userRatings.get(userid).containsKey(movieid)) return false;

        userRatings.get(userid).remove(movieid);
        movieRatings.get(movieid).remove(userid);
        totalSize--;
        return true;
    }

    /**
     * Sets a rating for a given user ID and movie ID. Therefore, should the given
     * user have already rated the given movie, the new data should overwrite the
     * existing rating. However, if the given user has not already rated the given
     * movie, then this rating should be added to the data structure
     * 
     * @param userID    The user ID
     * @param movieID   The movie ID
     * @param rating    The new rating to be given to the film by this user (between
     *                  0 and 5 inclusive)
     * @param timestamp The time at which the new rating was made
     * @return TRUE if the data able to be added/updated, FALSE otherwise
     */
    @Override
    public boolean set(int userid, int movieid, float rating, LocalDateTime timestamp) {
        float[] entry = { rating, timestamp.toEpochSecond(java.time.ZoneOffset.UTC) };

        if (!userRatings.containsKey(userid))
            userRatings.put(userid, new MyHashMap<>());

        // if new entry, increment size
        if (!userRatings.get(userid).containsKey(movieid)) totalSize++;

        userRatings.get(userid).put(movieid, entry);

        if (!movieRatings.containsKey(movieid))
            movieRatings.put(movieid, new MyHashMap<>());
        movieRatings.get(movieid).put(userid, entry);

        return true;
    }

    /**
     * Get all the ratings for a given film
     * 
     * @param movieID The movie ID
     * @return An array of ratings. If there are no ratings or the film cannot be
     *         found in Ratings, then return an empty array
     */
    @Override
    public float[] getMovieRatings(int movieid) {
        if (!movieRatings.containsKey(movieid)) return new float[0];

        MyHashMap<Integer, float[]> ratings = movieRatings.get(movieid);

        Object[] keys = ratings.getKeys();
        float[] result = new float[keys.length];

        for (int i = 0; i < keys.length; i++)
            result[i] = ratings.get((Integer) keys[i])[0];

        return result;
    }

    /**
     * Get all the ratings for a given user
     * 
     * @param userID The user ID
     * @return An array of ratings. If there are no ratings or the user cannot be
     *         found in Ratings, then return an empty array
     */
    @Override
    public float[] getUserRatings(int userid) {
        if (!userRatings.containsKey(userid)) return new float[0];

        MyHashMap<Integer, float[]> ratings = userRatings.get(userid);

        Object[] keys = ratings.getKeys();
        float[] result = new float[keys.length];

        for (int i = 0; i < keys.length; i++)
            result[i] = ratings.get((Integer) keys[i])[0];

        return result;
    }

    /**
     * Get the average rating for a given film
     * 
     * @param movieID The movie ID
     * @return Produces the average rating for a given film. 
     *         If the film cannot be found in Ratings, but does exist in the Movies store, return 0.0f. 
     *         If the film cannot be found in Ratings or Movies stores, return -1.0f.
     */
    @Override
    public float getMovieAverageRating(int movieid) {
        // not in ratings — check movies store
        if (!movieRatings.containsKey(movieid)) {
            if (stores.getMovies().getTitle(movieid) != null) return 0.0f;
            return -1.0f;
        }

        MyHashMap<Integer, float[]> ratings = movieRatings.get(movieid);
        Object[] keys = ratings.getKeys();
        if (keys.length == 0) return 0.0f;

        float sum = 0;
        for (Object k : keys) sum += ratings.get((Integer) k)[0];
        return sum / keys.length;
    }

    /**
     * Get the average rating for a given user
     * 
     * @param userID The user ID
     * @return Produces the average rating for a given user. If the user cannot be
     *         found in Ratings, or there are no rating, return -1.0f
     */
    @Override
    public float getUserAverageRating(int userid) {
        if (!userRatings.containsKey(userid)) return -1.0f;

        MyHashMap<Integer, float[]> ratings = userRatings.get(userid);
        Object[] keys = ratings.getKeys();
        if (keys.length == 0) return -1.0f;

        float sum = 0;
        for (Object k : keys) sum += ratings.get((Integer) k)[0];
        return sum / keys.length;
    }

    /**
     * Gets the top N movies with the most ratings, in order from most to least
     * 
     * @param num The number of movies that should be returned
     * @return A sorted array of movie IDs with the most ratings. The array should be
     *         no larger than num. If there are less than num movies in the store,
     *         then the array should be the same length as the number of movies in Ratings
     */
    @Override
    public int[] getMostRatedMovies(int num) {
        Object[] movieKeys = movieRatings.getKeys();
        int n = movieKeys.length;
        if (n == 0 || num == 0) return new int[0];

        float[] scores = new float[n];
        int[]   ids    = new int[n];
        for (int i = 0; i < n; i++) {
            ids[i]    = (Integer) movieKeys[i];
            scores[i] = movieRatings.get(ids[i]).getSize(); // cast int → float
        }

        mergeSort(scores, ids, 0, n);

        return Arrays.copyOf(ids, Math.min(num, n));
    }

    /**
     * Gets the top N users with the most ratings, in order from most to least
     * 
     * @param num The number of users that should be returned
     * @return A sorted array of user IDs with the most ratings. The array should be
     *         no larger than num. If there are less than num users in the store,
     *         then the array should be the same length as the number of users in Ratings
     */
    @Override
    public int[] getMostRatedUsers(int num) {
        Object[] userKeys = userRatings.getKeys();
        int n = userKeys.length;
        if (n == 0 || num == 0) return new int[0];

        float[] scores = new float[n];
        int[]   ids    = new int[n];
        for (int i = 0; i < n; i++) {
            ids[i]    = (Integer) userKeys[i];
            scores[i] = userRatings.get(ids[i]).getSize();
        }

        mergeSort(scores, ids, 0, n);

        return Arrays.copyOf(ids, Math.min(num, n));
    }

    /**
     * Get the number of ratings that a movie has
     * 
     * @param movieid The movie id to be found
     * @return The number of ratings the specified movie has. 
     *         If the movie exists in the Movies store, but there are no ratings for it, then return 0. 
     *         If the movie does not exist in the Ratings or Movies store, then return -1.
     */
    @Override
    public int getNumRatings(int movieid) {
        if (!movieRatings.containsKey(movieid)) {
            // check if movie exists in Movies store
            if (stores.getMovies().getTitle(movieid) != null) return 0;
            return -1;
        }
        return movieRatings.get(movieid).getSize();
    }

    /**
     * Get the highest average rated film IDs, in order of there average rating
     * (hightst first).
     * 
     * @param numResults The maximum number of results to be returned
     * @return An array of the film IDs with the highest average ratings, highest
     *         first. If there are less than num movies in the store,
     *         then the array should be the same length as the number of movies in Ratings
     */
    @Override
    public int[] getTopAverageRatedMovies(int numResults) {
        Object[] movieKeys = movieRatings.getKeys();
        int n = movieKeys.length;
        if (n == 0 || numResults == 0) return new int[0];

        float[] scores = new float[n];
        int[]   ids    = new int[n];
        for (int i = 0; i < n; i++) {
            ids[i] = (Integer) movieKeys[i];
            MyHashMap<Integer, float[]> ratings = movieRatings.get(ids[i]);
            Object[] rKeys = ratings.getKeys();
            float sum = 0;
            for (Object k : rKeys) sum += ratings.get((Integer) k)[0];
            scores[i] = rKeys.length == 0 ? 0f : sum / rKeys.length;
        }

        mergeSort(scores, ids, 0, n);

        return Arrays.copyOf(ids, Math.min(numResults, n));
    }

    /**
     * Gets the number of ratings in the data structure
     * 
     * @return The number of ratings in the data structure
     */
    @Override
    public int size() {
        return totalSize;
    } 

    // merge sort 
    private void mergeSort(float[] scores, int[] ids, int left, int right) {
        if (right - left <= 1) return;

        int mid = (left + right) / 2;
        mergeSort(scores, ids, left, mid);
        mergeSort(scores, ids, mid, right);

        // merge
        int lenL = mid - left, lenR = right - mid;
        float[] sL = new float[lenL]; int[] iL = new int[lenL];
        float[] sR = new float[lenR]; int[] iR = new int[lenR];

        System.arraycopy(scores, left, sL, 0, lenL);
        System.arraycopy(ids,    left, iL, 0, lenL);
        System.arraycopy(scores, mid,  sR, 0, lenR);
        System.arraycopy(ids,    mid,  iR, 0, lenR);

        int l = 0, r = 0, k = left;
        while (l < lenL && r < lenR) {
            if (sL[l] >= sR[r]) { scores[k] = sL[l]; ids[k] = iL[l]; l++; }
            else                 { scores[k] = sR[r]; ids[k] = iR[r]; r++; }
            k++;
        }
        while (l < lenL) { scores[k] = sL[l]; ids[k] = iL[l]; l++; k++; }
        while (r < lenR) { scores[k] = sR[r]; ids[k] = iR[r]; r++; k++; }
    }
}


