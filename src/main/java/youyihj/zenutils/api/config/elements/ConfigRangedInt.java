package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigRangedInt")
public class ConfigRangedInt extends ConfigInt {

    protected final int min;
    protected final int max;
    public ConfigRangedInt(ConfigGroup parentIn, String nameIn, int defaultVal, int min, int max) {
        super(parentIn, nameIn, defaultVal);
        this.min = min;
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public ConfigRangedInt sliding() {
        this.slidingOption = true;
        return this;
    }
}
