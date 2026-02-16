package learn.Cloud.service.impl;

import learn.Cloud.entity.Folder;
import learn.Cloud.entity.UserEntityInfo;
import learn.Cloud.exception.UserException;
import learn.Cloud.model.UserDto;
import learn.Cloud.repository.FolderRepository;
import learn.Cloud.repository.UserRepository;
import learn.Cloud.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository mongoFolderRepository;
    private final UserRepository userRepository;

    @Override
    public void createFolder(String folderName, UserDto userDto,String parentId) {

        UserEntityInfo userbyEmail = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(()->new UserException("User Not found",HttpStatus.NOT_FOUND));


        Folder newFolder = new Folder();
        newFolder.setName(folderName);
        newFolder.setOwnerId(valueOf(userbyEmail.getId()));
        newFolder.setParentId(parentId);

        String path;
        if (parentId != null && !parentId.isEmpty()) {
            // Находим родительскую папку для построения пути
            Folder parentFolder = mongoFolderRepository.findById(parentId)
                    .orElseThrow(() -> new UserException("Parent folder not found"));
            path = parentFolder.getPath() + "/" + folderName;
        } else {
            path = "/" + folderName;
        }
        newFolder.setPath(path);

        mongoFolderRepository.save(newFolder);
        log.info("Папка успешно сохранена с ID: {}, путь: {}",
                savedFolder.getId(), savedFolder.getPath());
    }

    @Override
    public List<Folder> getRootFolders(UserDto userDetails) {
        UserEntityInfo byEmail = userRepository.findByEmail(userDetails.getEmail())
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
