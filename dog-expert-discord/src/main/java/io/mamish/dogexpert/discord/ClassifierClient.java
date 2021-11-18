package io.mamish.dogexpert.discord;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static java.net.http.HttpResponse.BodyHandlers;
import static java.net.http.HttpRequest.BodyPublishers;

public class ClassifierClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String classify(byte[] imageBytes) {
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        String jsonPayload = String.format("{\"ImageData\":\"%s\"}", imageBase64);
        try {
            HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build(),
                    BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to post to classifier", e);
        }
    }
}
