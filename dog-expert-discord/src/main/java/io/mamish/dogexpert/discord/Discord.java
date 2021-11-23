package io.mamish.dogexpert.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Discord implements MessageCreateListener {

    private static final String API_TOKEN_SECRET_ID = "discord/api-token";
    private static final String DISCORD_CHANNEL_ID = "910852320076648498";

    private final ClassifierProcess classifierProcess = new ClassifierProcess();
    private final ClassifierClient classifierClient = new ClassifierClient();

    public static void main(String[] args) {
        new Discord();
    }

    public Discord() {
        var secretsManagerClient = SecretsManagerClient.create();
        String apiToken = secretsManagerClient.getSecretValue(r -> r.secretId(API_TOKEN_SECRET_ID)).secretString();
        new DiscordApiBuilder()
                .setToken(apiToken)
                .addMessageCreateListener(this)
                .login()
                .orTimeout(60, TimeUnit.SECONDS)
                .join();
    }

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (!channel.getIdAsString().equals(DISCORD_CHANNEL_ID)) {
            return;
        }

        Message sourceMessage = messageCreateEvent.getMessage();
        messageCreateEvent.getMessageAttachments().forEach(attachment -> handleMessageAttachment(sourceMessage, attachment));
    }

    private void handleMessageAttachment(Message sourceMessage, MessageAttachment attachment) {
        if (attachment.isImage()) {
            byte[] imageBytes = attachment.downloadAsByteArray().join();
            String response = classifierClient.classify(imageBytes);
            sourceMessage.reply("Test! Classifier response: " + response);
        }
    }
}
