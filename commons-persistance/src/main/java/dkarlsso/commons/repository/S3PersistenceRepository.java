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

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository used for storing stuff in S3 since I am a cheapskate which dont want to use DBs for simple stuff
 * @param <PersistedType> The datatype being stored
 * @param <IdentifierType> The type which is used as an entities identifier
 */
public abstract class S3PersistenceRepository<PersistedType, IdentifierType> {

    private S3Client client = S3Client.create();

    private final ObjectMapper mapper;

    private final String keyName;

    private final String bucket;

    private final IdentifierRetrieval<PersistedType, IdentifierType> identifierRetrieval;

    private final JavaType javaType;

    protected S3PersistenceRepository(final String keyName,
                                      final String bucket,
                                      final IdentifierRetrieval<PersistedType, IdentifierType> identifierRetrieval,
                                      final Class<PersistedType> type) {
        this.keyName = keyName;
        this.bucket = bucket;
        this.identifierRetrieval = identifierRetrieval;
        try {
            client.headObject(HeadObjectRequest.builder()
                    .key(keyName)
                    .bucket(bucket)
                    .build());
        }
        catch (final NoSuchKeyException e) {
            // If object doesnt exist, create a new empty list
            client.putObject(PutObjectRequest.builder()
                    .key(keyName)
                    .bucket(bucket)
                    .build(), RequestBody.fromString("[]"));
        }
        this.mapper = new ObjectMapper();
        this.mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.javaType = mapper.getTypeFactory().
                constructCollectionType(List.class, type);
    }

    @SneakyThrows
    public List<PersistedType> findAll() {
        final InputStream is = client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(keyName)
                .build());
        return mapper.readValue(is, javaType);
    }


    @SneakyThrows
    public void save(final PersistedType object) {
        List<PersistedType> objects = findAll();
        List<PersistedType> newObjects = objects.stream()
                .filter(existingObject -> !identifierRetrieval.getId(existingObject).equals(identifierRetrieval.getId(object)))
                .collect(Collectors.toList());
        newObjects.add(object);
        client.putObject(PutObjectRequest.builder()
                .key(keyName)
                .bucket(bucket)
                .build(), RequestBody.fromString(mapper.writeValueAsString(newObjects)));
    }

    @SneakyThrows
    public void delete(final IdentifierType identifier) {
        final List<PersistedType> newObjects = findAll().stream()
                .filter(existingObject -> !identifierRetrieval.getId(existingObject).equals(identifier))
                .collect(Collectors.toList());
        client.putObject(PutObjectRequest.builder()
                .key(keyName)
                .bucket(bucket)
                .build(), RequestBody.fromString(mapper.writeValueAsString(newObjects)));
    }

    public PersistedType findById(final IdentifierType identifier) {
        return findAll().stream()
                .filter(existingObject -> identifierRetrieval.getId(existingObject).equals(identifier))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    };

    public boolean exists(final IdentifierType identifier) {
        try {
            this.findById(identifier);
            return true;
        }
        catch (final IllegalArgumentException e) {
            return false;
        }
    };
}
