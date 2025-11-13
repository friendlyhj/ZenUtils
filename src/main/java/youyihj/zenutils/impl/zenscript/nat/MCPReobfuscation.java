package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.LookupRequester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.security.MessageDigest;
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

    private static final int MAX_RETRY = 10;
    private static final Logger LOGGER = LogManager.getLogger();

    private final CompletableFuture<Pair<Multimap<String, String>, Multimap<String, String>>> mappers = CompletableFuture.supplyAsync(this::init);

    public FieldData reobfField(ClassData owner, String name, LookupRequester requester) {
        Collection<String> possibleNames = mappers.join().getRight().get(name);
        List<FieldData> fields = owner.fields(requester);
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

    public Stream<ExecutableData> reobfMethodOverloads(ClassData owner, String name, LookupRequester requester) {
        Collection<String> possibleNames = ImmutableList.<String>builder().addAll(mappers.join().getLeft().get(name)).add(name).build();
        return owner.methods(requester).stream()
                     .filter(it -> possibleNames.contains(it.name()));
    }

    public ExecutableData reobfMethod(ClassData owner, String name, LookupRequester requester) {
        return reobfMethodOverloads(owner, name, requester).findFirst().orElse(null);
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
                downloadWithCheck(remoteMapping, remoteMapping + ".sha1", localMapping);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download mcp mapping, try download it manually to `config/mcp_stable-39-1.12.zip`, uri: " + remoteMapping, e);
            }
        }
        try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:" + localMapping.toUri().toASCIIString()), Collections.emptyMap())) {
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

    private static void downloadWithCheck(String fileUrl, String sha1Url, Path dest) throws Exception {
        String expectedSha1 = downloadSha1(sha1Url);

        for (int i = 1; i <= MAX_RETRY; i++) {
            LOGGER.info("{} attempts to download file", i);
            try {
                downloadFile(fileUrl, dest);
            } catch (IOException e) {
                LOGGER.error("Download failed: {}", e.getMessage());
                continue;
            }
            String actualSha1 = calcSha1(dest);
            if (expectedSha1.equalsIgnoreCase(actualSha1)) {
                return;
            } else {
                LOGGER.error("SHA-1 Verify Error: expected: {}, actual: {}", expectedSha1, actualSha1);
                Files.deleteIfExists(dest);
            }
        }
        throw new IOException("Exceeded maximum retry attempts to download file: " + fileUrl);
    }

    private static String downloadSha1(String sha1Url) throws IOException {
        URLConnection connection = new URL(sha1Url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        try (InputStream in = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                throw new IOException("Empty sha1 file: " + sha1Url);
            }
            return line.split("\\s+")[0].trim();
        }
    }

    private static void downloadFile(String fileUrl, Path dest) throws IOException {
        URLConnection connection = new URL(fileUrl).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        try (InputStream in = connection.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String calcSha1(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream fis = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
