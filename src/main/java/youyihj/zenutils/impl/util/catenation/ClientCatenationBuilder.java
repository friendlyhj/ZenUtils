package youyihj.zenutils.impl.util.catenation;

import com.google.common.base.Preconditions;
import net.minecraftforge.fml.common.FMLCommonHandler;
import youyihj.zenutils.api.util.catenation.Catenation;

/**
 * @author youyihj
 */
public class ClientCatenationBuilder extends AbstractCatenationBuilder {
    @Override
    protected void register(Catenation catenation) {
        Preconditions.checkState(FMLCommonHandler.instance().getEffectiveSide().isClient(), "Trying to run a client catenation on server side.");
        CatenationManager.addClientCatenation(catenation);
    }
}
