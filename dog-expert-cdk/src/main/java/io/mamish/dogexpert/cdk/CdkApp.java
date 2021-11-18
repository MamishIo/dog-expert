package io.mamish.dogexpert.cdk;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public class CdkApp {
    private static final String ACCOUNT = "759972196229";
    private static final String REGION = "ap-southeast-2";

    public static void main(final String[] args) {
        App app = new App();
        new PipelineStack(app, "CDKPipeline", StackProps.builder()
                .env(Environment.builder()
                        .account(ACCOUNT)
                        .region(REGION)
                        .build())
                .build());
        app.synth();
    }
}
