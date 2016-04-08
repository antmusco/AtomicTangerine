package atomic.data;

/**
 * Created by Gustavo on 4/7/16.
 */
public abstract class DatastoreEntity {
    /**
     * The kind of entity represented by the object (USER, COMIC, etc.)
     */
    protected final EntityKind ENTITY_KIND;

    /**
     * Initializes the entity_kind.
     * @param entity_kind The type of entity to assign to the  current object.
     */
    protected DatastoreEntity(EntityKind entity_kind) {
        ENTITY_KIND = entity_kind;
    }

    /**
     * Takes the this object and attempts to place it into the datastore.
     */
    protected abstract void putEntityIntoDatastore();

    /**
     * Takes this object and updates it in the datastore. If it does not exist
     * yet in the datastore, it inserts it into the datastore;
     */
    protected abstract void saveEntity();
}
