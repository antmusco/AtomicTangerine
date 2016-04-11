package atomic.user;

import atomic.comic.Comic;
import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.Jsonable;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines preferences and settings which are associated with a particular User.
 *
 * @autho Anthony G. Musco
 */
public class Preferences extends DatastoreEntity implements Jsonable {

    private String       userGmail;
    private List<Key>    subscriptions;
    private List<String> likeTags;
    private List<String> dislikeTags;
    private List<Key>    favoriteComics;

    public Preferences() {
        super(EntityKind.PREFERENCES);

        subscriptions = new LinkedList<>();
        likeTags = new LinkedList<>();
        dislikeTags = new LinkedList<>();
        favoriteComics = new LinkedList<>();


    }
    public Preferences(JsonObject obj) {

        super(EntityKind.PREFERENCES);

        if(obj.has(JsonProperty.USER_GMAIL.toString())) {
            userGmail = obj.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            fromJson(obj);
        } else {
            throw new IllegalArgumentException("Json does not contain a unique key (userGmail).");
        }

    }

    @Override
    protected Key generateKey() {
        return KeyFactory.createKey(this.entityKind.toString(), this.userGmail);
    }

    @Override
    protected void toEntity(Entity entity) {
        entity.setProperty(JsonProperty.USER_GMAIL.toString(), this.userGmail);
        entity.setProperty(JsonProperty.SUBSCRIPTIONS.toString(), this.subscriptions);
        entity.setProperty(JsonProperty.LIKE_TAGS.toString(), this.likeTags);
        entity.setProperty(JsonProperty.DISLIKE_TAGS.toString(), this.dislikeTags);
        entity.setProperty(JsonProperty.FAVORITE_COMICS.toString(), this.favoriteComics);
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
    public void fromJson(JsonObject obj) {

    }
}
