package youyihj.zenutils.impl.member.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GenericSignatureUtils {
    private static final String OBJECT_SIGNATURE = "Ljava/lang/Object;";

    private GenericSignatureUtils() {
    }

    public static String eraseMethodReturnSignature(String methodSignature, Map<String, String> bounds) {
        return eraseTypeVariables(extractMethodReturnSignature(methodSignature), bounds);
    }

    public static List<String> eraseMethodParameterSignatures(String methodSignature, Map<String, String> bounds) {
        if (methodSignature == null) {
            return Collections.emptyList();
        }
        try {
            List<String> erased = new ArrayList<>();
            for (String genericInfo : extractMethodParameterSignatures(methodSignature)) {
                erased.add(eraseTypeVariables(genericInfo, bounds));
            }
            return erased;
        } catch (IllegalArgumentException ignored) {
            return Collections.emptyList();
        }
    }

    public static List<String> extractMethodParameterSignatures(String methodSignature) {
        int lParen = methodSignature.indexOf('(');
        int rParen = methodSignature.indexOf(')', lParen + 1);
        if (lParen < 0 || rParen < 0 || rParen < lParen) {
            throw new IllegalArgumentException("Invalid method signature: " + methodSignature);
        }

        List<String> paramTypes = new ArrayList<>();
        int i = lParen + 1;
        while (i < rParen) {
            int start = i;
            i = consumeTypeSignature(methodSignature, i, rParen);
            paramTypes.add(methodSignature.substring(start, i));
        }
        return paramTypes;
    }

    public static String extractMethodReturnSignature(String methodSignature) {
        if (methodSignature == null) {
            return null;
        }
        int rightParen = methodSignature.indexOf(')');
        if (rightParen < 0 || rightParen + 1 >= methodSignature.length()) {
            return null;
        }
        int returnStart = rightParen + 1;
        int returnEnd = consumeReturnTypeSignature(methodSignature, returnStart, methodSignature.length());
        return methodSignature.substring(returnStart, returnEnd);
    }

    public static Map<String, String> extractTypeVariableErasureBounds(String signature) {
        if (signature == null || signature.isEmpty() || signature.charAt(0) != '<') {
            return Collections.emptyMap();
        }

        Map<String, String> bounds = new LinkedHashMap<>();
        int i = 1;
        int end = signature.length();

        while (i < end && signature.charAt(i) != '>') {
            int nameStart = i;
            int colon = signature.indexOf(':', nameStart);
            if (colon < 0) {
                break;
            }
            String variableName = signature.substring(nameStart, colon);
            i = colon + 1;

            String erasureBound = null;
            if (i < end && signature.charAt(i) != ':') {
                int boundStart = i;
                i = consumeFieldTypeSignature(signature, i, end);
                erasureBound = signature.substring(boundStart, i);
            }

            while (i < end && signature.charAt(i) == ':') {
                i++;
                if (i >= end || signature.charAt(i) == ':') {
                    continue;
                }
                int boundStart = i;
                i = consumeFieldTypeSignature(signature, i, end);
                if (erasureBound == null) {
                    erasureBound = signature.substring(boundStart, i);
                }
            }

            String normalizedBound = erasureBound != null ? eraseTypeVariables(erasureBound, bounds) : OBJECT_SIGNATURE;
            bounds.put(variableName, normalizedBound);
        }

        return bounds.isEmpty() ? Collections.emptyMap() : bounds;
    }

    public static Map<String, String> mergeTypeVariableBounds(Map<String, String> classBounds, String methodSignature) {
        Map<String, String> methodBounds = extractTypeVariableErasureBounds(methodSignature);
        if (methodBounds.isEmpty()) {
            return classBounds;
        }
        Map<String, String> merged = new LinkedHashMap<>(classBounds);
        // Method type variables shadow class-level variables with same symbol.
        merged.putAll(methodBounds);
        return merged;
    }

    public static String eraseTypeVariables(String signature, Map<String, String> bounds) {
        if (signature == null || signature.isEmpty()) {
            return signature;
        }

        Map<String, String> safeBounds = bounds == null ? Collections.emptyMap() : bounds;
        StringBuilder result = new StringBuilder(signature.length());
        int index = appendErasedTypeSignature(signature, 0, signature.length(), safeBounds, result);
        if (index < signature.length()) {
            result.append(signature, index, signature.length());
        }
        return result.toString();
    }

    private static int appendErasedTypeSignature(String signature, int index, int endExclusive, Map<String, String> bounds, StringBuilder out) {
        if (index >= endExclusive) {
            return index;
        }

        char current = signature.charAt(index);
        switch (current) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
            case 'V':
                out.append(current);
                return index + 1;
            case '[':
                out.append('[');
                return appendErasedTypeSignature(signature, index + 1, endExclusive, bounds, out);
            case 'T':
                return appendTypeVariableErasure(signature, index, endExclusive, bounds, out);
            case 'L':
                return appendErasedClassTypeSignature(signature, index, endExclusive, bounds, out);
            case '*':
                out.append(OBJECT_SIGNATURE);
                return index + 1;
            case '+':
            case '-':
                return appendErasedTypeSignature(signature, index + 1, endExclusive, bounds, out);
            default:
                out.append(current);
                return index + 1;
        }
    }

    private static int appendTypeVariableErasure(String signature, int index, int endExclusive, Map<String, String> bounds, StringBuilder out) {
        if (isTypeVariableMarker(signature, index)) {
            out.append('T');
            return index + 1;
        }

        int semicolon = signature.indexOf(';', index);
        if (semicolon < 0 || semicolon >= endExclusive) {
            out.append(signature, index, endExclusive);
            return endExclusive;
        }
        String variableName = signature.substring(index + 1, semicolon);
        out.append(bounds.getOrDefault(variableName, OBJECT_SIGNATURE));
        return semicolon + 1;
    }

    private static boolean isTypeVariableMarker(String signature, int index) {
        return index > 0 && signature.charAt(index) == 'T' && (signature.charAt(index - 1) == '/' || signature.charAt(index - 1) == '.' || signature.charAt(index - 1) == '$');
    }

    private static int appendErasedClassTypeSignature(String signature, int index, int endExclusive, Map<String, String> bounds, StringBuilder out) {
        out.append('L');
        int i = index + 1;
        while (i < endExclusive) {
            char current = signature.charAt(i);
            if (current == '<') {
                out.append('<');
                i++;
                while (i < endExclusive && signature.charAt(i) != '>') {
                    i = appendErasedTypeSignature(signature, i, endExclusive, bounds, out);
                }
                if (i >= endExclusive) {
                    throw new IllegalArgumentException("Unterminated class type arguments in: " + signature);
                }
                out.append('>');
                i++;
                continue;
            }

            out.append(current);
            if (current == ';') {
                return i + 1;
            }
            i++;
        }
        throw new IllegalArgumentException("Unterminated class type signature in: " + signature);
    }

    private static int consumeReturnTypeSignature(String signature, int index, int endExclusive) {
        if (index >= endExclusive) {
            throw new IllegalArgumentException("Unexpected end while parsing return type: " + signature);
        }
        if (signature.charAt(index) == 'V') {
            return index + 1;
        }
        return consumeTypeSignature(signature, index, endExclusive);
    }

    private static int consumeTypeSignature(String signature, int index, int endExclusive) {
        if (index >= endExclusive) {
            throw new IllegalArgumentException("Unexpected end while parsing: " + signature);
        }

        char current = signature.charAt(index);
        switch (current) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
                return index + 1;
            case '[':
                return consumeTypeSignature(signature, index + 1, endExclusive);
            case 'T':
                return consumeTypeVariableSignature(signature, index, endExclusive);
            case 'L':
                return consumeClassTypeSignature(signature, index, endExclusive);
            default:
                throw new IllegalArgumentException("Unexpected character '" + current + "' at " + index + " in: " + signature);
        }
    }

    private static int consumeTypeVariableSignature(String signature, int index, int endExclusive) {
        int semicolon = signature.indexOf(';', index);
        if (semicolon < 0 || semicolon >= endExclusive) {
            throw new IllegalArgumentException("Unterminated type variable in: " + signature);
        }
        return semicolon + 1;
    }

    private static int consumeFieldTypeSignature(String signature, int index, int endExclusive) {
        if (index >= endExclusive) {
            throw new IllegalArgumentException("Unexpected end while parsing: " + signature);
        }

        char current = signature.charAt(index);
        if (current == '[') {
            return consumeFieldTypeSignature(signature, index + 1, endExclusive);
        }
        if (current == 'T') {
            return consumeTypeVariableSignature(signature, index, endExclusive);
        }
        if (current == 'L') {
            return consumeClassTypeSignature(signature, index, endExclusive);
        }

        throw new IllegalArgumentException("Unexpected field type signature char '" + current + "' in: " + signature);
    }

    private static int consumeClassTypeSignature(String signature, int index, int endExclusive) {
        int i = index + 1;
        int genericDepth = 0;

        while (i < endExclusive) {
            char current = signature.charAt(i);
            if (current == '<') {
                genericDepth++;
            } else if (current == '>') {
                genericDepth--;
                if (genericDepth < 0) {
                    throw new IllegalArgumentException("Unbalanced generic brackets in: " + signature);
                }
            } else if (current == ';' && genericDepth == 0) {
                return i + 1;
            }
            i++;
        }

        throw new IllegalArgumentException("Unterminated class type signature in: " + signature);
    }
}
