package learn.Cloud.service.impl;

import learn.Cloud.entity.FileMetaDataInfo;
import learn.Cloud.entity.UserEntityInfo;
import learn.Cloud.exception.UserException;
import learn.Cloud.repository.FileMetaDataRepository;
import learn.Cloud.repository.UserRepository;
import learn.Cloud.service.FileDisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;

@Service
@RequiredArgsConstructor
public class FileDisplayServiceImpl implements FileDisplayService {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final UserRepository userRepository;

    @Override
    public List<FileMetaDataInfo> displayFolderFiles(UserDetails userDetails, String folderId) {

        UserEntityInfo byEmail = userRepository.findByEmail("string")
                .orElseThrow(()->new UserException("User not found"));

        return fileMetaDataRepository.findByOwnerIdAndFolderId(valueOf(byEmail.getId()), folderId);


    }
}
