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
import java.util.stream.Collectors;

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

        if (parentId != null && !parentId.isEmpty()) {
            newFolder.setParentId(parentId);
        } else {
            newFolder.setParentId(null); // ВАЖНО: null, а не пустая строка!
        }

        String path;
        if (parentId != null && !parentId.isEmpty()) {
            // Вложенная папка
            Folder parentFolder = mongoFolderRepository.findById(parentId)
                    .orElseThrow(() -> new UserException("Parent folder not found"));
            path = parentFolder.getPath() + "/" + folderName;
        } else {
            // Корневая папка
            path = "/" + folderName;  // "/Documents", "/Photos" и т.д.
        }

        newFolder.setName(folderName);
        newFolder.setOwnerId(valueOf(userbyEmail.getId()));
        newFolder.setPath(path);

        mongoFolderRepository.save(newFolder);
        log.info("Папка успешно сохранена с ID: {}, путь: {}",
                newFolder.getId(), newFolder.getPath());
    }

    @Override
    public List<Folder> getRootFolders(UserDto userDetails) {
        UserEntityInfo byEmail = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(()->new UserException("User not found"));
        return  mongoFolderRepository.findByOwnerIdAndParentIdIsNull(valueOf(byEmail.getId()));

    }

    @Override
    public Folder getFolder(String folderId, UserDetails userDetails) {

        UserEntityInfo byEmail = userRepository.findByEmail("string")
                .orElseThrow(()->new UserException("User not found"));
        return mongoFolderRepository.findFolderByIdAndOwnerId(folderId,valueOf(byEmail.getId()));
    }

    @Override
    public List<Folder> getSubfolders(String folderId, UserDetails userDetails) {
        log.info("Получение подпапок для папки ID: {}", folderId);

        // Получаем пользователя
        UserEntityInfo user = userRepository.findByEmail("string")
                .orElseThrow(() -> new UserException("User not found"));

        // Ищем все папки, у которых parentId = folderId
        List<Folder> subfolders = mongoFolderRepository.findByParentId(folderId);
        log.info("Найдено подпапок: {}", subfolders.size());

        // Фильтруем по владельцу (для безопасности)
        String ownerId = String.valueOf(user.getId());
        return subfolders.stream()
                .filter(f -> f.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
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
