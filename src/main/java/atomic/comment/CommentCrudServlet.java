package atomic.comment;

import atomic.comic.Comic;
import atomic.comic.ComicAlreadyExistsException;
import atomic.comic.ComicNotFoundException;
import atomic.comic.ComicRequest;
import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import atomic.user.User;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
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
public class CommentCrudServlet extends CrudServlet {

    @Override
    protected JsonElement create(JsonElement json) {

        // Convert Json to object and retrieve comment data.
        JsonObject obj = json.getAsJsonObject();

        // Attempt to create comic.
        try {

            // Make sure commentor Gmail is specified, and capture it.
            String commentorGmail;
            if (obj.has(JsonProperty.COMMENTOR_GMAIL.toString()))
                commentorGmail = obj.get(JsonProperty.COMMENTOR_GMAIL.toString()).getAsString();
            else
                return failedRequest();

            // Make sure gmail is specified, and capture it.
            String userGmail;
            if (obj.has(JsonProperty.USER_GMAIL.toString()))
                userGmail = obj.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            else
                return failedRequest();

            // Make sure title is specified, and capture it.
            String title;
            if (obj.has(JsonProperty.TITLE.toString()))
                title = obj.get(JsonProperty.TITLE.toString()).getAsString();
            else
                return failedRequest();

            // Make sure comment is specified, and capture it.
            String comment;
            if (obj.has(JsonProperty.COMMENT.toString()))
                comment = obj.get(JsonProperty.COMMENT.toString()).getAsString();
            else
                return failedRequest();

            Comment.postNewComment(commentorGmail, userGmail, title, comment);

            // Successful create.
            return successfulRequest();

        } catch (NoUniqueKeyException nuke) {

            return failedRequest();

        }

    }

    @Override
    protected JsonElement retrieve(JsonElement json) {

        return unsupportedRequest();

    }

    @Override
    protected JsonElement update(JsonElement json) {

        // Retrieve the user as a JSON.
        JsonObject request = json.getAsJsonObject();
        JsonObject response = new JsonObject();

        try {

            // Determine what the request was.
            if (request.has(JsonProperty.REQUEST.toString())) {

                String req = request.get(JsonProperty.REQUEST.toString()).getAsString();

                // Request to update comic data.
                if (req.equals(CommentRequest.GET_COMMENTS_FOR_COMIC.toString())) {

                    processCommentsForComicRequest(request, response);

                } else if (req.equals(CommentRequest.GET_COMMENTS_FOR_USER.toString())) {

                    processCommentsForUserRequest(request, response);
                }

                return response;

            } else {

                System.err.println("Request not specified.");
                return failedRequest();

            }

        } catch (Exception e) {

            System.err.println(e.getMessage());

        }

        return failedRequest();

    }

    @Override
    protected JsonElement delete(JsonElement json) {

        // Cannot delete comics as of yet.
        return unsupportedRequest();

    }

    private void processCommentsForComicRequest(JsonObject request, JsonObject response) {

        // Retrieve the user gmail from the request.
        String userGmail;
        if(request.has(JsonProperty.USER_GMAIL.toString())) {
            userGmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();
        } else {
            throw new IllegalArgumentException("Attempted request for comments without user gmail.");
        }

        // Retireve the comic title from the request.
        String title;
        if(request.has(JsonProperty.TITLE.toString())) {
            title =request.get(JsonProperty.TITLE.toString()).getAsString();
        } else {
            throw new IllegalArgumentException("Attempted request for comments without title.");
        }

        // Retrieve the list of Comments for the specified Comic.
        List<Comment> comments = Comment.getCommentsForComic(userGmail, title);
        response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());

        // Assemble the comments into a Json array to return as a response.
        response.add(JsonProperty.COMMENTS.toString(), listOfCommentsToJsonArray(comments));

    }

    private void processCommentsForUserRequest(JsonObject request, JsonObject response) {

        // Retrieve the user gmail from the request.
        String userGmail;
        if(request.has(JsonProperty.USER_GMAIL.toString())) {
            userGmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();
        } else {
            throw new IllegalArgumentException("Attempted request for comments without user gmail.");
        }
        // Retrieve the list of Comments for the specified Comic.
        List<Comment> comments = Comment.getCommentsFromUser(userGmail);
        response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());

        // Assemble the comments into a Json array to return as a response.
        response.add(JsonProperty.COMMENTS.toString(), listOfCommentsToJsonArray(comments));

    }

    private JsonArray listOfCommentsToJsonArray(List<Comment> comments) {

        JsonArray commentArray = new JsonArray();
        for(Comment c : comments) {

            // Create the Comment Json object.
            JsonObject commentObj = new JsonObject();
            commentObj.addProperty(JsonProperty.COMMENTOR_GMAIL.toString(), c.getCommentorGmail());
            commentObj.addProperty(JsonProperty.COMMENT.toString(), c.getComment());
            commentObj.addProperty(JsonProperty.DATE_POSTED.toString(), c.getDatePosted().getTime());

            // Add the Comment to the array.
            commentArray.add(commentObj);

        }

        return commentArray;

    }

}
