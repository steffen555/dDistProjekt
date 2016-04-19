import java.io.*;
import java.net.Socket;

public class WebEventHistory implements IEventHistory {
    public WebEventHistory(Socket socket) {
    }

    @Override
    public MyTextEvent take() throws InterruptedException {
        return null;
    }

    @Override
    public void add(MyTextEvent textEvent) {

    }
/*
    private void foo() {
        while (true) {
            Socket socket = waitForConnectionFromClient();

            if (socket != null) {
                System.out.println("Server from " + socket);
                try {
                    ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
                    Object o;
                    MyTextEvent mte = null;
                    // Read and print what the client is sending
                    while ((o = fromClient.readObject()) != null) { // Ctrl-D terminates the connection
                        if (o.getClass() == MyTextEvent.class)
                            mte = (MyTextEvent) o;
                        System.out.println("From the client: " + mte.toString());
                        history.add(mte);
                    }
                    socket.close();
                } catch (IOException e) {
                    // We report but otherwise ignore IOExceptions
                    System.err.println(e);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("Server closed by client.");
            } else {
                // We rather agressively terminate the server on the first connection exception
                break;
            }
        }
    }

    private void bar() {


        if (socket != null) {
            System.out.println("Connected to " + socket);
            try {
                // For reading from standard input
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                // For sending text to the server
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                String s;
                // Read from standard input and send to server
                // Ctrl-D terminates the connection
                System.out.print("Type something for the server and then RETURN> ");
                while ((s = stdin.readLine()) != null && !toServer.checkError()) {
                    toServer.println(s);
                    receiveResponse(socket);
                    System.out.print("Type something for the server and then RETURN> ");
                }
                socket.close();
            } catch (IOException e) {
                // We ignore IOExceptions
            }
        }
    }*/
}
