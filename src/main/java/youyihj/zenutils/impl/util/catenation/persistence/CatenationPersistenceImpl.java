package youyihj.zenutils.impl.util.catenation.persistence;

import com.google.common.collect.ImmutableMap;
import crafttweaker.api.data.*;
import crafttweaker.api.world.IWorld;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.util.ExpandData;
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
            objDataSingle.put("type", new DataString(objHolder.getType().getValueType().getName()));
            objDataSingle.put("value", objHolder.serializeToData());
            objData.put(key.getKey(), new DataMap(objDataSingle, true));
        });
        total.put("objects", new DataMap(objData, true));

        return new DataMap(total, true);
    }

    public static Catenation deserialize(IData data, IWorld world) throws DeserializationException {
        String persistKey = data.memberGet("key").asString();
        Entry persistEntry = persistData.get(persistKey);
        Catenation catenation = persistEntry.getCatenationFactory().get(world);
        catenation.getContext().setStatus(CatenationStatus.SERIAL, world);
        catenation.setPersistenceKey(persistKey);

        Queue<ICatenationTask> tasks = catenation.getTasks();
        int toRemoveTasks = tasks.size() - data.memberGet("taskCount").asInt();
        try {
            for (int i = 0; i < toRemoveTasks; i++) {
                tasks.remove();
            }
            tasks.element().deserializeFromData(data.memberGet("taskData"));
            if (tasks.element().isComplete()) {
                tasks.remove();
            }
        } catch (NoSuchElementException e) {
            throw new DeserializationException("Too few tasks. At least: " + toRemoveTasks);
        }
        if (data.contains(new DataString("data"))) {
            catenation.getContext().setData(data.memberGet("data"));
        }

        Map<String, IData> objData = data.memberGet("objects").asMap();
        persistEntry.checkObjData(objData);
        Map<ICatenationObjectHolder.Key<?>, ICatenationObjectHolder<?>> objectHolders = catenation.getContext().getObjectHolders();
        objData.forEach((key, value) -> {
            ICatenationObjectHolder.Type<?> holderType = ObjectHolderTypeRegistry.get(value.memberGet("type").asString());
            ICatenationObjectHolder.Key<?> holderKey = ICatenationObjectHolder.Key.of(key, holderType);
            ICatenationObjectHolder<?> holder = holderType.createHolder();
            holder.deserializeFromData(value.memberGet("value"));
            objectHolders.put(holderKey, holder);
        });

        return catenation;
    }

    public static void loadCatenations(IWorld world) {
        IData catenationsData = ZenUtilsWorld.getCustomWorldData(world).memberGet("catenations");
        if (catenationsData == null) return;
        for (IData catenationData : catenationsData.asList()) {
            Catenation catenation;
            try {
                catenation = deserialize(catenationData, world);
            } catch (DeserializationException e) {
                ZenUtils.forgeLogger.error("Failed to read catenation from data " + catenationData.asString() + ". The catenation format maybe changed?", e);
                continue;
            }
            if (catenation.isAllObjectsValid()) {
                catenation.getContext().setStatus(CatenationStatus.WORKING, world);
            } else {
                addWaitingCatenation(catenation);
            }
        }
    }

    public static void saveCatenations(IWorld world, List<Catenation> unfinished) {
        List<IData> catenationDataList = new ArrayList<>();
        for (Catenation catenation : unfinished) {
            if (catenation.getPersistenceKey() != null) {
                catenationDataList.add(serialize(catenation));
            }
        }
        Map<String, IData> newCatenationDataMap = new HashMap<>();
        newCatenationDataMap.put("catenations", new DataList(catenationDataList, true));
        DataMap newCatenationData = new DataMap(newCatenationDataMap, true);
        Map<String, IData> operationMap = new HashMap<>();
        operationMap.put("catenations", ExpandData.DataUpdateOperation.OVERWRITE);
        DataMap operationData = new DataMap(operationMap, true);
        ZenUtilsWorld.setCustomWorldData(world, ExpandData.deepUpdate(ZenUtilsWorld.getCustomWorldData(world), newCatenationData, operationData));
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

        public void checkObjData(Map<String, IData> objectsData) throws DeserializationException {
            for (Map.Entry<String, IData> entry : objectsData.entrySet()) {
                String key = entry.getKey();
                IData value = entry.getValue();
                ICatenationObjectHolder.Type<?> type = objectHolderTypes.get(key);
                if (type == null) {
                    throw new DeserializationException("Extra object key: " + key);
                } else if (!type.getValueType().getName().equals(value.memberGet("type").asString())) {
                    throw new DeserializationException("Type mismatches at key " + key);
                }
            }
            for (String key : objectHolderTypes.keySet()) {
                if (!objectsData.containsKey(key)) {
                    throw new DeserializationException("Catenation object " + key + " is missing.");
                }
            }
        }
    }

    public static class DeserializationException extends Exception {
        public DeserializationException(String message) {
            super(message);
        }
    }
}
