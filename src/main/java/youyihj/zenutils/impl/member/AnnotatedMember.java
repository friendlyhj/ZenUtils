package youyihj.zenutils.impl.member;

import java.lang.annotation.Annotation;

/**
 * @author youyihj
 */
public interface AnnotatedMember {
    <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationClass);

    <A extends Annotation> A getAnnotation(Class<A> annotationClass);
}
