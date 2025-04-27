package youyihj.zenutils.impl.util.catenation;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import youyihj.zenutils.api.util.catenation.Catenation;

/**
 * @author youyihj
 */
public class ClientCatenationBuilder extends AbstractCatenationBuilder {
    @Override
    protected void register(Catenation catenation) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            throw new IllegalStateException("Trying to run a client catenation on server side.");
        }
        CatenationManager.addClientCatenation(catenation);
    }
}
