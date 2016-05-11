package atomic.comic;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.Jsonable;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Primary data class representing a Comic posted and shared on the application. Uniquely identified by the owner
 * (gmail) and the title of the comic.
 *
 * @author Anthony G. Musco
 * @author Gustavo Poscidonio
 */
public class Comic extends DatastoreEntity implements Jsonable {

    /**
     * Email of the owner of the comic. This member is used in conjuntion with `title` to produce the unique key for
     * this Comic.
     */
    private String userGmail;
    /**
     * Title of this Comic. While this member is not unique across all Comic instances, it is unique for each User. This
     * member is used in conjunction with `userGmail` to produce the unique key for this Comic.
     */
    private String title;
    /**
     * Caption for all frames of this Comic. This may be null if the User wishes not to use the caption feature.
     */
    private String globalCaption;
    /**
     * List of assetID's associated with the frames of this Comic, in order. In most cases, this List will consist of a
     * single element (a comic with a single image), though more are supported. Each element in the list is a unique
     * comic frame asset ID, which is a Universally Unique Identifier (UUID). This identifier can be used to retrieve
     * the asset associated with the frame (via the `AssetServlet`), as well as the content information stored with the
     * frame in the datastore (i.e. the `assetID` for each frame is also it's Entity's unique Key).
     */
    private List<Text> frames;
    /**
     * The current state of this Comic. If DRAFT, then the Comic is only visible to the owning User. If PUBLISHED, this
     * Comic is available for viewing for all other Users.
     */
    private ComicState state;
    /**
     * Date on which this Comic was last updated.
     */
    private Date dateModified;
    /**
     * Date on which this Comic was created.
     */
    private Date dateCreated;
    /**
     * List of tags associated with the comic.
     */
    private List<String> tags;

    //TODO private ComicStyle style;


    /**
     * Constructor which creates a comic using the indicated `userGmail` and `title`. Note that these two fields
     * comprise the unique key of each Comic instance. If the Comic exists in the datastore, the previous values are
     * retrieved and restored to this instance. If no Comic yet exists, this instance is initialized with default values
     * and saved to the datastore.
     *
     * @param userGmail Email of the owner of this Comic. This String is unique among users.
     * @param title     Title of the Comic. While this String is not unique among all Comics in the datastore, it is unique
     *                  for EACH User.
     * @throws NoUniqueKeyException Thrown if either of the parameters are null.
     */
    private Comic(String userGmail, String title) throws NoUniqueKeyException {

        super(EntityKind.COMIC);

        // Ensure that a unique key has been passed.
        if (userGmail == null)
            throw new NoUniqueKeyException("Comic - userGmail");
        else if (title == null)
            throw new NoUniqueKeyException(("Comic - title"));

        this.userGmail = userGmail;
        this.title = title;

        // Attempt to retrieve the user from the datastore.
        try {

            fromEntity(retrieveEntity());

            if(this.frames == null) this.frames = new LinkedList<>();
            if(this.tags == null) this.tags = new LinkedList<>();

        } catch (EntityNotFoundException ex) {

            // Entity doesn't exist yet, init default values
            this.globalCaption = null;
            this.frames = new LinkedList<>();
            this.state = ComicState.DRAFT;
            this.dateCreated = new Date();
            this.dateModified = (Date) dateCreated.clone();
            this.tags = new LinkedList<>();
            // Put the entity in the datastore.
            saveEntity();

        }

    }

    /**
     * Constructor which creates a Comic instance based off a JsonObject. This constructor can only be used if the
     * JsonObject parameter contains at minimum the unique userGmail and title required to instantiate this Comic. Once
     * the values have been extracted from the JsonObject, the Comic is saved to an Entity to the datastore, overwriting
     * previous values it already exists or initializing the Enitity if it does not.
     *
     * @param obj JsonObject containing values used to initialize or update this Comic.
     * @throws NoUniqueKeyException Thrown if `obj` does not contain the requisite userGmail or title.
     */
    public Comic(JsonObject obj) throws NoUniqueKeyException {

        super(EntityKind.COMIC);
        fromJson(obj);
        saveEntity();

    }

    /*******************************************************************************************************************
     * Jsonable Methods
     ******************************************************************************************************************/

