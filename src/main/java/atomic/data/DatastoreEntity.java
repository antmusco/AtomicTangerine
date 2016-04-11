package atomic.data;

import atomic.crud.CrudResult;
import com.google.appengine.api.datastore.*;

/**
 * Created by Gustavo on 4/7/16.
 */
public abstract class DatastoreEntity {

    /**
     * The kind of entity represented by the object (USER, COMIC, etc.)
     */
    protected final EntityKind entityKind;

    /**
     * Initializes the entity_kind.
     * @param entityKind The type of entity to assign to the  current object.
     */
    protected DatastoreEntity(EntityKind entityKind) {
        this.entityKind = entityKind;
    }

    /**
     * Function which produces a Key for this DatastoreEntity.
     * @return The Key for this DatastoreEntity.
     */
    protected abstract Key generateKey();

    /**
     * Create a new Entity and save it to the datastore.
     * @return The Entity which was created.
     */
    protected Entity createEntity() {

        // Create a key for the current entity kind.
        Key userKey = generateKey();

        try {

            // Retreive the entity from the datastore. If it exists, simply return it.
            Entity entity = retrieveEntity();
            System.err.println("Error: attempt to create entity twice. Returning original.");
            return entity;

        } catch (EntityNotFoundException ex) {

            // If the entity was not found, create it.
            Entity newEntity = new Entity(userKey);
            saveEntity(newEntity);

            // Entity successfully created and placed in the data store.
            return newEntity;

        }

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
    protected void saveEntity(Entity entity) {

        toEntity(entity);
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(entity);

    }

    /**
     * Write the current values of the object to the Entity parameter.
     * @param entity Entity to have properties written to it.
     */
    protected abstract void toEntity(Entity entity);

    /**
     * Read values from an Entity and update this object's state.
     * @param entity
     */
    protected abstract void fromEntity(Entity entity);

}
