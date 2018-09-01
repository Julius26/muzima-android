package com.muzima.scheduler;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.muzima.MuzimaApplication;
import com.muzima.api.model.Person;
import com.muzima.api.model.User;
import com.muzima.controller.NotificationController;
import com.muzima.service.DataSyncService;
import com.muzima.service.MuzimaSyncService;

import static com.muzima.utils.Constants.DataSyncServiceConstants.MuzimaJobSchedularConstants.JOB_INDICATOR_STOP;
import static com.muzima.utils.Constants.DataSyncServiceConstants.MuzimaJobSchedularConstants.MUZIMA_JOB_SCHEDULE_INTENT;
import static com.muzima.utils.Constants.DataSyncServiceConstants.MuzimaJobSchedularConstants.WORK_DURATION_KEY;

@SuppressLint("NewApi")
public class MuzimaJobScheduler extends JobService {

    private NotificationController notificationController;
    private MuzimaSyncService muzimaSynService;
    private String authenticatedUserUuid;
    private User authenticatedUser;
    private Person person;
    private boolean isAuthPerson = false;

    @Override
    public void onCreate() {
        super.onCreate();
        MuzimaApplication muzimaApplication = (MuzimaApplication) getApplicationContext();
        notificationController = muzimaApplication.getNotificationController();
        muzimaSynService = muzimaApplication.getMuzimaSyncService();
        authenticatedUser = muzimaApplication.getAuthenticatedUser();
        if (authenticatedUser != null){
            person = authenticatedUser.getPerson();

            if (person != null){
                authenticatedUserUuid = person.getUuid();
                isAuthPerson = true;
            }else{
                isAuthPerson = false;
            }

        }else {
            isAuthPerson = false;
            Log.e(getClass().getSimpleName(), "Authenticated user is not a person");
        }
    }

    @Override
    public boolean onStartJob(final JobParameters params) {

        if (authenticatedUser == null || !isAuthPerson) {
            onStopJob(params);
        } else {
            //execute job
            handleBackgroundWork(params);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(getClass().getSimpleName(), "mUzima Job Service stopped" + params.getJobId());
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getClass().getSimpleName(), "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(getClass().getSimpleName(), "Downloading messages in Job");
        return START_NOT_STICKY;
    }

    private void handleBackgroundWork(JobParameters parameters) {

        if (parameters == null) {
            Log.e(getClass().getSimpleName(), "Parameters for job is null");
        } else {
            new NotificationDownloadBackgroundTask().execute();
        }
    }

    private class  NotificationDownloadBackgroundTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            muzimaSynService.downloadNotifications(authenticatedUserUuid);
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.e(getClass().getSimpleName(), "Downloading messages in Job");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
