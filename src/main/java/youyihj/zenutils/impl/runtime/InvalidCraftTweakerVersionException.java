package youyihj.zenutils.impl.runtime;

import crafttweaker.mc1120.CraftTweaker;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import youyihj.zenutils.ZenUtils;

/**
 * @author youyihj
 */
public class InvalidCraftTweakerVersionException extends MissingModsException {
    private final String requiredVersion;

    public InvalidCraftTweakerVersionException(String requiredVersion) {
        super(ZenUtils.MODID, ZenUtils.NAME);
        this.requiredVersion = requiredVersion;
        try {
            this.addMissingMod(
                    new DefaultArtifactVersion(CraftTweaker.MODID, VersionRange.createFromVersionSpec("[" + requiredVersion + ",)")),
                    null,
                    true
            );
        } catch (InvalidVersionSpecificationException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String getMessage() {
        return String.format("crafttweaker version must be %s or above!", requiredVersion);
    }
}
