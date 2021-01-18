package youyihj.zenutils.ftbq;

import com.feed_the_beast.ftbquests.quest.QuestObjectBase;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.*;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.QuestObjectBase")
@ModOnly("ftbquests")
public class CTQuestObjectBase {
    public final QuestObjectBase object;

    public CTQuestObjectBase(QuestObjectBase object) {
        this.object = object;
    }

    @ZenGetter("parentID")
    public int getParentID() {
        return object.getParentID();
    }

    @ZenGetter("data")
    public IData getData() {
        NBTTagCompound nbt = new NBTTagCompound();
        object.writeData(nbt);
        return CraftTweakerMC.getIData(nbt);
    }

    @ZenGetter("title")
    public String getTitle() {
        return object.getTitle();
    }

    @ZenGetter("id")
    public int getID() {
        return object.id;
    }

    @ZenGetter("icon")
    public IItemStack getIcon() {
        return CraftTweakerMC.getIItemStack(object.icon);
    }

    @ZenGetter("codeString")
    public String getCodeString() {
        return object.getCodeString();
    }

    @ZenGetter("type")
    public String getType() {
        return object.getObjectType().name();
    }

    @ZenGetter("tags")
    public String[] getTags() {
        return object.getTags().toArray(new String[0]);
    }

    @ZenMethod
    public boolean hasTag(String tag) {
        return object.hasTag(tag);
    }

    @ZenOperator(OperatorType.EQUALS)
    public boolean equals(CTQuestObjectBase others) {
        if (others == null)
            return false;
        return this.object.equals(others.object);
    }
}
