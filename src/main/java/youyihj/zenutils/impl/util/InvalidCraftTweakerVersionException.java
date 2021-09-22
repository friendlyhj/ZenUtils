package youyihj.zenutils.impl.util;

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
    public InvalidCraftTweakerVersionException(String requiredCraftTweakerVersion) {
        super(ZenUtils.MODID, ZenUtils.NAME);
        try {
            this.addMissingMod(
                    new DefaultArtifactVersion(CraftTweaker.MODID, VersionRange.createFromVersionSpec("[" + requiredCraftTweakerVersion + ",)")),
                    null,
                    true
            );
        } catch (InvalidVersionSpecificationException e) {
            throw new AssertionError();
        }
    }
}
