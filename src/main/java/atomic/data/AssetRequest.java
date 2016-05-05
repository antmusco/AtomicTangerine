package atomic.data;

import java.util.Map;
import java.util.TreeMap;

/**
 * Enum which describes an Asset request made to the AssetServlet.
 *
 * @author Anthony G. Musco
 */
public enum AssetRequest {

    PROFILE_PIC("profilepic"),
    COMIC_FRAME("frame");

    private String request;
    private static Map<String, AssetRequest> requestMap = new TreeMap<>();

    /**
     * Static operation which inserts all enum values into the request map.
     */
    static {
        for (AssetRequest jp : AssetRequest.values()) {
            requestMap.put(jp.toString(), jp);
        }
    }

    /**
     * Constructor which binds the request String to it's enum value.
     *
     * @param request The String representation of the request.
     */
    AssetRequest(String request) {
        this.request = request;
    }

    /**
     * Static function which parses a request from a String parameter.
     *
     * @param request The String representation of the request, such as what is submitted in JSON.
     * @return The appropriate JsonProperty enum.
     */
    public static AssetRequest fromString(String request) {

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
