package io.mamish.dogexpert.discord;

public class UserResponse {

    private final String message;
    private final String reactTag;

    public UserResponse(String message, String reactTag) {
        this.message = message;
        this.reactTag = reactTag;
    }

    public String getMessage() {
        return message;
    }

    public String getReactTag() {
        return reactTag;
    }
}
