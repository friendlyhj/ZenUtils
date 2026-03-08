package youyihj.zenutils.api.config;

import crafttweaker.annotations.ZenRegister;
import net.minecraftforge.fml.common.ModMetadata;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.config.ModMetaData")
public class ModMeta {
    public final ModMetadata metadata = new ModMetadata();

    public ModMeta(String name) {
        metadata.name = name;
        metadata.description = null; //to distinguish between deliberately empty and not provided (=null, will be filled with default description)
        metadata.credits = "§3Created via CraftTweaker script";
    }

    @ZenMethod
    public ModMeta setDescription(String string) {
        this.metadata.description = string;
        return this;
    }

    @ZenMethod
    public ModMeta setVersion(String string) {
        this.metadata.version = string;
        return this;
    }

    @ZenMethod
    public ModMeta addAuthor(String string) {
        this.metadata.authorList.add(string);
        return this;
    }

    @ZenMethod
    public ModMeta setCredits(String string) {
        this.metadata.credits = string;
        return this;
    }

    @ZenMethod
    public ModMeta setLogoLocation(String string) {
        this.metadata.logoFile = string;
        return this;
    }
}
