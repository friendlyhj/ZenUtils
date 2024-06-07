package youyihj.zenutils.impl.mixin.crafttweaker;

import net.minecraft.block.material.Material;

import com.teamacronymcoders.base.blocks.BlockBase;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = BlockContent.class, remap = false)
public abstract class MixinBlockContent extends BlockBase {
    public MixinBlockContent(Material mat) {
        super(mat);
    }

    @ModifyArg(method = "setFields", at = @At(value = "INVOKE", target = "Lcom/teamacronymcoders/contenttweaker/modules/vanilla/blocks/BlockContent;setTranslationKey(Ljava/lang/String;)Lnet/minecraft/block/Block;", remap = true), index = 0)
    private String fixTranslationKey(String original) {
        if (this.getMod() == null || original.contains(this.getMod().getID())) return original;
        return this.getMod().getID() + "." + original;
    }
}
