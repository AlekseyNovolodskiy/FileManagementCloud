package learn.Cloud.service;

import learn.Cloud.entity.Folder;
import learn.Cloud.model.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface FolderService {
    void createFolder(String folderName, UserDto userDto,String parentFolder);

    List<Folder> getRootFolders(UserDto userDto);

    Folder getFolder(String folderId, UserDetails userDetails);

    List<Folder> getSubfolders(String folderId, UserDetails userDetails);

    List<Folder> getFolderPath(String folderId, UserDetails userDetails);
}
