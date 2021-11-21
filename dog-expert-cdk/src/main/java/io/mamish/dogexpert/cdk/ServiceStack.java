package io.mamish.dogexpert.cdk;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.patterns.QueueProcessingFargateService;
import software.amazon.awscdk.services.ecs.patterns.QueueProcessingFargateServiceProps;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretProps;
import software.constructs.Construct;

public class ServiceStack extends Stack {

    private static final int CPU_UNITS = 256; // .25 vCPU, minimum in order to save cost
    private static final int MEMORY_MB = 2048; // 2GB, largest possible for this memory size (needs to be large for Python+Java)

    public ServiceStack(@Nullable Construct scope, @Nullable String id) {
        super(scope, id);

        var discordTokenSecret = new Secret(this, "DiscordTokenSecret", SecretProps.builder()
                .secretName("discord/api-token")
                .build());

        var discordFargateService = new QueueProcessingFargateService(this, "Service", QueueProcessingFargateServiceProps.builder()
                .cpu(CPU_UNITS)
                .memoryLimitMiB(MEMORY_MB)
                .image(ContainerImage.fromAsset("dog-expert-docker"))
                .build());

        discordTokenSecret.grantRead(discordFargateService.getTaskDefinition().getTaskRole());
    }
}
