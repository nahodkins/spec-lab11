import java.io.Closeable;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class CalculatorApplication implements Closeable {

    private static final Integer PORT = 9999;
    private static final String RESPONSE_MSG_TEMPLATE = "%s = %s";
    private static final String EXIT_LINE = "exit";
    private static final String SERVER_STARTED_MSG_TEMPLATE = "Started: %s:%d";
    private static final String RECEIVED_MSG = "Received: ";
    private static final String CALCULATION_FAILED_MSG = "Calculation failed: ";
    private static final String RESPONSE_MSG = "Response: ";

    private final ServerSocket server;
    private final Calculator calculator;
    private PrintWriter writer;
    private Scanner scanner;

    public CalculatorApplication() throws IOException {
        server = new ServerSocket(PORT);
        calculator = new Calculator();
        System.out.println(String.format(SERVER_STARTED_MSG_TEMPLATE, server.getInetAddress().getHostAddress(), PORT));
    }

    private boolean isExit(String line) {
        return line.trim().toLowerCase(Locale.ROOT).equals(EXIT_LINE);
    }

    public void run() {
        try (Socket socket = server.accept()) {
            scanner = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(RECEIVED_MSG + line);
                if (isExit(line)) {
                    break;
                }
                try {
                    double result = calculator.calculate(line);
                    String response = String.format(RESPONSE_MSG_TEMPLATE, line, result);
                    writer.println(response);
                    System.out.println(RESPONSE_MSG + response);
                } catch (CalculatorException e) {
                    writer.println(CALCULATION_FAILED_MSG + e.getMessage());
                    System.err.println(CALCULATION_FAILED_MSG + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (scanner != null) {
            scanner.close();
        }
    }
}
