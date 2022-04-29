package com.codeinvestigator.springbootsftpmessagehandler.services;

import java.util.Date;

public interface JobService {

    void runJob(Date date);
    void runFail(Exception e, Date date);


}
