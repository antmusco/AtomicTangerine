package atomic.user;

import atomic.crud.CrudServlet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * CrudServlet implementation which will be used to create, retrieve, update, and delete user data.
 *
 * @author Anthony G. Musco
 */
public class UserCrudServlet extends CrudServlet {

    /**
     * For now, the json format should be as follows:
     *
     * {
     *     "request"  : "create",
     *     "gmail"    : "google@gmail.com",
     *     "username" : "gman1995"
     * }
     *
     * @param json
     * @return
     */
    @Override
    protected JsonElement create(JsonElement json) {

        JsonObject obj = json.getAsJsonObject();

        User newUser = new User();
        newUser.setGmail(obj.get("gmail").getAsString());
        newUser.setUsername(obj.get("gmail").getAsString());



        return null;

    }

    @Override
    protected JsonElement retrieve(JsonElement json) {
        return null;
    }

    @Override
    protected JsonElement update(JsonElement json) {
        return null;
    }

    @Override
    protected JsonElement delete(JsonElement json) {
        return null;
    }

}