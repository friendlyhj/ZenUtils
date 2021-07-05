package youyihj.zenutils.impl.util.delay;

import youyihj.zenutils.api.util.delay.DelayRunnable;

/**
 * @author youyihj
 */
public class DelayRunnableList {
    private Node first;
    private Node last;
    public final String uid;
    public boolean isReady = true;

    public DelayRunnableList(String uid) {
        this.uid = uid;
    }

    public DelayRunnableList add(DelayRunnable runnable) {
        Node node = new Node(runnable);

        synchronized (this) {
            if (first == null)
                first = node;
            if (last != null)
                last.next = node;
            last = node;
        }
        return this;
    }

    public void clear() {
        first = last = null;
    }

    public void run() {
        Node current;

        synchronized (this) {
            current = first;
        }

        while (current != null) {
            current.runnable.run();
            synchronized (this) {
                current = current.next;
            }
        }

        this.clear();
    }

    private static class Node {
        final DelayRunnable runnable;
        Node next;

        public Node(DelayRunnable runnable) {
            this.runnable = runnable;
        }
    }
}
