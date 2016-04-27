package atomic.user;

/**
 * Simple Exception class indicating a User was not found for some reason.
 *
 * @author Anthony G. Musco
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }

}
