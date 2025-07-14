package youyihj.zenutils.api.config;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.*;
import youyihj.zenutils.api.config.elements.ConfigGroup;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@ZenRegister
@ZenClass("mods.zenutils.config.ConfigUtils")
public class ConfigUtils {

    @ZenMethod
    public static ConfigGroup named(String name) {
        return new ConfigGroup(null, name);
    }

    @ZenMethod // dataMap has fake type arg, Map<String, Object>
    public static DataMap dataMap(Map<String, IData> dataMap) {
        HashMap<String, IData> actualMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
            actualMap.put(entry.getKey(), castToData(entry.getValue()));
        }
        return new DataMap(actualMap, false);
    }

    public static IData castToData(Object o) {
        if (o instanceof Integer) {
            return new DataInt((Integer)o);
        } else if (o instanceof Double) {
            return new DataDouble((Double)o);
        } else if (o instanceof Boolean) {
            return new DataBool((Boolean)o);
        } else if (o instanceof String) {
            return new DataString((String) o);
        } else if (o.getClass().isArray()) {
            return castToDataList(cast(o));
        } else return null;
    }

    public static DataList castToDataList(Object[] o) {
        LinkedList<IData> data = new LinkedList<>();
        for (Object obj : o) {
            data.add(castToData(obj));
        }
        return new DataList(data, true);
    }

    @SuppressWarnings("unchecked")
    private static<T> T cast(Object o) {
        return (T)o;
    }


}
