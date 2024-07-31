package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.lang.reflect.Method;

/**
 * @author youyihj
 */
public enum WrappedMethodNameGenerator {
    INSTANCE;

    private final Table<String, Method, Integer> assignedMethods = HashBasedTable.create();

    public String get(Method method) {
        String name = method.getName();
        Integer id = assignedMethods.get(name, method);
        if (id == null) {
            id = assignedMethods.row(name).size();
            assignedMethods.put(name, method, id);
        }
        return id == 0 ? name : name + "_" + id;
    }
}
