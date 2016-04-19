import java.util.concurrent.LinkedBlockingQueue;

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
