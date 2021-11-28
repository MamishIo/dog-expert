package io.mamish.dogexpert.discord.model;

public class ClassifierPrediction {

    // Labels from: https://gist.github.com/fnielsen/4a5c94eaa6dcdf29b7a62d886f540372
    private static final int DOG_BREED_SEQUENCE_NUMBER_MIN = 2085620; // n02085620
    private static final int DOG_BREED_SEQUENCE_NUMBER_MAX = 2113978; // n02113978
    private static final String BORDER_COLLIE_SEQUENCE_NUMBER = "n02106166";

    private final String sequenceNumber;
    private final String imageNetClassName;
    private final double confidence;

    public ClassifierPrediction(String sequenceNumber, String imageNetClassName, double confidence) {
        this.sequenceNumber = sequenceNumber;
        this.imageNetClassName = imageNetClassName;
        this.confidence = confidence;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public String getImageNetClassName() {
        return imageNetClassName;
    }

    public double getConfidence() {
        return confidence;
    }

    public boolean isDogBreedCategory() {
        if (!sequenceNumber.startsWith("n")) {
            throw new RuntimeException("Unexpected sequence number format: <" + sequenceNumber + ">");
        }
        int sequenceAsInt = Integer.parseInt(sequenceNumber.substring(1));
        return (DOG_BREED_SEQUENCE_NUMBER_MIN <= sequenceAsInt && sequenceAsInt <= DOG_BREED_SEQUENCE_NUMBER_MAX);
    }

    public boolean isBorderCollie() {
        return BORDER_COLLIE_SEQUENCE_NUMBER.equals(sequenceNumber);
    }

    @Override
    public String toString() {
        return "ClassifierPrediction{" +
                "sequenceNumber='" + sequenceNumber + '\'' +
                ", imageNetClassName='" + imageNetClassName + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
