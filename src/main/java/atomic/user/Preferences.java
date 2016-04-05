package atomic.user;

import atomic.comic.Comic;
import com.google.appengine.repackaged.com.google.gson.JsonElement;

import java.util.List;

/**
 * Defines preferences and settings which are associated with a particular User.
 *
 * @autho Anthony G. Musco
 */
public class Preferences {

    private List<User>   subscriptions;
    private List<String> likeTags;
    private List<String> dislikeTags;
    private List<Comic>  favoriteComics;

    public Preferences() {



    }

    public JsonElement toJsonObject() {



    }
}
