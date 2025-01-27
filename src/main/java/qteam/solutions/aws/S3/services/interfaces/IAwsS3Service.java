package qteam.solutions.aws.S3.services.interfaces;

import qteam.solutions.aws.S3.model.ListResult;
import qteam.solutions.aws.S3.model.Resource;

import java.io.File;

public interface IAwsS3Service {

    public ListResult<Resource> listFolder(Resource parent, String cursor);

    public Resource getResource(String id);

    public File getAsFile(Resource resource);
}
