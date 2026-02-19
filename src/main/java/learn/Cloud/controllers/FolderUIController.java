package learn.Cloud.controllers;

import learn.Cloud.entity.FileMetaDataInfo;
import learn.Cloud.entity.Folder;
import learn.Cloud.model.UserDto;
import learn.Cloud.service.FileDisplayService;
import learn.Cloud.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/ui")
public class FolderUIController {

    private final FolderService folderService;
    private final FileDisplayService fileDisplayService;

    @GetMapping("/folders")
    public String listRootFolders(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        UserDto userDto = new UserDto();
        userDto.setEmail("string");

        // Получаем все КОРНЕВЫЕ папки пользователя (parentId = null)
        List<Folder> rootFolders = folderService.getRootFolders(userDto);

        model.addAttribute("folders", rootFolders);
        model.addAttribute("currentPath", "Корневая папка");
        model.addAttribute("parentId", null); // для формы создания

        return "list";
    }

    @GetMapping("/folders/{folderId}")
    public String viewFolder(
            @PathVariable String folderId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        log.info("=== ПРОСМОТР ПАПКИ ID: {} ===", folderId);

        Folder currentFolder = folderService.getFolder(folderId, userDetails);
        log.info("Текущая папка: {}", currentFolder != null ? currentFolder.getName() : "null");

        // Получаем подпапки
        List<Folder> subfolders = folderService.getSubfolders(folderId, userDetails);
        log.info("Найдено подпапок: {}", subfolders.size());

        // ✅ ПОЛУЧАЕМ ФАЙЛЫ В ЭТОЙ ПАПКЕ
        List<FileMetaDataInfo> files = fileDisplayService.displayFolderFiles(userDetails, folderId);
        log.info("Найдено файлов: {}", files.size());

        // Получаем путь для навигации (хлебные крошки)
        List<Folder> breadcrumbs = folderService.getFolderPath(folderId, userDetails);

        model.addAttribute("currentFolder", currentFolder);
        model.addAttribute("subfolders", subfolders);
        model.addAttribute("folderFiles", files);  // ✅ Добавляем файлы в модель
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("parentId", folderId); // для формы создания подпапок

        return "view";
    }

    @PostMapping("/create")
    public String createFolder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String folderName,
            @RequestParam(required = false) String parentId,  // имя должно совпадать с HTML!
            RedirectAttributes redirectAttributes) {

        try {
            UserDto userDto = new UserDto();
            userDto.setEmail("string");  // используйте реальный email

            folderService.createFolder(folderName, userDto, parentId);

            // Перенаправление обратно в папку или в корень
            if (parentId != null && !parentId.isEmpty()) {
                return "redirect:/ui/folders/" + parentId;
            } else {
                return "redirect:/ui/folders";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/folders";
        }
    }
}