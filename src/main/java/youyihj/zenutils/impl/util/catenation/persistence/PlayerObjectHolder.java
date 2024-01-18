package youyihj.zenutils.impl.util.catenation.persistence;

import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.IData;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.api.util.catenation.persistence.BuiltinObjectHolderTypes;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author youyihj
 */
public class PlayerObjectHolder implements ICatenationObjectHolder<IPlayer> {

    private IPlayer player;

    private UUID uuid;

    @Override
    public Type<IPlayer> getType() {
        return BuiltinObjectHolderTypes.PLAYER;
    }

    @Override
    public IData serializeToData() {
        return new DataList(Arrays.asList(new DataLong(uuid.getMostSignificantBits()), new DataLong(uuid.getLeastSignificantBits())), true);
    }

    @Override
    public void deserializeFromData(IData data) {
        List<IData> list = data.asList();
        uuid = new UUID(list.get(0).asLong(), list.get(1).asLong());
    }

    @Override
    public void receiveObject(IPlayer object) {
        if (Objects.equals(EntityPlayer.getUUID(CraftTweakerMC.getPlayer(object).getGameProfile()), uuid)) {
            this.player = object;
        }
    }

    @Override
    public IPlayer getValue() {
        return player;
    }

    @Override
    public void setValue(IPlayer value) {
        this.player = value;
        this.uuid = EntityPlayer.getUUID(CraftTweakerMC.getPlayer(player).getGameProfile());
    }

    @Override
    public ValidationResult validate(Catenation catenation) {
        return player != null && CraftTweakerMC.getWorld(catenation.getWorld()).playerEntities.contains(CraftTweakerMC.getPlayer(player)) ? ValidationResult.VALID : ValidationResult.INVALID_PAUSE;
    }

    @Override
    public void invalidate() {
        this.player = null;
    }
}
