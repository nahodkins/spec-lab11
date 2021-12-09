import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AutoClient implements Runnable{

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 9999;
    private static final List<String> SIGNS = Collections.unmodifiableList(List.of("+", "-", "/", "*"));

    private String generateExpression() {
        String first = String.valueOf((int) (Math.random() * 5));
        String second = String.valueOf((int) (Math.random() * 5));
        String sign = SIGNS.get((int) (Math.random() * 3));
        return (first + sign + second);
    }

    private void sendMessageToServer(String message, PrintWriter writer) {
        writer.println(message);
        writer.flush();
    }

    public void run() {
        try {
            Socket socket = new Socket(HOST, PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            for (int i = 0; i < 5; i++) {
                Thread.sleep(i * 1000);
                sendMessageToServer(generateExpression(), writer);
            }
            sendMessageToServer("1", writer);
            Thread.sleep(1000);
            sendMessageToServer("5/0", writer);
            Thread.sleep(1000);
            sendMessageToServer("exit", writer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        IntStream.range(0, 3)
                .mapToObj(i -> new AutoClient())
                .map(Thread::new)
                .forEach(Thread::start);
    }
}
