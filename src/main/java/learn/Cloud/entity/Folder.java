package learn.Cloud.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "folders")
@CompoundIndex(name = "owner_parent_idx", def = "{'ownerId': 1, 'parentId': 1}")
public class Folder {
    @Id
    private String id;

    private String name;

    @Indexed
    private String parentId;

    @Indexed
    private String ownerId;

    private String path;  // денормализованный путь, например "/documents/work"
}