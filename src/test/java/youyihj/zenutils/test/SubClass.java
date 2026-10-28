package youyihj.zenutils.test;

/**
 * @author youyihj
 */
public abstract class SubClass extends CommonClass {
    public SubClass(String field) {
        super(field);
    }

    public abstract int method(String s, long l);

    @Override
    public int method(String s, int i) {
        return 0;
    }
}
