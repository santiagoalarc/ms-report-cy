package co.com.bancolombia.snsaws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.sns")
public class AwsProperties {

    @Getter @Setter
    private String region;

    @Getter @Setter
    private String topicArn;

    @Getter @Setter
    private String accessKey;

    @Getter @Setter
    private String secretKey;




}
