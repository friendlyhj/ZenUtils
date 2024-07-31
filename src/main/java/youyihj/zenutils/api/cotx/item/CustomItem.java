package youyihj.zenutils.api.cotx.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import net.minecraft.item.Item;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.zenscript.nat.ReloadableCustomClass;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author youyihj
 */
public class CustomItem extends ReloadableCustomClass {
    private static final Method GET_GENERATED_MODEL_METHOD;
    private static final Method GET_ITEM_METHOD;
    private static final Method SET_MOD_METHOD;
    private static final Method GET_MOD_METHOD;
    private static final Method GET_MODEL_NAMES_METHOD;

    static {
        try {
            GET_GENERATED_MODEL_METHOD = CustomItem.class.getMethod("getGeneratedModels", Item.class);
            GET_ITEM_METHOD = CustomItem.class.getMethod("getItem", Item.class);
            SET_MOD_METHOD = CustomItem.class.getMethod("setMod", Item.class, IBaseMod.class);
            GET_MOD_METHOD = CustomItem.class.getMethod("getMod", Item.class);
            GET_MODEL_NAMES_METHOD = CustomItem.class.getMethod("getModelNames", Item.class, List.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomItem(String name, Class<?> superClass, List<Class<?>> interfaces) {
        super(name, superClass, interfaces, Arrays.asList(
                new OuterImplementsInfo(IHasGeneratedModel.class, Collections.singletonList(GET_GENERATED_MODEL_METHOD)),
                new OuterImplementsInfo(IHasModel.class, Arrays.asList(
                        GET_MOD_METHOD, SET_MOD_METHOD, GET_ITEM_METHOD, GET_MODEL_NAMES_METHOD
                ))
        ));
    }

    @ReflectionInvoked(asm = true)
    public static List<IGeneratedModel> getGeneratedModels(Item item) {
        List<IGeneratedModel> models = Lists.newArrayList();
        TemplateFile templateFile = TemplateManager.getTemplateFile("item_model");
        Map<String, String> replacements = Maps.newHashMap();

        replacements.put("texture", "contenttweaker:items/" + Objects.requireNonNull(item.getRegistryName()).getPath());
        templateFile.replaceContents(replacements);
        models.add(new GeneratedModel(item.getRegistryName().getPath(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
        return models;
    }

    @ReflectionInvoked(asm = true)
    public static Item getItem(Item item) {
        return item;
    }

    @ReflectionInvoked(asm = true)
    public static IBaseMod<?> getMod(Item item) {
        return ContentTweaker.instance;
    }

    @ReflectionInvoked(asm = true)
    public static void setMod(Item item, IBaseMod<?> mod) {
        // NO-OP
    }

    @ReflectionInvoked(asm = true)
    public static List<String> getModelNames(Item item, List<String> modelNames) {
        modelNames.add(item.getTranslationKey().substring(5));
        return modelNames;
    }
}
