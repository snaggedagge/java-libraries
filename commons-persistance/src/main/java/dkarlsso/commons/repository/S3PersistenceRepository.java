package dkarlsso.commons.repository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

/**
 * Repository used for storing stuff in S3 since I am a cheapskate which dont want to use DBs for simple stuff
 * @param <PersistedType> The datatype being stored
 * @param <IdentifierType> The type which is used as an entities identifier
 */
public class S3PersistenceRepository<PersistedType, IdentifierType>
        extends AbstractPersistenceRepository<PersistedType, IdentifierType> {

    private S3Client client = S3Client.create();

    private final String keyName;

    private final String bucket;

    public S3PersistenceRepository(final String keyName,
                                      final String bucket,
                                      final IdentifierRetrieval<PersistedType, IdentifierType> identifierRetrieval,
                                      final Class<PersistedType> type) {
        super(identifierRetrieval, type);
        this.keyName = keyName;
        this.bucket = bucket;
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
    }

    @Override
    protected InputStream readContent() {
        return client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(keyName)
                .build());
    }

    @Override
    protected void saveContent(String content) {
        client.putObject(PutObjectRequest.builder()
                .key(keyName)
                .bucket(bucket)
                .build(), RequestBody.fromString(content));
    }
}
