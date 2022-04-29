package com.codeinvestigator.springbootsftpmessagehandler.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ScheduledService {

    @Autowired
    private JobService jobService;

    @Scheduled(cron = "0 0 10 ? * * ")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 30 * 60 *1000))
    public void runFirstAttempt() {
        long now = System.currentTimeMillis() / 1000;
        System.out.println(
                "schedule tasks using cron jobs - " + now);
        jobService.runJob(new Date());
    }

    @Recover
    public void sendFailEmail(Exception e) {
        long now = System.currentTimeMillis() / 1000;
        System.out.println(
                "schedule tasks using cron jobs - " + now);
        jobService.runFail(e, new Date());

    }
}
