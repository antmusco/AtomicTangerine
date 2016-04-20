package atomic.user;

import atomic.data.AssetServlet;
import atomic.json.NoUniqueKeyException;
import atomic.json.JsonProperty;
import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.Jsonable;
import com.google.appengine.api.datastore.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

import com.google.gson.JsonElement;

/**
 * Data class abstracting a User of the application. Each user is uniquely identified by their `gmail` String, which
 * is the gmail account bound to this user.
 *
 * @author Anthony G. Musco
 */
public class User extends DatastoreEntity implements Jsonable {

    public static final String DEFAULT_PROFILE_PIC_ID = "default-profile-pic";

    private String      gmail;
    private String      handle;
    private String      firstName;
    private String      lastName;
    private String      bio;
    private double      expPoints;
    private Date        dateJoined;
    private Date        birthday;
    private Preferences preferences;
    private List<Key>   createdComics;
    private String profilePicID;

    /**
     * Default constructor used to instantiate a user with a particular gmail..
     */
    public User(String gmail) {

        super(EntityKind.USER);
        this.gmail = gmail;

        try {

            fromEntity(retrieveEntity());

        } catch (EntityNotFoundException ex) {

            // Entity doesn't exist yet, init default values
            this.handle = "";
            this.expPoints = 0;
            this.dateJoined = new Date();
            this.preferences = new Preferences(this.gmail);
            this.createdComics = new LinkedList<>();
            this.profilePicID = DEFAULT_PROFILE_PIC_ID;

            // Put the entity in the datastore.
            saveEntity();

        }

    }

    /**
     * Constructor which instantiates a User from a Json object.
     * @param obj JsonObject to construct a User from.
     */
    public User(JsonObject obj) throws NoUniqueKeyException {

        super(EntityKind.USER);
        fromJson(obj);
        saveEntity();

    }

    /************************************************************************
     * Jsonable Methods
     ***********************************************************************/

    public void fromJson(JsonObject obj) throws NoUniqueKeyException {

        if(obj == null) {
            throw new IllegalArgumentException("JSON object null");
        }

        if(obj.has(JsonProperty.GMAIL.toString())) {
            gmail = obj.get(JsonProperty.GMAIL.toString()).getAsString();
        } else {
            throw new NoUniqueKeyException("User - gmail");
        }

        if(obj.has(JsonProperty.HANDLE.toString())) {
            handle = obj.get(JsonProperty.HANDLE.toString()).getAsString();
        }

        if(obj.has(JsonProperty.FIRST_NAME.toString())) {
            firstName = obj.get(JsonProperty.FIRST_NAME.toString()).getAsString();
        }

        if(obj.has(JsonProperty.LAST_NAME.toString())) {
            lastName = obj.get(JsonProperty.LAST_NAME.toString()).getAsString();
        }

        if(obj.has(JsonProperty.BIO.toString())) {
            bio = obj.get(JsonProperty.BIO.toString()).getAsString();
        }

        if(obj.has(JsonProperty.EXP_POINTS.toString())) {
            expPoints = obj.get(JsonProperty.EXP_POINTS.toString()).getAsDouble();
        }

        if(obj.has(JsonProperty.DATE_JOINED.toString())) {
            dateJoined = new Date(obj.get(JsonProperty.DATE_JOINED.toString()).getAsLong());
        }

        if(obj.has(JsonProperty.BIRTHDAY.toString())) {
            birthday = new Date(obj.get(JsonProperty.BIRTHDAY.toString()).getAsLong());
        }

        if(obj.has(JsonProperty.PREFERENCES.toString())) {
            JsonObject prefObj = obj.get(JsonProperty.DATE_JOINED.toString()).getAsJsonObject();

            //preferences = new Preferences(prefObj);
            preferences = new Preferences(this.gmail);

        }
        if(obj.has(JsonProperty.CREATED_COMICS.toString())) {
            JsonArray comicsList = obj.get(JsonProperty.CREATED_COMICS.toString()).getAsJsonArray();

            for(JsonElement c : comicsList) {
                Key k = KeyFactory.createKey("Comic", c.getAsLong());
                createdComics.add(k);
            }

        }
        saveEntity();

    }

