package com.codeinvestigator.springbootsftpmessagehandler.sftp;

public interface SftpServerConnector {
    boolean uploadFile(String localFilePath, String remoteFilePath);

    boolean downloadFile(String localFilePath, String remoteFilePath);
}
