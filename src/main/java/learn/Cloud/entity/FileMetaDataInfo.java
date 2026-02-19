package learn.Cloud.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "files")
public class FileMetaDataInfo {
    @Id
    private String id;

    @Indexed
    private String ownerId;      // ID пользователя из PostgreSQL

    @Indexed
    private String folderId;     // ID папки

    private String name;

    private String minioPath;    // путь в MinIO

}

