package io.mamish.dogexpert.discord;

import io.mamish.dogexpert.discord.model.ClassifierPrediction;
import io.mamish.dogexpert.discord.model.ClassifierResponse;

public class UserSummaryGenerator {

    private static final double CONFIDENCE_75 = 0.75d;
    private static final double CONFIDENCE_50 = 0.50d;
    private static final double CONFIDENCE_25 = 0.25d;

    public String summarise(ClassifierResponse classifierResponse) {
        ClassifierPrediction bc = classifierResponse.getBorderColliePrediction();
        ClassifierPrediction prediction0 = classifierResponse.getPredictions().get(0);
        ClassifierPrediction prediction1 = classifierResponse.getPredictions().get(1);
        ClassifierPrediction prediction2 = classifierResponse.getPredictions().get(2);
        ClassifierPrediction prediction3 = classifierResponse.getPredictions().get(3);
        boolean borderCollieIsTop = prediction0.isBorderCollie();
        int borderCollieIndex = classifierResponse.getBorderCollieIndex();
        double topPredictionConfidence = prediction0.getConfidence();

        if (borderCollieIsTop) {
            if (topPredictionConfidence >= CONFIDENCE_75) {
                return summaryDefinitelyBC(bc);
            } else if (topPredictionConfidence >= CONFIDENCE_50) {
                return summaryAlmostDefinitelyBC(bc, prediction1);
            } else if (topPredictionConfidence >= CONFIDENCE_25) {
                return summaryProbablyBC(bc, prediction1, prediction2);
            } else {
                return summaryMaybeBC(bc, prediction1, prediction2, prediction3);
            }
        } else {
            if (prediction0.isDogBreedCategory() && topPredictionConfidence > CONFIDENCE_50) {
                return summaryProbablyOtherDog(bc, prediction0);
            } else if (!prediction0.isDogBreedCategory() && topPredictionConfidence > CONFIDENCE_25) {
                return summaryNotADog(bc, prediction0);
            } else {
                if (borderCollieIndex == 1) {
                    return summaryBCSecondBest(bc, prediction0);
                } else if (borderCollieIndex == 2) {
                    return summaryBCThirdBest(bc, prediction0, prediction1);
                } else {
                    return summaryNotBC(bc, prediction0, prediction2);
                }
            }
        }
    }

    private String summaryDefinitelyBC(ClassifierPrediction bc) {
        return String.format("That is *absolutely* a border collie, no doubt about it (`%s`). Well done :)", asPercentage(bc.getConfidence()));
    }

    // Border collie as top pick with varying levels of confidence

    private String summaryAlmostDefinitelyBC(ClassifierPrediction bc, ClassifierPrediction next) {
        return String.format("That is almost definitely a %s, but could also be a %s. Nice :)",
                bcPrediction(bc), nonBCPrediction(next));
    }

    private String summaryProbablyBC(ClassifierPrediction bc, ClassifierPrediction next1, ClassifierPrediction next2) {
        return String.format("That's probably a %s, but could also be a %s or a %s. It'll do.",
                bcPrediction(bc), nonBCPrediction(next1), nonBCPrediction(next2));
    }

    private String summaryMaybeBC(ClassifierPrediction bc, ClassifierPrediction next1, ClassifierPrediction next2, ClassifierPrediction next3) {
        return String.format("That might be a %s, but could be a %s, a %s or even a %s. Not great, but I'll take it.",
                bcPrediction(bc), nonBCPrediction(next1), nonBCPrediction(next2), nonBCPrediction(next3));
    }

    // Most likely (>50%) another dog that's not a border collie

    private String summaryProbablyOtherDog(ClassifierPrediction bc, ClassifierPrediction otherTop) {
        return String.format("That's a %s, not a %s. Try harder.",
                nonBCPrediction(otherTop), bcPrediction(bc));
    }

    // Border collie is in the top picks but not #1, with varying positions

    private String summaryBCSecondBest(ClassifierPrediction bc, ClassifierPrediction otherTop) {
        return String.format("That's probably a %s, but there's a decent chance it's a %s, so you get a pass.",
                nonBCPrediction(otherTop), bcPrediction(bc));
    }

    private String summaryBCThirdBest(ClassifierPrediction bc, ClassifierPrediction otherTop1, ClassifierPrediction otherTop2) {
        return String.format("That's probably a %s or a %s and not a %s, so I'm not thrilled :|",
                nonBCPrediction(otherTop1), nonBCPrediction(otherTop2), bcPrediction(bc));
    }

    // Border collie doesn't appear in the top picks

    private String summaryNotBC(ClassifierPrediction bc, ClassifierPrediction otherTop1, ClassifierPrediction otherTop2) {
        return String.format("That's not even close to being a %s, more like a %s or a %s. I'm disappointed >:(",
                bcPrediction(bc), nonBCPrediction(otherTop1), nonBCPrediction(otherTop2));
    }

    // Very likely (>25%) not even a dog breed

    private String summaryNotADog(ClassifierPrediction bc, ClassifierPrediction otherTop) {
        return String.format("I don't think that's even a dog, looks more like a %s. What are you doing, this is supposed to be a %s.",
                nonBCPrediction(otherTop), bcPrediction(bc));
    }

    // Common string builders

    private String bcPrediction(ClassifierPrediction bc) {
        return String.format("border collie (`%s`)", asPercentage(bc.getConfidence()));
    }

    private String nonBCPrediction(ClassifierPrediction otherPrediction) {
        String isDogString = otherPrediction.isDogBreedCategory() ? " dog " : " ";
        return String.format("`%s`%s(`%s`)", otherPrediction.getImageNetClassName(), isDogString, asPercentage(otherPrediction.getConfidence()));
    }

    private String asPercentage(double confidence) {
        return String.format("%.1f%%", confidence * 100d);
    }
}
