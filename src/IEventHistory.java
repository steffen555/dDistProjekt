interface IEventHistory {

    TextEvent take() throws InterruptedException;

    void add(TextEvent textEvent);
}
