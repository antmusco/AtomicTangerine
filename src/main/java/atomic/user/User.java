package atomic.user;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import com.google.appengine.api.datastore.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.List;

/**
 * Data class abstracting a User of the application. Each user is uniquely identified by their `gmail` String, which
 * is the gmail account bound to this user.
 *
 * @author Anthony G. Musco
 */
public class User extends DatastoreEntity{

    private String      gmail;
    private String      username;
    private double      expPoints;
    private Date        dateJoined;
    private Preferences preferences;
    private List<Key>  createdComics;

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
    public User(String gmail) {
        super(EntityKind.USER);
        this.gmail = gmail;
        try {
            fromDatastoreEntity();
        } catch (EntityNotFoundException ex) {
            // Entity doesn't exist yet, init default values
            this.username = this.gmail;
            this.expPoints = 0;
            this.dateJoined = new Date();
            this.preferences = new Preferences();
            this.createdComics = new LinkedList<>();
            putEntityIntoDatastore();
        }
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
            dateJoined = new Date(obj.get(JsonFormat.DATE_JOINED.toString()).getAsLong());
        }

        if(obj.has(JsonFormat.PREFERENCES.toString())) {
            JsonObject prefObj = obj.get(JsonFormat.DATE_JOINED.toString()).getAsJsonObject();

            // @TODO Parse the preferences object.

        }

        if(obj.has(JsonFormat.CREATED_COMICS.toString())) {
            JsonArray comicsList = obj.get(JsonFormat.CREATED_COMICS.toString()).getAsJsonArray();

            for(JsonElement c : comicsList) {
                Key k = KeyFactory.createKey("Comic", c.getAsLong());
                createdComics.add(k);
            }

        }

    }

    public JsonObject toJsonObject() {

        // Create a new JsonObject.
        JsonObject obj = new JsonObject();

        // Add all the straightforward values.
        obj.addProperty(JsonFormat.GMAIL.toString(), gmail);

        if(username != null)
            obj.addProperty(JsonFormat.USERNAME.toString(), username);

        obj.addProperty(JsonFormat.EXP_POINTS.toString(), expPoints);

        if(dateJoined != null)
            obj.addProperty(JsonFormat.DATE_JOINED.toString(), dateJoined.toString());

        // The preferences property will be a JsonObject in itself.
        if(preferences != null)
            obj.add(JsonFormat.PREFERENCES.toString(), preferences.toJsonObject());

        // The createdComics property will be a JsonArray of Comic ID's.
        JsonArray comicsList = new JsonArray();
        // @TODO Figure out why the line below is causing a NullPointerException
        //for (Key k : createdComics) comicsList.add(k.getId());
            obj.add(JsonFormat.CREATED_COMICS.toString(), comicsList);

        // Return the JsonObject.
        return obj;

    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
        saveEntity();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        saveEntity();
    }

    public double getExpPoints() {
        return expPoints;
    }

    public void setExpPoints(double expPoints) {
        this.expPoints = expPoints;
        saveEntity();
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
        saveEntity();
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
        saveEntity();
    }

    public List<Key> getCreatedComics() {
        return createdComics;
    }

    public void setCreatedComics(List<Key> createdComics) {
        this.createdComics = createdComics;
        saveEntity();
    }

    /**
     * Database Methods
     */

    @Override
    protected void putEntityIntoDatastore() {
        // Create a key for the current entity kind (which in this case should be "User") with the unique string
        // being the gmail.
        Key userKey = KeyFactory.createKey(this.ENTITY_KIND.toString(), this.gmail);

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity datastoreEntity = ds.get(userKey);
            // If this code is reached, no exception was thrown which means the key already exists. We don't want to put
            // a user into the datastore twice so just exit.
            return;
        } catch (EntityNotFoundException ex) {
            // If an exception is thrown, then the entity was not found, so we should initialize its data and place
            // it into the datastore.
            Entity newEntity = new Entity(this.ENTITY_KIND.toString(), this.gmail);
            entitySetterHelper(newEntity);
            ds.put(newEntity);
        }

    }

    @Override
    protected void saveEntity() {
        Key userKey = KeyFactory.createKey(this.ENTITY_KIND.toString(), this.gmail);

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        try {
            Entity datastoreEntity = ds.get(userKey);
            // Entity was found, so update all of its properties.
            entitySetterHelper(datastoreEntity);
            ds.put(datastoreEntity);
        } catch (EntityNotFoundException ex) {
            // Entity was not found, so place it in the datastore. The code in 'putEntityIntoDatastore' is very similar
            // to this function. Unfortunately, some data is reproduced, but this
            putEntityIntoDatastore();
        }
    }

    /**
     * Helper method that sets all the attributes of the entity based on this objects instance variable values.
     * @param entity The entity to assign values to.
     */
    private void entitySetterHelper(Entity entity) {
        entity.setProperty(JsonFormat.USERNAME.toString(), this.username);
        entity.setProperty(JsonFormat.EXP_POINTS.toString(), this.expPoints);
        entity.setProperty(JsonFormat.DATE_JOINED.toString(), this.dateJoined);
        //entity.setProperty(JsonFormat.PREFERENCES.toString(), this.preferences);
        entity.setProperty(JsonFormat.CREATED_COMICS.toString(), this.createdComics);
    }

    private void fromDatastoreEntity() throws EntityNotFoundException {
        Key userKey = KeyFactory.createKey(this.ENTITY_KIND.toString(), this.gmail);

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity datastoreEntity = ds.get(userKey);

        this.username = (String) datastoreEntity.getProperty(JsonFormat.USERNAME.toString());
        this.expPoints = (double) datastoreEntity.getProperty(JsonFormat.EXP_POINTS.toString());
        this.dateJoined = (Date) datastoreEntity.getProperty(JsonFormat.DATE_JOINED.toString());
        //this.preferences = (Preferences) datastoreEntity.getProperty(JsonFormat.PREFERENCES.toString());
        this.createdComics = (List<Key>) datastoreEntity.getProperty(JsonFormat.CREATED_COMICS.toString());
    }

}
