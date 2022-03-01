package dkarlsso.commons.repository;

public interface IdentifierRetrieval<ObjectType, IdentifierType> {
    IdentifierType getId(ObjectType object);
}
