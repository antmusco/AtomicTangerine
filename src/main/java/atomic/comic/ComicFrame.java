package atomic.comic;

import com.google.appengine.api.datastore.Key;

/**
 * Created by Gustavo on 4/3/16.
 */
public class ComicFrame {
    public ComicFrame() {
    }

    private Key associatedComicKey;
    private int index;
    private String localCaption;
    //private **IMAGE CLASS** image;

    public String getLocalCaption() {
        return localCaption;
    }

    public void setLocalCaption(String localCaption) {
        this.localCaption = localCaption;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Key getAssociatedComicKey() {
        return associatedComicKey;
    }

    public void setAssociatedComicKey(Key associatedComicKey) {
        this.associatedComicKey = associatedComicKey;
    }
}
