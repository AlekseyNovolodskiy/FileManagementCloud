package learn.Cloud.service.util;

import org.springframework.stereotype.Service;

@Service
public class FileManagementServiceUtil {

    public String makeBucketName(String email){

        String prepareEmail = email.toLowerCase()
                .trim()
                .replace("@", "-at-")
                .replaceAll("[^a-z0-9.-]", "-");

        prepareEmail = prepareEmail.replaceAll("^[.-]+|[.-]+$", "");

        prepareEmail = prepareEmail.replaceAll("[.-]{2,}", "-");

        if (prepareEmail.length() < 3) {
            prepareEmail = "bucket-" + prepareEmail;
        }
        if (prepareEmail.length() > 63) {
            prepareEmail = prepareEmail.substring(0, 63);
        }

        return prepareEmail;
    }

}
