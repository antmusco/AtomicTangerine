package atomic.comic;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.gson.JsonElement;
import com.google.appengine.repackaged.com.google.io.base.shell.AbnormalTerminationException;

import java.util.Date;
import java.util.List;

/**
 * Primary data class representing a Comic posted and shared on the application.
 *
 * @author Anthony G. Musco
 */
public class Comic extends DatastoreEntity {

    private Key ownerKey;
    private String title;
    //TODO private ComicStyle style;
    private ComicState state;
    private List<ComicFrame> frames;
    private String globalCaption;
    //private List<ComicTag> tags;
    private Date dateCreated;
    private Date dateModified;

    protected Comic(String title) {
        super(EntityKind.COMIC);
    }

    @Override
    protected Key generateKey() {
        return KeyFactory.createKey(this.entityKind.toString(), this.ownerKey + "_" + this.title);
    }

    @Override
    public Entity toEntity() {
        Entity entity = null;

        try {

            entity = retrieveEntity();

        } catch (EntityNotFoundException e) {

            entity = new Entity(generateKey());

        }

        // Write each of the properties to the entity.
        entity.setProperty(JsonProperty.OWNER_KEY.toString(), this.ownerKey);
        entity.setProperty(JsonProperty.TITLE.toString(), this.title);
        entity.setProperty(JsonProperty.STATE.toString(), this.state.toString());
        entity.setProperty(JsonProperty.FRAMES.toString(), this.frames);
        entity.setProperty(JsonProperty.GLOBAL_CAPTION.toString(), this.globalCaption);
        entity.setProperty(JsonProperty.DATE_CREATED.toString(), this.dateCreated);
        entity.setProperty(JsonProperty.DATE_MODIFIED.toString(), this.dateModified);

        return entity;
    }

    @Override
    protected void fromEntity(Entity entity) {
        // Read each of the properties from the entity.
        this.ownerKey      = (Key)       entity.getProperty(JsonProperty.OWNER_KEY.toString());
        this.title         = (String)    entity.getProperty(JsonProperty.TITLE.toString());
        this.state         =             ComicState.fromString((String)entity.getProperty(JsonProperty.STATE.toString()));
        this.frames        =             ComicFrame.initFromKeys((List<Key>)entity.getProperty(JsonProperty.FRAMES.toString()));
        this.globalCaption = (String)    entity.getProperty(JsonProperty.GLOBAL_CAPTION.toString());
        this.dateCreated   = (Date)      entity.getProperty(JsonProperty.DATE_CREATED.toString());
        this.dateModified  = (Date)      entity.getProperty(JsonProperty.DATE_MODIFIED.toString());
    }
}
