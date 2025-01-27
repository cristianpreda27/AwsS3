package qteam.solutions.aws.S3.services.interfaces;

import qteam.solutions.aws.S3.model.ListResult;
import qteam.solutions.aws.S3.model.Resource;

import java.io.File;

public interface IAwsS3Service {

    ListResult<Resource> listFolder(Resource parent, String cursor);

    Resource getResource(String id);

    File getAsFile(Resource resource);
}
