import java.util.HashSet;
import java.util.Set;

public class ConnectionsEvent extends InfoEvent {

    private String ip;
    private Set<String> connections;

    public ConnectionsEvent(int id, String ip) {
        super(id);
        this.ip = ip;
        connections = new HashSet<>();
    }

    public String getIp() {
        return ip;
    }

    public void addConnection(String ip) {
        connections.add(ip);
    }

    public void addConnections(Set<String> connections) {
        this.connections.addAll(connections);
    }

    public Set<String> getConnections() {
        return connections;
    }
}
