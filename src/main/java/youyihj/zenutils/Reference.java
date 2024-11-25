package youyihj.zenutils;

/**
 * @author youyihj
 */
public class Reference {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.21.3";
    public static final String DEPENDENCIES = "required-after:crafttweaker;required-after:mixinbooter@[8.0,);required-after:configanytime;before:contenttweaker;after:ftbquests;";
    public static final String MOD_COT = "contenttweaker";
    public static final boolean IS_CLEANROOM = !System.getProperty("java.version").startsWith("1.8");
}
