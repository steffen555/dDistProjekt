import java.util.HashMap;

public class InfoEvent extends Event {

    private String ip;
    private HashMap<Integer,String> connections;

    public InfoEvent(int id, String ip) {
        super(id);
        this.ip = ip;
        connections = new HashMap<Integer,String>();
    }

    public String getIP() {
        return ip;
    }

    public void addConnection(int id, String ip) {
        connections.put(id, ip);
    }

    public HashMap<Integer, String> getConnections() {
        return connections;
    }
}
