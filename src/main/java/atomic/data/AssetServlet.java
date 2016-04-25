package atomic.data;

import atomic.comic.Comic;
import atomic.json.JsonProperty;
import atomic.user.User;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Asset handling servlet.
 *
 * @author Anthony G. Musco.
 */
public class AssetServlet extends HttpServlet {

    private static final int INIT_RETRY_DELAY_MILLIS = 10;
    private static final int RETRY_MAX_ATTEMPTS = 10;
    private static final int RETRY_PERIOD_MILLIS = 15000;
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;
    public static final String BUCKET_NAME = "comics-cse-308";
    public static final String GCS_DOMAIN = "storage.googleapis.com";

    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(INIT_RETRY_DELAY_MILLIS)
            .retryMaxAttempts(RETRY_MAX_ATTEMPTS)
            .totalRetryPeriodMillis(RETRY_PERIOD_MILLIS)
            .build());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {

            // Grab a reference to the current User.
            User currentUser = User.getCurrentUser();

            // Grab the request tokens.
            String[] tokens = req.getRequestURI().split("/");

            // Request and option parameters.
            GcsFilename resource = null;
            GcsFileOptions options = null;

            // tokens[0] should be empty string.
            // tokens[1] should be "assets".
            AssetRequest assetRequest = AssetRequest.fromString(tokens[2]);
            switch(assetRequest) {

                // /assets/profilepic/{imageName}
                // 0  1         2          3
                case PROFILE_PIC: {

                    // Grab the ID of the user profile pic.
                    String profilePicID = currentUser.getProfilePicID();

                    // Uploading first time - generate new unique ID.
                    if (profilePicID.equals(User.DEFAULT_PROFILE_PIC_ID)) {

                        profilePicID = UUID.randomUUID().toString();
                        currentUser.setProfilePicID(profilePicID);
                        currentUser.saveEntity();

                    }

                    // Construct the resource and the options.
                    resource = new GcsFilename(BUCKET_NAME, profilePicID);
                    options = getOptions(tokens[3]/*imagename*/);

                    // Copy the date from the input stream to the output stream.
                    GcsOutputChannel outputChannel = gcsService.createOrReplace(resource, options);
                    copy(req.getInputStream(), Channels.newOutputStream(outputChannel));

                    // Return a JSON containing the Profile Pic URL.
                    JsonObject json = new JsonObject();
                    json.addProperty(JsonProperty.PROFILE_PIC_URL.toString(), getAssetURL(resource.getObjectName()));

                    // Write out the response.
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(json.toString());

                } break;

                // /assets/comic/{title}/add/{imageName}
                // /assets/comic/{title}/delete/{index}
                // /assets/comic/{title}/save/{index}/{imageName}
                // 0  1      2      3     4      5        6
                case COMIC_FRAME: {

                    // Grab the requested comic.
                    Comic comic = new Comic(currentUser.getGmail(), tokens[3]/*title*/);

                    // Determine which request was made.
                    if(tokens[4].equals("add")) {

                        // Append a new asset to the list of frames and save the entitiy.
                        String newAssetID = UUID.randomUUID().toString();
                        comic.getFrames().add(newAssetID);
                        comic.saveEntity();

                        // Construct the resource and the options.
                        resource = new GcsFilename(BUCKET_NAME, newAssetID);
                        options = GcsFileOptions.getDefaultInstance(); //getOptions(/*new default image*/);

                        // Copy over the asset from the input stream to the output stream.
                        GcsOutputChannel outputChannel = gcsService.createOrReplace(resource, options);
                        copy(req.getInputStream(), Channels.newOutputStream(outputChannel));

                        // Write out the comic data.
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        resp.getWriter().write(comic.toJson().toString());

                    } else if (tokens[4].equals("save")) {

                        // Grab the assetID of the frame to save.
                        int frameIndex = Integer.parseInt(tokens[5]);
                        String assetID = comic.getFrames().get(frameIndex);

                        // Overwrite the
                        resource = new GcsFilename(BUCKET_NAME, assetID);
                        options = GcsFileOptions.getDefaultInstance(); //getOptions(/*new default image*/);

                        GcsOutputChannel outputChannel = gcsService.createOrReplace(resource, options);
                        copy(req.getInputStream(), Channels.newOutputStream(outputChannel));

                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        resp.getWriter().write(comic.toJson().toString());

                    }else if (tokens[4].equals("delete")) {

                        // TODO: Implemented.

                    }

                } break;

            }

        } catch (Exception e) {

            System.err.println(e.getMessage());
            e.printStackTrace();

        }

        System.err.println("Request failed: " + req.getRequestURI());

    }

    private GcsFileOptions getOptions(String filename) {

        try {

            String mime = null;
            String ext = filename.split("\\.", 2)[1];

            if(ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg"))
                mime = "image/jpeg";
            else if(ext.equalsIgnoreCase("png"))
                mime = "image/png";
            else if(ext.equalsIgnoreCase("gif"))
                mime = "image/gif";
            else if(ext.equalsIgnoreCase("bmp"))
                mime = "image/bmp";

            return (new  GcsFileOptions.Builder()).mimeType(mime).build();

        } catch (Exception e) {

            System.err.println("Illegal image upload: " + filename);

        }

        return GcsFileOptions.getDefaultInstance();

    }


    /**
     * Transfer the data from the inputStream to the outputStream. Then close both streams.
     */
    private void copy(InputStream input, OutputStream output) throws IOException {

        try {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);

            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }

        } finally {

            input.close();
            output.close();

        }

    }

    public static String getAssetURL(String assetID) {

        return GCS_DOMAIN + "/" + BUCKET_NAME + "/" + assetID;

    }

}
