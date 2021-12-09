import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try (CalculatorApplication application = new CalculatorApplication()){
            application.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
