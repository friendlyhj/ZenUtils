package youyihj.zenutils.api.reload;

import java.lang.annotation.*;

/**
 * Marks an action that will not be affected by the action freezing.
 * In other words, it will be applied again when script reloading.
 * <p>
 * If the action requires some cleanup code, please add a public method called <code>undo</code> that rolls back changes carried by the action.
 * It shouldn't have any arguments and return value.
 * It will be called before script reloading.
 *
 * <b>Example</b>: <blockquote><pre>{@code
 * @Reloadable
 * public class TestAction implements IAction {
 *     public static final List<String> CONTENTS = new ArrayList<>();
 *
 *     private final String content;
 *
 *     public TestAction(String content) {
 *         this.content = content;
 *     }
 *
 *     @Override
 *     public void apply() {
 *         CONTENTS.add(content);
 *     }
 *
 *     public void undo() {
 *         CONTENTS.remove(content);
 *     }
 *
 *     @Override
 *     public String describe() {
 *         return "Adding " + content + " to content list";
 *     }
 * }
 * }</pre></blockquote
 *
 * @author youyihj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Reloadable {
}
