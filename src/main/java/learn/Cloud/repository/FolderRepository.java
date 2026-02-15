package learn.Cloud.repository;


import learn.Cloud.entity.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {

    // Найти все папки пользователя
    List<Folder> findByOwnerId(String ownerId);

    // Найти подпапки в конкретной папке
    List<Folder> findByParentId(String parentId);

    // Найти папки пользователя в родительской папке
    List<Folder> findByOwnerIdAndParentId(String ownerId, String parentId);

    // Найти корневые папки пользователя (где parentId = null)
    List<Folder> findByOwnerIdAndParentIdIsNull(String ownerId);

    // Удалить все папки в родительской папке
    void deleteByParentId(String parentId);

    Folder findFolderByIdAndOwnerId(String folrderId,String ownerId);


}