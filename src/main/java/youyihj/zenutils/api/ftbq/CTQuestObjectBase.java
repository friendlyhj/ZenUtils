package youyihj.zenutils.api.ftbq;

import com.feed_the_beast.ftbquests.quest.QuestObjectBase;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.text.ITextComponent;
import crafttweaker.mc1120.text.expand.ExpandTextComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
    @Deprecated
    public String getTitle() {
        CraftTweakerAPI.logWarning("Use titleText getter instead. The method doesn't work on dedicated server.");
        return object.getTitle();
    }

    @ZenGetter("titleText")
    public ITextComponent getTitleText() {
        String textTitle = object.loadText().getString("title");
        if (!textTitle.isEmpty()) {
            return ExpandTextComponent.fromString(textTitle);
        }
        // object.title is an unlocalized title
        if (object.title.isEmpty()) {
            throw new IllegalArgumentException(String.format("Quest object %08x doesn't have an explicit title", getID()));
        } else {
            return serverAvailableText(object.title);
        }
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

    /* FIXME: it can't handle recursive translation text
        ftb quests text: {text1}
        language files:
            text1={text2} and {text3}
            text2=foo
            text3=bar
     */
    protected static ITextComponent serverAvailableText(String ftbqUnlocalizedText) {
        net.minecraft.util.text.ITextComponent text = new TextComponentString("");
        // Formatting codes cleared for ease of processing, maybe someone will ask to keep?
        ftbqUnlocalizedText = TextFormatting.getTextWithoutFormattingCodes(ftbqUnlocalizedText);
        boolean inTranslation = false;
        StringBuilder currentSnippet = new StringBuilder();
        if (ftbqUnlocalizedText != null) {
            for (int i = 0; i < ftbqUnlocalizedText.length(); i++) {
                char c = ftbqUnlocalizedText.charAt(i);
                if (c == '{' && !inTranslation) {
                    inTranslation = true;
                    if (currentSnippet.length() != 0) {
                        text.appendSibling(new TextComponentString(currentSnippet.toString()));
                        currentSnippet = new StringBuilder();
                    }
                } else if (c == '}' && inTranslation) {
                    inTranslation = false;
                    if (currentSnippet.length() != 0) {
                        text.appendSibling(new TextComponentTranslation(currentSnippet.toString()));
                        currentSnippet = new StringBuilder();
                    }
                } else {
                    currentSnippet.append(c);
                }
            }
            if (currentSnippet.length() != 0) {
                text.appendSibling(new TextComponentString(currentSnippet.toString()));
            }
        }
        return CraftTweakerMC.getITextComponent(text);
    }
}
