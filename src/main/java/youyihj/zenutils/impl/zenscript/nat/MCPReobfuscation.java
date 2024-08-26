package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.Pair;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @author youyihj
 */
public enum MCPReobfuscation {
    INSTANCE;

    private final CompletableFuture<Pair<Multimap<String, String>, Multimap<String, String>>> mappers = CompletableFuture.supplyAsync(this::init);

    public FieldData reobfField(ClassData owner, String name) {
        Collection<String> possibleNames = mappers.join().getRight().get(name);
        List<FieldData> fields = owner.fields(true);
        for (String possibleSrgName : possibleNames) {
            try {
                return findField(fields, possibleSrgName);
            } catch (NoSuchFieldException ignored) {}
        }
        try {
            return findField(fields, name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public Stream<ExecutableData> reobfMethodOverloads(ClassData owner, String name) {
        Collection<String> possibleNames = ImmutableList.<String>builder().addAll(mappers.join().getLeft().get(name)).add(name).build();
        return owner.methods(true).stream()
                     .filter(it -> possibleNames.contains(it.name()));

    }

    public ExecutableData reobfMethod(ClassData owner, String name) {
        return reobfMethodOverloads(owner, name).findFirst().orElse(null);
    }

    private FieldData findField(List<FieldData> fields, String name) throws NoSuchFieldException {
        for (FieldData field : fields) {
            if (field.name().equals(name)) {
                return field;
            }
        }
        throw new NoSuchFieldException(name);
    }

    private Pair<Multimap<String, String>, Multimap<String, String>> init() {
        Multimap<String, String> methodMap = HashMultimap.create();
        Multimap<String, String> fieldMap = HashMultimap.create();
        Path localMapping = Paths.get("config", "mcp_stable-39-1.12.zip");
        String remoteMapping = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_stable/39-1.12/mcp_stable-39-1.12.zip";
        if (!Files.exists(localMapping)) {
            try {
                URL url = new URL(remoteMapping);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);
                Files.copy(url.openStream(), localMapping);
            } catch (IOException e) {
                throw new RuntimeException("Failed to download mcp mapping, try download it manually to `config/mcp_stable-39-1.12.zip`, uri: " + remoteMapping, e);
            }
        }
        try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:file:" + localMapping.toUri().getPath().replace(" ", "%20")), Collections.emptyMap())) {
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
        return Pair.of(methodMap, fieldMap);
    }
}
