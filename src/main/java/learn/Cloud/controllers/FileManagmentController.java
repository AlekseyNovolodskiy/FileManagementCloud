package learn.Cloud.controllers;

import learn.Cloud.model.UserDto;
import learn.Cloud.service.FileManagementService;
import learn.Cloud.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

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

        try {

            fileManagementService.uploadFiles(userDto, file,model);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла", e.getMessage());

        }

        return "main-page";
    }
    @GetMapping("/all-files")
    public List<String> getUsersFiles(@AuthenticationPrincipal UserDto userDto){
        return fileManagementService.listAllFiles(userDto);
    }
}
