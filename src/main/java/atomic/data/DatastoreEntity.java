package atomic.data;

/**
 * Created by Freya on 4/7/16.
 */
public abstract class DatastoreEntity {
    protected final String ENTITY_KIND;

    protected DatastoreEntity(String entity_kind) {
        ENTITY_KIND = entity_kind;
    }

    protected abstract void putEntityIntoDatastore();

    protected abstract void saveEntity();
}
