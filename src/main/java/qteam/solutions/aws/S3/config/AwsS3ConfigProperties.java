package qteam.solutions.aws.S3.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("aws")
@Configuration
@Getter
@Setter
public class AwsS3ConfigProperties {
    private String keyId;
    private String region;
    private String accessKey;
    private String bucket;
}
