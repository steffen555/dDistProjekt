import java.util.ArrayList;
import java.util.HashMap;

public class NewConnectionEvent extends InfoEvent {

    private String ip;
    private HashMap<Integer, String> connections;

    public NewConnectionEvent(int id, String ip) {
        super(id);
        this.ip = ip;
        connections = new HashMap<>();
    }

    public String getIp() {
        return ip;
    }

    public void addConnection(int id, String ip) {
        connections.put(id, ip);
    }

    public HashMap<Integer, String> getConnections() {
        return connections;
    }
}
