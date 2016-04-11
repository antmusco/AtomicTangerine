package atomic.json;

import java.util.Map;
import java.util.TreeMap;

/**
 * Enumeration listing the possible Json property Strings. These properties represent the available "keys" to the
 * various key-value pairs embedded within a Json text document.
 *
 * @author Anthony G. Musco
 */
public enum JsonProperty {

    // Header properties.
    REQUEST("REQUEST"),
    RESULT("RESULT"),

    // Login Servlet.
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),

    // User Servlet.
    USER("USER"),
    GMAIL("GMAIL"),
    HANDLE("HANDLE"),
    FIRST_NAME("FIRST_NAME"),
    LAST_NAME("LAST_NAME"),
    BIO("BIO"),
    EXP_POINTS("EXP_POINTS"),
    DATE_JOINED("DATE_JOINED"),
    BIRTHDAY("BIRTHDAY"),
    PREFERENCES("PREFERENCES"),
    CREATED_COMICS("CREATED_COMICS"),

    // USER PREFERENCES
    USER_GMAIL("USER_GMAIL"),
    SUBSCRIPTIONS("SUBSCRIPTIONS"),
    LIKE_TAGS("LIKE_TAGS"),
    DISLIKE_TAGS("DISLIKE_TAGS"),
    FAVORITE_COMICS("FAVORITE_COMICS");

    private String property;
    private static Map<String, JsonProperty> propertyMap = new TreeMap<>();

    /**
     * Static operation which inserts all enum values into the property map.
     */
    static {
        for(JsonProperty jp : JsonProperty.values()) {
            propertyMap.put(jp.toString(), jp);
        }
    }

    /**
     * Constructor which binds the property String to it's enum value.
     * @param property The String representation of the property.
     */
    JsonProperty(String property) {
        this.property = property;
    }

    /**
     * Static function which parses a property from a String parameter.
     * @param property The String representation of the property, such as what is submitted in JSON.
     * @return The appropriate JsonProperty enum.
     */
    public static JsonProperty fromString(String property) {

        if(propertyMap.containsKey(property))
            return propertyMap.get(property);
        else
            throw new IllegalArgumentException("JsonProperty not found: " + property);

    }

    /**
     * Public method which returns the indicated property in it's String form.
     * @return The property as a String.
     */
    public String toString() {

        return property;

    }

}
