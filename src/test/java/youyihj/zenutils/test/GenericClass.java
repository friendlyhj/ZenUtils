package youyihj.zenutils.test;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
public abstract class GenericClass<A, B extends CharSequence> {
    public A a;
    public B b;
    public List<B> listB;
    public Type t;
    public List<Type> tList;

    public abstract List<String> plain(int x, List<String> strings, int y);

    public abstract List<String> nested(int x, List<List<String>> strings, int y);

    public abstract List<Type> plainTypes(List<Type> types);

    public abstract void referA(A a);

    public abstract void referB(B b);

    public abstract void referListA(List<A> list);

    public abstract void referListB(List<B> list);

    public abstract void referListA2(List<? extends A> list);

    public abstract void referListB2(List<? extends B> list);

    public abstract <C> void referC(C c);

    public abstract <D extends Collection<?>> void referD(D d);

    public abstract <C> void referListC(List<C> list);

    public abstract <D extends Collection<?>> void referListD(List<D> list);

    public abstract <C, D extends Collection<?>> Map<Map<A, B>, Map<C, D>> combination(A a, B b, C c, D d, List<A> as, List<B> bs, List<C> cs, List<D> ds);
}
