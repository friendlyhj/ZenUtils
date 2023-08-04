package youyihj.zenutils.impl.util.catenation;

/**
 * @author youyihj
 */
public class SleepTask extends TimerTask {

    public SleepTask(long sleepTime) {
        super(sleepTime, ((timer, world, context) -> timer.update()));
    }

}
