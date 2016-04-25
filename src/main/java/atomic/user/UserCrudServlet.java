package atomic.user;

import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * CrudServlet implementation which will be used to create, retrieve, update, and delete User data.
 *
 * @author Anthony G. Musco
 */
public class UserCrudServlet extends CrudServlet {

    @Override
    protected JsonElement create(JsonElement json) {

        // Convert Json to object and retrieve user via their gmail account.
        JsonObject obj = json.getAsJsonObject();

        // Attempt to create user.
        try {

            new User(obj.get(JsonProperty.GMAIL.toString()).getAsString()); // creates user.

            // Successful create.
            return successfulRequest();

        } catch (NoUniqueKeyException nuke) {

            return failedRequest();

        }

    }

    @Override
    protected JsonElement retrieve(JsonElement json) {

        JsonElement response;

        // Construct the user - This will create in the data store if non-existent.
        try {
            // Grab the UserService.
            User user = User.getCurrentUser();
            response = successfulRequest();
            ((JsonObject)response).add(JsonProperty.USER.toString(), user.toJson());

        } catch (Exception e) {

            // Problem occurred while retrieving the user.
            System.err.println(e.getMessage());
            response = failedRequest();
            ((JsonObject)response).add(JsonProperty.USER.toString(), null);

        }

        return response;

    }

    @Override
    protected JsonElement update(JsonElement json) {

        // Retrieve the user as a JSON.
        JsonObject obj = json.getAsJsonObject();

        // Attempt to update the user.
        try {

            // Simply construct a user from the Json passed.
            JsonObject userObj = obj.getAsJsonObject(JsonProperty.USER.toString());
            new User(userObj);

            // Successful update.
            return successfulRequest();

        } catch (NoUniqueKeyException nuke) {

            return failedRequest();

        }


    }

    @Override
    protected JsonElement delete(JsonElement json) {

        // Cannot delete users as of yet.
        return unsupportedRequest();

    }

}
