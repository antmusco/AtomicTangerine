package atomic.comic;

import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * CrudServlet implementation which will be used to create, retrieve, update, and delete Comic data.
 *
 * @author Anthony G. Musco
 */
public class ComicCrudServlet extends CrudServlet {

    @Override
    protected JsonElement create(JsonElement json) {

        // Convert Json to object and retrieve comic via the combo of gmail and title.
        JsonObject obj = json.getAsJsonObject();

        // Attempt to create comic.
        try {

            // Make sure gmail is specified, and capture it.
            String userGmail;
            if(obj.has(JsonProperty.USER_GMAIL.toString()))
                userGmail = obj.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            else
                return failedRequest();

            // Make sure title is specified, and capture it.
            String title;
            if(obj.has(JsonProperty.TITLE.toString()))
                title = obj.get(JsonProperty.TITLE.toString()).getAsString();
            else
                return failedRequest();

            // TODO: Make sure that comic does not already exist!
            new Comic(userGmail, title); // creates comic.

            // Successful create.
            return successfulRequest();

        } catch (NoUniqueKeyException nuke) {

            return failedRequest();

        }

    }

    @Override
    protected JsonElement retrieve(JsonElement json) {

        // Initialize response.
        JsonObject response = new JsonObject();

        // Convert Json to object and retrieve comic via the combo of gmail and title.
        JsonObject obj = json.getAsJsonObject();

        // Retrieve the comic.
        try {

            // Make sure gmail is specified, and capture it.
            String userGmail;
            if(obj.has(JsonProperty.USER_GMAIL.toString()))
                userGmail = obj.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            else
                return failedRequest();

            // Make sure title is specified, and capture it.
            String title;
            if(obj.has(JsonProperty.TITLE.toString()))
                title = obj.get(JsonProperty.TITLE.toString()).getAsString();
            else
                return failedRequest();

            // Retrieve comic from the datastore and return it as a JSON.
            Comic comic = new Comic(userGmail, title);
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.add(JsonProperty.USER.toString(), comic.toJson());

        } catch (NoUniqueKeyException nuke) {

            // No unique key.
            System.err.println(nuke.getMessage());
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
            response.add(JsonProperty.COMIC.toString(), null);

        }

        return response;

    }

    @Override
    protected JsonElement update(JsonElement json) {

        // Retrieve the user as a JSON.
        JsonObject obj = json.getAsJsonObject();

        // Attempt to update the comic.
        try {

            new Comic(obj); // updates simply by creating the comic.
            return successfulRequest();

        } catch (NoUniqueKeyException nuke) {

            return failedRequest();

        }


    }

    @Override
    protected JsonElement delete(JsonElement json) {

        // Cannot delete comics as of yet.
        return unsupportedRequest();

    }

}
