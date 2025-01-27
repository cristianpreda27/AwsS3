package qteam.solutions.aws.S3.services;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qteam.solutions.aws.S3.config.S3ClientConfig;
import qteam.solutions.aws.S3.model.ListResult;
import qteam.solutions.aws.S3.model.Resource;
import qteam.solutions.aws.S3.services.interfaces.IAwsS3Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AwsS3Service implements IAwsS3Service {

    private final S3ClientConfig s3ClientConfig;

    public AwsS3Service(S3ClientConfig s3ClientConfig) {
        this.s3ClientConfig = s3ClientConfig;
    }

    @Override
    public ListResult<Resource> listFolder(@NonNull Resource parent, String cursor) {
        String prefix = parent.getId() + "/";

        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(s3ClientConfig.getBucketName())
                .prefix(prefix)
                .continuationToken(cursor);

        ListObjectsV2Response response = s3ClientConfig.getAmazonS3().listObjectsV2(requestBuilder.build());
        List<Resource> resources = new ArrayList<>();

        for (S3Object s3Object : response.contents()) {
            String key = s3Object.key();
            String name = key.substring(key.lastIndexOf('/') + 1);

            // Check if it is a file or a folder
            int type = key.endsWith("/") ? 1 : 0;

            Resource resource = Resource.builder().id(key).name(name).type(type).build();

            resources.add(resource);
        }

        return ListResult.<Resource>builder().resources(resources).cursor(response.nextContinuationToken()).build();
    }

    @Override
    public Resource getResource(@NonNull String id) {

        try {
            log.debug("S3 Bucket - Start Get object with id: {}", id);

            GetObjectRequest objectRequest =
                    GetObjectRequest.builder().bucket(s3ClientConfig.getBucketName()).key(id).build();

            log.debug("S3 Bucket - Completed getting object with id: {}", id);
            return Resource.builder().id(objectRequest.key()).name(id.substring(id.lastIndexOf('/') + 1))
                    .type(id.endsWith("/") ? 1 : 0).build();
        } catch (AwsServiceException ase) {
            log.error("Caught an AmazonServiceException " + ase.getMessage() + " " + ase.statusCode());
        } catch (Exception e) {
            log.error("Exception occurred when getting objects from S3 bucket " + e.getMessage());
        }
        return Resource.builder().type(id.endsWith("/") ? 1 : 0).build();
    }

    @Override
    public File getAsFile(@NonNull Resource resource) {
        if (resource.getType() != 0) {
            throw new IllegalArgumentException("The resource is not a file");
        }

        String key = resource.getId();

        File file;
        try {
            file = Files.createTempFile("S3File", null).toFile();

            s3ClientConfig.getAmazonS3().getObject(GetObjectRequest.builder().bucket(s3ClientConfig.getBucketName()).key(key).build(),
                    ResponseTransformer.toFile(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }

        return file;
    }
}
