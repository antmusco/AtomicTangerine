package atomic.comic;

/**
 * Exception class indicating that an attempt was made to create a comic which already exits.
 *
 * @author Anthony G. Musco
 */
public class ComicAlreadyExistsException extends Exception {

    public ComicAlreadyExistsException(String gmail, String title) {

        super("Attempt to create comic <" + gmail + ", " + title + ">, which already exists.");

    }

}
