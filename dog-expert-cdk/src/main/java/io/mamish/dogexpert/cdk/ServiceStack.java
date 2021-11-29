package io.mamish.dogexpert.cdk;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.secretsmanager.Secret;
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
                .subnetConfiguration(List.of(SubnetConfiguration.builder()
                        .name("Public")
                        .subnetType(SubnetType.PUBLIC)
                        .cidrMask(20)
                        .build()))
                .build());



        var fargateCluster = new Cluster(this, "FargateCluster", ClusterProps.builder()
                .vpc(serviceVpc)
                .enableFargateCapacityProviders(true)
                .containerInsights(true)
                .build());
        var task = new FargateTaskDefinition(this, "Task", FargateTaskDefinitionProps.builder()
                .cpu(CPU_UNITS)
                .memoryLimitMiB(MEMORY_MB)
                .build());
        var logGroup = new LogGroup(this, "ServiceLogs", LogGroupProps.builder()
                .retention(RetentionDays.THREE_MONTHS)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build());
        var logDriver = LogDriver.awsLogs(AwsLogDriverProps.builder()
                .logGroup(logGroup)
                .streamPrefix("service")
                .build());
        task.addContainer("ServiceContainer", ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromAsset("dog-expert-docker"))
                .logging(logDriver)
                .build());
        var service = new FargateService(this, "Service", FargateServiceProps.builder()
                .cluster(fargateCluster)
                .taskDefinition(task)
                .capacityProviderStrategies(List.of(CapacityProviderStrategy.builder()
                        .capacityProvider("FARGATE_SPOT")
                        .weight(1)
                        .build()))
                .assignPublicIp(true)
                .desiredCount(1)
                .circuitBreaker(DeploymentCircuitBreaker.builder()
                        .rollback(true)
                        .build())
                .build());

        var existingTokenSecret = Secret.fromSecretNameV2(this, "ImportedTokenSecret", "discord/api-token");
        existingTokenSecret.grantRead(task.getTaskRole());
    }
}
