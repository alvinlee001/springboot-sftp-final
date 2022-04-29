package com.codeinvestigator.springbootsftpmessagehandler.job;

import java.util.Date;

public interface CleanupJobService {
    void deleteFilesBeforeDate(Date date);
}
