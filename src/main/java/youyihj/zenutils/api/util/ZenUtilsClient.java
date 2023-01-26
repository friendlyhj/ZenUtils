package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.client.IClient;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.catenation.ICatenationBuilder;
import youyihj.zenutils.impl.util.catenation.ClientCatenationBuilder;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.api.IClient")
public class ZenUtilsClient {
    @ZenMethod
    public static ICatenationBuilder catenation(IClient client) {
        return new ClientCatenationBuilder();
    }
}
