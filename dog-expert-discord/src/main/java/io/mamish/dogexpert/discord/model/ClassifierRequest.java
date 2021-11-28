package io.mamish.dogexpert.discord.model;

public class ClassifierRequest {
    private final String imageDataBase64;

    public ClassifierRequest(String imageDataBase64) {
        this.imageDataBase64 = imageDataBase64;
    }

    public String getImageDataBase64() {
        return imageDataBase64;
    }
}
