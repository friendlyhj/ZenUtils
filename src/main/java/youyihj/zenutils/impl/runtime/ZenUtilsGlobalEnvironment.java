package youyihj.zenutils.impl.runtime;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.zenscript.GlobalRegistry;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.compiler.ClassNameGenerator;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author youyihj
 */
public class ZenUtilsGlobalEnvironment implements IEnvironmentGlobal {
    private final ZenUtilsGlobalEnvironment parent;
    private final ClassNameGenerator generator;
    private final Map<String, byte[]> classes = new HashMap<>();
    private final Map<String, IZenSymbol> symbols = new HashMap<>();

    public ZenUtilsGlobalEnvironment(ZenUtilsGlobalEnvironment parent, String loaderName) {
        this.parent = parent;
        this.generator = new ClassNameGenerator("ZenClass" + loaderName);
    }

    @Override
    public IZenCompileEnvironment getEnvironment() {
        return GlobalRegistry.getEnvironment();
    }

    @Override
    public TypeExpansion getExpansion(String name) {
        return GlobalRegistry.getExpansions().computeIfAbsent(name, TypeExpansion::new);
    }

    @Override
    public ClassNameGenerator getClassNameGenerator() {
        return generator;
    }

    @Override
    public String makeClassName() {
        return generator.generate();
    }

    @Override
    public String makeClassNameWithMiddleName(String middleName) {
        return generator.generateWithMiddleName(middleName);
    }

    @Override
    public boolean containsClass(String name) {
        return classes.containsKey(name) || (parent != null && parent.containsClass(name));
    }

    @Override
    public Set<String> getClassNames() {
        Set<String> classNames = new HashSet<>(classes.keySet());
        if (parent != null) {
            classNames.addAll(parent.getClassNames());
        }
        return Collections.unmodifiableSet(classNames);
    }

    @Override
    public byte[] getClass(String name) {
        byte[] data = classes.get(name);
        if (data == null && parent != null) {
            data = parent.getClass(name);
        }
        return data;
    }

    @Override
    public void putClass(String name, byte[] data) {
        classes.put(name, data);
    }

    @Override
    public IPartialExpression getValue(String name, ZenPosition position) {
        if (symbols.containsKey(name)) {
            return symbols.get(name).instance(position);
        } else if (parent != null) {
            return parent.getValue(name, position);
        } else if (GlobalRegistry.getGlobals().containsKey(name)) {
            return GlobalRegistry.getGlobals().get(name).instance(position);
        } else {
            IZenSymbol pkg = GlobalRegistry.getRoot().get(name);
            if (pkg == null) {
                return null;
            } else {
                return pkg.instance(position);
            }
        }
    }

    @Override
    public void putValue(String name, IZenSymbol value, ZenPosition position) {
        if (symbols.containsKey(name)) {
            error(position, "Value already defined in this scope: " + name);
        } else {
            symbols.put(name, value);
        }
    }

    @Override
    public void error(ZenPosition position, String message) {
        CraftTweakerAPI.logError(position.toString() + " > " + message);
    }

    @Override
    public void warning(ZenPosition position, String message) {
        CraftTweakerAPI.logWarning(position.toString() + " > " + message);
    }

    @Override
    public void info(ZenPosition position, String message) {
        CraftTweakerAPI.logInfo(position.toString() + " > " + message);
    }

    @Override
    public void error(String message) {
        CraftTweakerAPI.logError(message);
    }

    @Override
    public void error(String message, Throwable e) {
        CraftTweakerAPI.logError(message, e);
    }

    @Override
    public void warning(String message) {
        CraftTweakerAPI.logWarning(message);
    }

    @Override
    public void info(String message) {
        CraftTweakerAPI.logInfo(message);
    }

    @Override
    public ZenType getType(Type type) {
        return GlobalRegistry.getTypes().getType(type);
    }

    public Map<String, byte[]> getClasses() {
        return classes;
    }
}
