package atomic.comic;

import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import atomic.user.User;
import atomic.user.UserNotFoundException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.repackaged.com.google.api.client.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;

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

            Comic.makeNewComic(userGmail, title); // creates comic.

            // Successful create.
            return successfulRequest();

        } catch (ComicAlreadyExistsException | NoUniqueKeyException nuke) {

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
                if (req.equals(ComicRequest.UPLOAD_FRAME.toString())) {

                    processUploadNewFrameRequest(request, response);

                } else if (req.equals(ComicRequest.UPDATE_COMIC.toString())) {

                    // Instantiate and save entity.
                    new Comic(request.getAsJsonObject(JsonProperty.COMIC.toString()));

                    // Request to retrieve a list of comics.
                } else if (req.equals(ComicRequest.GET_COMIC_LIST_DEFAULT.toString())) {

                    processDefaultComicListRequest(request, response);

                    // Request to retrieve a custom list of comics.
                } else if (req.equals(ComicRequest.GET_COMIC_LIST_CUSTOM.toString())) {

                    //processCustomComicListRequest(request, response);

                    // Request to retrieve a single comic.
                } else if (req.equals(ComicRequest.GET_SINGLE_COMIC.toString())) {

                    processSingleComicRequest(request, response);

                    // Request is unsupported
                } else if (req.equals(ComicRequest.GET_USER_COMICS.toString())) {

                    processUserComicsRequest(request, response);

                } else if (req.equals(ComicRequest.VOTE_FOR_COMIC.toString())) {

                    processVoteRequest(request, response);

                } else if (req.equals(ComicRequest.PUBLISH_COMIC.toString())) {

                    processPublishComicRequest(request, response);

                } else {

                    System.err.println("Unsupported request: " + req);
                    return failedRequest();

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
    protected JsonElement delete(JsonElement json){

        // Cannot delete comics as of yet.
        return unsupportedRequest();

    }

    /**
     * Appends or updates a frame in the comic given a user gmail, a title, and a frame index.
     * @param request JSON data which should contain the user gmail, the title, the frame index, and the svg data.
     * @param response The response which will return CrudResult.SUCCESS on successful upload.
     */
    protected void processUploadNewFrameRequest(JsonObject request, JsonObject response) {

        try {

            // Grab the current user.
            User currentUser = User.getCurrentUser();

            // Grab the title of the comic to upload a frame to.
            String title;
            if(request.has(JsonProperty.TITLE.toString())) {
                title = request.get(JsonProperty.TITLE.toString()).getAsString();
            } else {
                throw new IllegalArgumentException("Request must include title");
            }

            // Grab the embedded SVG data.
            String svgData;
            if(request.has(JsonProperty.SVG_DATA.toString())) {
                svgData = request.get(JsonProperty.SVG_DATA.toString()).getAsString();
            } else {
                throw new IllegalArgumentException("Request must include svg data");
            }

            // Grab the indicated frame index.
            int frameIndex;
            if(request.has(JsonProperty.FRAME_INDEX.toString())) {
                frameIndex = request.get(JsonProperty.FRAME_INDEX.toString()).getAsInt();
            } else {
                throw new IllegalArgumentException("Request must include svg data");
            }

            // Retrieve the comic using the gmail of the current user and the title.
            Comic comic = Comic.retrieveComic(currentUser.getGmail(), title);

            // Overwrite or append.
            if(frameIndex == comic.getFrames().size()) {
                comic.getFrames().add(new Text(svgData));
            } else if (frameIndex < comic.getFrames().size()) {
                comic.getFrames().set(frameIndex, new Text(svgData));
            } else {
                throw new IllegalArgumentException("Frame index " + frameIndex + "out of bounds!");
            }

            // Save the entity and return success.
            comic.saveEntity();
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());

        } catch (Exception  e) {

            processGeneralException(response, e);

        }
    }

    protected void processDefaultComicListRequest(JsonObject request, JsonObject response) {

        try {
            // Query to construct.
            Query q = null;

            // All requests must have a generation criteria.

            // Generation criteria - Date created.
            if (request.has(JsonProperty.DATE_CREATED.toString())) {

                // Get the start date timestamp.
                long timestamp = request.get(JsonProperty.DATE_CREATED.toString()).getAsLong();
                Date lastDateCreated = new Date(timestamp);

                // Generate the filter.
                Query.Filter dateFilter = new Query.FilterPredicate(
                        JsonProperty.DATE_CREATED.toString(),      // Comics which were created...
                        Query.FilterOperator.LESS_THAN_OR_EQUAL,   // on or before...
                        lastDateCreated                            // last date created.
                );

                // Construct the query.
                q = new Query(EntityKind.COMIC.toString())
                        .setFilter(dateFilter)
                        .addSort(JsonProperty.DATE_CREATED.toString(), Query.SortDirection.DESCENDING); // latest first.

                // Generation criteria - User gmail.
            } else if (request.has(JsonProperty.USER_GMAIL.toString())) {

                // Grab the gmail.
                String gmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();

                // Generate the filter.
                Query.Filter userFilter = new Query.FilterPredicate(
                        JsonProperty.USER_GMAIL.toString(),       // Comics created by...
                        Query.FilterOperator.EQUAL,               // user with...
                        gmail                                     // indicated gmail.
                );

                // Construct the query.
                q = new Query(EntityKind.COMIC.toString())
                        .setFilter(userFilter)
                        .addSort(JsonProperty.DATE_CREATED.toString(), Query.SortDirection.DESCENDING); // latest first.

            }

            // Make sure a query was generated.
            if (q == null) throw new IllegalArgumentException("Comic list cannot be generated.");

            // Execute the query and generate the results.
            List<Entity> results = DatastoreEntity.executeQuery(q);
            if (!results.isEmpty()) {

                // Add field indicating the end of the date ranges.
                Entity lastEntity = results.get(results.size() - 1);
                Date dateOfLast = (Date) lastEntity.getProperty(JsonProperty.DATE_CREATED.toString());
                response.addProperty(JsonProperty.DATE_CREATED.toString(), dateOfLast.getTime());

                // Add the list of comic keys to the JSON response.
                JsonArray comicArray = new JsonArray();
                for (Entity e : results) {
                    String gmail = (String) e.getProperty(JsonProperty.USER_GMAIL.toString());
                    String title = (String) e.getProperty(JsonProperty.TITLE.toString());
                    Comic c = null;
                    try {
                        c = Comic.retrieveComic(gmail, title);
                    } catch (NoUniqueKeyException nuke) {
                        nuke.printStackTrace();
                        return;
                    } catch (ComicNotFoundException cnfe) {
                        cnfe.printStackTrace();
                        return;
                    }
                    JsonObject comicObj = new JsonObject();
                    comicObj.addProperty(JsonProperty.USER_GMAIL.toString(), gmail);
                    comicObj.addProperty(JsonProperty.TITLE.toString(), c.getTitle());
                    comicObj.addProperty(JsonProperty.SVG_DATA.toString(), c.getFrames().get(0).getValue());
                    comicObj.addProperty(JsonProperty.COMIC_ID_STRING.toString(), c.generateKeyString());
                    comicArray.add(comicObj);
                }

                response.add(JsonProperty.COMICS.toString(), comicArray);

            } else {
                // return 404 comics not found.
            }

        } catch (Exception e) {

            processGeneralException(response, e);

        }

    }

    /**
     * Process a request to retrieve a single comic.
     *
     * @param request  The request which contains the gmail and title of the comic to retrieve.
     * @param response Response which contains the comic, if retrieval was successful.
     * @throws NoUniqueKeyException   Thrown if either the gmail or title was missing from the request.
     * @throws ComicNotFoundException Thrown if the comic does not exist in the datastore.
     */
    protected void processSingleComicRequest(JsonObject request, JsonObject response) {

        // Make sure gmail is specified, and capture it.
        try {

            String userGmail;
            if (request.has(JsonProperty.USER_GMAIL.toString()))
                userGmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            else
                throw new NoUniqueKeyException("Cannot find comic. Missing comic creator gmail.");

            // Make sure title is specified, and capture it.
            String title;
            if (request.has(JsonProperty.TITLE.toString()))
                title = request.get(JsonProperty.TITLE.toString()).getAsString();
            else
                throw new NoUniqueKeyException("Cannot find comic. Missing comic title.");

            // Retrieve comic from the datastore and return it as a JSON.
            Comic comic = Comic.retrieveComic(userGmail, title);
            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.add(JsonProperty.COMIC.toString(), comic.toJson());

        } catch (Exception e) {

            processGeneralException(response, e);

        }

    }

    private void processUserComicsRequest(JsonObject request, JsonObject response) {

        try {

            User currentUser = User.getCurrentUser();
            List<Comic> comics = Comic.retrieveUserComics(currentUser.getGmail());

            JsonArray comicArray = new JsonArray();
            for(Comic c : comics) {

                JsonObject titleAndSvg = new JsonObject();
                titleAndSvg.addProperty(JsonProperty.TITLE.toString(), c.getTitle());
                titleAndSvg.addProperty(JsonProperty.SVG_DATA.toString(), c.getFrames().get(0).getValue());
                comicArray.add(titleAndSvg);

            }
            response.add(JsonProperty.DRAFTS.toString(), comicArray);


        } catch (Exception e) {

            processGeneralException(response, e);

        }
    }

    private void processVoteRequest(JsonObject request, JsonObject response) {

        try {

            // Retrieve the current user.
            User user = User.getCurrentUser();

            // Retrieve the comic creator gmail from the request.
            String creatorGmail;
            if(request.has(JsonProperty.USER_GMAIL.toString())) {
                creatorGmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();
            } else {
                throw new IllegalArgumentException("Vote request must include creator's gmail.");
            }

            // Retrieve the comic title from the request.
            String title;
            if(request.has(JsonProperty.TITLE.toString())) {
                title = request.get(JsonProperty.TITLE.toString()).getAsString();
            } else {
                throw new IllegalArgumentException("Vote request must include comic's title.");
            }

            // Retrieve the vote type from the request.
            ComicVote vote;
            if(request.has(JsonProperty.COMIC_VOTE.toString())) {
                vote = ComicVote.fromString(request.get(JsonProperty.COMIC_VOTE.toString()).getAsString());
            } else {
                throw new IllegalArgumentException("Vote request must include vote type.");
            }

            // Retrieve the comic to upvote and generate it's keystring.
            Comic comicToUpvote = Comic.retrieveComic(creatorGmail, title);
            String comicKeyString = comicToUpvote.generateKeyString();

            // Check to see if the comic was previously upvoted by this user.
            if(user.getUpvotedComics().contains(comicKeyString)) {

                switch (vote) {
                    // Do nothing, comic already upvoted.
                    case UPVOTE:
                        response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
                        return;
                    // Remove vote from upvoted list and add it to downvoted list (double decrement).
                    case DOWNVOTE:
                        user.getUpvotedComics().remove(comicKeyString);
                        comicToUpvote.decrementScore();
                        user.getDownvotedComics().add(comicKeyString);
                        comicToUpvote.decrementScore();
                        break;
                    default:
                        throw new IllegalArgumentException("Unsuppoted vote type.");
                }

            // Check to see if the comic was previously downvoted by this user.
            } else if (user.getDownvotedComics().contains(comicKeyString)) {

                switch(vote) {
                    // Remove vote from downvoted list and add it to upvoted list (double increment).
                    case UPVOTE:
                        user.getDownvotedComics().remove(comicKeyString);
                        comicToUpvote.incrementScore();
                        user.getUpvotedComics().add(comicKeyString);
                        comicToUpvote.incrementScore();
                        break;
                    // Do nothing, comic already downvoted.
                    case DOWNVOTE:
                        response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
                        return;
                    default:
                        throw new IllegalArgumentException("Unsuppoted vote type.");
                }

            // First time a user is voting on the comic.
            } else {

                switch(vote) {
                    case UPVOTE:
                        user.getUpvotedComics().add(comicKeyString);
                        comicToUpvote.incrementScore();
                        break;
                    case DOWNVOTE:
                        user.getDownvotedComics().add(comicKeyString);
                        comicToUpvote.decrementScore();
                        break;
                }

            }

            // Save the user and the entity.
            user.saveEntity();
            comicToUpvote.saveEntity();

            response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
            response.addProperty(JsonProperty.SCORE.toString(), comicToUpvote.getScore());

        } catch (Exception e) {

            processGeneralException(response, e);

        }

    }


    private void processPublishComicRequest(JsonObject request, JsonObject response) {

        try {

            // Grab the current user.
            User currentUser = User.getCurrentUser();

            // Upload the frame.
            processUploadNewFrameRequest(request, response);

            // Retrieve the comic title from the request.
            String title;
            if(request.has(JsonProperty.TITLE.toString())) {
                title = request.get(JsonProperty.TITLE.toString()).getAsString();
            } else {
                throw new IllegalArgumentException("Vote request must include comic's title.");
            }

            Comic comicToPublish = Comic.retrieveComic(currentUser.getGmail(), title);
            comicToPublish.publish();
            comicToPublish.saveEntity();

            // Property is set during the `processUploadNewFrameRequest()`
            //response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());

        } catch (Exception e) {

            processGeneralException(response, e);

        }

    }

}
