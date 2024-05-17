package youyihj.zenutils.impl.mixin.crafttweaker;

import com.teamacronymcoders.base.items.ItemBase;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ItemContent.class, remap = false)
public abstract class MixinItemContent extends ItemBase {
    public MixinItemContent(String name) {
        super(name);
    }

    @ModifyArg(method = "setFields", at = @At(value = "INVOKE", target = "Lcom/teamacronymcoders/contenttweaker/modules/vanilla/items/ItemContent;setUnlocalizedName(Ljava/lang/String;)Lnet/minecraft/item/Item;", remap = true), index = 0)
    private String fixTranslationKey(String original) {
        if (this.getMod() == null || original.contains(this.getMod().getID())) return original;
        return this.getMod().getID() + "." + original;
    }
}
