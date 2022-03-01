package dkarlsso.commons.repository;

/**
 * Class used for identifying how to get the id of a certain type {@link IdentifierType}
 * from a class of type {@link ObjectType}
 * @param <ObjectType>
 * @param <IdentifierType>
 */
public interface IdentifierRetrieval<ObjectType, IdentifierType> {
    IdentifierType getId(ObjectType object);
}
