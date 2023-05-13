package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import org.apache.commons.lang3.StringUtils;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.StaticString")
@SuppressWarnings("unused")
public class StaticString {
    @ZenMethod
    public static String valueOf(int i) {
        return String.valueOf(i);
    }

    @ZenMethod
    public static String valueOf(boolean b) {
        return String.valueOf(b);
    }

    @ZenMethod
    public static String valueOf(long l) {
        return String.valueOf(l);
    }

    @ZenMethod
    public static String valueOf(float f) {
        return String.valueOf(f);
    }

    @ZenMethod
    public static String valueOf(double d) {
        return String.valueOf(d);
    }

    @ZenMethod
    public static String valueOf(Object obj) {
        return String.valueOf(obj);
    }

    @ZenMethod
    public static String format(String format, Object... args) {
        return String.format(format, args);
    }

    @ZenMethod
    public static String trim(String str) {
        return StringUtils.trim(str);
    }

    @ZenMethod
    public static String trimToNull(String str) {
        return StringUtils.trimToNull(str);
    }

    @ZenMethod
    public static String trimToEmpty(String str) {
        return StringUtils.trimToEmpty(str);
    }

    @ZenMethod
    public static String truncate(String str, int maxWidth) {
        return StringUtils.truncate(str, maxWidth);
    }

    @ZenMethod
    public static String truncate(String str, int offset, int maxWidth) {
        return StringUtils.truncate(str, offset, maxWidth);
    }

    @ZenMethod
    public static String strip(String str) {
        return StringUtils.strip(str);
    }

    @ZenMethod
    public static String strip(String str, String stripChars) {
        return StringUtils.strip(str, stripChars);
    }

    @ZenMethod
    public static String stripToNull(String str) {
        return StringUtils.stripToNull(str);
    }

    @ZenMethod
    public static String stripStart(String str, String stripChars) {
        return StringUtils.stripStart(str, stripChars);
    }

    @ZenMethod
    public static String stripEnd(String str, String stripChars) {
        return StringUtils.stripEnd(str, stripChars);
    }

    @ZenMethod
    public static String[] stripAll(String[] strings) {
        return StringUtils.stripAll(strings);
    }

    @ZenMethod
    public static String[] stripAll(String[] strings, String stripChars) {
        return StringUtils.stripAll(String.valueOf(strings));
    }

    @ZenMethod
    public static String stripAccents(String input) {
        return StringUtils.stripAccents(input);
    }

    @ZenMethod
    public static String stripToEmpty(String str) {
        return StringUtils.stripToEmpty(str);
    }

    @ZenMethod
    public static int compare(String str1, String str2) {
        return StringUtils.compare(str1, str2);
    }

    @ZenMethod
    public static int compare(String str1, String str2, boolean nullIsLess) {
        return StringUtils.compare(str1, str2, nullIsLess);
    }

    @ZenMethod
    public static int compareIgnoreCase(String str1, String str2) {
        return StringUtils.compareIgnoreCase(str1, str2, true);
    }

    @ZenMethod
    public static int compareIgnoreCase(String str1, String str2, boolean nullIsLess) {
        return StringUtils.compareIgnoreCase(str1, str2, nullIsLess);
    }

    @ZenMethod
    public static String substring(String str, int start) {
        return StringUtils.substring(str, start);
    }

    @ZenMethod
    public static String substring(String str, int start, int end) {
        return StringUtils.substring(str, start, end);
    }

    @ZenMethod
    public static String left(String str, int len) {
        return StringUtils.left(str,len);
    }

    @ZenMethod
    public static String right(String str, int len) {
        return StringUtils.right(str, len);
    }

    @ZenMethod
    public static String mid(String str, int pos, int len) {
        return StringUtils.mid(str, pos, len);
    }

    @ZenMethod
    public static String substringBefore(String str, String separator) {
        return StringUtils.substringBefore(str, separator);
    }

    @ZenMethod
    public static String substringAfter(String str, String separator) {
        return StringUtils.substringAfter(str, separator);
    }

