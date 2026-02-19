package learn.Cloud.service.impl;

import io.minio.*;
import io.minio.messages.Item;
import learn.Cloud.entity.FileMetaDataInfo;
import learn.Cloud.entity.Folder;
import learn.Cloud.entity.UserEntityInfo;
import learn.Cloud.exception.UserException;
import learn.Cloud.model.UserDto;
import learn.Cloud.repository.FileMetaDataRepository;
import learn.Cloud.repository.FolderRepository;
import learn.Cloud.repository.UserRepository;
import learn.Cloud.service.FileManagementService;
import learn.Cloud.service.util.FileManagementServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileManagementServiceImpl implements FileManagementService {


    private final MinioClient minioClient;
    private final FileManagementServiceUtil fileManagementServiceUtil;
    private final FileMetaDataRepository fileMetaDataRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    private String bucketName;

    private void initBucket(String email) {

        bucketName = fileManagementServiceUtil.makeBucketName(email);
        try {

            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!bucketExists) {
                // Создаем bucket если не существует
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Bucket '{}' создан успешно", bucketName);
            } else {
                log.info("Bucket '{}' уже существует", bucketName);
            }

        } catch (Exception e) {
            log.error("Ошибка при инициализации MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось инициализировать MinIO", e);
        }
    }

    @Override
    public String uploadFiles(UserDetails userDetails, MultipartFile file, Model model, String folderId) {
        UserDto userDto1 = new UserDto();
        userDto1.setEmail("string");

        if (isNull(userDto1)) {
            throw new UserException("UserDto не может быть null");
        }
        UserEntityInfo userEntityInfo = userRepository.findByEmail(userDto1.getEmail())
                .orElseThrow(()-> new UserException("User not found"));

        Folder folderByIdAndOwnerId = folderRepository.findFolderByIdAndOwnerId(folderId, valueOf(userEntityInfo.getId()));

        if (folderByIdAndOwnerId == null) {
            throw new UserException("Папка не найдена или доступ запрещен");
        }
        FileMetaDataInfo fileMetaDataInfo = new FileMetaDataInfo();
        fileMetaDataInfo.setFolderId(folderId);
        fileMetaDataInfo.setOwnerId(String.valueOf(userEntityInfo.getId()));
        fileMetaDataInfo.setMinioPath(folderByIdAndOwnerId.getPath());
        fileMetaDataInfo.setName(file.getOriginalFilename());
        fileMetaDataRepository.save(fileMetaDataInfo);

        model.addAttribute("firstname", "string");

        initBucket(userDto1.getEmail());
        String filename = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("Файл '{}' успешно загружен в bucket '{}'", filename, bucketName);

        } catch (Exception e) {

            model.addAttribute("exception name", e.getMessage());
            return "exception-page";
        }

        return "main-page";
    }



    @Override
    public List<String> listAllFiles(UserDto userDto) {
        List<String> fileNames = new ArrayList<>();
        initBucket(userDto.getEmail());
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                fileNames.add(item.objectName());
            }

            log.info("Найдено {} файлов в бакете {}", fileNames.size(), bucketName);

        } catch (Exception e) {
            log.error("Ошибка при получении списка файлов: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить список файлов", e);
        }

        return fileNames;
    }


    @Override
    public Resource downloadFile(UserDetails userDetails, String fileName) {

//        initBucket(userDetails.getUsername());
        initBucket("string");
        try {
            // Получаем объект из MinIO
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            // Возвращаем как Resource
            return new InputStreamResource(response);

        } catch (Exception e) {
            log.error("Ошибка скачивания файла {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Файл не найден: " + fileName, e);
        }

    }
}
