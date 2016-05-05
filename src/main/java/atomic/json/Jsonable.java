package atomic.json;

import com.google.gson.JsonObject;

/**
 * Interface forcing an object to implement the `toJson` and `fromJson` methods.
 *
 * @author Anthony
 */
public interface Jsonable {

    /**
     * Serializes the object to it's JSON representation.
     *
     * @return A JsonElement containing the content of this object as a String.
     */
    JsonObject toJson();

    /**
     * Deserializes an object from a String which contains the JSON representation of this object.
     *
     * @param obj The JsonElement containing the JSON representation of this object.
     */
    void fromJson(JsonObject obj) throws NoUniqueKeyException;

}
