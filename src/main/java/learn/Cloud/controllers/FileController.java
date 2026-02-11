package learn.Cloud.controllers;


import learn.Cloud.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String savedFilename = minioService.uploadFile(file, filename);
            return ResponseEntity.ok("Файл загружен: " + savedFilename);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла", e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = minioService.downloadFile(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Ошибка скачивания файла", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/url/{filename}")
    public ResponseEntity<String> getFileUrl(@PathVariable String filename) {
        try {
            URL url = minioService.getFileUrl(filename);
            return ResponseEntity.ok(url.toString());
        } catch (Exception e) {
            log.error("Ошибка получения URL", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            minioService.deleteFile(filename);
            return ResponseEntity.ok("Файл удален: " + filename);
        } catch (Exception e) {
            log.error("Ошибка удаления файла", e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка удаления файла: " + e.getMessage());
        }
    }
}