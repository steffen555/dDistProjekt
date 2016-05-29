import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

class EventReceiver extends Thread {
    private final Socket socket;
    private final LinkedBlockingQueue<TextEvent> textQueue;
    private final LinkedBlockingQueue<InfoEvent> infoQueue;
    private final Communicator communicator;
    private ObjectInputStream input;

    EventReceiver(Socket socket, LinkedBlockingQueue<TextEvent> textQueue, LinkedBlockingQueue<InfoEvent> infoQueue, Communicator communicator) {
        this.socket = socket;
        this.textQueue = textQueue;
        this.infoQueue = infoQueue;
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
                        inputEvent.setReceivingSocket(socket);
                    } catch (ClassCastException e) {
                        System.out.println("I received something I don't understand");
                        break;
                    }
                    if (TextEvent.class.isAssignableFrom(inputEvent.getClass())) {
                        textQueue.add((TextEvent) inputEvent);
                        communicator.sendExcept(inputEvent, socket);
                    } else if (InfoEvent.class.isAssignableFrom(inputEvent.getClass())) {
                        System.out.println("Received InfoEvent");
                        infoQueue.add((InfoEvent) inputEvent);
                    }
                } catch (IOException e) {
                    communicator.disconnect(socket);
                    System.out.println("Disconnected from a socket.");
                    communicator.connectToNeighbour(socket);
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}