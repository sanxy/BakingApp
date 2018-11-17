package com.sanxynet.bakingapp.service;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import com.sanxynet.bakingapp.remote.RetroCall;

public class ScheduleService extends JobService {

    private RetroCall retroCall;

    @Override
    public boolean onStartJob(JobParameters job) {
        retroCall = new RetroCall();
        retroCall.syncData(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (retroCall != null) {
            retroCall.cancelRequest();
        }
        return true;
    }
}
