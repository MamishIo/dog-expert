package io.mamish.dogexpert.cdk;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.patterns.QueueProcessingFargateService;
import software.amazon.awscdk.services.ecs.patterns.QueueProcessingFargateServiceProps;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretProps;
import software.constructs.Construct;

import java.util.List;

public class ServiceStack extends Stack {

    private static final int CPU_UNITS = 256; // .25 vCPU, minimum in order to save cost
    private static final int MEMORY_MB = 2048; // 2GB, largest possible for this memory size (needs to be large for Python+Java)

    public ServiceStack(@Nullable Construct scope, @Nullable String id) {
        super(scope, id);

        var serviceVpc = new Vpc(this, "DefaultVpc", VpcProps.builder()
                .cidr("10.10.0.0/16")
                .maxAzs(3)
                .subnetConfiguration(List.of(
                        SubnetConfiguration.builder()
                                .name("Public")
                                .subnetType(SubnetType.PUBLIC)
                                .cidrMask(20)
                                .build()))
                .build());

        var discordFargateService = new QueueProcessingFargateService(this, "Service", QueueProcessingFargateServiceProps.builder()
                .vpc(serviceVpc)
                .assignPublicIp(true)
                .cpu(CPU_UNITS)
                .memoryLimitMiB(MEMORY_MB)
                .image(ContainerImage.fromAsset("dog-expert-docker"))
                .build());

        var existingTokenSecret = Secret.fromSecretNameV2(this, "ImportedTokenSecret", "discord/api-token");
        existingTokenSecret.grantRead(discordFargateService.getTaskDefinition().getTaskRole());
    }
}
