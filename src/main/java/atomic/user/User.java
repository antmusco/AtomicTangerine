package atomic.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import atomic.comic.Comic;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.List;

/**
 * Data class abstracting a User of the application. Each user is uniquely identified by their `gmail` String, which
 * is the gmail account bound to this user.
 *
 * @author Anthony G. Musco
 */
public class User {

    private String        gmail;
    private String        username;
    private double        expPoints;
    private LocalDateTime dateJoined;
    private Preferences   preferences;
    private List<Long>    createdComics;

    /**
     * Enumeration used for defining the JSON structure of this Java Object. Essentially, this enumeration maps the
     * internal member variables of the User class to specific String tags, which help to translate to/from JSON
     * objects.
     */
    public enum JsonFormat {

        GMAIL("gmail"),
        USERNAME("username"),
        EXP_POINTS("expPoints"),
        DATE_JOINED("dateJoined"),
        PREFERENCES("preferences"),
        CREATED_COMICS("createdComics");

        /**
         * String used to indicate the property in the User object (typically the member name itself).
         */
        private String keyString;

        /**
         * Constructor which initializes the keyString.
         * @param keyString String to bind to the member variable.
         */
        JsonFormat(String keyString) { this.keyString = keyString; }

        /**
         * Convenience method which returns the JsonFormat enum value based on a key String.
         * @param key String to look up.
         * @return The JsonFormat enum value which matches the indicated key.
         */
        public static JsonFormat fromKey(String key) {

            // I know formatting is unconventional, but it makes it easier to read :)
                 if(GMAIL.keyString.equalsIgnoreCase(key))          return GMAIL;
            else if(USERNAME.keyString.equalsIgnoreCase(key))       return USERNAME;
            else if(EXP_POINTS.keyString.equalsIgnoreCase(key))     return EXP_POINTS;
            else if(DATE_JOINED.keyString.equalsIgnoreCase(key))    return DATE_JOINED;
            else if(PREFERENCES.keyString.equalsIgnoreCase(key))    return PREFERENCES;
            else if(CREATED_COMICS.keyString.equalsIgnoreCase(key)) return CREATED_COMICS;

            // Key is null or not one of the above..
            throw new IllegalArgumentException("Unsupported key for User class.");

        }

        /**
         * @return The keyString bound to this enum value.
         */
        public String toString() { return keyString; }

    }

    /**
     * Default constructor used to instantiate a user.
     */
    public User() {

        createdComics = new LinkedList<>();

    }

    /**
     * Constructs a User object from a JsonObject. This method uses the format defined by the JsonFormat of this class,
     * and matches the property keys in the JsonObject to the keyStrings bound in the enumeration above.
     * @param obj JsonObject form of a User object to be parsed.
     */
    public void fromJsonObject(JsonObject obj) {

        if(obj.has(JsonFormat.GMAIL.toString())) {
            gmail = obj.get(JsonFormat.GMAIL.toString()).getAsString();
        }

        if(obj.has(JsonFormat.USERNAME.toString())) {
            username = obj.get(JsonFormat.USERNAME.toString()).getAsString();
        }

        if(obj.has(JsonFormat.EXP_POINTS.toString())) {
            expPoints = obj.get(JsonFormat.EXP_POINTS.toString()).getAsDouble();
        }

        if(obj.has(JsonFormat.DATE_JOINED.toString())) {
            dateJoined = LocalDateTime.parse(obj.get(JsonFormat.DATE_JOINED.toString()).getAsString());
        }

        if(obj.has(JsonFormat.PREFERENCES.toString())) {
            JsonObject prefObj = obj.get(JsonFormat.DATE_JOINED.toString()).getAsJsonObject();

            // @TODO Parse the preferences object.

        }

        if(obj.has(JsonFormat.CREATED_COMICS.toString())) {
            JsonArray comicsList = obj.get(JsonFormat.CREATED_COMICS.toString()).getAsJsonArray();

            for(JsonElement c : comicsList) {
                createdComics.add(c.getAsLong());
            }

        }

    }

    public JsonObject toJsonObject() {

        JsonObject obj = new JsonObject();

        // Add all the straightforward values.
        obj.addProperty(JsonFormat.GMAIL.toString(), gmail);
        obj.addProperty(JsonFormat.USERNAME.toString(), username);
        obj.addProperty(JsonFormat.EXP_POINTS.toString(), expPoints);
        obj.addProperty(JsonFormat.DATE_JOINED.toString(), dateJoined.toString());

        // The preferences property will be a JsonObject in itself.
        obj.add(JsonFormat.PREFERENCES.toString(), preferences.toJsonObject());

        // The createdComics property will be a
        JsonArray comicsList = new JsonArray();
        for(Long c : createdComics) comicsList.add(c);
        obj.add(JsonFormat.CREATED_COMICS.toString(), comicsList);

    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getExpPoints() {
        return expPoints;
    }

    public void setExpPoints(double expPoints) {
        this.expPoints = expPoints;
    }

    public LocalDateTime getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDateTime dateJoined) {
        this.dateJoined = dateJoined;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public List<Long> getCreatedComics() {
        return createdComics;
    }

    public void setCreatedComics(List<Long> createdComics) {
        this.createdComics = createdComics;
    }

}
