package atomic.data;

import atomic.json.JsonProperty;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Gustavo on 4/7/16.
 */
public enum EntityKind {

    USER(JsonProperty.USER.toString()),
    COMIC(JsonProperty.COMIC.toString()),
    COMMENT(JsonProperty.COMMENT.toString()),
    PREFERENCES(JsonProperty.PREFERENCES.toString());

    /**
     * The string representation of the kind of entity.
     */
    private String entityKindString;
    private static Map<String, EntityKind> entityMap = new TreeMap<>();

    static {
        for (EntityKind ek : EntityKind.values()) {
            entityMap.put(ek.toString(), ek);
        }
    }

    /**
     * Constructor to initialize the entity kind string.
     *
     * @param entityKindString The name of the entity to initialize.
     */
    EntityKind(String entityKindString) {
        this.entityKindString = entityKindString;
    }

    /**
     * Converts a String parameter to an EntityKind enumeration value.
     *
     * @param entityKind The String to convert to an EntityKind.
     * @return The EntityKind represented by the String.
     * @throws IllegalArgumentException If the indicated String could not be matched to an EntityKind.
     */
    public static EntityKind fromString(String entityKind) {

        if (entityMap.containsKey(entityKind)) {
            return entityMap.get(entityKind);
        } else {
            throw new IllegalArgumentException("Entity Kind not found: " + entityKind);
        }

    }

    /**
     * @return The string representation of the entity kind.
     */
    public String toString() {
        return this.entityKindString;
    }
}