    public JsonObject toJson() {

        // Create a new JsonObject.
        JsonObject obj = new JsonObject();

        // Add all the straightforward values.
        obj.addProperty(JsonProperty.GMAIL.toString(), gmail);

        if(handle != null)
            obj.addProperty(JsonProperty.HANDLE.toString(), handle);

        if(firstName != null) {
            obj.addProperty(JsonProperty.FIRST_NAME.toString(), firstName);
        }

        if(lastName != null) {
            obj.addProperty(JsonProperty.LAST_NAME.toString(), lastName);
        }

        if(bio != null) {
            obj.addProperty(JsonProperty.BIO.toString(), bio);
        }

        obj.addProperty(JsonProperty.EXP_POINTS.toString(), expPoints);

        if(dateJoined != null)
            obj.addProperty(JsonProperty.DATE_JOINED.toString(), dateJoined.getTime());

        if(birthday != null)
            obj.addProperty(JsonProperty.BIRTHDAY.toString(), birthday.getTime());

        if(profilePicID != null)
            obj.addProperty(JsonProperty.PROFILE_PIC_URL.toString(), AssetServlet.getAssetURL(profilePicID));

        // The preferences property will be a JsonObject in itself.
        if(preferences != null)
            obj.add(JsonProperty.PREFERENCES.toString(), preferences.toJson());

        // The createdComics property will be a JsonArray of Comic ID's.
        JsonArray comicsList = new JsonArray();

        if(createdComics != null) {
            for (Key k : createdComics)
                comicsList.add(k.getId());
            obj.add(JsonProperty.CREATED_COMICS.toString(), comicsList);
        }

        // Return the JsonObject.
        return obj;

    }

    /************************************************************************
     * DatastoreEntity Methods
     ***********************************************************************/

    /**
     * Generates a key based on the EntityKind and gmail string.
     * @return A Key representing this unique User.
     */
    @Override
    protected Key generateKey() {
        return KeyFactory.createKey(this.entityKind.toString(), this.gmail);
    }

    /**
     * Helper method that sets all the attributes of the entity based on this objects instance variable values.
     * @return The Entity containing the current state of the object.
     */
    @Override
    public Entity toEntity() {

        Entity entity = null;

        try {

            entity = retrieveEntity();

        } catch (EntityNotFoundException e) {

            entity = new Entity(generateKey());

        }

        // Write each of the properties to the entity.
        entity.setProperty(JsonProperty.HANDLE.toString(), this.handle);
        entity.setProperty(JsonProperty.FIRST_NAME.toString(), this.firstName);
        entity.setProperty(JsonProperty.LAST_NAME.toString(), this.lastName);
        entity.setProperty(JsonProperty.BIO.toString(), this.bio);
        entity.setProperty(JsonProperty.EXP_POINTS.toString(), this.expPoints);
        entity.setProperty(JsonProperty.DATE_JOINED.toString(), this.dateJoined);
        entity.setProperty(JsonProperty.BIRTHDAY.toString(), this.birthday);
        entity.setProperty(JsonProperty.PREFERENCES.toString(), this.preferences.toEmbeddedEntity());
        entity.setProperty(JsonProperty.CREATED_COMICS.toString(), this.createdComics);
        entity.setProperty(JsonProperty.PROFILE_PIC_URL.toString(), this.profilePicID);

        return entity;

    }

    /**
     * Builds this User object by reading properties from the datastore entity.
     * @param entity Entity to read values from.
     */
    @Override
    protected void fromEntity(Entity entity) {

        // Read each of the properties from the entity.
        this.gmail         =               entity.getKey().getName();
        this.handle        = (String)      entity.getProperty(JsonProperty.HANDLE.toString());
        this.firstName     = (String)      entity.getProperty(JsonProperty.FIRST_NAME.toString());
        this.lastName      = (String)      entity.getProperty(JsonProperty.LAST_NAME.toString());
        this.bio           = (String)      entity.getProperty(JsonProperty.BIO.toString());
        //this.expPoints     = (double)      entity.getProperty(JsonProperty.EXP_POINTS.toString());
        this.dateJoined    = (Date)        entity.getProperty(JsonProperty.DATE_JOINED.toString());
        this.birthday      = (Date)        entity.getProperty(JsonProperty.BIRTHDAY.toString());
        this.createdComics = (List<Key>)   entity.getProperty(JsonProperty.CREATED_COMICS.toString());
        this.profilePicID  = (String)      entity.getProperty(JsonProperty.PROFILE_PIC_URL.toString());

        // Extract the Preferences entity.
        try {
            this.preferences = Preferences.fromEmbeddedEntity((EmbeddedEntity) entity.getProperty(JsonProperty.PREFERENCES.toString()));
        } catch (NoUniqueKeyException n) {
            this.preferences = new Preferences(this.gmail);
        }

    }


    public String getProfilePicID() {  return profilePicID; }

    public void setProfilePicID(String profilePicID) {
        this.profilePicID = profilePicID;
    }

    public String getGmail() {
        return gmail;
    }
}
