package io.mamish.dogexpert.discord;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import static java.net.http.HttpResponse.BodyHandlers;
import static java.net.http.HttpRequest.BodyPublishers;

public class ClassifierClient {

    private static final String JSON_KEY_IMAGE_DATA = "image_data";
    private static final URI LOCALHOST_CLASSIFIER_URI = URI.create("http://localhost:8081/");
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();

    public String classify(byte[] imageBytes) {
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        String jsonPayload = String.format("{\"%s\":\"%s\"}", JSON_KEY_IMAGE_DATA, imageBase64);
        try {
            HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                    .uri(LOCALHOST_CLASSIFIER_URI)
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
