package youyihj.zenutils.api.world;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.world.GameRules;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.GameRuleHelper")
@SuppressWarnings("unused")
public class GameRuleHelper {

    private static GameRules rules;

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
