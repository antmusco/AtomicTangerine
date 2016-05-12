package atomic.user;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.Jsonable;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Data class abstracting a User of the application. Each user is uniquely identified by their `gmail` String, which
 * is the gmail account bound to this user.
 *
 * @author Anthony G. Musco
 */
public class User extends DatastoreEntity implements Jsonable {

    public static final String DEFAULT_PROFILE_PIC_URL = "https://storage.googleapis.com/comics-cse-308/default-profile-pic";

    private String gmail;
    private String handle;
    private String firstName;
    private String lastName;
    private String bio;
    private double expPoints;
    private Date dateJoined;
    private Date birthday;
    private Preferences preferences;
    private List<String> createdComics;
    private String profilePicUrl;
    private Text signature;

    // Lists of Comic KeyStrings.
    private List<String> upvotedComics;
    private List<String> downvotedComics;

    /**
     * Static utility function which grabs the user that is currently logged in and returns it to the caller.
     *
     * @return The user who is currently logged in.
     * @throws NoUniqueKeyException  If the gmail for the user could not be recovered or is null.
     * @throws UserNotFoundException If there is no user logged in.
     */
    public static User getCurrentUser() throws NoUniqueKeyException, UserNotFoundException {

        UserService service = UserServiceFactory.getUserService();

        com.google.appengine.api.users.User user = service.getCurrentUser();
        if (user == null) throw new UserNotFoundException("User not logged in.");

        String gmail = user.getEmail();
        if (gmail == null) throw new NoUniqueKeyException("No gmail found.");

        return new User(gmail);

    }

    /**
     * Default constructor used to instantiate a user with a particular gmail..
     */
    public User(String gmail) throws NoUniqueKeyException {

        super(EntityKind.USER);

        // Make sure unique key is non-null.
        if (gmail == null) {

            throw new NoUniqueKeyException("User - gmail == null");

        }

        // Record unique key.
        this.gmail = gmail;

        // Retrieve the entity from the datastore if it exists.
        try {

            fromEntity(retrieveEntity());
            if (createdComics == null) createdComics = new LinkedList<>();
            if (signature == null) signature = new Text("{}");

        } catch (EntityNotFoundException ex) {

            // Entity doesn't exist yet, init default values
            this.handle = null;
            this.expPoints = 0;
            this.dateJoined = new Date();
            this.preferences = new Preferences(this.gmail);
            this.createdComics = new LinkedList<>();
            this.profilePicUrl = DEFAULT_PROFILE_PIC_URL;
            this.signature = new Text("{}");

            // Put the entity in the datastore.
            saveEntity();

        }
    }

    /**
     * Constructor which instantiates a User from a Json object.
     *
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

        if (obj == null) {
            throw new IllegalArgumentException("JSON object null");
        }

        if (obj.has(JsonProperty.GMAIL.toString())) {
            gmail = obj.get(JsonProperty.GMAIL.toString()).getAsString();
        } else {
            throw new NoUniqueKeyException("User - gmail");
        }

        if (obj.has(JsonProperty.HANDLE.toString())) {
            handle = obj.get(JsonProperty.HANDLE.toString()).getAsString();
        }

        if (obj.has(JsonProperty.FIRST_NAME.toString())) {
            firstName = obj.get(JsonProperty.FIRST_NAME.toString()).getAsString();
        }

        if (obj.has(JsonProperty.LAST_NAME.toString())) {
            lastName = obj.get(JsonProperty.LAST_NAME.toString()).getAsString();
        }

        if (obj.has(JsonProperty.BIO.toString())) {
            bio = obj.get(JsonProperty.BIO.toString()).getAsString();
        }

        if (obj.has(JsonProperty.EXP_POINTS.toString())) {
            expPoints = obj.get(JsonProperty.EXP_POINTS.toString()).getAsDouble();
        }

        if (obj.has(JsonProperty.DATE_JOINED.toString())) {
            dateJoined = new Date(obj.get(JsonProperty.DATE_JOINED.toString()).getAsLong());
        }

        if (obj.has(JsonProperty.BIRTHDAY.toString())) {
            birthday = new Date(obj.get(JsonProperty.BIRTHDAY_LONG.toString()).getAsLong());
        }

        if (obj.has(JsonProperty.PREFERENCES.toString())) {
            //JsonObject prefObj = obj.get(JsonProperty.PREFERENCES.toString()).getAsJsonObject();

            //preferences = new Preferences(prefObj);
            preferences = new Preferences(this.gmail);

        }
        if (obj.has(JsonProperty.CREATED_COMICS.toString())) {
            JsonArray comicsList = obj.get(JsonProperty.CREATED_COMICS.toString()).getAsJsonArray();

            for (JsonElement c : comicsList) {
                createdComics.add(c.getAsString());
            }

        }
        if (obj.has(JsonProperty.SIGNATURE.toString())) {
            signature = new Text(obj.get(JsonProperty.SIGNATURE.toString()).getAsString());
        }
        if (obj.has(JsonProperty.UPVOTED_COMICS.toString())) {
            upvotedComics = new LinkedList<>();
            JsonArray upvotedList = obj.get(JsonProperty.UPVOTED_COMICS.toString()).getAsJsonArray();

            for (JsonElement c : upvotedList) {
                upvotedList.add(c.getAsString());
            }

        }
        if (obj.has(JsonProperty.DOWNVOTED_COMICS.toString())) {
            downvotedComics = new LinkedList<>();
            JsonArray downvotedList = obj.get(JsonProperty.DOWNVOTED_COMICS.toString()).getAsJsonArray();

            for (JsonElement c : downvotedList) {
                downvotedList.add(c.getAsString());
            }

        }
        saveEntity();

    }

    public JsonObject toJson() {

        // Create a new JsonObject.
        JsonObject obj = new JsonObject();

        // Add all the straightforward values.
        obj.addProperty(JsonProperty.GMAIL.toString(), gmail);

        if (handle != null)
            obj.addProperty(JsonProperty.HANDLE.toString(), handle);

        if (firstName != null) {
            obj.addProperty(JsonProperty.FIRST_NAME.toString(), firstName);
        }

        if (lastName != null) {
            obj.addProperty(JsonProperty.LAST_NAME.toString(), lastName);
        }

        if (bio != null) {
            obj.addProperty(JsonProperty.BIO.toString(), bio);
        }

        obj.addProperty(JsonProperty.EXP_POINTS.toString(), expPoints);

        if (dateJoined != null)
            obj.addProperty(JsonProperty.DATE_JOINED.toString(), dateJoined.getTime());

        if (birthday != null)
            obj.addProperty(JsonProperty.BIRTHDAY.toString(), birthday.getTime());

        if (profilePicUrl != null)
            obj.addProperty(JsonProperty.PROFILE_PIC_URL.toString(), profilePicUrl);

        // The preferences property will be a JsonObject in itself.
        if (preferences != null)
            obj.add(JsonProperty.PREFERENCES.toString(), preferences.toJson());

        // The createdComics property will be a JsonArray of Comic ID's.
        if (createdComics != null) {
            JsonArray comicsList = new JsonArray();
            for (String s : createdComics)
                comicsList.add(s);
            obj.add(JsonProperty.CREATED_COMICS.toString(), comicsList);
        }

        if (signature != null) {
            obj.addProperty(JsonProperty.SIGNATURE.toString(), signature.getValue());
        }

        if(upvotedComics != null) {
            JsonArray upvotedComicsList = new JsonArray();
            for (String s : upvotedComics)
                upvotedComicsList.add(s);
            obj.add(JsonProperty.UPVOTED_COMICS.toString(), upvotedComicsList);
        }

        if(downvotedComics != null) {
            JsonArray downvotedComicsList = new JsonArray();
            for (String s : downvotedComics)
                downvotedComicsList.add(s);
            obj.add(JsonProperty.DOWNVOTED_COMICS.toString(), downvotedComicsList);
        }

        // Return the JsonObject.
        return obj;

    }

    /************************************************************************
     * DatastoreEntity Methods
     ***********************************************************************/