    @Override
    public void fromJson(JsonObject obj) throws NoUniqueKeyException {

        if (obj.has(JsonProperty.USER_GMAIL.toString())) {
            userGmail = obj.get(JsonProperty.USER_GMAIL.toString()).getAsString();
        } else {
            throw new NoUniqueKeyException("Comic - userGmail");
        }

        if (obj.has(JsonProperty.TITLE.toString())) {
            title = obj.get(JsonProperty.TITLE.toString()).getAsString();
        } else {
            throw new NoUniqueKeyException("Comic - title");
        }

        if (obj.has(JsonProperty.GLOBAL_CAPTION.toString())) {
            globalCaption = obj.get(JsonProperty.GLOBAL_CAPTION.toString()).getAsString();
        }

        if (obj.has(JsonProperty.STATE.toString())) {
            state = ComicState.fromString(obj.get(JsonProperty.STATE.toString()).getAsString());
        }

        if (obj.has(JsonProperty.FRAMES.toString())) {
            JsonArray framesList = obj.get(JsonProperty.FRAMES.toString()).getAsJsonArray();

            for (JsonElement c : framesList) {
                framesList.add(c.getAsString());
            }

        }

        if (obj.has(JsonProperty.TAGS.toString())) {

            JsonArray tagsList = obj.get(JsonProperty.TAGS.toString()).getAsJsonArray();

            for (JsonElement t : tagsList) {
                tagsList.add(t.getAsString());
            }

        }

        // Record modification.
        dateModified = new Date();

        // Shouldn't edit `dateCreated`.
        saveEntity();

    }

    @Override
    public JsonObject toJson() {

        // Create a new JsonObject.
        JsonObject obj = new JsonObject();

        // Add all the straightforward values.
        obj.addProperty(JsonProperty.USER_GMAIL.toString(), userGmail);
        obj.addProperty(JsonProperty.TITLE.toString(), title);

        if (globalCaption != null) {
            obj.addProperty(JsonProperty.GLOBAL_CAPTION.toString(), globalCaption);
        }

        if (state != null) {
            obj.addProperty(JsonProperty.STATE.toString(), state.toString());
        }

        obj.addProperty(JsonProperty.DATE_CREATED.toString(), dateCreated.getTime());
        obj.addProperty(JsonProperty.DATE_MODIFIED.toString(), dateModified.getTime());

        // The frames property will be a JsonArray of assetIDs.
        if (frames != null) {
            JsonArray framesList = new JsonArray();
            for (Text f : frames)
                framesList.add(f.toString());
            obj.add(JsonProperty.FRAMES.toString(), framesList);
        }

        if (tags != null) {
            JsonArray tagsList = new JsonArray();
            for (String s : tags)
                tagsList.add(s);
            obj.add(JsonProperty.TAGS.toString(), tagsList);
        }

        // Return the JsonObject.
        return obj;

    }

    /*******************************************************************************************************************
     * DatastoreEntity Methods
     ******************************************************************************************************************/

    @Override
    protected Key generateKey() {
        return KeyFactory.createKey(this.entityKind.toString(), this.userGmail + "_" + this.title);
    }

    @Override
    public Entity toEntity() {

        Entity entity;

        // Attempt to load previous entity values from the datastore.
        try {

            entity = retrieveEntity();

        } catch (EntityNotFoundException e) {

            entity = new Entity(generateKey());

        }

        // Write each of the properties to the entity.
        entity.setProperty(JsonProperty.USER_GMAIL.toString(), this.userGmail);
        entity.setProperty(JsonProperty.TITLE.toString(), this.title);
        entity.setProperty(JsonProperty.STATE.toString(), this.state.toString());
        entity.setProperty(JsonProperty.FRAMES.toString(), this.frames);
        entity.setProperty(JsonProperty.GLOBAL_CAPTION.toString(), this.globalCaption);
        entity.setProperty(JsonProperty.DATE_CREATED.toString(), this.dateCreated);
        entity.setProperty(JsonProperty.DATE_MODIFIED.toString(), this.dateModified);
        entity.setProperty(JsonProperty.TAGS.toString(), this.tags);
        return entity;
    }

    @Override
    protected void fromEntity(Entity entity) {

        // Read each of the properties from the entity.
        this.userGmail = (String) entity.getProperty(JsonProperty.USER_GMAIL.toString());
        this.title = (String) entity.getProperty(JsonProperty.TITLE.toString());


        if (entity.hasProperty(JsonProperty.STATE.toString())) {
            this.state = ComicState.fromString((String) entity.getProperty(JsonProperty.STATE.toString()));
        }

        if (entity.hasProperty(JsonProperty.FRAMES.toString())) {
            this.frames = (List<Text>) entity.getProperty(JsonProperty.FRAMES.toString());
        } else {
            this.frames = new LinkedList<>();
        }

        if (entity.hasProperty(JsonProperty.GLOBAL_CAPTION.toString())) {
            this.globalCaption = (String) entity.getProperty(JsonProperty.GLOBAL_CAPTION.toString());
        }

        if (entity.hasProperty(JsonProperty.DATE_CREATED.toString())) {
            this.dateCreated = (Date) entity.getProperty(JsonProperty.DATE_CREATED.toString());
        }

        if (entity.hasProperty(JsonProperty.DATE_MODIFIED.toString())) {
            this.dateModified = (Date) entity.getProperty(JsonProperty.DATE_MODIFIED.toString());
        }

        if (entity.hasProperty(JsonProperty.TAGS.toString())) {
            this.tags = (List<String>) entity.getProperty(JsonProperty.TAGS.toString());
        } else {
            this.tags = new LinkedList<>();
        }

    }

