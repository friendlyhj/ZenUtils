package youyihj.zenutils.api.cotx.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import net.minecraft.item.Item;
import youyihj.zenutils.impl.zenscript.nat.ReloadableCustomClass;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author youyihj
 */
public class CustomItem extends ReloadableCustomClass {
    private static final Method GET_GENERATED_MODEL_METHOD;

    static {
        try {
            GET_GENERATED_MODEL_METHOD = CustomItem.class.getMethod("getGeneratedModels", Item.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomItem(String name, Class<?> superClass, List<Class<?>> interfaces) {
        super(name, superClass, interfaces, Collections.singletonList(new OuterImplementsInfo(IHasGeneratedModel.class, Collections.singletonList(GET_GENERATED_MODEL_METHOD))));
    }

    public static List<IGeneratedModel> getGeneratedModels(Item item) {
        List<IGeneratedModel> models = Lists.newArrayList();
        TemplateFile templateFile = TemplateManager.getTemplateFile("item_model");
        Map<String, String> replacements = Maps.newHashMap();

        replacements.put("texture", "contenttweaker:items/" + Objects.requireNonNull(item.getRegistryName()).getPath());
        templateFile.replaceContents(replacements);
        models.add(new GeneratedModel(item.getRegistryName().getPath(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
        return models;
    }
}
