package co.com.bancolombia.sqs.listener.config;

import co.com.bancolombia.sqs.listener.DailyReportSQSProcessor;
import co.com.bancolombia.sqs.listener.SQSProcessor;
import co.com.bancolombia.sqs.listener.helper.SQSListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
public class SQSConfig {

    @Bean("loanAmountSQSListener")
    public SQSListener loanAmountSQSListener(
            SqsAsyncClient client,
            SQSProperties properties,
            SQSProcessor sqsProcessor) {

        SQSProperties loanProperties = createPropertiesForQueue(properties, properties.queueUrl());

        return SQSListener.builder()
                .client(client)
                .properties(loanProperties)
                .processor(sqsProcessor)
                .build()
                .start();
    }

    @Bean("dailyReportSQSListener")
    public SQSListener dailyReportSQSListener(
            SqsAsyncClient client,
            SQSProperties properties,
            DailyReportSQSProcessor dailyReportProcessor) {

        SQSProperties dailyReportProperties = createPropertiesForQueue(properties, properties.dailyReportUrl());

        return SQSListener.builder()
                .client(client)
                .properties(dailyReportProperties)
                .processor(dailyReportProcessor)
                .build()
                .start();
    }

    @Bean
    public SqsAsyncClient configSqs(SQSProperties properties, MetricPublisher publisher) {
        return SqsAsyncClient.builder()
                .endpointOverride(resolveEndpoint(properties))
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(getProviderChain(properties))
                .build();
    }

    private SQSProperties createPropertiesForQueue(SQSProperties originalProperties, String queueUrl) {
        return new SQSProperties(
                originalProperties.region(),
                originalProperties.endpoint(),
                queueUrl,
                originalProperties.dailyReportUrl(),
                originalProperties.accessKey(),
                originalProperties.secretKey(),
                originalProperties.waitTimeSeconds(),
                originalProperties.visibilityTimeoutSeconds(),
                originalProperties.maxNumberOfMessages(),
                originalProperties.numberOfThreads()
        );
    }

    private AwsCredentialsProviderChain getProviderChain(SQSProperties properties) {

        AwsCredentialsProviderChain.Builder chainBuilder = AwsCredentialsProviderChain.builder();

        if (properties.accessKey() != null && properties.secretKey() != null) {
            chainBuilder.addCredentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(properties.accessKey(), properties.secretKey())
                    )
            );
        }

        return chainBuilder
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    protected URI resolveEndpoint(SQSProperties properties) {
        if (properties.endpoint() != null) {
            return URI.create(properties.endpoint());
        }
        return null;
    }
}
