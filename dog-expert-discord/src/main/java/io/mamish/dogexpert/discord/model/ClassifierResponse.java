package io.mamish.dogexpert.discord.model;

import java.util.List;

public class ClassifierResponse {
    private final ClassifierPrediction borderColliePrediction;
    private final int borderCollieIndex;
    private final List<ClassifierPrediction> predictions;

    public ClassifierResponse(ClassifierPrediction borderColliePrediction, int borderCollieIndex, List<ClassifierPrediction> predictions) {
        this.borderColliePrediction = borderColliePrediction;
        this.borderCollieIndex = borderCollieIndex;
        this.predictions = predictions;
    }

    public ClassifierPrediction getBorderColliePrediction() {
        return borderColliePrediction;
    }

    public int getBorderCollieIndex() {
        return borderCollieIndex;
    }

    public List<ClassifierPrediction> getPredictions() {
        return predictions;
    }
}
