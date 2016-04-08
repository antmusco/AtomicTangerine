package atomic.data;

/**
 * Created by Gustavo on 4/7/16.
 */
public enum EntityKind {

    USER("User"),
    COMIC("Comic");

    /**
     * The string representation of the kind of entity.
     */
    private String entityKindString;

    /**
     * Constructor to initialize the entity kind string.
     * @param entityKindString The name of the entity to initialize.
     */
    EntityKind(String entityKindString) { this.entityKindString = entityKindString; }

    /**
     * @return The string representation of the entity kind.
     */
    public String toString() {
        return this.entityKindString;
    }
}
