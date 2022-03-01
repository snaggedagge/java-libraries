package dkarlsso.commons.repository;

import java.util.List;

/**
 * Repository interface for dealing with simple CRUD operations for a particular resource.
 * @param <PersistedType> The datatype of the resource being stored
 * @param <IdentifierType> The type which is used as a resource entity identifier
 */
public interface CrudRepository<PersistedType, IdentifierType> {

    /**
     * Retrieves all entities
     * @return List<PersistedType>
     */
    List<PersistedType> findAll();

    /**
     * Saves a particular entity. Updates the entity if it already exists
     * @param object object
     */
    public void save(final PersistedType object);

    /**
     * Deletes a particular entity given a certain identifier
     * @param identifier identifier
     */
    public void delete(final IdentifierType identifier);

    /**
     * Returns a single entity
     * @param identifier identifier
     * @return PersistedType
     */
    public PersistedType findById(final IdentifierType identifier);

    /**
     * Checks if a entity exists
     * @param identifier identifier
     * @return true if exists
     */
    public boolean exists(final IdentifierType identifier);
}
