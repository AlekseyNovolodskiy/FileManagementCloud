package learn.Cloud.repository;

import learn.Cloud.entity.FileMetaDataInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetaDataRepository extends MongoRepository<FileMetaDataInfo,String> {

    List<FileMetaDataInfo> findByOwnerIdAndFolderId(String ownerId, String folderId);
}
