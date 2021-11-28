package io.mamish.dogexpert.discord;

import io.mamish.dogexpert.discord.model.ClassifierPrediction;
import io.mamish.dogexpert.discord.model.ClassifierResponse;

public class UserResponder {

    private static final double CONFIDENCE_75 = 0.75d;
    private static final double CONFIDENCE_50 = 0.50d;
    private static final double CONFIDENCE_25 = 0.25d;

    private final ReactionEmojis emojis;

    public UserResponder(ReactionEmojis emojis) {
        this.emojis = emojis;
    }

    public UserResponse createResponse(ClassifierResponse classifierResponse) {
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

    // Border collie as top pick with varying levels of confidence

    private UserResponse summaryDefinitelyBC(ClassifierPrediction bc) {
        return respond(emojis.getTagCooldog(),
                "That is *absolutely* a border collie, no doubt about it (`%s`). Well done :)",
                asPercentage(bc.getConfidence()));
    }

    private UserResponse summaryAlmostDefinitelyBC(ClassifierPrediction bc, ClassifierPrediction next) {
        return respond(
                emojis.getTagHeartEyes(),
                "That is almost definitely a %s, but could also be a %s. Nice :)",
                bcPrediction(bc), nonBCPrediction(next));
    }

    private UserResponse summaryProbablyBC(ClassifierPrediction bc, ClassifierPrediction next1, ClassifierPrediction next2) {
        return respond(
                emojis.getTagRelieved(),
                "That's probably a %s, but could also be a %s or a %s. Good enough!",
                bcPrediction(bc), nonBCPrediction(next1), nonBCPrediction(next2));
    }

    private UserResponse summaryMaybeBC(ClassifierPrediction bc, ClassifierPrediction next1, ClassifierPrediction next2, ClassifierPrediction next3) {
        return respond(
                emojis.getTagMonocleDoubt(),
                "That might be a %s, but could be a %s, a %s or even a %s. It will have to do.",
                bcPrediction(bc), nonBCPrediction(next1), nonBCPrediction(next2), nonBCPrediction(next3));
    }

    // Most likely (>50%) another dog that's not a border collie

    private UserResponse summaryProbablyOtherDog(ClassifierPrediction bc, ClassifierPrediction otherTop) {
        return respond(
                emojis.getTagRage(),"That's a %s, not a %s. Try harder.",
                nonBCPrediction(otherTop), bcPrediction(bc));
    }

    // Border collie is in the top picks but not #1, with varying positions

    private UserResponse summaryBCSecondBest(ClassifierPrediction bc, ClassifierPrediction otherTop) {
        return respond(
                emojis.getTagDog(),
                "That's probably a %s, but there's a decent chance it's a %s, so you get a pass.",
                nonBCPrediction(otherTop), bcPrediction(bc));
    }

    private UserResponse summaryBCThirdBest(ClassifierPrediction bc, ClassifierPrediction otherTop1, ClassifierPrediction otherTop2) {
        return respond(
                emojis.getTagMonocleDoubt(),
                "That's probably a %s or a %s and not a %s, so I'm not thrilled :|",
                nonBCPrediction(otherTop1), nonBCPrediction(otherTop2), bcPrediction(bc));
    }

    // Border collie doesn't appear in the top picks

    private UserResponse summaryNotBC(ClassifierPrediction bc, ClassifierPrediction otherTop1, ClassifierPrediction otherTop2) {
        return respond(
                emojis.getTagRage(),
                "That's not even close to being a %s, more like a %s or a %s. I'm disappointed >:(",
                bcPrediction(bc), nonBCPrediction(otherTop1), nonBCPrediction(otherTop2));
    }

    // Very likely (>25%) not even a dog breed

    private UserResponse summaryNotADog(ClassifierPrediction bc, ClassifierPrediction otherTop) {
        return respond(
                emojis.getTagRage(),
                "I don't think that's even a dog, looks more like a %s. What are you doing, this is supposed to be a %s.",
                nonBCPrediction(otherTop), bcPrediction(bc));
    }

    // Common builders

    private UserResponse respond(String reactTag, String format, Object... args) {
        return new UserResponse(String.format(format, args), reactTag);
    }

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