    public List<Text> getFrames() {
        return frames;
    }

    /**
     * Static function which creates a new comic with the indicated gmail and title. The function first checks to ensure
     * that no other comics with the given unique key exist - if one does then an exception is thrown.
     *
     * @param gmail Gmail of the user creating the comic.
     * @param title Title fo the comic to create (must be unique per user).
     * @throws ComicAlreadyExistsException Thrown if a comic with the given <gmail, title> pair already exists.
     * @throws NoUniqueKeyException        Thrown if gmail or title are empty or illegal.
     */
    public static void makeNewComic(String gmail, String title) throws ComicAlreadyExistsException,
            NoUniqueKeyException {

        // Make filter to ensure that a comic with the given key does not already exist.
        Query.Filter comicFilter = new Query.FilterPredicate(
                JsonProperty.TITLE.toString(),
                Query.FilterOperator.EQUAL,
                title
        );

        // Execute query to ensure no other comics exist with the specified key.
        Query q = new Query(EntityKind.COMIC.toString()).setFilter(comicFilter);

        List<Entity> entities = DatastoreEntity.executeQuery(q);
        if (!entities.isEmpty()) {
            throw new ComicAlreadyExistsException(gmail, title);
        }

        // Create new comic.
        new Comic(gmail, title);

    }

    /**
     * Factory method used to retrieve a comic.
     *
     * @param gmail Gmail of the user who created the comic.
     * @param title Title of the comic.
     * @return The Comic if it could be found, otherwise an exception is thrown.
     * @throws NoUniqueKeyException Thrown if no comic exists with the indicated user and gmail.
     */
    public static Comic retrieveComic(String gmail, String title) throws NoUniqueKeyException, ComicNotFoundException {

        // Make filter to locate a comic with the indicated title.
        Query.Filter titleFilter = new Query.FilterPredicate(
                JsonProperty.TITLE.toString(),
                Query.FilterOperator.EQUAL,
                title
        );

        // Make filter to locate comics with the user gmail.
        Query.Filter userFilter = new Query.FilterPredicate(
                JsonProperty.USER_GMAIL.toString(),
                Query.FilterOperator.EQUAL,
                gmail
        );

        // Combine both filters in to one.
        Query.Filter comicFilter = Query.CompositeFilterOperator.and(userFilter, titleFilter);

        // Execute query to ensure comic exist with the specified key.
        List<Entity> allResults = DatastoreEntity.executeQuery(new Query(EntityKind.COMIC.toString()));
        Query q = new Query(EntityKind.COMIC.toString()).setFilter(comicFilter);
        List<Entity> result = DatastoreEntity.executeQuery(q);
        if (result.isEmpty())
            throw new ComicNotFoundException(gmail, title);

        // Create the comic and return in.
        return new Comic(gmail, title);

    }

    /**
     * Factory method which returns a list of all comics created by a specific user.
     *
     * @param gmail Gmail of the user to retrieve comics for.
     * @return A list of Comics created by a specific user (may be empty if user has not created any comics).
     */
    public static List<Comic> retrieveUserComics(String gmail) {

        // Make filter to locate comics with the user gmail.
        Query.Filter userFilter = new Query.FilterPredicate(
                JsonProperty.USER_GMAIL.toString(),
                Query.FilterOperator.EQUAL,
                gmail
        );

        // Execute query to ensure comic exist with the specified key.
        Query q = new Query(EntityKind.COMIC.toString()).setFilter(userFilter);
        List<Entity> result = DatastoreEntity.executeQuery(q);

        // Generate the list of comics (may be empty if user has not created any comics).
        List<Comic> comics = new LinkedList<>();
        try {
            for (Entity e : result) {

                String title = (String) e.getProperty(JsonProperty.TITLE.toString());
                comics.add(new Comic(gmail, title));

            }
        } catch(NoUniqueKeyException nuke) {

            System.err.println(nuke.getMessage());

        }

        // Return the list of comics.
        return comics;

    }

    public String getTile() {
        return title;
    }
}
