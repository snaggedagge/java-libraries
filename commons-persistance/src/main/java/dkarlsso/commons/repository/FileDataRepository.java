package dkarlsso.commons.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Repository used for storing simple things such as settings on the hard drive
 * @param <T>
 */
@Slf4j
public class FileDataRepository <T> {

    private final Class<T> clazz;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String filepath;

    private final Supplier<T> fallbackSupplier;

    public FileDataRepository(final Class<T> clazz,
                              final String filepath,
                              final Supplier<T> fallbackSupplier) {
        this.clazz = clazz;
        this.filepath = filepath;
        this.fallbackSupplier = fallbackSupplier;
    }

    @SneakyThrows
    public T read() {
        try {
            final File settingsFile = new File(filepath);
            try (InputStream is = new FileInputStream(settingsFile)) {
                return objectMapper.readValue(is, clazz);
            }
        }
        catch (final Exception e) {
            log.warn("Could not read settings, will use fallback instead.", e);
        }
        return fallbackSupplier.get();
    }


    public void save(final T settingsData) {
        try {
            final File settingsFile = new File(filepath);
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
            }
            try (FileWriter writer = new FileWriter(filepath)){
                writer.write(objectMapper.writeValueAsString(settingsData));
            }
        }
        catch (final Exception e) {
            log.error("Could not write to file", e);
        }
    }
}
