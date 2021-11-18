package io.mamish.dogexpert.cdk;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.pipelines.*;
import software.constructs.Construct;

public class PipelineStack extends Stack {

    private static final String GITHUB_CONNECTION
            = "arn:aws:codestar-connections:ap-southeast-2:759972196229:connection/9a8a38a0-66a9-4662-93ee-7f9e0a1a6f1c";
    private static final String GITHUB_REPO_STRING = "MamishIo/dog-expert";
    private static final String GITHUB_BRANCH = "dog-expert";

    public PipelineStack(@Nullable Construct scope, @Nullable String id, StackProps props) {
        super(scope, id, props);

        var pipeline = new CodePipeline(this, "Pipeline", CodePipelineProps.builder()
                .synth(new CodeBuildStep("BuildProject", CodeBuildStepProps.builder()
                        .input(CodePipelineSource.connection(GITHUB_REPO_STRING, GITHUB_BRANCH, ConnectionSourceOptions.builder()
                                .connectionArn(GITHUB_CONNECTION)
                                .build()))
                        .build()))
                .build());

        var prodStage = pipeline.addStage(new PipelineStage(this, "Prod"));
    }
}
