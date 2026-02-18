package learn.Cloud.service;

import learn.Cloud.entity.FileMetaDataInfo;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface FileDisplayService {

    public List<FileMetaDataInfo> displayFolderFiles(UserDetails userDetails, String folderId);
}
