package atomic.comic;

import com.google.appengine.repackaged.com.google.gson.JsonElement;

/**
 * Primary data class representing a Comic posted and shared on the application.
 *
 * @author Anthony G. Musco
 */
public class Comic {


    private long ID;

    public Comic() {


    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
}
