package youyihj.zenutils.api.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.text.ITextComponent;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.stats.StatList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.registry.EntityEntry;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.PlayerStat")
public class PlayerStat {
    private final StatBase internal;

    public PlayerStat(StatBase internal) {
        this.internal = internal;
    }

    public static PlayerStat of(StatBase stat) {
        return stat != null ? new PlayerStat(stat) : null;
    }

    @ZenMethod
    public static PlayerStat getBasicStat(String id) {
        StatBase stat = StatList.getOneShotStat(id);
        if (stat == null) {
            throw new IllegalArgumentException("Stat " + id + " does not exist!");
        }
        return PlayerStat.of(stat);
    }

    @ZenMethod
    public static PlayerStat getBlockStats(IBlockDefinition block) {
        return PlayerStat.of(StatList.getBlockStats(CraftTweakerMC.getBlock(block)));
    }

    @ZenMethod
    public static PlayerStat getCraftStats(IItemDefinition item) {
        return PlayerStat.of(StatList.getCraftStats(CraftTweakerMC.getItem(item)));
    }

    @ZenMethod
    public static PlayerStat getObjectUseStats(IItemDefinition item) {
        return PlayerStat.of(StatList.getObjectUseStats(CraftTweakerMC.getItem(item)));
    }

    @ZenMethod
    public static PlayerStat getObjectBreakStats(IItemDefinition item) {
        return PlayerStat.of(StatList.getObjectBreakStats(CraftTweakerMC.getItem(item)));
    }

    @ZenMethod
    public static PlayerStat getObjectsPickedUpStats(IItemDefinition item) {
        return PlayerStat.of(StatList.getObjectsPickedUpStats(CraftTweakerMC.getItem(item)));
    }

    @ZenMethod
    public static PlayerStat getDroppedObjectStats(IItemDefinition item) {
        return PlayerStat.of(StatList.getDroppedObjectStats(CraftTweakerMC.getItem(item)));
    }

    @ZenMethod
    public static PlayerStat getKillEntityStats(IEntityDefinition entity) {
        EntityEntry internal = (EntityEntry) entity.getInternal();
        return PlayerStat.of(internal.getEgg().killEntityStat);
    }

    @ZenMethod
    public static PlayerStat getKilledByEntityStats(IEntityDefinition entity) {
        EntityEntry internal = (EntityEntry) entity.getInternal();
        return PlayerStat.of(internal.getEgg().entityKilledByStat);
    }

    @ZenMethod
    public static PlayerStat create(String id, ITextComponent name, @Optional(methodClass = DefaultStatFormatters.class, methodName = "simple") IStatFormatter formatter) {
        if (Loader.instance().getLoaderState().compareTo(LoaderState.AVAILABLE) > 0) {
            return java.util.Optional.ofNullable(StatList.getOneShotStat(id))
                    .map(PlayerStat::new)
                    .orElseThrow(() -> new IllegalStateException("Could not create a new stat after game initialization."));
        }
        return PlayerStat.of(new StatBasic(id, CraftTweakerMC.getITextComponent(name), formatter.toType()).registerStat());
    }

    @ZenMethod
    @ZenGetter("name")
    public ITextComponent getName() {
        return CraftTweakerMC.getITextComponent(internal.getStatName());
    }

    public StatBase getInternal() {
        return internal;
    }
}
