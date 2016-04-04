package atomic.comic;

import atomic.user.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import java.util.List;

/**
 * Created by Gustavo on 4/3/16.
 */
public class Comic {
    public Comic() {

    }

    private Key comicKey;
    private User owner;
    private String title;
    private ComicState state;
    private List<ComicFrame> frames;
    private String globalCaption;
    private List<ComicTag> tags;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public Key getComicKey() {
        return comicKey;
    }

    public void setComicKey(Key comicKey) {
        this.comicKey = comicKey;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<ComicTag> getTags() {
        return tags;
    }

    public void setTags(List<ComicTag> tags) {
        this.tags = tags;
    }

    public String getGlobalCaption() {
        return globalCaption;
    }

    public void setGlobalCaption(String globalCaption) {
        this.globalCaption = globalCaption;
    }

    public List<ComicFrame> getFrames() {
        return frames;
    }

    public void setFrames(List<ComicFrame> frames) {
        this.frames = frames;
    }

    public ComicState getState() {
        return state;
    }

    public void setState(ComicState state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
