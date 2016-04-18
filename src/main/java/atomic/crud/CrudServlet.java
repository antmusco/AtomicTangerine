package atomic.crud;

// GSON library.
import atomic.json.JsonProperty;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Java Servlet library.
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// I/O library.
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Primary servlet for entities which can be created, destroyed, updated, and deleted - the primary operations for
 * CRUD content on the web. This class provides the basic implementation of the `doGet()`, `doPost()`, `doPut(), and
 * `doDelete()` methods for the `HttpServlet` class, and instead provides wrapper methods for extending classes to
 * implement. These include 'create()`, `remove()`, `update()`, and `destroy()`.
 *
 * @author Anthony G. Musco
 */
public abstract class CrudServlet extends HttpServlet {

    /**
     * Static parsing object used to construct JsonElement objects from JSON strings.
     */
    private static JsonParser jsonParser = new JsonParser();

    /**
     * Binding point for an HTTP 'get' request, which should be used to retrieve content from the server.
     *
     * @param request  HttpResponse from a client wishing to perform a `retrieve` operation from the back-end server.
     * @param response HttpResponse to be returned to the caller.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read input from the request object.
        JsonElement input = readJsonFromRequest(request);
        System.out.println("RECV-GET: " + input.toString());
        // Perform the operation using the input and generat the output.
        JsonElement output = retrieve(input);

        // Write the output to response.
        writeJsonToResponse(output, response);
        System.out.println("SEND-GET: " + output.toString());
    }

    /**
     * Binding point for an HTTP 'post' request, which should be used to update content from the server, or to
     * retrieve sensitive data.
     *
     * @param request  HttpResponse from a client wishing to perform a `retrieve` operation from the back-end server.
     * @param response HttpResponse to be returned to the caller.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read input from the request object.
        JsonElement input = readJsonFromRequest(request);
        System.out.println("RECV-POST: " + input.toString());

        // Perform the operation using the input and generat the output.
        JsonElement output = update(input);

        // Write the output to response.
        writeJsonToResponse(output, response);
        System.out.println("SEND-POST: " + output.toString());

    }

    /**
     * Binding point for an HTTP 'put' request, which should be used to create content on the server.
     *
     * @param request  HttpResponse from a client wishing to perform a `retrieve` operation from the back-end server.
     * @param response HttpResponse to be returned to the caller.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read input from the request object.
        JsonElement input = readJsonFromRequest(request);
        System.out.println("RECV-PUT: " + input.toString());

        // Perform the operation using the input and generat the output.
        JsonElement output = create(input);

        // Write the output to response.
        writeJsonToResponse(output, response);
        System.out.println("SEND-PUT: " + output.toString());

    }

    /**
     * Binding point for an HTTP 'delete' request, which should be used to delete content from the server.
     *
     * @param request  HttpResponse from a client wishing to perform a `retrieve` operation from the back-end server.
     * @param response HttpResponse to be returned to the caller.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read input from the request object.
        JsonElement input = readJsonFromRequest(request);
        System.out.println("RECV-DELETE: " + input.toString());

        // Perform the operation using the input and generat the output.
        JsonElement output = delete(input);

        // Write the output to response.
        writeJsonToResponse(output, response);
        System.out.println("SEND-DELETE: " + output.toString());

    }

    /**
     * Reads, parses, and returns a JsonElement from an HttpRequest. If an IO exception occurs, the error is logged and
     * null is returned to the caller.
     *
     * @param request HttpRequest containing a reader used to construct the JSON string.
     * @return The constructed JSON object if it could be parsed successfully, otherwise, null.
     */
    protected JsonElement readJsonFromRequest(HttpServletRequest request) {

        try {

            // Construct the JSON string from the request reader.
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Parse the JSON string into a JsonElement.
            return jsonParser.parse(builder.toString());

        // Exception handling.
        } catch (JsonIOException jioe) {

            System.err.println("The retrieved request did not follow valid JSON syntax.");

        } catch (IOException ioe) {

            System.err.println("An IO exception occurred while trying to parse JSON request.");

        }

        // If an error occurred, return null.
        return null;

    }

    /**
     * Simply writes the parameter JsonElement to the indicated HttpResponse.
     *
     * @param json The JsonElement tree to write as a response.
     * @param response The HttpResponse which will received the Json as a string.
     */
    protected void writeJsonToResponse(JsonElement json, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());

    }

    /**
     * Creates a JsonObject indicating that the operation is unsupported.
     * @return A JsonObject with a single field: "RESULT" : "UNSUPPORTED".
     */
    protected JsonElement unsupportedRequest() {

        JsonObject response = new JsonObject();
        response.addProperty(JsonProperty.RESULT.toString(), CrudResult.UNSUPPORTED.toString());
        return response;

    }

    /**
     * Creates a JsonObject indicating that the operation completed successfully.
     * @return A JsonObject with a single field: "RESULT" : "SUCCESS".
     */
    protected JsonElement successfulRequest() {

        JsonObject response = new JsonObject();
        response.addProperty(JsonProperty.RESULT.toString(), CrudResult.SUCCESS.toString());
        return response;

    }

    /**
     * Creates a JsonObject indicating that the operation failed.
     * @return A JsonObject with a single field: "RESULT" : "FAILED".
     */
    protected JsonElement failedRequest() {

        JsonObject response = new JsonObject();
        response.addProperty(JsonProperty.RESULT.toString(), CrudResult.FAILURE.toString());
        return response;

    }

    protected abstract JsonElement create(JsonElement json);

    protected abstract JsonElement retrieve(JsonElement json);

    protected abstract JsonElement update(JsonElement json);

    protected abstract JsonElement delete(JsonElement json);


}
