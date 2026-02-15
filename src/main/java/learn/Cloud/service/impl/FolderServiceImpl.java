package learn.Cloud.service.impl;

import learn.Cloud.entity.Folder;
import learn.Cloud.entity.UserEntityInfo;
import learn.Cloud.exception.UserException;
import learn.Cloud.repository.FolderRepository;
import learn.Cloud.repository.UserRepository;
import learn.Cloud.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository mongoFolderRepository;
    private final UserRepository userRepository;

    @Override
    public void createFolder(String folderName, UserDetails userDetails,String parentId) {

        UserEntityInfo userbyEmail = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()->new UserException("User Not found",HttpStatus.NOT_FOUND));



        List<Folder> byParentId = mongoFolderRepository.findByParentId(parentId);
//        Folder parentFolder = byParentId
        Folder newFolder = new Folder();
        newFolder.setName(folderName);
        newFolder.setOwnerId(valueOf(userbyEmail.getId()));
//        newFolder.setParentId(parentFolder);
        

        mongoFolderRepository.save(newFolder);
    }

    @Override
    public List<Folder> getRootFolders(UserDetails userDetails) {
        UserEntityInfo byEmail = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()->new UserException("User not found"));
        return  mongoFolderRepository.findByParentId(valueOf(byEmail.getId()));

    }

    @Override
    public Folder getFolder(String folderId, UserDetails userDetails) {
        UserEntityInfo byEmail = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()->new UserException("User not found"));
        return mongoFolderRepository.findFolderByIdAndOwnerId(folderId,valueOf(byEmail.getId()));
    }

    @Override
    public List<Folder> getSubfolders(String folderId, UserDetails userDetails) {
        return List.of();
    }

    @Override
    public List<Folder> getFolderPath(String folderId, UserDetails userDetails) {
        List<Folder> path = new ArrayList<>();

        // Начинаем с текущей папки
        Folder current = getFolder(folderId, userDetails);
        path.add(current);

        // Идем вверх по родителям до корня
        while (current.getParentId() != null) {
            current = getFolder(current.getParentId(), userDetails);
            path.add(0, current); // Добавляем в начало списка
        }

        return path; // [Корень, Documents, Work, Project]
    }
}
