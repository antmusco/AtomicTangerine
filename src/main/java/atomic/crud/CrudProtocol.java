package atomic.crud;

/**
 * Details the enumeration for the CRUD protocol. This enum provides several utility functions for converting the
 * CrudProtocol to/from String representation for exchanging data over JSON.
 *
 * @author anthony
 */
public enum CrudProtocol {

    CREATE("create"),
    RETRIEVE("retrieve"),
    UPDATE("update"),
    DELETE("delete");

    /**
     * String used to indicate this request in a JSON object.
     */
    private String request;

    /**
     * Constructor which initializes the enumerated CrudProtocol requests.
     * @param request String representing the request.
     */
    CrudProtocol(String request) {

        this.request = request;

    }

    /**
     * Static function which parses a request from a String parameter.
     * @param request The String representation of the request, such as what is submitted in JSON.
     * @return The appropriate CrudProtocol enum.
     */
    public static CrudProtocol getRequest(String request) {

        // Switch on the request String.
        switch(request) {

            case "create": return CREATE;
            case "retrieve": return RETRIEVE;
            case "update": return UPDATE;
            case "delete": return DELETE;

        }

        // If no match found, Illegal request type.
        throw new IllegalArgumentException("No such Protocol.");

    }

    /**
     * Public method which returns the indicated protocol in it's String form.
     * @return The request as a String.
     */
    public String toString() {

        return request;

    }

}
