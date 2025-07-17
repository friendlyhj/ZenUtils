package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigElement")
public class ConfigElement {
    protected final ConfigGroup parent;
    protected final String name;
    protected String langKey;
    protected String displayName;
    protected String[] comment;
    protected boolean requiresMcRestart, requiresWorldRestart, slidingOption;

    protected ConfigElement(ConfigGroup parentIn, String nameIn) {
        this.parent = parentIn;
        this.name = nameIn;
        this.langKey = null;
        this.comment = null;
        this.displayName = null;
        this.requiresMcRestart = false;
        this.requiresWorldRestart = false;
        this.slidingOption = false;
    }

    @ZenMethod
    public ConfigGroup add() {
        return this.parent;
    }

    @ZenMethod
    public ConfigGroup getParent() {
        return parent;
    }

    @ZenMethod
    public boolean isRequiresMcRestart() {
        return requiresMcRestart;
    }

    @ZenMethod
    public boolean isRequiresWorldRestart() {
        return requiresWorldRestart;
    }

    @ZenMethod
    public boolean isSlidingOption() {
        return slidingOption;
    }

    @ZenMethod
    public ConfigElement sliding() {
        this.slidingOption = true;
        return this;
    }

    @ZenMethod
    public String getDisplayName() {
        return displayName;
    }

    @ZenMethod
    public String getLangKey() {
        return langKey;
    }

    @ZenMethod
    public String getName() {
        return name;
    }

    @ZenMethod
    public String[] getComment() {
        return comment;
    }

    @ZenMethod
    public String getAbsoluteName(){
        return this.parent == null ? this.name : this.parent.name + "." + this.name;
    }

    @ZenMethod
    public ConfigElement requiresMcRestart() {
        this.requiresMcRestart = true;
        return this;
    }

    @ZenMethod
    public ConfigElement requiresWorldRestart() {
        this.requiresWorldRestart = true;
        return this;
    }

    @ZenMethod
    public ConfigElement langKey(String langKey) {
        this.langKey = langKey;
        return this;
    }

    @ZenMethod
    public ConfigElement comment(String... comment) {
        this.comment = comment;
        return this;
    }

    @ZenMethod
    public ConfigElement displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
}
