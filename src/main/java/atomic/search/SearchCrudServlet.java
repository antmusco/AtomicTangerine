package atomic.search;

import atomic.comic.Comic;
import atomic.comment.Comment;
import atomic.comment.CommentRequest;
import atomic.comment.CommentVote;
import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import atomic.user.User;
import com.google.appengine.repackaged.com.google.api.client.json.Json;
import com.google.appengine.repackaged.com.google.appengine.api.search.SearchServicePb;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;

/**
 * CrudServlet implementation which will be used to create, retrieve, update, and delete Comment data.
 *
 * @author Anthony G. Musco
 */
public class SearchCrudServlet extends CrudServlet {

    @Override
    protected JsonElement create(JsonElement json) {

        return unsupportedRequest();

    }

    @Override
    protected JsonElement retrieve(JsonElement json) {

        return unsupportedRequest();

    }

    @Override
    protected JsonElement update(JsonElement json) {

        // Retrieve the request, construct the response.
        JsonObject request = json.getAsJsonObject();
        JsonObject response = new JsonObject();

        try {

            // Determine what the request was.
            if (!request.has(JsonProperty.REQUEST.toString())) {
                throw new IllegalArgumentException("Request type not specified.");
            }

            String req = request.get(JsonProperty.REQUEST.toString()).getAsString();

            if(req.equals(SearchRequest.SEARCH_ALL.toString())) {

                processSearchAllRequest(request, response);

            } else if (req.equals(SearchRequest.SEARCH_USERS.toString())) {

                processSearchUsersRequest(request, response);

            } else if (req.equals(SearchRequest.SEARCH_COMICS.toString())) {

                processSearchComicsRequest(request, response);

            }

            return response;

        } catch (Exception e) {

            processGeneralException(response, e);

        }

        return response;

    }

    @Override
    protected JsonElement delete(JsonElement json) {

        // Cannot delete comics as of yet.
        return unsupportedRequest();

    }

    private void processSearchAllRequest(JsonObject request, JsonObject response) {

        // Extract the search key.
        String searchKey = request.get(JsonProperty.SEARCH_KEY.toString()).getAsString();
        System.out.println(searchKey);

        // Get results from users (handle and gmail) and comics (tags).
        JsonArray comicArray = Comic.searchPublishedComicsByTitle(searchKey);
        JsonArray userArray = User.searchUsersByHandleOrGmail(searchKey);

        if((comicArray.size() + userArray.size()) > 0) {
            // Add success as well as the other properties.
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.add(JsonProperty.COMICS.toString(), comicArray);
            response.add(JsonProperty.USERS.toString(), userArray);
        } else {
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
        }
    }

    private void processSearchUsersRequest(JsonObject request, JsonObject response) {

        // Extract the search key.
        String searchKey = request.get(JsonProperty.SEARCH_KEY.toString()).getAsString();

        // Get results from users (handle and gmail) and comics (tags).
        JsonArray userArray = User.searchUsersByHandleOrGmail(searchKey);

        // Add success as well as the other properties.
        if(userArray.size() > 0) {
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.add(JsonProperty.USERS.toString(), userArray);
        } else {
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
        }

    }

    private void processSearchComicsRequest(JsonObject request, JsonObject response) {

        // Extract the search key.
        String searchKey = request.get(JsonProperty.SEARCH_KEY.toString()).getAsString();

        // Get results from users (handle and gmail) and comics (tags).
        JsonArray comicArray = Comic.searchPublishedComicsByTitle(searchKey);

        // Add success as well as the other properties.
        if(comicArray.size() > 0) {
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.add(JsonProperty.COMICS.toString(), comicArray);
        } else {
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
        }

    }

}
