import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

class EventReceiver extends Thread {
    private final Socket socket;
    private final LinkedBlockingQueue<Object> queue;
    private final Communicator communicator;
    private ObjectInputStream input;

    EventReceiver(Socket socket, LinkedBlockingQueue<Object> queue, Communicator communicator) {
        this.socket = socket;
        this.queue = queue;
        this.communicator = communicator;
    }

    public void run() {
        while (true) {
            if (socket != null) {
                try {
                    if (input == null) {
                        input = new ObjectInputStream(socket.getInputStream());
                    }
                    Event inputEvent = (Event) input.readObject();
                    inputEvent.setReceivingSocket(socket);
                    queue.add(inputEvent);
                    communicator.sendExcept(inputEvent, socket);
                } catch (IOException e) {
                    communicator.disconnect(socket);
                    System.out.println("Disconnected from a socket.");
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
