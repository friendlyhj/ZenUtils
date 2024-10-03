package youyihj.zenutils.impl.zenscript.nat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author youyihj
 */
public enum CraftTweakerBridge {
    INSTANCE;

    private final Map<String, ExecutableData> casters = new HashMap<>();

    private LoaderState loaderState;

    CraftTweakerBridge() {
        refresh();
    }

    private void refresh() {
        loaderState = Loader.instance().getLoaderState();
        casters.clear();
        try {
            ClassData craftTweakerMC = InternalUtils.getClassDataFetcher().forName("crafttweaker.api.minecraft.CraftTweakerMC");
            for (ExecutableData method : craftTweakerMC.methods(true)) {
                if (method.name().startsWith("get") && method.parameterCount() == 1) {
                    ClassData toConvert = method.parameters().get(0).asClassData();
                    String toConvertClassName = toConvert.name();
                    if (toConvertClassName.startsWith("crafttweaker.")) {
                        casters.put(toConvert.name(), method);
                    } else if (toConvertClassName.startsWith("net.minecraft")) {
                        casters.put(toConvert.name(), method);
                    }
                }
            }
            // There are many overloads in ItemStack, Ingredient conversations, specify additionally
            MCPReobfuscation.INSTANCE.reobfMethodOverloads(craftTweakerMC, "getItemStack", true).forEach(it -> {
                String paraName = it.parameters().get(0).asClassData().name();
                if (paraName.endsWith("ItemStack")) {
                    casters.put(paraName, it);
                }
            });
            casters.put("net.minecraft.item.ItemStack", MCPReobfuscation.INSTANCE.reobfMethod(craftTweakerMC, "getIItemStack", true));
            casters.put("crafttweaker.api.item.IIngredient", MCPReobfuscation.INSTANCE.reobfMethod(craftTweakerMC, "getIngredient", true));
            casters.put("net.minecraft.item.crafting.Ingredient", MCPReobfuscation.INSTANCE.reobfMethod(craftTweakerMC, "getIIngredient", true));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExecutableData> getCaster(final ClassData clazz) {
        if (loaderState != Loader.instance().getLoaderState()) {
            refresh();
        }
        return Optional.ofNullable(casters.get(clazz.name()));
    }
}
