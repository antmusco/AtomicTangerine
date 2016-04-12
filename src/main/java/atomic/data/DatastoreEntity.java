package atomic.data;

import atomic.crud.CrudResult;
import com.google.appengine.api.datastore.*;

/**
 * @author Gustavo Poscidonio
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
     * @return The Key for this DatastoreEntity.
     */
    protected abstract Key generateKey();

    /**
     * Write the current values of the object to the Entity parameter.
     * @returns The entity which was created using the current state of the object.
     */
    public abstract Entity toEntity();

    /**
     * Read values from an Entity and update this object's state.
     * @param entity
     */
    protected abstract void fromEntity(Entity entity);

}
