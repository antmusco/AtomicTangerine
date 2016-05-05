package atomic.user;

import atomic.crud.CrudServlet;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A simple CRUD servlet which only supports request. `Retrieve` returns a login or logout link, depending on whether
 * the user is currently logged in or not. No other CRUD operation is supported (`Create`, `Update`, `Delete`).
 *
 * @author Anthony G. Musco
 */
public class LoginServlet extends CrudServlet {

    @Override
    protected JsonElement create(JsonElement json) {
        return unsupportedRequest();
    }

    /**
     * Returns a login or logout link, depending on whether the user is currently logged in or not.
     *
     * @param json Request parameter, which is ignored for this function.
     * @return A JsonObject containing a login or logout link.
     */
    @Override
    protected JsonElement retrieve(JsonElement json) {

        // Initialize response.
        JsonObject response = new JsonObject();

        // Grab the UserService.
        UserService service = UserServiceFactory.getUserService();

        // If the user is not logged in, return login url.
        if (service.getCurrentUser() == null) {

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
        return unsupportedRequest();
    }

    @Override
    protected JsonElement delete(JsonElement json) {
        return unsupportedRequest();
    }

}
