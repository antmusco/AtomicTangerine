package atomic.comic;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
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
    private List<ComicTag> tags;
    private Date dateCreated;
    private Date dateModified;

    protected Comic(String title) {
        super(EntityKind.COMIC);
    }

    @Override
    protected Key generateKey() {
        return null;
    }

    @Override
    public Entity toEntity() {
        return null;
    }

    @Override
    protected void fromEntity(Entity entity) {

    }
}
