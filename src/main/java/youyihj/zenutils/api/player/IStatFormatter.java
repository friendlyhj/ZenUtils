package youyihj.zenutils.api.player;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.stats.IStatType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IStatFormatter")
public interface IStatFormatter {
    @ZenMethod
    String format(int number);

    default IStatType toType() {
        return new IStatType() {
            @SideOnly(Side.CLIENT)
            @Override
            public String format(int number) {
                return IStatFormatter.this.format(number);
            }
        };
    }
}
