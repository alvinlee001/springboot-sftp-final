package com.codeinvestigator.springbootsftpmessagehandler.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CleanupJobServiceImpl implements CleanupJobService {

    @Value("${sftp.job.local.path}")
    private String localPath;

    @Override
    public void deleteFilesBeforeDate(Date date) {
        try {
            deleteFilesOlderThanDate(date, localPath);
        } catch (IOException e) {
            log.error("Failed to delete files in folder [" + localPath+"]", e);
        }

    }

    private void deleteFilesOlderThanDate(Date date, String dirPath) throws IOException {
        long cutOff = date.getTime();
        Files.list(Paths.get(dirPath))
                .filter(path -> {
                    try {
                        return Files.isRegularFile(path) && Files.getLastModifiedTime(path).to(TimeUnit.MILLISECONDS) < cutOff;
                    } catch (IOException ex) {
                        log.error("File/Folder ", ex);
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException ex) {
                        // log here and move on
                    }
                });
    }
}
