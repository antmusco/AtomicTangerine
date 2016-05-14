package atomic.user;

import atomic.comic.ComicRequest;
import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.com.google.api.client.json.Json;
import com.google.gson.JsonArray;
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
            ((JsonObject) response).add(JsonProperty.USER.toString(), user.toJson());

        } catch (Exception e) {

            // Problem occurred while retrieving the user.
            System.err.println(e.getMessage());
            response = failedRequest();
            ((JsonObject) response).add(JsonProperty.USER.toString(), null);

        }

        return response;

    }

    @Override
    protected JsonElement update(JsonElement json) {

        // Retrieve the user as a JSON.
        JsonObject request = json.getAsJsonObject();
        JsonObject response = new JsonObject();

        // Attempt to update the user.
        try {

            // Determine what the request was.
            if (request.has(JsonProperty.REQUEST.toString())) {

                String req = request.get(JsonProperty.REQUEST.toString()).getAsString();

                // Request to update comic data.
                if (req.equals(UserRequest.SUBSCRIBE.toString()) ||
                        req.equals(UserRequest.UNSUBSCRIBE.toString())) {

                    processSubscribeRequest(request, response, UserRequest.fromString(req));

                } else if (req.equals(UserRequest.GET_SUBSCRIPTION_LIST.toString())) {

                    processGetSubscriptionListRequest(request, response);

                } else if (req.equals(UserRequest.GET_USER_BY_GMAIL.toString())) {

                    processGetUserByGmailRequest(request, response);
                    return response;

                }

            } else {

                // Simply construct a user from the Json passed.
                JsonObject userObj = request.getAsJsonObject(JsonProperty.USER.toString());
                new User(userObj);

            }

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

    private void processSubscribeRequest(JsonObject request, JsonObject response, UserRequest which) {

        try {

            User currentUser = User.getCurrentUser();

            String userGmail;
            if (request.has(JsonProperty.USER_GMAIL.toString())) {
                userGmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            } else {
                throw new IllegalArgumentException("Request must include user gmail.");
            }

            if(which == UserRequest.SUBSCRIBE) {
                currentUser.subscribeTo(userGmail);
            } else {
                currentUser.unsubscribeFrom(userGmail);
            }

            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());

        } catch (Exception e) {

            processGeneralException(response, e);

        }
        
    }

    private void processGetSubscriptionListRequest(JsonObject request, JsonObject response) {

        try {

            User currentUser = User.getCurrentUser();
            Preferences currentUserPreferences = currentUser.getPreferences();

            JsonArray subscriptionList = new JsonArray();
            for(String s : currentUserPreferences.getSubscriptions()) {

                subscriptionList.add(s);

            }

            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.add(JsonProperty.SUBSCRIPTIONS.toString(), subscriptionList);

        } catch (Exception e) {

            processGeneralException(response, e);

        }

    }

    private void processGetUserByGmailRequest(JsonObject request, JsonObject response) {

        if(request.has(JsonProperty.USER_GMAIL.toString())) {
            String gmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            try {
                User user = new User(gmail);
                response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
                response.add(JsonProperty.USER.toString(), user.toJson());
            } catch (NoUniqueKeyException e){
                processGeneralException(response, e);
            }
        }

    }

}
