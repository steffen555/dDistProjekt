import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alexandra on 16/04/16.
 */
public class LocalEventHistory implements IEventHistory {

    protected LinkedBlockingQueue<MyTextEvent> eventHistory = new LinkedBlockingQueue<MyTextEvent>();

    @Override
    public MyTextEvent take() throws InterruptedException {
        return eventHistory.take();
    }

    @Override
    public void add(MyTextEvent textEvent) {
        eventHistory.add(textEvent);
    }
}
