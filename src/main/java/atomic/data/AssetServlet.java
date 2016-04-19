package atomic.data;

import atomic.json.JsonProperty;
import atomic.user.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.api.client.json.Json;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

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
    private static final String BUCKET_NAME = "comics-cse-308";

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

        GcsFilename fileName = getFileName(req.getRequestURI());

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

        GcsFilename filename = getFileName(req.getRequestURI(), new User(service.getCurrentUser().getEmail()));

        GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());
        copy(req.getInputStream(), Channels.newOutputStream(outputChannel));

        JsonObject json = new JsonObject();
        json.addProperty(JsonProperty.PROFILE_PIC_URL.toString(), filename.toString());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json.toString());

    }

    private GcsFilename getFileName(String uri) {

        String[] splits = uri.split("/", 2);
        return new GcsFilename(BUCKET_NAME, splits[1]);

    }

    private GcsFilename getFileName(String uri, User user) {

        String[] splits = uri.split("/", 3);

        if (!splits[0].equals("") || !splits[1].equals("assets")) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /assets/<request>");
        }

        if(splits[2].equals("profilepic")) {

            // Grab the ID of the user profile pic.
            String profilePicID = user.getProfilePicID();

            // Uploading first time - generate new unique ID.
            if(profilePicID.equals(User.DEFAULT_PROFILE_PIC_ID))
                profilePicID = UUID.randomUUID().toString();

            return new GcsFilename(BUCKET_NAME, profilePicID);

        } else if (splits[2].startsWith("comic")) {

            int comicNo = Integer.parseInt(splits[2].split("_", 2)[1]);
            int frameNo = Integer.parseInt(splits[3].split("_", 2)[1]);

            // @TODO: IMPLEMENT.

            return new GcsFilename(BUCKET_NAME, "default-comic");

        } else {

            return new GcsFilename(BUCKET_NAME, "img-not-found");

        }

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

}
