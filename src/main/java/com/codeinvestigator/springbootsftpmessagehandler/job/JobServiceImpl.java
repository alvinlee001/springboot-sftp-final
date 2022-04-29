package com.codeinvestigator.springbootsftpmessagehandler.job;

import com.codeinvestigator.springbootsftpmessagehandler.email.EmailBean;
import com.codeinvestigator.springbootsftpmessagehandler.email.service.EmailSender;
import com.codeinvestigator.springbootsftpmessagehandler.sftp.SftpServerConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobServiceImpl implements JobService {

    @Value("${sftp.job.remote.path}")
    private String jobPath;


    @Value("${sftp.job.remote.filename}")
    private String jobFile;

    @Value("${sftp.job.local.path}")
    private String localPath;


    @Value("${email.recipient}")
    private String emailTo;

    @Value("${email.smtp.account}")
    private String emailFrom;

    @Autowired
    private SftpServerConnector sftpService;

    @Autowired
    private EmailSender emailSender;

    @Override
    public void runJob(Date date) {

        log.info("run() entry... date: {}", date);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dateString = df.format(date);

        // download file
        String filename = jobFile.replace("{DATE_PLACEHOLDER}", dateString);
        Boolean IsDownloadSuccess;
        try{
            IsDownloadSuccess = sftpService.downloadFile(localPath+"/"+filename, jobPath+"/"+filename);
            if (!IsDownloadSuccess) {
                log.error("Download File failed");
                throw new FileNotExistException();
            }
        } catch (Exception e) {
            throw new RemoteServiceNotAvailableException();
        }

        //process file
        Boolean isFileProcessSuccess;
        String data = "";
        File fileForJob = new File(localPath+"/"+filename);
        try {
            Scanner myReader = new Scanner(fileForJob);
            int lines = 0;
            while (myReader.hasNextLine() && lines < 2) {
                data += myReader.nextLine()+"\n";
                lines++;
            }
        } catch (Exception e) {
            log.error("Failed to Process file", e);
            throw new FileProcessingException();
        }

        // send success email
        EmailBean email= new EmailBean("email.ftl");


        Map<String, Object> map = new HashMap<>();
        map.put("content", data);
        email.setDatamap(map);
        email.setSubject("Job Success");
        email.setAttachmentFiles(Arrays.asList(fileForJob));
        email.setFrom(emailFrom);
        email.setTo(Arrays.stream(emailTo.split(",")).collect(Collectors.toList()));
        try {
            emailSender.sendEmail(email);
            fileForJob.delete();
            return;
        } catch (Exception e) {
            log.error("Fail to send email", e);
        }

    }



    @Override
    public void runFail(Exception originalException, Date date) {
        log.info("runFail() entry...");
        log.error("Error when tried to runJob().. ", originalException);
        log.info("Sending failure email");

        Map<String, Object> map = new HashMap<>();
        EmailBean email= new EmailBean("fail.ftl");
        email.setFrom(emailFrom);
        email.setSubject("Job Failed");
        email.setTo(Arrays.stream(emailTo.split(",")).collect(Collectors.toList()));
        if (originalException instanceof RemoteServiceNotAvailableException) {
            map.put("reason",  "SFTP Error: SFTP server might not be reachable. Please contact IT");
        } else if (originalException instanceof  FileNotExistException){
            map.put("reason",  "SFTP Error: File not downloadable or does not exist. Please contact IT");
        } else if (originalException instanceof  FileProcessingException){
            map.put("reason",  "File Processing fail. Please contact IT.");
        } else {
            map.put("reason",  "Unknown error. Please contact IT.");
        }
        email.setDatamap(map);
        try {
            emailSender.sendEmail(email);
        } catch (Exception e) {
            log.error("Fail to send failure email", e);
        }
    }


    class FileNotExistException extends RuntimeException {

    }

    class RemoteServiceNotAvailableException extends RuntimeException {

    }

    class FileProcessingException extends RuntimeException {

    }

}