    /**
     * Generates a String based on the EntityKind and gmail string.
     *
     * @return A String representing this unique User.
     */
    @Override
    protected String generateKeyString() {
        return this.gmail;
    }

    /**
     * Helper method that sets all the attributes of the entity based on this objects instance variable values.
     *
     * @return The Entity containing the current state of the object.
     */
    @Override
    public Entity toEntity() {

        Entity entity;

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
        entity.setProperty(JsonProperty.PROFILE_PIC_URL.toString(), this.profilePicUrl);
        entity.setUnindexedProperty(JsonProperty.SIGNATURE.toString(), this.signature);
        entity.setProperty(JsonProperty.UPVOTED_COMICS.toString(), this.upvotedComics);
        entity.setProperty(JsonProperty.DOWNVOTED_COMICS.toString(), this.downvotedComics);

        return entity;

    }

    /**
     * Builds this User object by reading properties from the datastore entity.
     *
     * @param entity Entity to read values from.
     */
    @Override
    protected void fromEntity(Entity entity) {

        // Read each of the properties from the entity.
        this.gmail = (String) entity.getKey().getName();
        this.handle = (String) entity.getProperty(JsonProperty.HANDLE.toString());
        this.firstName = (String) entity.getProperty(JsonProperty.FIRST_NAME.toString());
        this.lastName = (String) entity.getProperty(JsonProperty.LAST_NAME.toString());
        this.bio = (String) entity.getProperty(JsonProperty.BIO.toString());
        this.expPoints = (double) entity.getProperty(JsonProperty.EXP_POINTS.toString());
        this.dateJoined = (Date) entity.getProperty(JsonProperty.DATE_JOINED.toString());
        this.birthday = (Date) entity.getProperty(JsonProperty.BIRTHDAY.toString());
        this.createdComics = (List<String>) entity.getProperty(JsonProperty.CREATED_COMICS.toString());
        this.profilePicUrl = (String) entity.getProperty(JsonProperty.PROFILE_PIC_URL.toString());
        this.signature = (Text) entity.getProperty(JsonProperty.SIGNATURE.toString());
        this.upvotedComics = (List<String>) entity.getProperty(JsonProperty.UPVOTED_COMICS.toString());
        this.downvotedComics = (List<String>) entity.getProperty(JsonProperty.DOWNVOTED_COMICS.toString());

        // Extract the Preferences entity.
        try {
            this.preferences = Preferences.fromEmbeddedEntity((EmbeddedEntity) entity.getProperty(JsonProperty.PREFERENCES.toString()));
        } catch (NoUniqueKeyException n) {
            this.preferences = new Preferences(this.gmail);
        }

    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getGmail() {
        return gmail;
    }

    public List<String> getUpvotedComics() {
        return upvotedComics;
    }

    public List<String> getDownvotedComics() {
        return downvotedComics;
    }
}
