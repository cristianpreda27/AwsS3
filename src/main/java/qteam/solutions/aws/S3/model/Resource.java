package qteam.solutions.aws.S3.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    private String id;
    private String name;
    private int type;  // 0 for file, 1 for folder
}
