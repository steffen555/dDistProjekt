import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;

public abstract class Event implements Serializable {
    private final int id;
    private final HashMap<Integer, Integer> timeStamp;
    private Socket receivingSocket;

    public Event(int id, HashMap<Integer, Integer> timeStamp) {
        this.id = id;
        this.timeStamp = timeStamp;
    }

    int getID() {
        return id;
    }

    HashMap<Integer, Integer> getTimeStamp() {
        return timeStamp;
    }

    public void setReceivingSocket(Socket socket){
        receivingSocket = socket;
    }

    public Socket getReceivingSocket(){
        return receivingSocket;
    }
}
