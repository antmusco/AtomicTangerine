package atomic.user;

import atomic.comic.Comic;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

        return new JsonObject();

    }
}
