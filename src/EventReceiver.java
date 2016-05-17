import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class EventReceiver extends Thread{
    private Socket socket;
    private LinkedBlockingQueue queue;
    private boolean justContinue = true;
    private Communicator communicator;
    private ObjectInputStream input;

    public EventReceiver(Socket socket, LinkedBlockingQueue queue, Communicator communicator, ObjectInputStream input) {
        this.socket = socket;
        this.queue = queue;
        this.communicator = communicator;
        this.input = input;
    }

    public void run() {
        while (true) {
            if (socket != null && justContinue) {
                try {
                    if (input == null) {
                        input = new ObjectInputStream(socket.getInputStream());
                    }
                    MyTextEvent inputEvent = (MyTextEvent) input.readObject();
                    queue.add(inputEvent);
                } catch (IOException e) {
                    communicator.disconnect(this, socket);
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
