package io.mamish.dogexpert.cdk;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.Stage;
import software.constructs.Construct;

public class PipelineStage extends Stage {
    public PipelineStage(@NotNull Construct scope, @NotNull String id) {
        super(scope, id);

        new ServiceStack(this, "DogExpertService");
    }
}
