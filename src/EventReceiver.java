import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

class EventReceiver extends Thread {
    private final Socket socket;
    private final LinkedBlockingQueue<Event> queue;
    private final Communicator communicator;
    private ObjectInputStream input;

    EventReceiver(Socket socket, LinkedBlockingQueue<Event> queue, Communicator communicator) {
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
                    Object inputObject = input.readObject();
                    Event inputEvent;
                    try {
                        inputEvent = (Event) inputObject;
                    } catch (ClassCastException e) {
                        System.out.println("I received something I don't understand");
                        break;
                    }
                    if (TextEvent.class.isAssignableFrom(inputEvent.getClass())) {
                        queue.add(inputEvent);
                        communicator.sendExcept(inputObject, socket);
                    } else if(InfoEvent.class.isAssignableFrom(inputEvent.getClass())){
                        System.out.println("Received InfoEvent");
                        //queue.add(inputEvent);
                    }
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
