package learn.Cloud.service.impl;

import learn.Cloud.entity.FileMetaDataInfo;
import learn.Cloud.entity.UserEntityInfo;
import learn.Cloud.exception.UserException;
import learn.Cloud.repository.FileMetaDataRepository;
import learn.Cloud.repository.UserRepository;
import learn.Cloud.service.FileDisplayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileDisplayServiceImpl implements FileDisplayService {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final UserRepository userRepository;

    @Override
    public List<FileMetaDataInfo> displayFolderFiles(UserDetails userDetails, String folderId) {

        log.info("Поиск файлов для пользователя: {}, папка: {}", "string", folderId);


        //todo
        UserEntityInfo byEmail = userRepository.findByEmail("string")
                .orElseThrow(()->new UserException("User not found"));

        List<FileMetaDataInfo> files = fileMetaDataRepository.findByOwnerIdAndFolderId(
                String.valueOf(byEmail.getId()),
                folderId
        );

        log.info("Найдено файлов: {}", files.size());

        return files;


    }
}
