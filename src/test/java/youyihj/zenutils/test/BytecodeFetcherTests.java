package youyihj.zenutils.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.LookupRequester;
import youyihj.zenutils.impl.member.bytecode.BytecodeClassDataFetcher;
import youyihj.zenutils.impl.member.reflect.ReflectionClassDataFetcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class BytecodeFetcherTests {
    private ClassDataFetcher classDataFetcher;

    @Before
    public void setupClassDataFetcher() {
        classDataFetcher = new BytecodeClassDataFetcher(
                new ReflectionClassDataFetcher(this.getClass().getClassLoader()),
                new GradleTestBytesProvider()
        );
    }

    @Test
    public void testCommonClass() throws ClassNotFoundException {
        ClassData commonClassData = introduce("CommonClass");

        Assert.assertEquals(CommonClass.class.getName(), commonClassData.name());
        Assert.assertEquals("Ljava/lang/String;", commonClassData.constructors(LookupRequester.SELF).get(0).parameters().get(0).descriptor());
        Assert.assertTrue(hasMethod(commonClassData, "method", "(Ljava/lang/String;I)I"));
        Assert.assertTrue(hasMethod(commonClassData, "method2", "(I)I"));
        Assert.assertEquals(1, commonClassData.fields(LookupRequester.SELF).size());
    }

    @Test
    public void testSubClass() throws ClassNotFoundException {
        ClassData subClassData = introduce("SubClass");

        ClassData superClassData = subClassData.superClass();
        Assert.assertNotNull(superClassData);
        Assert.assertEquals(CommonClass.class.getName(), superClassData.name());
        Assert.assertEquals("Ljava/lang/String;", subClassData.constructors(LookupRequester.SELF).get(0).parameters().get(0).descriptor());

        List<String> methodDescriptors = subClassData.methods("method", LookupRequester.SELF)
                .stream()
                .map(ExecutableData::descriptor)
                .collect(Collectors.toList());
        Assert.assertTrue(methodDescriptors.contains("(Ljava/lang/String;I)I"));
        Assert.assertTrue(methodDescriptors.contains("(Ljava/lang/String;J)I"));
        long intOverloadCount = methodDescriptors.stream()
                .filter("(Ljava/lang/String;I)I"::equals)
                .count();
        Assert.assertEquals(1L, intOverloadCount);

        List<ExecutableData> inheritedMethod2 = subClassData.methods("method2", LookupRequester.SELF);
        Assert.assertEquals(1, inheritedMethod2.size());
        Assert.assertEquals(CommonClass.class.getName(), inheritedMethod2.get(0).declaringClass().name());
    }

    @Test
    public void testGenericClass() throws ClassNotFoundException {
        ClassData genericClassData = introduce("GenericClass");

        Map<String, FieldData> fieldsByName = new HashMap<>();
        for (FieldData field : genericClassData.fields(LookupRequester.SELF)) {
            fieldsByName.put(field.name(), field);
        }
        Assert.assertEquals("Ljava/lang/Object;", fieldsByName.get("a").type().toString());
        Assert.assertEquals("Ljava/lang/CharSequence;", fieldsByName.get("b").type().toString());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/CharSequence;>;", fieldsByName.get("listB").type().toString());
        Assert.assertEquals("Ljava/lang/reflect/Type;", fieldsByName.get("t").type().toString());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/reflect/Type;>;", fieldsByName.get("tList").type().toString());

        ExecutableData plain = getSingleMethod(genericClassData, "plain");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/String;>;", plain.returnType().toString());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/String;>;", plain.parameters().get(1).toString());

        ExecutableData nested = getSingleMethod(genericClassData, "nested");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/String;>;", nested.returnType().toString());
        Assert.assertEquals("Ljava/util/List<Ljava/util/List<Ljava/lang/String;>;>;", nested.parameters().get(1).toString());

        ExecutableData plainTypes = getSingleMethod(genericClassData, "plainTypes");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/reflect/Type;>;", plainTypes.returnType().toString());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/reflect/Type;>;", plainTypes.parameters().get(0).toString());

        ExecutableData referA = getSingleMethod(genericClassData, "referA");
        Assert.assertEquals("Ljava/lang/Object;", referA.parameters().get(0).toString());

        ExecutableData referB = getSingleMethod(genericClassData, "referB");
        Assert.assertEquals("Ljava/lang/CharSequence;", referB.parameters().get(0).toString());

        ExecutableData referListA = getSingleMethod(genericClassData, "referListA");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/Object;>;", referListA.parameters().get(0).toString());

        ExecutableData referListB = getSingleMethod(genericClassData, "referListB");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/CharSequence;>;", referListB.parameters().get(0).toString());

        ExecutableData referListA2 = getSingleMethod(genericClassData, "referListA2");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/Object;>;", referListA2.parameters().get(0).toString());

        ExecutableData referListB2 = getSingleMethod(genericClassData, "referListB2");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/CharSequence;>;", referListB2.parameters().get(0).toString());

        ExecutableData referC = getSingleMethod(genericClassData, "referC");
        Assert.assertEquals("Ljava/lang/Object;", referC.parameters().get(0).toString());

        ExecutableData referD = getSingleMethod(genericClassData, "referD");
        Assert.assertEquals("Ljava/util/Collection<Ljava/lang/Object;>;", referD.parameters().get(0).toString());

        ExecutableData referListC = getSingleMethod(genericClassData, "referListC");
        Assert.assertEquals("Ljava/util/List<Ljava/lang/Object;>;", referListC.parameters().get(0).toString());

        ExecutableData referListD = getSingleMethod(genericClassData, "referListD");
        Assert.assertEquals("Ljava/util/List<Ljava/util/Collection<Ljava/lang/Object;>;>;", referListD.parameters().get(0).toString());

        ExecutableData combination = getSingleMethod(genericClassData, "combination");
        Assert.assertEquals(
                "Ljava/util/Map<Ljava/util/Map<Ljava/lang/Object;Ljava/lang/CharSequence;>;Ljava/util/Map<Ljava/lang/Object;Ljava/util/Collection<Ljava/lang/Object;>;>;>;",
                combination.returnType().toString()
        );
        Assert.assertEquals("Ljava/lang/Object;", combination.parameters().get(0).toString());
        Assert.assertEquals("Ljava/lang/CharSequence;", combination.parameters().get(1).toString());
        Assert.assertEquals("Ljava/lang/Object;", combination.parameters().get(2).toString());
        Assert.assertEquals("Ljava/util/Collection<Ljava/lang/Object;>;", combination.parameters().get(3).toString());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/Object;>;", combination.parameters().get(4).toString());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/CharSequence;>;", combination.parameters().get(5).toString());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/Object;>;", combination.parameters().get(6).toString());
        Assert.assertEquals("Ljava/util/List<Ljava/util/Collection<Ljava/lang/Object;>;>;", combination.parameters().get(7).toString());
    }

    private static boolean hasMethod(ClassData classData, String name, String descriptor) {
        return classData.methods(name, LookupRequester.SELF)
                .stream()
                .map(ExecutableData::descriptor)
                .anyMatch(descriptor::equals);
    }

    private ClassData introduce(String name) throws ClassNotFoundException {
        return classDataFetcher.forName("youyihj/zenutils/test/" + name);
    }

    private static ExecutableData getSingleMethod(ClassData classData, String name) {
        List<ExecutableData> methods = classData.methods(name, LookupRequester.SELF);
        Assert.assertEquals(1, methods.size());
        return methods.get(0);
    }
}
