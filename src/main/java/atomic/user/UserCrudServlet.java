package atomic.user;

import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.api.client.json.Json;
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
     * @param json JsonObject containing the request parameters passed to this servlet.
     * @return A JsonObject containing the return parameters.
     */
    @Override
    protected JsonElement create(JsonElement json) {

        // Convert Json to object and retrieve user via their gmail account.
        JsonObject obj = json.getAsJsonObject();
        User newUser = new User(obj.get(JsonProperty.GMAIL.toString()).getAsString());

        // Successful create.
        return successfulRequest();

    }

    /**
     * Retrieves the data associated with the user currently logged in to Gmail.
     * @param json
     * @return
     */
    @Override
    protected JsonElement retrieve(JsonElement json) {

        // Initialize response.
        JsonObject response = new JsonObject();

        // Grab the UserService.
        UserService service = UserServiceFactory.getUserService();

        // If the user is not logged in, user can not be retrieved.
        if(service.getCurrentUser() == null) {

            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
            response.add(JsonProperty.USER.toString(), null);

        } else {

            String gmail = service.getCurrentUser().getEmail();

            // Construct the user - This will create in the data store if non-existent.
            User user = new User(gmail);

            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.add(JsonProperty.USER.toString(), user.toJson());

        }

        return response;

    }

    @Override
    protected JsonElement update(JsonElement json) {

        JsonObject obj = json.getAsJsonObject();
        // System.out.println(obj);
        try {

            new User(obj);
            return successfulRequest();

        } catch (NoUniqueKeyException nuke) {

            return failedRequest();

        }


    }

    @Override
    protected JsonElement delete(JsonElement json) {
        return unsupportedRequest();
    }

}
