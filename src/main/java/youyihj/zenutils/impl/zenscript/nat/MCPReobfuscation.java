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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * @author youyihj
 */
public enum MCPReobfuscation {
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    private final CompletableFuture<Pair<Multimap<String, String>, Multimap<String, String>>> mappers = CompletableFuture.supplyAsync(this::unzip);

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

    private Pair<Multimap<String, String>, Multimap<String, String>> unzip() {
        long startTime = System.currentTimeMillis();

        Multimap<String, String> methodMap = HashMultimap.create();
        Multimap<String, String> fieldMap = HashMultimap.create();

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(Objects.requireNonNull(this.getClass().getResourceAsStream("/mapping")))))) {
            int prevSrg = dis.readInt();
            addEntry(methodMap, dis, prevSrg, true);
            short offset;
            while ((offset = dis.readShort()) != -1) {
                prevSrg += offset;
                addEntry(methodMap, dis, prevSrg, true);
            }

            prevSrg = dis.readInt();
            addEntry(fieldMap, dis, prevSrg, false);
            while ((offset = dis.readShort()) != -1) {
                prevSrg += offset;
                addEntry(fieldMap, dis, prevSrg, false);
            }
        } catch (EOFException ignored) {

        } catch (IOException e) {
            throw new RuntimeException("Failed to read mcp mapping", e);
        }

        long endTime = System.currentTimeMillis();
        LOGGER.info("MCP mapping unzip took {} ms", (endTime - startTime));
        return Pair.of(methodMap, fieldMap);
    }

    private void addEntry(Multimap<String, String> map, DataInputStream dis, int srgId, boolean isMethod) throws IOException {
        String notchName;
        int ch1 = dis.read();
        int ch2 = dis.read();
        if (ch1 == 0) {
            notchName = String.valueOf((char) ch2);
        } else {
            notchName = "" + (char) ch1 + (char) ch2;
        }
        int deobfNameLen = dis.read();
        byte[] deobfNameBytes = new byte[deobfNameLen];
        dis.readFully(deobfNameBytes);
        String deobfName = StandardCharsets.US_ASCII.newDecoder().decode(ByteBuffer.wrap(deobfNameBytes)).toString();
        map.put(deobfName, (isMethod ? "func_" : "field_") + srgId + "_" + notchName);
    }
}
