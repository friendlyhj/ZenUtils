package youyihj.zenutils;

import org.apache.commons.lang3.SystemUtils;

/**
 * @author youyihj
 */
public class Reference {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.26.7";
    public static final String DEPENDENCIES = "required-after:crafttweaker;required-after:mixinbooter@[10.6,);required-after:configanytime;before:contenttweaker;after:ftbquests;";
    public static final String MOD_COT = "contenttweaker";
    public static final boolean IS_CLEANROOM = !SystemUtils.IS_JAVA_1_8;
}
