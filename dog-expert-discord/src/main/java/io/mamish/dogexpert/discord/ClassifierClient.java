package io.mamish.dogexpert.discord;

import com.squareup.moshi.Moshi;
import io.mamish.dogexpert.discord.model.ClassifierRequest;
import io.mamish.dogexpert.discord.model.ClassifierResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import static java.net.http.HttpRequest.BodyPublishers;
import static java.net.http.HttpResponse.BodyHandlers;

public class ClassifierClient {

    private static final URI LOCALHOST_CLASSIFIER_URI = URI.create("http://localhost:8081/");
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    private final Moshi moshi = new Moshi.Builder().build();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();

    public ClassifierResponse classify(byte[] imageBytes) {
        String jsonPayload = moshi.adapter(ClassifierRequest.class).toJson(new ClassifierRequest(
                Base64.getEncoder().encodeToString(imageBytes)
        ));
        try {
            HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                    .uri(LOCALHOST_CLASSIFIER_URI)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build(),
                    BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Classifier endpoint error response: " + response.body());
            }
            return moshi.adapter(ClassifierResponse.class).fromJson(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to post to classifier", e);
        }
    }
}
