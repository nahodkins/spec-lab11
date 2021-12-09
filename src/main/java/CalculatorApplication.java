import java.io.Closeable;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CalculatorApplication implements Closeable {

    private static final Integer PORT = 9999;
    private static final String SERVER_STARTED_MSG_TEMPLATE = "Started: %s:%d";
    private static final String SERVER_STOPPED_MESSAGE = "Server was stopped successfully";
    private static final String CLOSED_SOCKETS_MSG = "All sockets were closed";
    private static final String START_CLIENT_MSG = "Client #%d was started";

    private static boolean isStopped = false;
    private static int clientsCount = 0;

    private final ServerSocket server;
    private final List<Socket> sockets;

    public CalculatorApplication() throws IOException {
        server = new ServerSocket(PORT);
        sockets = new ArrayList<>();
        System.out.println(String.format(SERVER_STARTED_MSG_TEMPLATE, server.getInetAddress().getHostAddress(), PORT));
    }

    private void closeSockets() throws IOException {
        for (Socket socket : sockets) {
            if (!socket.isClosed()) {
                socket.close();
            }
        }
        System.out.println(CLOSED_SOCKETS_MSG);
    }

    public void run() throws IOException {
        while (!isStopped) {
            try {
                Socket socket;
                try {
                    socket = server.accept();
                } catch (SocketException e) {
                    closeSockets();
                    break;
                }
                sockets.add(socket);
                ServerClient client = new ServerClient(socket, clientsCount++, this);
                System.out.println(String.format(START_CLIENT_MSG, client.getClientId()));
                Thread clientThread = new Thread(client);
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(SERVER_STOPPED_MESSAGE);
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.close();
        }
    }
}
