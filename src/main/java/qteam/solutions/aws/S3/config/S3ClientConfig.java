package qteam.solutions.aws.S3.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class S3ClientConfig {

    private final AwsS3ConfigProperties s3ConfigProperties;
    private volatile S3Client s3Client;

    public S3ClientConfig(AwsS3ConfigProperties s3ConfigProperties) {
        this.s3ConfigProperties = s3ConfigProperties;
    }

    @PostConstruct
    protected void doPostConstruct() {

        String awsKeyId = s3ConfigProperties.getKeyId();
        String awsAccessKey =
                s3ConfigProperties.getAccessKey();
        String awsRegion = s3ConfigProperties.getRegion();

        log.info("Starting to create AWS S3 SDK Client Config");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsKeyId, awsAccessKey);

        try {
            s3Client =
                    software.amazon.awssdk.services.s3.S3Client.builder()
                            .credentialsProvider(StaticCredentialsProvider.create(credentials))
                            .region(Region.of(awsRegion))
                            .build();
            log.info("AWS Client - S3 Configuration completed");
        } catch (Exception e) {
            log.error("Error while getting S3 Client: " + e.getMessage());
            throw e;
        }
    }

    public S3Client getAmazonS3() {
        if (this.s3Client == null) {
            throw new IllegalStateException("S3Client is not initialized. Please check the configuration.");
        }
        return this.s3Client;
    }

    public String getBucketName() {
        return s3ConfigProperties.getBucket();
    }
}
