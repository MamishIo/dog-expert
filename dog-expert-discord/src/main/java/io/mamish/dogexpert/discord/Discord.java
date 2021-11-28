package io.mamish.dogexpert.discord;

import io.mamish.dogexpert.discord.model.ClassifierResponse;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.util.concurrent.TimeUnit;

public class Discord implements MessageCreateListener {

    private static final String ECS_CREDS_URI_ENV_VAR_KEY = "AWS_CONTAINER_CREDENTIALS_RELATIVE_URI";
    private static final String API_TOKEN_SECRET_ID = "discord/api-token";
    private static final String DISCORD_CHANNEL_ID_DEV = "910852320076648498";
    private static final String DISCORD_CHANNEL_ID_PROD = "710475630902247486";

    private final DiscordApi discordApi;
    private final ClassifierProcess classifierProcess = new ClassifierProcess();
    private final ClassifierClient classifierClient = new ClassifierClient();
    private final UserResponder userResponder;
    private final String watchChannelId;

    public static void main(String[] args) {
        new Discord();
    }

    public Discord() {
        this.watchChannelId = (isRunningOnECS()) ? DISCORD_CHANNEL_ID_PROD : DISCORD_CHANNEL_ID_DEV;
        var secretsManagerClient = SecretsManagerClient.create();
        String apiToken = secretsManagerClient.getSecretValue(r -> r.secretId(API_TOKEN_SECRET_ID)).secretString();
        this.discordApi = new DiscordApiBuilder()
                .setToken(apiToken)
                .addMessageCreateListener(this)
                .login()
                .orTimeout(60, TimeUnit.SECONDS)
                .join();
        ReactionEmojis emojis = new ReactionEmojis(this.discordApi);
        userResponder = new UserResponder(emojis);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (!channel.getIdAsString().equals(watchChannelId)) {
            return;
        }

        Message sourceMessage = messageCreateEvent.getMessage();
        messageCreateEvent.getMessageAttachments().forEach(attachment -> handleMessageAttachment(sourceMessage, attachment));
    }

    private void handleMessageAttachment(Message sourceMessage, MessageAttachment attachment) {
        if (attachment.isImage()) {
            byte[] imageBytes = attachment.downloadAsByteArray().join();
            ClassifierResponse response = classifierClient.classify(imageBytes);
            System.out.println("Border collie prediction=" + response.getBorderColliePrediction());
            UserResponse userResponse = userResponder.createResponse(response);
            System.out.println("User react tag=<" + userResponse.getReactTag() + ">");
            System.out.println("User reply=<" + userResponse.getMessage() + ">");
            sourceMessage.reply(userResponse.getMessage());
            sourceMessage.addReaction(userResponse.getReactTag());
        }
    }

    private boolean isRunningOnECS() {
        return System.getenv(ECS_CREDS_URI_ENV_VAR_KEY) != null;
    }
}
