package youyihj.zenutils;

import net.minecraftforge.fml.common.Loader;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.reflect.ReflectionClassDataFetcher;

/**
 * @author youyihj
 */
public class Reference {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.19.1";
    public static final String DEPENDENCIES = "required-after:crafttweaker;required-after:mixinbooter@[8.0,);before:contenttweaker;after:ftbquests;";
    public static final String MOD_COT = "contenttweaker";
    public static final boolean IS_CLEANROOM = !System.getProperty("java.version").startsWith("1.8");

    public static ClassDataFetcher classDataFetcher = new ReflectionClassDataFetcher(Loader.instance().getModClassLoader());
}
