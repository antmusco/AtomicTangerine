package atomic.data;

import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.*;

import java.util.List;

/**
 * @author Gustavo Poscidonio
 */
public abstract class DatastoreEntity {

    public static final int DEFAULT_QUERY_LIMIT = 10;

    /**
     * The kind of entity represented by the object (USER, COMIC, etc.)
     */
    protected final EntityKind entityKind;

    /**
     * Initializes the entity_kind.
     *
     * @param entityKind The type of entity to assign to the  current object.
     */
    protected DatastoreEntity(EntityKind entityKind) {
        this.entityKind = entityKind;
    }

    /**
     * Retrieves an entity from the datastore. If the entity does not exist, it is created and placed into the
     * datastore.
     *
     * @return The Entity which was retrieved from the datastore.
     * @throws EntityNotFoundException Thrown if the Entity does not exist in the datastore.
     */
    protected Entity retrieveEntity() throws EntityNotFoundException {

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return ds.get(generateKey());

    }

    /**
     * Takes this object and updates it in the datastore. If it does not exist yet in the datastore, it inserts it into
     * the datastore.
     */
    protected void saveEntity() {

        Entity entity = toEntity();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(entity);

    }

    /**
     * Function which produces a Key for this DatastoreEntity.
     *
     * @return The Key for this DatastoreEntity.
     */
    protected Key generateKey() {
        String keyString = this.generateKeyString();
        if(keyString == null || keyString.equals("")) {
            throw new IllegalArgumentException("Could not generate a unique key string based on result of generateKeyString().");
        }
        return KeyFactory.createKey(this.entityKind.toString(), this.generateKeyString());
    }

    /**
     * Function which produces a String which represents a Key for this particular DatastoreEntity.
     *
     * @return The String that represents a Key for this DatastoreEntity.
     */
    protected abstract String generateKeyString();

    /**
     * Write the current values of the object to the Entity parameter.
     *
     * @returns The entity which was created using the current state of the object.
     */
    public abstract Entity toEntity();

    /**
     * Read values from an Entity and update this object's state.
     *
     * @param entity
     */
    protected abstract void fromEntity(Entity entity);

    /**
     * Static function which executes a query on behalf of the Datastore. Limit is set to DEFAULT_QUERY_LIMIT.
     *
     * @param q Query to execute.
     * @return A list of Entities which match the query criteria.
     */
    public static List<Entity> executeQuery(Query q) {

        return executeQuery(q, DEFAULT_QUERY_LIMIT);

    }

    /**
     * Static function which executes a query on behalf of the Datastore.
     *
     * @param q     Query to execute.
     * @param limit Limits the number of Entities returned.
     * @return A list of Entities which match the query criteria.
     */
    public static List<Entity> executeQuery(Query q, int limit) {

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery result = ds.prepare(q);
        return result.asList(FetchOptions.Builder.withLimit(limit));

    }

}
