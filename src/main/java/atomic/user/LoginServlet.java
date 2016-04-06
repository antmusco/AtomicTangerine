package atomic.user;

import atomic.crud.CrudServlet;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author Anthony G. Musco
 */
public class LoginServlet extends CrudServlet {


    @Override
    protected JsonElement create(JsonElement json) {
        return null;
    }

    @Override
    protected JsonElement retrieve(JsonElement json) {

        // Initialize response.
        JsonObject response = new JsonObject();

        // Grab the UserService.
        UserService service = UserServiceFactory.getUserService();

        // If the user is not logged in, return login url.
        if(service.getCurrentUser() == null) {

            String loginLink = service.createLoginURL("/");
            response.addProperty("LOGIN", loginLink);

        // Otherwise, return logout url.
        } else {

            String logoutLink = service.createLogoutURL("/");
            response.addProperty("LOGOUT", logoutLink);

        }

        return response;

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
