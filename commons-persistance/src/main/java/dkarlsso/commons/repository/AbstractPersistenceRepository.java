package dkarlsso.commons.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository used for storing stuff in S3 since I am a cheapskate which dont want to use DBs for simple stuff
 * @param <PersistedType> The datatype being stored
 * @param <IdentifierType> The type which is used as an entities identifier
 */
public abstract class AbstractPersistenceRepository<PersistedType, IdentifierType>
        implements CrudRepository<PersistedType, IdentifierType> {

    private final ObjectMapper mapper;

    private final IdentifierRetrieval<PersistedType, IdentifierType> identifierRetrieval;

    private final JavaType javaType;

    protected AbstractPersistenceRepository(final IdentifierRetrieval<PersistedType, IdentifierType> identifierRetrieval,
                                            final Class<PersistedType> type) {

        this.identifierRetrieval = identifierRetrieval;
        this.mapper = new ObjectMapper();
        this.mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.javaType = mapper.getTypeFactory().
                constructCollectionType(List.class, type);
    }

    protected abstract InputStream readContent();

    protected abstract void saveContent(final String content);

    @Override
    @SneakyThrows
    public List<PersistedType> findAll() {
        try (InputStream is = readContent()) {
            return mapper.readValue(is, javaType);
        }
    }

    @Override
    @SneakyThrows
    public void save(final PersistedType object) {
        List<PersistedType> objects = findAll();
        List<PersistedType> newObjects = objects.stream()
                .filter(existingObject -> !identifierRetrieval.getId(existingObject).equals(identifierRetrieval.getId(object)))
                .collect(Collectors.toList());
        newObjects.add(object);
        saveContent(mapper.writeValueAsString(newObjects));
    }

    @Override
    @SneakyThrows
    public void delete(final IdentifierType identifier) {
        final List<PersistedType> newObjects = findAll().stream()
                .filter(existingObject -> !identifierRetrieval.getId(existingObject).equals(identifier))
                .collect(Collectors.toList());
        saveContent(mapper.writeValueAsString(newObjects));
    }

    @Override
    public PersistedType findById(final IdentifierType identifier) {
        return findAll().stream()
                .filter(existingObject -> identifierRetrieval.getId(existingObject).equals(identifier))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public boolean exists(final IdentifierType identifier) {
        try {
            this.findById(identifier);
            return true;
        }
        catch (final IllegalArgumentException e) {
            return false;
        }
    }
}
