package learn.Cloud.service;

import learn.Cloud.model.UserDto;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileManagementService {
    String uploadFiles(UserDto userDto, MultipartFile file, Model model);
    List<String> listAllFiles(UserDto userDto);
}
