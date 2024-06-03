package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author youyihj
 */
public enum MCPReobfuscation {
    INSTANCE;

    private final Multimap<String, String> methodMap = HashMultimap.create();
    private final Multimap<String, String> fieldMap = HashMultimap.create();
    private final AtomicInteger initStatus = new AtomicInteger(UNINITIALIZED);
    private static final int UNINITIALIZED = 0;
    private static final int INITIALIZING = 1;
    private static final int INITIALIZED = 2;

    public void init() {
        if (initStatus.compareAndSet(UNINITIALIZED, INITIALIZING)) {
            new Thread(this::initLogic).start();
        }
    }

    public Optional<Field> reobfField(Class<?> owner, String name) {
        waitingToInit();
        Collection<String> possibleNames = fieldMap.get(name);
        for (String possibleSrgName : possibleNames) {
            try {
                return Optional.of(owner.getField(possibleSrgName));
            } catch (NoSuchFieldException ignored) {}
        }
        try {
            return Optional.of(owner.getField(name));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    public Stream<Method> reobfMethod(Class<?> owner, String name) {
        waitingToInit();
        Collection<String> possibleNames = ImmutableList.<String>builder().addAll(methodMap.get(name)).add(name).build();
        return Arrays.stream(owner.getMethods())
                     .filter(it -> possibleNames.contains(it.getName()));

    }

    private void waitingToInit() {
        while (initStatus.get() != INITIALIZED) {
            init();
        }
    }

    private void initLogic() {
        Path localMapping = Paths.get("config", "mcp_stable-39-1.12.zip");
        String remoteMapping = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_stable/39-1.12/mcp_stable-39-1.12.zip";
        if (!Files.exists(localMapping)) {
            try (
                    CloseableHttpClient client =
                            HttpClientBuilder.create()
                                             .setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(10000).build())
                                             .build()
            ) {
                client.execute(new HttpGet(remoteMapping), response -> {
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        Files.copy(response.getEntity().getContent(), localMapping);
                    } else {
                        throw new IOException(response.getStatusLine().toString());
                    }
                    return null;
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to download mcp mapping, try download it manually to `config/mcp_stable-39-1.12.zip`, uri: " + remoteMapping, e);
            }
        }
        try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:file:" + localMapping.toUri().getPath()), Collections.emptyMap())) {
            try (Stream<String> methodStream = Files.lines(zipFs.getPath("methods.csv"))) {
                methodStream.skip(1)
                            .map(it -> it.split(","))
                            .forEach(it -> methodMap.put(it[1], it[0]));
            }
            try (Stream<String> methodStream = Files.lines(zipFs.getPath("fields.csv"))) {
                methodStream.skip(1)
                            .map(it -> it.split(","))
                            .forEach(it -> fieldMap.put(it[1], it[0]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read mcp mapping", e);
        }
        initStatus.compareAndSet(INITIALIZING, INITIALIZED);
    }
}
