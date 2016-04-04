package atomic.user;

import atomic.comic.ComicTag;
import com.google.appengine.api.datastore.Key;

import java.util.List;

/**
 * Created by Freya on 4/4/16.
 */
public class Preferences {
    public Preferences() {
    }

    private Key userKey;
    private List<Key> subcriptionKeys;
    private List<ComicTag> likeTags;
    private List<ComicTag> dislikeTags;
    private List<Key> favoriteComics;

    public Key getUserKey() {
        return userKey;
    }

    public void setUserKey(Key userKey) {
        this.userKey = userKey;
    }

    public List<Key> getSubcriptionKeys() {
        return subcriptionKeys;
    }

    public void setSubcriptionKeys(List<Key> subcriptionKeys) {
        this.subcriptionKeys = subcriptionKeys;
    }

    public List<ComicTag> getLikeTags() {
        return likeTags;
    }

    public void setLikeTags(List<ComicTag> likeTags) {
        this.likeTags = likeTags;
    }

    public List<ComicTag> getDislikeTags() {
        return dislikeTags;
    }

    public void setDislikeTags(List<ComicTag> dislikeTags) {
        this.dislikeTags = dislikeTags;
    }

    public List<Key> getFavoriteComics() {
        return favoriteComics;
    }

    public void setFavoriteComics(List<Key> favoriteComics) {
        this.favoriteComics = favoriteComics;
    }
}
