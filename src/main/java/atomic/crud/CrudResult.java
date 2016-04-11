package atomic.crud;

/**
 * Enumeration class detailing the types of results of CRUD requests.
 *
 * @author Anthony
 */
public enum CrudResult {

    SUCCESS("success"),
    FAILURE("failure"),
    UNSUPPORTED("unsupported");

    private String result;

    /**
     * Constructor which initializes the result String member.
     * @param result The String representing this result.
     */
    CrudResult(String result) {  this.result = result; }

    /**
     * Public method which returns the indicated result in it's String form.
     * @return The result as a String.
     */
    public String toString() {

        return result;

    }

}
