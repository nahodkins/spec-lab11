import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import com.slack.api.model.Message;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class SlackApp {

    private static final String PROPERTIES_PATH = "/bot-full.properties";
    private static final String BOT_TOKEN_PROPERTY = "bot.token";

    private Slack slack;
    private MethodsClient methods;

    public SlackApp() throws IOException {
        slack = Slack.getInstance();
        methods = slack.methods(readBotToken());
    }

    private String readBotToken() throws IOException {
        Properties properties = new Properties();
        properties.load(SlackApp.class.getResourceAsStream(PROPERTIES_PATH));
        return properties.getProperty(BOT_TOKEN_PROPERTY);
    }

    public List<String> getChatHistory(String channelId) throws SlackApiException, IOException {
        ConversationsHistoryResponse response = methods.conversationsHistory(request -> request.channel(channelId));
        return response.getMessages().stream()
                .sorted(Comparator.comparing(o -> Long.valueOf(o.getTs().replaceAll("\\.", ""))))
                .map(Message::getText)
                .collect(Collectors.toList());
    }

    public void sendMessageToUser(String email, String message) throws SlackApiException, IOException {
        UsersLookupByEmailResponse response = methods
                .usersLookupByEmail(request -> request.email(email));
        String userId = response.getUser().getId();
        ChatPostMessageResponse messageResponse = methods
                .chatPostMessage(request -> request.channel(userId).text(message));
        if (!messageResponse.isOk()) {
            System.err.println(messageResponse.getError());
        }
    }
}
