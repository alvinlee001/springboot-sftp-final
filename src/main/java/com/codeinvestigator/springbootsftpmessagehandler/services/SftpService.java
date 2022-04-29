package com.codeinvestigator.springbootsftpmessagehandler.services;

public interface SftpService {
    boolean uploadFile(String localFilePath, String remoteFilePath);

    boolean downloadFile(String localFilePath, String remoteFilePath);
}
