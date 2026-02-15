package learn.Cloud.service;

import learn.Cloud.entity.Folder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface FolderService {
    void createFolder(String folderName, UserDetails userDetails,String parentFolder);

    List<Folder> getRootFolders(UserDetails userDetails);

    Folder getFolder(String folderId, UserDetails userDetails);

    List<Folder> getSubfolders(String folderId, UserDetails userDetails);

    List<Folder> getFolderPath(String folderId, UserDetails userDetails);
}
