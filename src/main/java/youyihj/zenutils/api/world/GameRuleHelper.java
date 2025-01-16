package youyihj.zenutils.api.world;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.command.CommandGameRule;
import net.minecraft.world.GameRules;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.GameRuleHelper")
@SuppressWarnings("unused")
public class GameRuleHelper {

    private final GameRules rules;

    public GameRuleHelper(GameRules gameRules) {
        rules = gameRules;
    }

    @ZenMethod
    public boolean getBoolean(String name) {
        return rules.getBoolean(name);
    }

    @ZenMethod
    public String getString(String name) {
        return rules.getString(name);
    }

    @ZenMethod
    public int getInt(String name) {
        return rules.getInt(name);
    }

    @ZenMethod
    public String[] getRules() {
        return rules.getRules();
    }

    @ZenMethod
    public boolean hasRule(String name) {
        return rules.hasRule(name);
    }

    @ZenMethod
    public void setInt(String name, int value) {
        if (rules.areSameType(name, GameRules.ValueType.NUMERICAL_VALUE)) {
            rules.setOrCreateGameRule(name, Integer.toString(value));
            CommandGameRule.notifyGameRuleChange(rules, name, CraftTweaker.server);
        } else {
            throw new IllegalArgumentException("GameRule " + name + " is not numerical value.");
        }
    }

    @ZenMethod
    public void setBoolean(String name, boolean value) {
        if (rules.areSameType(name, GameRules.ValueType.BOOLEAN_VALUE)) {
            rules.setOrCreateGameRule(name, Boolean.toString(value));
            CommandGameRule.notifyGameRuleChange(rules, name, CraftTweaker.server);
        } else {
            throw new IllegalArgumentException("GameRule " + name + " is not boolean value.");
        }
    }

    @ZenMethod
    public void setString(String name, String value) {
        if (rules.areSameType(name, GameRules.ValueType.ANY_VALUE)) {
            rules.setOrCreateGameRule(name, value);
            CommandGameRule.notifyGameRuleChange(rules, name, CraftTweaker.server);
        } else {
            throw new IllegalArgumentException("GameRule " + name + " is not string value.");
        }
    }

    @ZenMethod
    public void addGameRule(String key, String value, String type) {
        if(hasRule(key)) return;
        GameRules.ValueType valueType = GameRules.ValueType.ANY_VALUE;
        switch (type.toLowerCase()) {
            case "any": break;
            case "numeric":
                valueType = GameRules.ValueType.NUMERICAL_VALUE;
                break;
            case "boolean":
                valueType = GameRules.ValueType.BOOLEAN_VALUE;
                break;
            default:
                throw new IllegalArgumentException("Invalid GameRule type, must be Any, Numeric or Boolean.");
        }
        rules.addGameRule(key, value, valueType);
    }
}
