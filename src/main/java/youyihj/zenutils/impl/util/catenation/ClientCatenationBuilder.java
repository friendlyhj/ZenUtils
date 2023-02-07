package youyihj.zenutils.impl.util.catenation;

import youyihj.zenutils.api.util.catenation.Catenation;

/**
 * @author youyihj
 */
public class ClientCatenationBuilder extends AbstractCatenationBuilder {
    @Override
    protected void register(Catenation catenation) {
        CatenationManager.addClientCatenation(catenation);
    }
}
