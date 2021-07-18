package youyihj.zenutils.impl.util.delay;

import youyihj.zenutils.api.util.delay.DelayRunnable;
import youyihj.zenutils.api.util.delay.IsExecute;
import youyihj.zenutils.impl.util.LogMTErrorRunnableWrapper;

/**
 * @author youyihj
 */
public class DelayRunnableList {
    private Node first;
    private Node last;

    public DelayRunnableList add(DelayRunnable runnable, IsExecute isExecute) {
        Node node = new Node(runnable, isExecute);

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
            current.run();

            synchronized (this) {
                current = current.next;
            }
        }

        this.clear();
    }

    private static class Node implements Runnable {
        Node next;
        IsExecute isExecute;
        final Runnable runnable;

        public Node(DelayRunnable runnable, IsExecute isExecute) {
            this.runnable = LogMTErrorRunnableWrapper.create(runnable);
            this.isExecute = isExecute;
        }

        @Override
        public void run() {
            if(this.isExecute.isExec())
                this.runnable.run();
        }
    }
}
