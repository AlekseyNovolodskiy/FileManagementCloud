package learn.Cloud.controllers;


import learn.Cloud.entity.FileMetaDataInfo;
import learn.Cloud.service.FileDisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui")
public class FileDisplayMetaDataController {

    private final FileDisplayService fileDisplayService;

    @PostMapping("/display-files/{folderId}")
    public String displayFiles(@AuthenticationPrincipal UserDetails userDetails, Model model,
                               @PathVariable("folderId") String folderId){

        List<FileMetaDataInfo> folderFiles = fileDisplayService.displayFolderFiles(userDetails, folderId);

        model.addAttribute("currentFolder", folderId);
        model.addAttribute("folderFiles", folderFiles);
        return "FilesView";
    }

}
