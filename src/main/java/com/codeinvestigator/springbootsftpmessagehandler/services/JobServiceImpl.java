package com.codeinvestigator.springbootsftpmessagehandler.services;

import com.codeinvestigator.springbootsftpmessagehandler.services.email.EmailBean;
import com.codeinvestigator.springbootsftpmessagehandler.services.email.service.EmailService;
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
    private SftpService sftpService;

    @Autowired
    private EmailService emailService;

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
                throw new RemoteServiceNotAvailableException();
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

        // send email

        EmailBean email= new EmailBean("email.ftl");


        Map<String, Object> map = new HashMap<>();
        map.put("content", data);
        email.setDatamap(map);
        email.setSubject("Job Success");
        email.setAttachmentFiles(Arrays.asList(fileForJob));
        email.setFrom(emailFrom);
        email.setTo(Arrays.stream(emailTo.split(",")).collect(Collectors.toList()));
        try {
            emailService.sendEmail(email);
//                successMap.put(dateString, true);
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
            map.put("reason",  "SFTP File read fail.");
        } else if (originalException instanceof  FileProcessingException){
            map.put("reason",  "File Processing fail.");
        } else {
            map.put("reason",  "Unknown error, please refer to logs.");
        }
        email.setDatamap(map);
        try {
            emailService.sendEmail(email);
        } catch (Exception e) {
            log.error("Fail to send failure email", e);
        }
    }

    class RemoteServiceNotAvailableException extends RuntimeException {

    }

    class FileProcessingException extends RuntimeException {

    }

}
