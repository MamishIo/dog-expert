package io.mamish.dogexpert.cdk;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.pipelines.*;
import software.amazon.awscdk.services.codebuild.BuildSpec;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class PipelineStack extends Stack {

    private static final String GITHUB_CONNECTION
            = "arn:aws:codestar-connections:ap-southeast-2:759972196229:connection/9a8a38a0-66a9-4662-93ee-7f9e0a1a6f1c";
    private static final String GITHUB_REPO_STRING = "MamishIo/dog-expert";
    private static final String GITHUB_BRANCH = "mainline";

    public PipelineStack(@Nullable Construct scope, @Nullable String id, StackProps props) {
        super(scope, id, props);

        var pipeline = new CodePipeline(this, "Pipeline", CodePipelineProps.builder()
                .synth(new CodeBuildStep("BuildProject", CodeBuildStepProps.builder()
                        .input(CodePipelineSource.connection(GITHUB_REPO_STRING, GITHUB_BRANCH, ConnectionSourceOptions.builder()
                                .connectionArn(GITHUB_CONNECTION)
                                .build()))
                        .primaryOutputDirectory("cdk.out")
                        .partialBuildSpec(BuildSpec.fromObject(Map.of(
                                "phases", Map.of(
                                        "install", Map.of(
                                                "runtime-versions", Map.of(
                                                        "python", "3.7",
                                                        "java", "corretto11"),
                                                "commands", List.of(
                                                        "npm install -g aws-cdk"))),
                                "cache", Map.of(
                                        "paths", List.of(
                                                "/root/.m2/**/*",
                                                "/usr/local/lib/node_modules/aws-cdk/**/*")))))
                        .commands(List.of(
                                // Compile Java source
                                "mvn clean install -Dmaven.test.skip=true",
                                // Copy jar/scripts into docker dir (can't be referenced directly from Dockerfile, sadly)
                                "cp dog-expert-discord/target/dog-expert-discord-1.0-SNAPSHOT.jar dog-expert-docker/java/service.jar",
                                "cp -r dog-expert-python/* dog-expert-docker/python",
                                // Perform CDK synth
                                "cdk synth -o cdk.out"
                        ))
                        .build()))
                .build());

        // Use same environment as pipeline itself
        pipeline.addStage(new ServiceStage(this, "Prod"));
    }
}
