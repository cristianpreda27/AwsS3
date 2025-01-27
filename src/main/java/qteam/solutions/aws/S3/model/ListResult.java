package qteam.solutions.aws.S3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ListResult<T> {
    private List<T> resources;
    private String cursor;  // Used for pagination
}
