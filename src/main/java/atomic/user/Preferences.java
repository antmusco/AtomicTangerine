package atomic.user;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.Jsonable;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.*;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines preferences and settings which are associated with a particular User.
 *
 * @autho Anthony G. Musco
 */
public class Preferences extends DatastoreEntity implements Jsonable {

    private String userGmail;
    private List<Key> subscriptions;
    private List<String> likeTags;
    private List<String> dislikeTags;
    private List<Key> favoriteComics;

    public Preferences(String userGmail) {

        super(EntityKind.PREFERENCES);
        this.userGmail = userGmail;

        try {

            fromEntity(retrieveEntity());

        } catch (EntityNotFoundException ex) {

            subscriptions = new LinkedList<>();
            likeTags = new LinkedList<>();
            dislikeTags = new LinkedList<>();
            favoriteComics = new LinkedList<>();

        }

    }

    public Preferences(JsonObject obj) throws NoUniqueKeyException {

        super(EntityKind.PREFERENCES);
        fromJson(obj);
        saveEntity();

    }

    @Override
    protected Key generateKey() {
        return KeyFactory.createKey(this.entityKind.toString(), this.userGmail);
    }

    @Override
    public Entity toEntity() {

        Entity entity = null;
        try {
            entity = retrieveEntity();
        } catch (EntityNotFoundException e) {
            entity = new Entity(generateKey());
        }

        entity.setProperty(JsonProperty.USER_GMAIL.toString(), this.userGmail);
        entity.setProperty(JsonProperty.SUBSCRIPTIONS.toString(), this.subscriptions);
        entity.setProperty(JsonProperty.LIKE_TAGS.toString(), this.likeTags);
        entity.setProperty(JsonProperty.DISLIKE_TAGS.toString(), this.dislikeTags);
        entity.setProperty(JsonProperty.FAVORITE_COMICS.toString(), this.favoriteComics);

        return entity;

    }

    @Override
    protected void fromEntity(Entity entity) {

        this.userGmail = (String) entity.getProperty(JsonProperty.USER_GMAIL.toString());
        this.subscriptions = (List<Key>) entity.getProperty(JsonProperty.SUBSCRIPTIONS.toString());
        this.likeTags = (List<String>) entity.getProperty(JsonProperty.LIKE_TAGS.toString());
        this.dislikeTags = (List<String>) entity.getProperty(JsonProperty.DISLIKE_TAGS.toString());
        this.favoriteComics = (List<Key>) entity.getProperty(JsonProperty.FAVORITE_COMICS.toString());

    }

    @Override
    public JsonObject toJson() {

        return new JsonObject();

    }

    @Override
    public void fromJson(JsonObject obj) throws NoUniqueKeyException {

        if (obj.has(JsonProperty.USER_GMAIL.toString())) {

        } else {
            throw new NoUniqueKeyException("Preferences - userGmail");
        }

    }

    public EmbeddedEntity toEmbeddedEntity() {
        Entity thisEntity = this.toEntity();

        EmbeddedEntity embeddedEntity = new EmbeddedEntity();
        embeddedEntity.setKey(thisEntity.getKey());
        embeddedEntity.setPropertiesFrom(thisEntity);

        return embeddedEntity;
    }

    public static Preferences fromEmbeddedEntity(EmbeddedEntity embeddedEntity) throws NoUniqueKeyException {
        if (embeddedEntity == null) {
            System.out.println("TODO: PREFERENCES CLASS STILL BROKEN");
            return new Preferences("----");
        }
        if (embeddedEntity.hasProperty(JsonProperty.USER_GMAIL.toString())) {
            Preferences newPreferences = new Preferences((String) embeddedEntity.getProperty(JsonProperty.USER_GMAIL.toString()));

            // In order to avoid duplicating code
            Entity entity = new Entity(newPreferences.userGmail);
            entity.setPropertiesFrom(embeddedEntity);
            newPreferences.fromEntity(entity);
            return newPreferences;
        } else {
            throw new NoUniqueKeyException(JsonProperty.USER_GMAIL.toString());
        }
    }
}
