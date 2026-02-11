package learn.Cloud.controllers;

import io.minio.MinioClient;
import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestMinioController {

    private final MinioClient minioClient;

    @GetMapping("/buckets")
    public String listBuckets() {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            return "Доступные buckets: " + buckets.stream()
                    .map(Bucket::name)
                    .toList();
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        try {
            // Простая проверка соединения
            minioClient.listBuckets();
            return "MinIO подключен успешно";
        } catch (Exception e) {
            return "Ошибка подключения к MinIO: " + e.getMessage();
        }
    }
}