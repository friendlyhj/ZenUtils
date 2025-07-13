package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigRangedDouble")
public class ConfigRangedDouble extends ConfigDouble {
    protected final double min;
    protected final double max;
    protected ConfigRangedDouble(ConfigGroup parentIn, String nameIn, double defaultVal, double min, double max) {
        super(parentIn, nameIn, defaultVal);
        this.min = min;
        this.max = max;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public ConfigRangedDouble sliding() {
        this.slidingOption = true;
        return this;
    }
}
