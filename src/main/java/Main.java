import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        SlackApp slackApp = new SlackApp();
        List<String> messages = slackApp.getChatHistory("chatId");
        messages
                .forEach(System.out::println);
        slackApp.sendMessageToUser("mail" ,"hello from java");
    }
}