    @ZenMethod
    public static String substringBeforeLast(String str, String separator) {
        return StringUtils.substringBeforeLast(str, separator);
    }

    @ZenMethod
    public static String substringAfterLast(String str, String separator) {
        return StringUtils.substringAfterLast(str, separator);
    }

    @ZenMethod
    public static String substringBetween(String str, String tag) {
        return StringUtils.substringBetween(str, tag);
    }

    @ZenMethod
    public static String substringBetween(String str, String open, String close) {
        return StringUtils.substringBetween(str, open, close);
    }

    @ZenMethod
    public static String substringsBetween(String str, String open, String close) {
        return StringUtils.substringBetween(str, open, close);
    }

    @ZenMethod
    public static String[] split(String str) {
        return StringUtils.split(str);
    }

    @ZenMethod
    public static String[] split(String str, String separatorChar) {
        return StringUtils.split(str, separatorChar);
    }

    @ZenMethod
    public static String[] split(String str, String separatorChars, int max) {
        return StringUtils.split(str, separatorChars, max);
    }

    @ZenMethod
    public static String[] splitByWholeSeparator(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator);
    }

    @ZenMethod
    public static String[] splitByWholeSeparator( String str, String separator, int max) {
        return StringUtils.splitByWholeSeparator(str, separator, max);
    }

    @ZenMethod
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator);
    }

    @ZenMethod
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator, max);
    }

    @ZenMethod
    public static String[] splitPreserveAllTokens(String str) {
        return StringUtils.splitPreserveAllTokens(str);
    }

    @ZenMethod
    public static String[] splitPreserveAllTokens(String str, String separatorChar) {
        return StringUtils.splitPreserveAllTokens(str, separatorChar);
    }

    @ZenMethod
    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return StringUtils.splitPreserveAllTokens(str, separatorChars, max);
    }

    @ZenMethod
    public static String[] splitByCharacterType(String str) {
        return StringUtils.splitByCharacterType(str);
    }

    @ZenMethod
    public static String[] splitByCharacterTypeCamelCase(String str) {
        return StringUtils.splitByCharacterTypeCamelCase(str);
    }

    @ZenMethod
    public static String deleteWhitespace(String str) {
        return StringUtils.deleteWhitespace(str);
    }

    @ZenMethod
    public static String removeStart(String str, String remove) {
        return StringUtils.removeStart(str, remove);
    }

    @ZenMethod
    public static String removeStartIgnoreCase(String str, String remove) {
        return StringUtils.removeStartIgnoreCase(str, remove);
    }

    @ZenMethod
    public static String removeEnd(String str, String remove) {
        return StringUtils.removeEnd(str, remove);
    }

    @ZenMethod
    public static String removeEndIgnoreCase(String str, String remove) {
        return StringUtils.removeEndIgnoreCase(str, remove);
    }

    @ZenMethod
    public static String remove(String str, String remove) {
        return StringUtils.remove(str, remove);
    }

    @ZenMethod
    public static String removeIgnoreCase(String str, String remove) {
        return StringUtils.removeStartIgnoreCase(str, remove);
    }

    @ZenMethod
    public static String removeAll(String text, String regex) {
        return StringUtils.removeAll(text, regex);
    }

    @ZenMethod
    public static String removeFirst(String text, String regex) {
        return StringUtils.removeFirst(text, regex);
    }

    @ZenMethod
    public static String replaceOnce(String text, String searchString, String replacement) {
        return StringUtils.replaceOnce(text, searchString, replacement);
    }

    @ZenMethod
    public static String replaceOnceIgnoreCase(String text, String searchString, String replacement) {
        return StringUtils.replaceOnceIgnoreCase(text, searchString, replacement);
    }

    @ZenMethod
    public static String replacePattern(String source, String regex, String replacement) {
        return StringUtils.replacePattern(source, regex, replacement);
    }

    @ZenMethod
    public static String removePattern(String source, String regex) {
        return StringUtils.removePattern(source, regex);
    }

    @ZenMethod
    public static String replaceAll(String text, String regex, String replacement) {
        return StringUtils.replaceAll(text, regex, replacement);
    }

    @ZenMethod
    public static String replaceFirst(String text, String regex, String replacement) {
        return StringUtils.replaceFirst(text, regex, replacement);
    }

    @ZenMethod
    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }

    @ZenMethod
    public static String replaceIgnoreCase(String text, String searchString, String replacement) {
        return StringUtils.replaceIgnoreCase(text, searchString, replacement);
    }

    @ZenMethod
    public static String replace(String text, String searchString, String replacement, int max) {
        return StringUtils.replace(text, searchString, replacement, max);
    }

    @ZenMethod
    public static String replaceIgnoreCase(String text, String searchString, String replacement, int max) {
        return StringUtils.replaceIgnoreCase(text, searchString, replacement, max);
    }

    @ZenMethod
    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return StringUtils.replaceEach(text, searchList, replacementList);
    }

    @ZenMethod
    public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
        return StringUtils.replaceEachRepeatedly(text, searchList, replacementList);
    }

    @ZenMethod
    public static String replaceChars(String str, String searchChars, String replaceChars) {
        return StringUtils.replaceChars(str, searchChars, replaceChars);
    }

    @ZenMethod
    public static String overlay(String str, String overlay, int start, int end) {
        return StringUtils.overlay(str, overlay, start, end);
    }

    @ZenMethod
    public static String chomp(String str) {
        return StringUtils.chomp(str);
    }

    @ZenMethod
    public static String chop(String str) {
        return StringUtils.chop(str);
    }

    @ZenMethod
    public static String repeat(String str, int repeat) {
        return StringUtils.repeat(str, repeat);
    }

    @ZenMethod
    public static String repeat(String str, String separator, int repeat) {
        return StringUtils.repeat(str, separator, repeat);
    }

    @ZenMethod
    public static String rightPad(String str, int size) {
        return StringUtils.rightPad(str, size);
    }

    @ZenMethod
    public static String rightPad(String str, int size, String padStr) {
        return StringUtils.rightPad(str, size, padStr);
    }

    @ZenMethod
    public static String leftPad(String str, int size) {
        return StringUtils.leftPad(str, size);
    }

    @ZenMethod
    public static String leftPad(String str, int size, String padStr) {
        return StringUtils.leftPad(str, size, padStr);
    }

    @ZenMethod
    public static String center(String str, int size) {
        return StringUtils.center(str, size);
    }

    @ZenMethod
    public static String center(String str, int size, String padStr) {
        return StringUtils.center(str, size, padStr);
    }

    @ZenMethod
    public static String upperCase(String str) {
        return StringUtils.upperCase(str);
    }

    @ZenMethod
    public static String lowerCase(String str) {
        return StringUtils.lowerCase(str);
    }

    @ZenMethod
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    @ZenMethod
    public static String uncapitalize(String str) {
        return StringUtils.uncapitalize(str);
    }

    @ZenMethod
    public static String swapCase(String str) {
        return StringUtils.swapCase(str);
    }

    @ZenMethod
    public static String defaultString(String str) {
        return StringUtils.defaultString(str);
    }

    @ZenMethod
    public static String defaultString(String str, String defaultStr) {
        return StringUtils.defaultString(str, defaultStr);
    }

    @ZenMethod
    public static String rotate(String str, int shift) {
        return StringUtils.rotate(str, shift);
    }

    @ZenMethod
    public static String reverse(String str) {
        return StringUtils.reverse(str);
    }

    @ZenMethod
    public static String reverseDelimited(String str, String separatorChar) {
        return StringUtils.reverseDelimited(str, separatorChar.charAt(0));
    }

    @ZenMethod
    public static String abbreviate(String str, int maxWidth) {
        return StringUtils.abbreviate(str, maxWidth);
    }

    @ZenMethod
    public static String abbreviate(String str, int offset, int maxWidth) {
        return StringUtils.abbreviate(str, offset, maxWidth);
    }

    @ZenMethod
    public static String abbreviateMiddle(String str, String middle, int length) {
        return StringUtils.abbreviateMiddle(str, middle, length);
    }

    @ZenMethod
    public static String difference(String str1, String str2) {
        return StringUtils.difference(str1, str2);
    }

    @ZenMethod
    public static String getCommonPrefix(String[] strs) {
        return StringUtils.getCommonPrefix(strs);
    }

    @ZenMethod
    public static String normalizeSpace(String str) {
        return StringUtils.normalizeSpace(str);
    }

    @ZenMethod
    public static String wrap(String str, String wrapWith) {
        return StringUtils.wrap(str, wrapWith);
    }

    @ZenMethod
    public static String wrapIfMissing(String str, String wrapWith) {
        return StringUtils.wrapIfMissing(str, wrapWith);
    }

    @ZenMethod
    public static boolean isEmpty(String cs) {
        return StringUtils.isEmpty(cs);
    }

    @ZenMethod
    public static boolean isNotEmpty(String cs) {
        return StringUtils.isNotEmpty(cs);
    }

    @ZenMethod
    public static boolean isAnyEmpty(String[] css) {
        return StringUtils.isAnyEmpty(css);
    }

    @ZenMethod
    public static boolean isNoneEmpty(String[] css) {
        return StringUtils.isNoneEmpty(css);
    }

    @ZenMethod
    public static boolean isBlank(String cs) {
        return StringUtils.isBlank(cs);
    }

    @ZenMethod
    public static boolean isNotBlank(String cs) {
        return StringUtils.isNotBlank(cs);
    }

    @ZenMethod
    public static boolean isAnyBlank(String[] css) {
        return StringUtils.isAnyBlank(css);
    }

    @ZenMethod
    public static boolean isNoneBlank(String[] css) {
        return StringUtils.isNoneBlank(css);
    }

    @ZenMethod
    public static boolean equals(String cs1, String cs2) {
        return StringUtils.equals(cs1, cs2);
    }

    @ZenMethod
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }

    @ZenMethod
    public static boolean equalsAny(String string, String[] searchStrings) {
        return StringUtils.equalsAny(string, searchStrings);
    }

    @ZenMethod
    public static boolean equalsAnyIgnoreCase(String string, String[]searchStrings) {
        return StringUtils.equalsAnyIgnoreCase(string, searchStrings);
    }

    @ZenMethod
    public static int indexOf(String seq, int searchChar) {
        return StringUtils.indexOf(seq, searchChar);
    }

    @ZenMethod
    public static int indexOf(String seq, int searchChar, int startPos) {
        return StringUtils.indexOf(seq, searchChar, startPos);
    }

    @ZenMethod
    public static int indexOf(String seq, String searchSeq) {
        return StringUtils.indexOf(seq, searchSeq);
    }

    @ZenMethod
    public static int indexOf(String seq, String searchSeq, int startPos) {
        return StringUtils.indexOf(seq, searchSeq, startPos);
    }

    @ZenMethod
    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        return StringUtils.ordinalIndexOf(str, searchStr, ordinal);
    }

    @ZenMethod
    public static int indexOfIgnoreCase(String str, String searchStr) {
        return StringUtils.indexOfIgnoreCase(str, searchStr);
    }

    @ZenMethod
    public static int indexOfIgnoreCase(String str, String searchStr, int startPos) {
        return StringUtils.indexOfIgnoreCase(str, searchStr, startPos);
    }

    @ZenMethod
    public static int lastIndexOf(String seq, int searchChar) {
        return StringUtils.lastIndexOf(seq, searchChar);
    }

    @ZenMethod
    public static int lastIndexOf(String seq, int searchChar, int startPos) {
        return StringUtils.lastIndexOf(seq, searchChar, startPos);
    }

    @ZenMethod
    public static int lastIndexOf(String seq, String searchSeq) {
        return StringUtils.lastIndexOf(seq, searchSeq);
    }

    @ZenMethod
    public static int lastOrdinalIndexOf(String str, String searchStr, int ordinal) {
        return StringUtils.lastOrdinalIndexOf(str, searchStr, ordinal);
    }

    @ZenMethod
    public static int lastIndexOf(String seq, String searchSeq, int startPos) {
        return StringUtils.lastIndexOf(seq, searchSeq, startPos);
    }

    @ZenMethod
    public static int lastIndexOfIgnoreCase(String str, String searchStr) {
        return StringUtils.lastIndexOfIgnoreCase(str, searchStr);
    }

    @ZenMethod
    public static int lastIndexOfIgnoreCase(String str, String searchStr, int startPos) {
        return StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);
    }

    @ZenMethod
    public static boolean contains(String seq, int searchChar) {
        return StringUtils.contains(seq, searchChar);
    }

    @ZenMethod
    public static boolean contains(String seq, String searchSeq) {
        return StringUtils.contains(seq, searchSeq);
    }

    @ZenMethod
    public static boolean containsIgnoreCase(String str, String searchStr) {
        return StringUtils.containsIgnoreCase(str, searchStr);
    }

    @ZenMethod
    public static boolean containsWhitespace(String seq) {
        return StringUtils.containsWhitespace(seq);
    }

    @ZenMethod
    public static int indexOfAny(String cs, String searchChars) {
        return StringUtils.indexOfAny(cs, searchChars);
    }

    @ZenMethod
    public static boolean containsAny(String cs, String searchChars) {
        return StringUtils.containsAny(cs, searchChars);
    }

    @ZenMethod
    public static boolean containsAny(String cs, String[] searchStrings) {
        return StringUtils.containsAny(cs, searchStrings);
    }

    @ZenMethod
    public static int indexOfAnyBut(String seq, String searchChars) {
        return StringUtils.indexOfAnyBut(seq, searchChars);
    }

    @ZenMethod
    public static boolean containsOnly(String cs, String validChars) {
        return StringUtils.containsOnly(cs, validChars);
    }

    @ZenMethod
    public static boolean containsNone(String cs, String invalidChars) {
        return StringUtils.containsNone(cs, invalidChars);
    }

    @ZenMethod
    public static int indexOfAny(String str, String[] searchStrs) {
        return StringUtils.indexOfAny(str, searchStrs);
    }

    @ZenMethod
    public static int lastIndexOfAny(String str, String[] searchStrs) {
        return StringUtils.lastIndexOfAny(str, searchStrs);
    }

    @ZenMethod
    public static String join(Object[] array, String separator) {
        return StringUtils.join(array, separator);
    }

    @ZenMethod
    public static String join(long[] array, String separator) {
        return StringUtils.join(array, separator);
    }

    @ZenMethod
    public static String join(int[] array, String separator) {
        return StringUtils.join(array, separator);
    }

    @ZenMethod
    public static String join(short[] array, String separator) {
        return StringUtils.join(array, separator);
    }

    @ZenMethod
    public static String join(float[] array, String separator) {
        return StringUtils.join(array, separator);
    }

    @ZenMethod
    public static String join(double[] array, String separator) {
        return StringUtils.join(array, separator);
    }

    @ZenMethod
    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        return StringUtils.join(array, separator, startIndex, endIndex);
    }

    @ZenMethod
    public static String join(long[] array, String separator, int startIndex, int endIndex) {
        return StringUtils.join(array, separator, startIndex, endIndex);
    }

    @ZenMethod
    public static String join(int[] array, String separator, int startIndex, int endIndex) {
        return StringUtils.join(array, separator, startIndex, endIndex);
    }

    @ZenMethod
    public static String join(short[] array, String separator, int startIndex, int endIndex) {
        return StringUtils.join(array, separator, startIndex, endIndex);
    }

    @ZenMethod
    public static String join(float[] array, String separator, int startIndex, int endIndex) {
        return StringUtils.join(array, separator, startIndex, endIndex);
    }

    @ZenMethod
    public static String join(double[] array, String separator, int startIndex, int endIndex) {
        return StringUtils.join(array, separator, startIndex, endIndex);
    }

    @ZenMethod
    public static String joinWith(String separator, Object[] objects) {
        return StringUtils.joinWith(separator, objects);
    }

    @ZenMethod
    public static int length(String cs) {
        return StringUtils.length(cs);
    }

    @ZenMethod
    public static int countMatches(String str, String sub) {
        return StringUtils.countMatches(str, sub);
    }

    @ZenMethod
    public static boolean isAlpha(String cs) {
        return StringUtils.isAlpha(cs);
    }

    @ZenMethod
    public static boolean isAlphaSpace(String cs) {
        return StringUtils.isAlphaSpace(cs);
    }

    @ZenMethod
    public static boolean isAlphanumeric(String cs) {
        return StringUtils.isAlphanumeric(cs);
    }

    @ZenMethod
    public static boolean isAlphanumericSpace(String cs) {
        return StringUtils.isAlphanumericSpace(cs);
    }

    @ZenMethod
    public static boolean isAsciiPrintable(String cs) {
        return StringUtils.isAsciiPrintable(cs);
    }

    @ZenMethod
    public static boolean isNumeric(String cs) {
        return StringUtils.isNumeric(cs);
    }

    @ZenMethod
    public static boolean isNumericSpace(String cs) {
        return StringUtils.isNumericSpace(cs);
    }

    @ZenMethod
    public static boolean isWhitespace(String cs) {
        return StringUtils.isWhitespace(cs);
    }

    @ZenMethod
    public static boolean isAllLowerCase(String cs) {
        return StringUtils.isAllLowerCase(cs);
    }

    @ZenMethod
    public static boolean isAllUpperCase(String cs) {
        return StringUtils.isAllUpperCase(cs);
    }

    @ZenMethod
    public static int indexOfDifference(String cs1, String cs2) {
        return StringUtils.indexOfDifference(cs1, cs2);
    }

    @ZenMethod
    public static int indexOfDifference(String[] css) {
        return StringUtils.indexOfDifference(css);
    }

    @ZenMethod
    public static int getLevenshteinDistance(String s, String t) {
        return StringUtils.getLevenshteinDistance(s, t);
    }

    @ZenMethod
    public static int getLevenshteinDistance(String s, String t, int threshold) {
        return StringUtils.getLevenshteinDistance(s, t, threshold);
    }

    @ZenMethod
    public static double getJaroWinklerDistance(String first, String second) {
        return StringUtils.getJaroWinklerDistance(first, second);
    }

    @ZenMethod
    public static boolean startsWith(String str, String prefix) {
        return StringUtils.startsWith(str, prefix);
    }

    @ZenMethod
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return StringUtils.startsWithIgnoreCase(str, prefix);
    }

    @ZenMethod
    public static boolean startsWithAny(String sequence, String[] searchStrings) {
        return StringUtils.startsWithAny(sequence, searchStrings);
    }

    @ZenMethod
    public static boolean endsWith(String str, String suffix) {
        return StringUtils.endsWith(str, suffix);
    }

    @ZenMethod
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return StringUtils.endsWithIgnoreCase(str, suffix);
    }

    @ZenMethod
    public static boolean endsWithAny(String sequence, String[] searchStrings) {
        return StringUtils.endsWithAny(sequence, searchStrings);
    }

    @ZenMethod
    public static String appendIfMissing(String str, String suffix, String[] suffixes) {
        return StringUtils.appendIfMissing(str, suffix, suffixes);
    }

    @ZenMethod
    public static String appendIfMissingIgnoreCase(String str, String suffix, String[] suffixes) {
        return StringUtils.appendIfMissingIgnoreCase(str, suffix, suffixes);
    }

    @ZenMethod
    public static String prependIfMissing(String str, String prefix, String[] prefixes) {
        return StringUtils.prependIfMissing(str, prefix, prefixes);
    }

    @ZenMethod
    public static String prependIfMissingIgnoreCase(String str, String prefix, String[] prefixes) {
        return StringUtils.prependIfMissingIgnoreCase(str, prefix, prefixes);
    }
}
