package atomic.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    REASON("REASON"),

    // Login Servlet.
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),

    // User Servlet.
    USER("USER"),
    GMAIL("GMAIL"),
    HANDLE("HANDLE"),
    FIRST_NAME("FIRSTNAME"),
    LAST_NAME("LASTNAME"),
    BIO("BIO"),
    EXP_POINTS("EXP_POINTS"),
    DATE_JOINED("DATE_JOINED"),
    BIRTHDAY("BIRTHDAY"),
    BIRTHDAY_LONG("BIRTHDAY_LONG"),
    PREFERENCES("PREFERENCES"),
    CREATED_COMICS("CREATED_COMICS"),
    PROFILE_PIC_URL("PROFILE_PIC_URL"),
    SIGNATURE("SIGNATURE"),
    UPVOTED_COMICS("UPVOTED_COMICS"),
    DOWNVOTED_COMICS("DOWNVOTED_COMICS"),

    // USER PREFERENCES
    USER_GMAIL("USER_GMAIL"),
    SUBSCRIPTIONS("SUBSCRIPTIONS"),
    LIKE_TAGS("LIKE_TAGS"),
    DISLIKE_TAGS("DISLIKE_TAGS"),
    FAVORITE_COMICS("FAVORITE_COMICS"),

    // ASSET SERVLET
    FILES("FILES"),
    SVG_DATA("SVG_DATA"),
    SUBMISSION_TYPE("SUBMISSION_TYPE"),
    PROFILE_PIC("PROFILE_PIC"),
    COMIC_FRAME("COMIC_FRAME"),
    COMIC_INDEX("COMIC_INDEX"),
    REDIRECT_URL("REDIRECT_URL"),

    // COMICS
    COMIC("COMIC"),
    COMICS("COMICS"),
    COMIC_ID_STRING("COMIC_ID_STRING"),
    OWNER_GMAIL("OWNER_GMAIL"),
    TITLE("TITLE"),
    STATE("STATE"),
    FRAMES("FRAMES"),
    GLOBAL_CAPTION("GLOBAL_CAPTION"),
    DATE_CREATED("DATE_CREATED"),
    DATE_MODIFIED("DATE_MODIFIED"),
    TAGS("TAGS"),
    UPLOAD_URL("UPLOAD_URL"),
    FRAME_INDEX("FRAME_INDEX"),
    VOTE("VOTE"),
    SCORE("SCORE"),
    THUMBNAIL("THUMBNAIL"),
    THUMBNAILS("THUMBNAILS"),

    // COMMENT
    COMMENTS("COMMENTS"),
    COMMENT("COMMENT"),
    COMMENTOR_GMAIL("COMMENTOR_GMAIL"),
    DATE_POSTED("DATE_POSTED"),
    UPVOTERS("UPVOTERS"),
    DOWNVOTERS("DOWNVOTERS"),

    // Comic State
    DRAFT("DRAFT"),
    DRAFTS("DRAFTS"),
    PUBLISHED("PUBLISHED");

    private String property;
    public static final DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
    private static Map<String, JsonProperty> propertyMap = new TreeMap<>();

    /**
     * Static operation which inserts all enum values into the property map.
     */
    static {
        for (JsonProperty jp : JsonProperty.values()) {
            propertyMap.put(jp.toString(), jp);
        }
    }

    /**
     * Constructor which binds the property String to it's enum value.
     *
     * @param property The String representation of the property.
     */
    JsonProperty(String property) {
        this.property = property;
    }

    /**
     * Static function which parses a property from a String parameter.
     *
     * @param property The String representation of the property, such as what is submitted in JSON.
     * @return The appropriate JsonProperty enum.
     */
    public static JsonProperty fromString(String property) {

        if (propertyMap.containsKey(property))
            return propertyMap.get(property);
        else
            throw new IllegalArgumentException("JsonProperty not found: " + property);

    }

    /**
     * Public method which returns the indicated property in it's String form.
     *
     * @return The property as a String.
     */
    public String toString() {

        return property;

    }

}
