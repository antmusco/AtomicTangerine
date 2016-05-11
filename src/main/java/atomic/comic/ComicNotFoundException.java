package atomic.comic;

/**
 * Exception class which is thrown if a Comic with the indicated gmail and title pair could not be retrieved from the
 * datastore.
 */
public class ComicNotFoundException extends Exception {

    public ComicNotFoundException(String gmail, String title) {
        super("Comic with key <" + gmail + ". " + title + "> could not be found.");
    }

}
