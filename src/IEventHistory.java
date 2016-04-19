
/**
 * Created by Alexandra on 16/04/16.
 */
public interface IEventHistory {

    MyTextEvent take() throws InterruptedException;

    void add(MyTextEvent textEvent);
}
