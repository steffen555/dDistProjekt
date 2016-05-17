import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class EventReceiver extends Thread{
    private Socket socket;
    private LinkedBlockingQueue<Object> queue;
    private boolean justContinue = true;
    private Communicator communicator;
    private ObjectInputStream input;

    public EventReceiver(Socket socket, LinkedBlockingQueue<Object> queue, Communicator communicator) {
        this.socket = socket;
        this.queue = queue;
        this.communicator = communicator;
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
                    communicator.disconnect(socket);
                    System.out.println("Disconnected from a socket.");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
