package com.tournamentpool.application;

import com.deere.isg.worktracker.OutstandingWork;
import com.deere.isg.worktracker.servlet.WorkConfig;
import com.deere.isg.worktracker.servlet.WorkContextListener;

public class WorkTrackerContextListener extends WorkContextListener {
    public WorkTrackerContextListener() {
        super(new WorkConfig.Builder<>( MDC.init(new OutstandingWork<>()))
                .withHttpFloodSensor() //Optional, omit if not required
                .withZombieDetector() //Optional, omit if not required
                .build()
        );
    }
}