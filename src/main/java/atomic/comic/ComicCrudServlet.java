package atomic.comic;

import atomic.crud.CrudResult;
import atomic.crud.CrudServlet;
import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
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

                    uploadNewFrame(request, response);

                } else if (req.equals(ComicRequest.UPDATE_COMIC.toString())) {

                    // Instantiate and save entity.
                    new Comic(request.getAsJsonObject(JsonProperty.COMIC.toString()));

                    // Request to retrieve a list of comics.
                } else if (req.equals(ComicRequest.COMIC_LIST_DEFAULT.toString())) {

                    processDefaultComicListRequest(request, response);

                    // Request to retrieve a custom list of comics.
                } else if (req.equals(ComicRequest.COMIC_LIST_CUSTOM.toString())) {

                    //processCustomComicListRequest(request, response);

                    // Request to retrieve a single comic.
                } else if (req.equals(ComicRequest.SINGLE_COMIC.toString())) {

                    processSingleComicRequest(request, response);

                    // Request is unsupported
                } else {

                    System.err.println("Unsupported request: " + req);
                    return failedRequest();

                }

                return response;

            } else {

                System.err.println("Request not specified.");
                return failedRequest();

            }

        } catch (ComicNotFoundException | NoUniqueKeyException nuke) {

            System.err.println(nuke.getMessage());

        }

        return failedRequest();

    }

    @Override
    protected JsonElement delete(JsonElement json) {

        // Cannot delete comics as of yet.
        return unsupportedRequest();

    }

    protected void uploadNewFrame(JsonObject request, JsonObject response) {

        // Grab the upload URL.
        String uploadURL;
        if (request.has(JsonProperty.UPLOAD_URL.toString())) {
            uploadURL = request.get(JsonProperty.UPLOAD_URL.toString()).getAsString();
        } else {
            throw new IllegalArgumentException("Upload URL required!");
        }

        // Grab the redirect URL.
        String redirectURL;
        if (request.has(JsonProperty.REDIRECT_URL.toString())) {
            redirectURL = request.get(JsonProperty.REDIRECT_URL.toString()).getAsString();
        } else {
            throw new IllegalArgumentException("Redirect required!");
        }

        // Grab the comic title.
        String title;
        if (request.has(JsonProperty.TITLE.toString())) {
            title = request.get(JsonProperty.TITLE.toString()).getAsString();
        } else {
            throw new IllegalArgumentException("Title required!");
        }

        String gmail = null;
        if (request.has(JsonProperty.USER_GMAIL.toString())) {
            gmail = request.get(JsonProperty.USER_GMAIL.toString()).getAsString();
        }

        // Grab the comic svg data.
        String svgData;
        if (request.has(JsonProperty.SVG_DATA.toString())) {
            svgData = request.get(JsonProperty.SVG_DATA.toString()).getAsString();
        } else {
            throw new IllegalArgumentException("SVG Data required!");
        }

        try {

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(uploadURL);
            final HttpEntity entity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("file",
                            (new ByteArrayInputStream(svgData.getBytes("UTF-8"))),
                            ContentType.APPLICATION_ATOM_XML,
                            "test.svg")
                    .addTextBody(JsonProperty.SUBMISSION_TYPE.toString(), ComicRequest.UPLOAD_FRAME.toString())
                    .addTextBody(JsonProperty.REDIRECT_URL.toString(), redirectURL)
                    .addTextBody(JsonProperty.TITLE.toString(), title)
                    .addTextBody(JsonProperty.USER_GMAIL.toString(), gmail)
                    .build();

            post.setEntity(entity);

            CloseableHttpResponse rsp = client.execute(post);

            response.addProperty("RESP", EntityUtils.toString(rsp.getEntity()));

        } catch (Exception e) {

            System.err.println(e.getMessage());

        }


    }

    protected void processDefaultComicListRequest(JsonObject request, JsonObject response) {

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
                JsonObject comicObj = new JsonObject();
                comicObj.addProperty(JsonProperty.USER_GMAIL.toString(), gmail);
                comicObj.addProperty(JsonProperty.TITLE.toString(), title);
                comicArray.add(comicObj);
            }

        } else {

            // return 404 comics not found.

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
    protected void processSingleComicRequest(JsonObject request, JsonObject response) throws NoUniqueKeyException,
            ComicNotFoundException {

        // Make sure gmail is specified, and capture it.
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

    }

}
