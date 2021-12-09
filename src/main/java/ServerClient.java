import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class ServerClient implements Runnable, Closeable {

    private static final String RESPONSE_MSG_TEMPLATE = "%s = %s";
    private static final String EXIT_LINE = "exit";
    private static final String STOP_LINE = "stop";
    private static final String RECEIVED_MSG = "Received#%d: %s";
    private static final String CALCULATION_FAILED_MSG = "Calculation failed: ";
    private static final String RESPONSE_MSG = "Response#%d: %s";
    private static final String CLIENT_EXITED_MSG = "#%d is exit";
    private static final String CLIENT_STOPPED_MSG = "#%d stopped the server";

    private final Socket socket;
    private final int clientId;
    private final Calculator calculator;

    private Scanner scanner;
    private PrintWriter writer;
    private CalculatorApplication application;

    public ServerClient(Socket socket, int clientId, CalculatorApplication application) {
        this.socket = socket;
        this.clientId = clientId;
        this.application = application;
        calculator = new Calculator();
    }

    private boolean isExit(String line) {
        return line.trim().toLowerCase(Locale.ROOT).equals(EXIT_LINE);
    }

    private boolean isStop(String line) {
        return line.trim().toLowerCase(Locale.ROOT).equals(STOP_LINE);
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public void run() {
        try {
            scanner = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(String.format(RECEIVED_MSG, clientId, line));

            if (isExit(line)) {
                try {
                    System.out.println(String.format(CLIENT_EXITED_MSG, clientId));
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            } else if (isStop(line)) {
                try {
                    System.out.println(String.format(CLIENT_STOPPED_MSG, clientId));
                    application.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            try {
                double result = calculator.calculate(line);
                String response = String.format(RESPONSE_MSG_TEMPLATE, line, result);
                writer.println(response);
                System.out.println(String.format(RESPONSE_MSG, clientId, response));
            } catch (CalculatorException e) {
                writer.println(CALCULATION_FAILED_MSG + e.getMessage());
                System.err.println(CALCULATION_FAILED_MSG + e.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (scanner != null) {
            scanner.close();
        }
        if (writer != null) {
            writer.close();
        }
    }
}
