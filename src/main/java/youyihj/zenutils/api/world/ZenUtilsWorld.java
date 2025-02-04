package youyihj.zenutils.api.world;

import com.google.common.base.Predicates;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.game.ITeam;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.entity.MCEntityItem;
import crafttweaker.mc1120.player.MCPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.energy.CrTEnergyStorage;
import youyihj.zenutils.api.entity.INearbyEntityList;
import youyihj.zenutils.api.item.CrTItemHandler;
import youyihj.zenutils.api.liquid.CrTLiquidHandler;
import youyihj.zenutils.api.util.CrTUUID;
import youyihj.zenutils.api.util.catenation.ICatenationBuilder;
import youyihj.zenutils.api.util.catenation.persistence.CatenationPersistenceAPI;
import youyihj.zenutils.api.util.catenation.persistence.PersistedCatenationStarter;
import youyihj.zenutils.impl.capability.IZenWorldCapability;
import youyihj.zenutils.impl.capability.ZenWorldCapabilityHandler;
import youyihj.zenutils.impl.entity.NearbyEntityList;
import youyihj.zenutils.impl.player.FakePlayerHolder;
import youyihj.zenutils.impl.util.catenation.CatenationBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.world.IWorld")
@SuppressWarnings("unused")
public class ZenUtilsWorld {
    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByName(IWorld iWorld, String name) {
       return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getPlayerEntityByName(name));
    }

    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByUUID(IWorld iWorld, CrTUUID uuid) {
        return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getPlayerEntityByUUID(uuid.getInternal()));
    }

    @ZenMethod
    public static List<IPlayer> getAllPlayers(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).playerEntities.stream().map(MCPlayer::new).collect(Collectors.toList());
    }

    @ZenMethod
    public static IPlayer getClosestPlayerToEntity(IWorld iWorld, IEntity iEntity, double distance, boolean spectator) {
        return getClosestPlayer(iWorld, iEntity.getPosX(), iEntity.getPosY(), iEntity.getPosZ(), distance, spectator);
    }

    @ZenMethod
    public static IPlayer getClosestPlayer(IWorld iWorld, double posX, double posY, double posZ, double distance, boolean spectator) {
        return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getClosestPlayer(posX, posY, posZ, distance, spectator));
    }

    @ZenMethod
    public static List<IEntity> getEntities(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).loadedEntityList.stream().map(CraftTweakerMC::getIEntity).collect(Collectors.toList());
    }

    @ZenMethod
    public static List<IEntityItem> getEntityItems(IWorld iWorld) {
        //noinspection Guava
        return CraftTweakerMC.getWorld(iWorld).getEntities(EntityItem.class, Predicates.alwaysTrue()).stream().map(MCEntityItem::new).collect(Collectors.toList());
    }

    @ZenMethod
    public static List<IPlayer> getPlayers(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).playerEntities.stream().map(MCPlayer::new).collect(Collectors.toList());
    }

    @ZenMethod
    public static INearbyEntityList nearbyEntities(IWorld world, IVector3d pos, double radius, @stanhebben.zenscript.annotations.Optional IEntity exclude) {
        return new NearbyEntityList(CraftTweakerMC.getWorld(world), pos, radius, exclude);
    }

    @ZenMethod
    public static INearbyEntityList nearbyEntities(IWorld world, IEntity entity, double radius, @stanhebben.zenscript.annotations.Optional boolean excludeSelf) {
        return new NearbyEntityList(
                CraftTweakerMC.getWorld(world),
                CraftTweakerMC.getIVector3d(new Vec3d(entity.getPosX(), entity.getPosY(), entity.getPosZ())),
                radius,
                excludeSelf ? entity : null
        );
    }

    @ZenMethod
    public static IData getCustomWorldData(IWorld world) {
        return getWorldCap(world).getData();
    }

    @ZenMethod
    public static void setCustomWorldData(IWorld world, IData data) {
        getWorldCap(world).setData(data);
    }

    @ZenMethod
    public static void updateCustomWorldData(IWorld world, IData data) {
        getWorldCap(world).updateData(data);
    }

    @ZenMethod
    public static IData getCustomChunkData(IWorld world, IBlockPos posToGetChunk) {
        return getChunkCap(world, posToGetChunk).getData();
    }

    @ZenMethod
    public static void setCustomChunkData(IWorld world, IData data, IBlockPos posToGetChunk) {
        getChunkCap(world, posToGetChunk).setData(data);
        getChunk(world, posToGetChunk).markDirty();
    }

    @ZenMethod
    public static void updateCustomChunkData(IWorld world, IData data, IBlockPos posToGetChunk) {
        getChunkCap(world, posToGetChunk).updateData(data);
        getChunk(world, posToGetChunk).markDirty();
    }

    @ZenMethod
    public static void destroyBlock(IWorld world, IBlockPos pos, boolean dropBlock) {
        CraftTweakerMC.getWorld(world).destroyBlock(CraftTweakerMC.getBlockPos(pos), dropBlock);
    }

    @ZenMethod
    public static CrTItemHandler getItemHandler(IWorld world, IBlockPos pos, @stanhebben.zenscript.annotations.Optional IFacing facing) {
        return Optional.ofNullable(CraftTweakerMC.getWorld(world).getTileEntity(CraftTweakerMC.getBlockPos(pos)))
                .map(tileEntity -> {
                    IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, CraftTweakerMC.getFacing(facing));
                    return CrTItemHandler.of(itemHandler);
                })
                .orElse(null);
    }

    @ZenMethod
    public static CrTLiquidHandler getLiquidHandler(IWorld world, IBlockPos pos, @stanhebben.zenscript.annotations.Optional IFacing facing) {
        return Optional.ofNullable(CraftTweakerMC.getWorld(world).getTileEntity(CraftTweakerMC.getBlockPos(pos)))
                .map(tileEntity -> {
                    IFluidHandler fluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, CraftTweakerMC.getFacing(facing));
                    return CrTLiquidHandler.of(fluidHandler);
                })
                .orElse(null);
    }

    @ZenMethod
    public static CrTEnergyStorage getEnergyStorage(IWorld world, IBlockPos pos, @stanhebben.zenscript.annotations.Optional IFacing facing) {
        return Optional.ofNullable(CraftTweakerMC.getWorld(world).getTileEntity(CraftTweakerMC.getBlockPos(pos)))
                .map(tileEntity -> {
                    IEnergyStorage energyStorage = tileEntity.getCapability(CapabilityEnergy.ENERGY, CraftTweakerMC.getFacing(facing));
                    return CrTEnergyStorage.of(energyStorage);
                })
                .orElse(null);
    }

    @ZenMethod
    public static ICatenationBuilder catenation(IWorld world) {
        return new CatenationBuilder(world);
    }

    @ZenMethod
    public static PersistedCatenationStarter persistedCatenation(IWorld world, String catenationKey) {
        return CatenationPersistenceAPI.startPersistedCatenation(catenationKey, world);
    }

    @ZenMethod
    public static int getBlockBrightness(IWorld world, IBlockPos pos) {
        return CraftTweakerMC.getWorld(world).getLightFor(EnumSkyBlock.BLOCK, CraftTweakerMC.getBlockPos(pos));
    }

    @ZenMethod
    public static int getSkyBrightness(IWorld world, IBlockPos pos, @stanhebben.zenscript.annotations.Optional boolean subtracted) {
        World mcWorld = CraftTweakerMC.getWorld(world);
        int light = mcWorld.getLightFor(EnumSkyBlock.SKY, CraftTweakerMC.getBlockPos(pos));
        return subtracted ? Math.max(0, light - mcWorld.getSkylightSubtracted()) : light;
    }

    @ZenMethod
    public static int getBrightnessSubtracted(IWorld world, IBlockPos pos) {
        return CraftTweakerMC.getWorld(world).getLightFromNeighbors(CraftTweakerMC.getBlockPos(pos));
    }

    @ZenMethod
    @ZenGetter("gameRuleHelper")
    public static GameRuleHelper getGameRuleHelper(IWorld world) {
        return new GameRuleHelper(CraftTweakerMC.getWorld(world).getGameRules());
    }

    @ZenMethod
    @ZenGetter("fakePlayer")
    public static IPlayer getFakePlayer(IWorld world) {
        World mcWorld = CraftTweakerMC.getWorld(world);
        if (mcWorld instanceof WorldServer) {
            return CraftTweakerMC.getIPlayer(FakePlayerHolder.get(((WorldServer) mcWorld)));
        } else {
            throw new IllegalStateException("Server side only.");
        }
    }

    @ZenGetter("teams")
    public static List<ITeam> getTeams(IWorld world) {
        return CraftTweakerMC.getWorld(world).getScoreboard().getTeams()
                .stream()
                .map(CraftTweakerMC::getITeam)
                .collect(Collectors.toList());
    }

    @ZenMethod
    public static ITeam getTeam(IWorld world, String name) {
        return CraftTweakerMC.getITeam(CraftTweakerMC.getWorld(world).getScoreboard().getTeam(name));
    }

    @ZenMethod
    public static ITeam createTeam(IWorld world, String name) {
        return CraftTweakerMC.getITeam(CraftTweakerMC.getWorld(world).getScoreboard().createTeam(name));
    }

    @ZenMethod
    public static void removeTeam(IWorld world, String name) {
        Scoreboard scoreboard = CraftTweakerMC.getWorld(world).getScoreboard();
        ScorePlayerTeam team = scoreboard.getTeam(name);
        if (team != null) {
            scoreboard.removeTeam(team);
        }
    }

    private static IZenWorldCapability getWorldCap(IWorld world) {
        return CraftTweakerMC.getWorld(world).getCapability(ZenWorldCapabilityHandler.ZEN_WORLD_CAPABILITY, null);
    }

    private static IZenWorldCapability getChunkCap(IWorld world, IBlockPos posToGetChunk) {
        return getChunk(world, posToGetChunk).getCapability(ZenWorldCapabilityHandler.ZEN_WORLD_CAPABILITY, null);
    }

    private static Chunk getChunk(IWorld world, IBlockPos pos) {
        return CraftTweakerMC.getWorld(world).getChunk(CraftTweakerMC.getBlockPos(pos));
    }
}
