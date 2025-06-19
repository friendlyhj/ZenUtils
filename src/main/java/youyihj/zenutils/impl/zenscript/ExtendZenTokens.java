package youyihj.zenutils.impl.zenscript;

/**
 * @author youyihj
 */
public class ExtendZenTokens {
    public static final int T_TEMPLATE_STRING = 196;
    public static final String T_TEMPLATE_STRING_REGEX = "`([^`\\\\]|\\\\([\\\\`$bfnrt]|u[0-9a-fA-F]{4}))*`";
    public static final int T_ESCAPE_CHAR = 197;
    public static final String T_ESCAPE_CHAR_REGEX = "\\\\([\\\\`$ntbfr]|u[0-9a-fA-F]{4})";
    public static final int T_QUEST2 = 198;
    public static final String T_QUEST2_REGEX = "\\?\\?";
    public static final int T_QUEST_ASSIGN = 199;
    public static final String T_QUEST_ASSIGN_REGEX = "\\?=";
    public static final int T_QUEST_DOT = 200;
    public static final String T_QUEST_DOT_REGEX = "\\?\\.";
}
