import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class WebEventHistory extends Thread implements IEventHistory {

    protected LinkedBlockingQueue<MyTextEvent> eventHistory = new LinkedBlockingQueue<MyTextEvent>();
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public WebEventHistory(Socket socket) {
        this.socket = socket;
    }

    @Override
    public MyTextEvent take() throws InterruptedException {
        return eventHistory.take();
    }

    @Override
    public void add(MyTextEvent textEvent) {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                input = new ObjectInputStream(socket.getInputStream());
                MyTextEvent inputEvent = (MyTextEvent) input.readObject();
                eventHistory.add(inputEvent);
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
