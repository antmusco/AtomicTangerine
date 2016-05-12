package atomic.comic;

import java.util.Map;
import java.util.TreeMap;

/**
 * String enumeration which indicates the type of requests the front end can make to the ComicCrudServlet.
 *
 * @author Anthony G. Musco
 */
public enum ComicRequest {

    UPLOAD_FRAME("UPLOAD_FRAME"),
    UPDATE_COMIC("UPDATE_COMIC"),
    GET_COMIC_LIST_CUSTOM("GET_COMIC_LIST_CUSTOM"),
    GET_COMIC_LIST_DEFAULT("GET_COMIC_LIST_DEFAULT"),
    GET_USER_COMICS("GET_USER_COMICS"),
    GET_SINGLE_COMIC("GET_SINGLE_COMIC"),
    VOTE_FOR_COMIC("VOTE_FOR_COMIC");

    private String request;
    private static Map<String, ComicRequest> requestMap = new TreeMap<>();

    /**
     * Static operation which inserts all enum values into the request map.
     */
    static {
        for (ComicRequest jp : ComicRequest.values()) {
            requestMap.put(jp.toString(), jp);
        }
    }

    /**
     * Constructor which binds the request String to it's enum value.
     *
     * @param request The String representation of the request.
     */
    ComicRequest(String request) {
        this.request = request;
    }

    /**
     * Static function which parses a request from a String parameter.
     *
     * @param request The String representation of the request, such as what is submitted in JSON.
     * @return The appropriate ComicRequest enum.
     */
    public static ComicRequest fromString(String request) {

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
