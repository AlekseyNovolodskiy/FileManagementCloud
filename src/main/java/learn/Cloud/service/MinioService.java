package learn.Cloud.service;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;


    @PostConstruct
    public void init() {
        try {
            // Проверяем существует ли bucket
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

    public String uploadFile(MultipartFile file, String filename) throws Exception {
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
            return filename;
        }
    }

    public URL getFileUrl(String filename) throws Exception {
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(filename)
                        .expiry(1, TimeUnit.HOURS)
                        .build()
        );
        return new URL(url);
    }

    public Resource downloadFile(String filename) throws Exception {
        try {
            // Проверяем существует ли файл
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );

            // Скачиваем файл напрямую
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            )) {
                byte[] content = stream.readAllBytes();
                return new ByteArrayResource(content) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                };
            }
        } catch (Exception e) {
            log.error("Ошибка скачивания файла '{}': {}", filename, e.getMessage());
            throw e;
        }
    }

    public void deleteFile(String filename) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filename)
                        .build()
        );
        log.info("Файл '{}' удален из bucket '{}'", filename, bucketName);
    }

    public boolean fileExists(String filename) throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}