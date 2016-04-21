package atomic.data;

import atomic.json.JsonProperty;
import atomic.user.User;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
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

    /**
     * Retrieves a file from GCS and returns it in the http response.
     * If the request path is /gcs/Foo/Bar this will be interpreted as
     * a request to read the GCS file named Bar in the bucket Foo.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        GcsFilename fileName = getResource(req.getRequestURI());

        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
        copy(Channels.newInputStream(readChannel), resp.getOutputStream());

    }

    /**
     * Writes the payload of the incoming post as the contents of a file to GCS.
     * If the request path is /gcs/Foo/Bar this will be interpreted as
     * a request to create a GCS file named Bar in bucket Foo.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // Grab the UserService.
        UserService service = UserServiceFactory.getUserService();

        if(service.getCurrentUser() == null) {
            return;
        }

        String uri = req.getRequestURI();

        GcsFilename resource = getResource(uri, new User(service.getCurrentUser().getEmail()));
        GcsFileOptions options = getOptions(uri);

        GcsOutputChannel outputChannel = gcsService.createOrReplace(resource, options);
        copy(req.getInputStream(), Channels.newOutputStream(outputChannel));

        JsonObject json = new JsonObject();
        json.addProperty(JsonProperty.PROFILE_PIC_URL.toString(), getAssetURL(resource.getObjectName()));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json.toString());

    }

    private GcsFilename getResource(String uri) {

        // POSSIBLE FORMATS:
        // /assets/{assetID}

        try {

            String[] splits = uri.split("/", 2);
            return new GcsFilename(BUCKET_NAME, splits[1]);

        } catch (IndexOutOfBoundsException | NullPointerException ex) {

            System.err.println("Illegal format for retrieve: " + uri);
            ex.printStackTrace();

        }

        return new GcsFilename(BUCKET_NAME, "img-not-found");

    }

    private GcsFilename getResource(String uri, User user) {

        // POSSIBLE FORMATS:
        // /assets/profilepic
        // /assets/comic_{comicID}/frame_{frameID}

        // Assume valid format and catch exception otherwise.
        try {

            String[] tokens = uri.split("/", 4);

            if (!tokens[0].equals("") || !tokens[1].equals("assets")) {
                throw new IllegalArgumentException("The URL is not formed as expected. " +
                        "Expecting /assets/<request>");
            }

            if (tokens[2].equals("profilepic")) {

                // Grab the ID of the user profile pic.
                String profilePicID = user.getProfilePicID();

                // Uploading first time - generate new unique ID.
                if (profilePicID.equals(User.DEFAULT_PROFILE_PIC_ID)) {
                    profilePicID = UUID.randomUUID().toString();
                    user.setProfilePicID(profilePicID);
                    user.saveEntity();
                }

                return new GcsFilename(BUCKET_NAME, profilePicID);

            } else if (tokens[2].startsWith("comic")) {

                int comicNo = Integer.parseInt(tokens[2].split("_", 2)[1]);
                int frameNo = Integer.parseInt(tokens[3].split("_", 2)[1]);

                // @TODO: IMPLEMENT.

                return new GcsFilename(BUCKET_NAME, "default-comic");

            }

        } catch (IndexOutOfBoundsException | NullPointerException ex) {

            System.err.println("Illegal format for upload request by " + user.getGmail() + ": " + uri);
            ex.printStackTrace();

        }

        return new GcsFilename(BUCKET_NAME, "img-not-found");

    }

    private GcsFileOptions getOptions(String uri) {

        try {

            String mime = null;
            String ext = uri.split("\\.", 2)[1];

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

            System.err.println("Illegal image upload: " + uri);

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
