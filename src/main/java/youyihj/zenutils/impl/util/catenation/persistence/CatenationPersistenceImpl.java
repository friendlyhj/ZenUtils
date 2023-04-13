package youyihj.zenutils.impl.util.catenation.persistence;

import com.google.common.collect.ImmutableMap;
import crafttweaker.api.data.*;
import crafttweaker.api.world.IWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.api.util.catenation.CatenationContext;
import youyihj.zenutils.api.util.catenation.CatenationStatus;
import youyihj.zenutils.api.util.catenation.ICatenationTask;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationFactory;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;
import youyihj.zenutils.api.world.ZenUtilsWorld;

import java.util.*;

/**
 * @author youyihj
 */
public class CatenationPersistenceImpl {
    private static final Map<String, Entry> persistData = new HashMap<>();
    private static final Set<Catenation> waitingCatenation = new HashSet<>();

    public static final IForgeRegistry<ICatenationObjectHolder.TypeRegistryEntry> TYPE_REGISTRY = GameRegistry.findRegistry(ICatenationObjectHolder.TypeRegistryEntry.class);

    public static void registerPersistCatenation(String key, ICatenationFactory catenationFactory, Map<String, ICatenationObjectHolder.Type<?>> objectHolderTypes) {
        persistData.put(key, new Entry(catenationFactory, ImmutableMap.copyOf(objectHolderTypes)));
    }

    @SuppressWarnings("unchecked")
    public static Catenation startCatenation(IWorld world, String key, CatenationPersistedObjects objects) {
        if (!persistData.containsKey(key)) {
            throw new IllegalArgumentException("No such persisted catenation registered: " + key);
        }
        Entry entry = persistData.get(key);
        entry.verify(objects);
        Catenation catenation = entry.getCatenationFactory().get(world);
        Map<ICatenationObjectHolder.Key<?>, ICatenationObjectHolder<?>> objectHolders = catenation.getContext().getObjectHolders();
        objects.getObjects().forEach((objKey, obj) -> {
            ICatenationObjectHolder<Object> holder = (ICatenationObjectHolder<Object>) objKey.getType().createHolder();
            holder.setValue(obj);
            objectHolders.put(objKey, holder);
        });
        catenation.setPersistenceKey(key);
        return catenation;
    }

    public static IData serialize(Catenation catenation) {
        CatenationContext context = catenation.getContext();
        Map<String, IData> total = new HashMap<>();

        total.put("key", new DataString(catenation.getPersistenceKey()));

        Queue<ICatenationTask> tasks = catenation.getTasks();
        total.put("taskCount", new DataInt(tasks.size()));
        total.put("taskData", tasks.element().serializeToData());

        if (context.hasData()) {
            total.put("data", context.getData());
        }

        Map<String, IData> objData = new HashMap<>();
        Map<ICatenationObjectHolder.Key<?>, ICatenationObjectHolder<?>> objectHolders = context.getObjectHolders();
        objectHolders.forEach((key, objHolder) -> {
            Map<String, IData> objDataSingle = new HashMap<>();
            objDataSingle.put("type", new DataString(Objects.requireNonNull(objHolder.getType().getRegistryEntry().getRegistryName()).toString()));
            objDataSingle.put("value", objHolder.serializeToData());
            objData.put(key.getKey(), new DataMap(objDataSingle, true));
        });
        total.put("objects", new DataMap(objData, true));

        return new DataMap(total, true);
    }

