package atomic.json;

/**
 * Exception class used to indicate DatastoreEntity could not be constructed.
 * @author Anthony G. Musco
 */
public class NoUniqueKeyException extends Exception {

    public NoUniqueKeyException(String key) {

        super("Class does not contain unique key: " + key);

    }

}
