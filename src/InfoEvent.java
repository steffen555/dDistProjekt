import java.util.HashMap;

public class InfoEvent {

    private int id;
    private String ip;
    private HashMap<Integer,String> connections;

    public InfoEvent(int id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public int getId() {
        return id;
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