    public static Catenation deserialize(IData data, IWorld world) {
        String persistKey = data.memberGet("key").asString();
        Catenation catenation = persistData.get(persistKey).getCatenationFactory().get(world);
        catenation.getContext().setStatus(CatenationStatus.SERIAL, world);
        catenation.setPersistenceKey(persistKey);

        Queue<ICatenationTask> tasks = catenation.getTasks();
        int toRemoveTasks = tasks.size() - data.memberGet("taskCount").asInt();
        for (int i = 0; i < toRemoveTasks; i++) {
            tasks.poll();
        }
        tasks.element().deserializeFromData(data.memberGet("taskData"));
        if (tasks.element().isComplete()) {
            tasks.poll();
        }

        if (data.contains(new DataString("data"))) {
            catenation.getContext().setData(data.memberGet("data"));
        }

        Map<String, IData> objData = data.memberGet("objects").asMap();
        Map<ICatenationObjectHolder.Key<?>, ICatenationObjectHolder<?>> objectHolders = catenation.getContext().getObjectHolders();
        objData.forEach((key, value) -> {
            ResourceLocation type = new ResourceLocation(value.memberGet("type").asString());
            if (TYPE_REGISTRY.containsKey(type)) {
                ICatenationObjectHolder.Type<?> holderType = Objects.requireNonNull(TYPE_REGISTRY.getValue(type)).getType();
                ICatenationObjectHolder.Key<?> holderKey = ICatenationObjectHolder.Key.of(key, holderType);
                ICatenationObjectHolder<?> holder = holderType.createHolder();
                holder.deserializeFromData(value.memberGet("value"));
                objectHolders.put(holderKey, holder);
            }
        });

        return catenation;
    }

    public static void onWorldLoad(IWorld world) {
        IData catenationsData = ZenUtilsWorld.getCustomWorldData(world).memberGet("catenations");
        if (catenationsData == null) return;
        for (IData catenationData : catenationsData.asList()) {
            Catenation catenation = deserialize(catenationData, world);
            if (catenation.isAllObjectsValid()) {
                catenation.getContext().setStatus(CatenationStatus.WORKING, world);
            } else {
                addWaitingCatenation(catenation);
            }
        }
    }

    public static void onWorldSave(IWorld world, List<Catenation> unfinished) {
        List<IData> catenationDataList = new ArrayList<>();
        for (Catenation catenation : unfinished) {
            if (catenation.getPersistenceKey() != null) {
                catenationDataList.add(serialize(catenation));
            }
        }
        ZenUtilsWorld.getCustomWorldData(world).memberSet("catenations", new DataList(catenationDataList, true));
    }

    @SuppressWarnings("unchecked")
    public static <T> void receiveObject(ICatenationObjectHolder.Type<T> type, T object) {
        Iterator<Catenation> iterator = waitingCatenation.iterator();
        while (iterator.hasNext()) {
            Catenation catenation = iterator.next();
            for (Map.Entry<ICatenationObjectHolder.Key<?>, ICatenationObjectHolder<?>> holderEntry : catenation.getContext().getObjectHolders().entrySet()) {
                if (type.equals(holderEntry.getKey().getType())) {
                    ((ICatenationObjectHolder<Object>) holderEntry.getValue()).receiveObject(object);
                }
            }
            if (catenation.isAllObjectsValid()) {
                catenation.getContext().setStatus(CatenationStatus.WORKING, catenation.getWorld());
                iterator.remove();
            }
        }
    }

    public static void addWaitingCatenation(Catenation catenation) {
        waitingCatenation.add(catenation);
    }

    public static class Entry {
        private final ICatenationFactory catenationFactory;
        private final Map<String, ICatenationObjectHolder.Type<?>> objectHolderTypes;

        public Entry(ICatenationFactory catenationFactory, Map<String, ICatenationObjectHolder.Type<?>> objectHolderTypes) {
            this.catenationFactory = catenationFactory;
            this.objectHolderTypes = objectHolderTypes;
        }

        public ICatenationFactory getCatenationFactory() {
            return catenationFactory;
        }

        public void verify(CatenationPersistedObjects objects) {
            Set<ICatenationObjectHolder.Key<?>> dataKeys = objects.getObjects().keySet();
            for (Map.Entry<String, ICatenationObjectHolder.Type<?>> entry : objectHolderTypes.entrySet()) {
                String key = entry.getKey();
                boolean found = false;
                for (ICatenationObjectHolder.Key<?> dataKey : dataKeys) {
                    if (key.equals(dataKey.getKey())) {
                        found = true;
                        Class<?> expected = entry.getValue().getValueType();
                        Class<?> given = dataKey.getType().getValueType();
                        if (!expected.equals(given)) {
                            throw new IllegalArgumentException("Type mismatches at key " + key + ", expected " + expected + ", but given " + given);
                        }
                        break;
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("catenation object: {key: " + key + ", type: " + entry.getValue().getValueType().getName() + "} is missing.");
                }
            }
        }
    }
}
