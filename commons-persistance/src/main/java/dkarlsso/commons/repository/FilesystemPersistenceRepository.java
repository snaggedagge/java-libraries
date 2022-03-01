package dkarlsso.commons.repository;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

/**
 * Repository used for storing stuff in S3 since I am a cheapskate which dont want to use DBs for simple stuff
 * @param <PersistedType> The datatype being stored
 * @param <IdentifierType> The type which is used as an entities identifier
 */
public class FilesystemPersistenceRepository<PersistedType, IdentifierType>
        extends AbstractPersistenceRepository<PersistedType, IdentifierType> {

    private final String filepath;

    @SneakyThrows
    public FilesystemPersistenceRepository(final String filepath,
                                              final IdentifierRetrieval<PersistedType, IdentifierType> identifierRetrieval,
                                              final Class<PersistedType> type) {
        super(identifierRetrieval, type);
        this.filepath = filepath;
        final File settingsFile = new File(filepath);
        if (!settingsFile.exists()) {
            settingsFile.createNewFile();
            saveContent("[]");
        }
    }

    @SneakyThrows
    @Override
    protected InputStream readContent() {
        final File settingsFile = new File(filepath);
        return new FileInputStream(settingsFile);
    }

    @SneakyThrows
    @Override
    protected void saveContent(final String content) {
        try (FileWriter writer = new FileWriter(filepath)){
            writer.write(content);
        }
    }
}
