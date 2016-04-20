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
            if (output == null) {
                output = new ObjectOutputStream(socket.getOutputStream());
            }
            output.writeObject(textEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            if (socket != null) {
                try {
                    if (input == null) {
                        input = new ObjectInputStream(socket.getInputStream());
                    }
                    MyTextEvent inputEvent = (MyTextEvent) input.readObject();
                    eventHistory.add(inputEvent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
