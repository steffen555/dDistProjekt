public interface IEventHistory {

    MyTextEvent take() throws InterruptedException;

    void add(MyTextEvent textEvent);
}
