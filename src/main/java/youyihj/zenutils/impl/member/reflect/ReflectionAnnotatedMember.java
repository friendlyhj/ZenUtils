package youyihj.zenutils.impl.member.reflect;

import youyihj.zenutils.impl.member.AnnotatedMember;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author youyihj
 */
public class ReflectionAnnotatedMember implements AnnotatedMember {
    private final AnnotatedElement element;

    public ReflectionAnnotatedMember(AnnotatedElement element) {
        this.element = element;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return element.getAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationClass) {
        return element.isAnnotationPresent(annotationClass);
    }
}
