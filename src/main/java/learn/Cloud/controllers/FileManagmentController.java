package learn.Cloud.controllers;

import learn.Cloud.model.UserDto;
import learn.Cloud.service.FileManagementService;
import learn.Cloud.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
public class FileManagmentController {
    private final FileManagementService fileManagementService;

    private final MinioService minioService;

    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal UserDto userDto, MultipartFile file, Model model) {

        UserDto userDto1 = new UserDto();
        userDto1.setEmail("string");
        userDto1.setPassword("string");

        try {

            fileManagementService.uploadFiles(userDto1, file, model);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла", e.getMessage());

        }

        return "main-page";
    }

    @GetMapping("/all-files")
    @ResponseBody
    public List<String> getUsersFiles(@AuthenticationPrincipal UserDto userDto) {
        UserDto userDto1 = new UserDto();
        userDto1.setEmail("string");
        userDto1.setPassword("string");
        return fileManagementService.listAllFiles(userDto1);
    }


    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String fileName) {

        log.info("Скачивание файла: {}", fileName);

        try {
            Resource resource = fileManagementService.downloadFile(userDetails, fileName);

            // Кодируем имя файла для HTTP заголовка
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .body(resource);

        } catch (Exception e) {
            log.error("Ошибка при скачивании файла: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

