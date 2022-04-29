package com.codeinvestigator.springbootsftpmessagehandler.controller;

import com.codeinvestigator.springbootsftpmessagehandler.services.JobService;
import com.codeinvestigator.springbootsftpmessagehandler.services.email.EmailBean;
import com.codeinvestigator.springbootsftpmessagehandler.services.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("job")
@Slf4j
public class JobRestController {

    @Autowired
    private JobService jobService;

    @Autowired
    private EmailService emailService;

    @GetMapping({"/trigger/{dateString}"})
    public String triggerJob(@PathVariable(required = false) String dateString) throws ParseException {

        log.info("triggerJob() entry...");

        Date date = null;
        if (dateString == null) {
            date = new Date();
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = df.parse(dateString);
        }

        jobService.runJob(date);
        return "Done";
    }

    @GetMapping({"/testEmail"})
    public void run() {
        try {
            EmailBean email= new EmailBean("email.ftl");
            Map<String, Object> map = new HashMap<>();
            map.put("content", "Conent is here");
            email.setDatamap(map);
            email.setSubject("title here");
            email.setFrom("talentlab.donotreply@gmail.com");
            email.setTo(Arrays.asList("alvinlee001@live.com.my"));
            emailService.sendEmail(email);
        } catch (Exception ex){
            log.error("", ex);
        }
    }
}
