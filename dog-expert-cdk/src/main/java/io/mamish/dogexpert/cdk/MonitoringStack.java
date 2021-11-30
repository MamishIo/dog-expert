package io.mamish.dogexpert.cdk;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.events.EventPattern;
import software.amazon.awscdk.services.events.Rule;
import software.amazon.awscdk.services.events.RuleProps;
import software.amazon.awscdk.services.events.targets.CloudWatchLogGroup;
import software.amazon.awscdk.services.logs.*;
import software.constructs.Construct;

import java.util.List;

public class MonitoringStack extends Stack{

    // Our chosen metric details
    private static final String METRIC_NAMESPACE = "DogExpert";
    private static final String INTERRUPTIONS_METRIC_NAME = "FargateSpotInterruptions";

    // Constant event details fixed by EventBridge
    // https://docs.aws.amazon.com/AmazonECS/latest/developerguide/fargate-capacity-providers.html#fargate-capacity-providers-termination
    private static final String EVENT_SOURCE_ECS = "aws.ecs";
    private static final String EVENT_DETAIL_TYPE_ECS_STATE_CHANGE = "ECS Task State Change";
    private static final IFilterPattern FARGATE_SPOT_INTERRUPTION_FILTER = FilterPattern.stringValue(
            "$.detail.stopCode", "=", "TerminationNotice"
    );

    public MonitoringStack(@Nullable Construct scope, @Nullable String id) {
        super(scope, id);

        var ecsStateChangeLogs = new LogGroup(this, "ECSStateChangeLogs", LogGroupProps.builder()
                .removalPolicy(RemovalPolicy.RETAIN)
                .retention(RetentionDays.TWO_YEARS)
                .build());

        // The rule logs all state changes for monitoring/debug purposes
        new Rule(this, "ECSStateChangesLogRule", RuleProps.builder()
                .eventPattern(EventPattern.builder()
                        .source(List.of(EVENT_SOURCE_ECS))
                        .detailType(List.of(EVENT_DETAIL_TYPE_ECS_STATE_CHANGE))
                        .build())
                .targets(List.of(new CloudWatchLogGroup(ecsStateChangeLogs)))
                .build());

        // For the metric filter we only want to know about interruption rate as the key metric
        new MetricFilter(this, "FargateSpotInterruptionsMetric", MetricFilterProps.builder()
                .logGroup(ecsStateChangeLogs)
                .defaultValue(1)
                .metricNamespace(METRIC_NAMESPACE)
                .metricName(INTERRUPTIONS_METRIC_NAME)
                .filterPattern(FARGATE_SPOT_INTERRUPTION_FILTER)
                .build());
    }
}
