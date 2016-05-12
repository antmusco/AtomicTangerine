package atomic.comment;

import java.util.Map;
import java.util.TreeMap;

/**
 * String enumeration which indicates the type of requests the front end can make to the CommentCrudServlet.
 *
 * @author Anthony G. Musco
 */
public enum CommentRequest {

    GET_COMMENTS_FOR_COMIC("GET_COMMENTS_FOR_COMIC"),
    GET_COMMENTS_FOR_USER("GET_COMMENTS_FOR_USER"),
    DELETE_COMMENT("DELETE_COMMENT"),
    VOTE("VOTE");

    private String request;
    private static Map<String, CommentRequest> requestMap = new TreeMap<>();

    /**
     * Static operation which inserts all enum values into the request map.
     */
    static {
        for (CommentRequest jp : CommentRequest.values()) {
            requestMap.put(jp.toString(), jp);
        }
    }

    /**
     * Constructor which binds the request String to it's enum value.
     *
     * @param request The String representation of the request.
     */
    CommentRequest(String request) {
        this.request = request;
    }

    /**
     * Static function which parses a request from a String parameter.
     *
     * @param request The String representation of the request, such as what is submitted in JSON.
     * @return The appropriate ComicRequest enum.
     */
    public static CommentRequest fromString(String request) {

        if (requestMap.containsKey(request))
            return requestMap.get(request);
        else
            throw new IllegalArgumentException("AssetRequest not found: " + request);

    }

    /**
     * Public method which returns the indicated request in it's String form.
     *
     * @return The request as a String.
     */
    public String toString() {

        return request;

    }

}
