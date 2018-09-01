package com.muzima.scheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.muzima.MuzimaApplication;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static com.muzima.utils.Constants.DataSyncServiceConstants.MuzimaJobSchedularConstants.MESSAGE_SYNC_JOB_ID;
import static com.muzima.utils.Constants.DataSyncServiceConstants.MuzimaJobSchedularConstants.MUZIMA_JOB_PERIODIC;

public class MuzimaJobScheduleBuilder {

    private MuzimaApplication muzimaApplication;
    private Context context;

    public MuzimaJobScheduleBuilder(Context context){
        this.muzimaApplication = (MuzimaApplication) context.getApplicationContext();
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void schedulePeriodicBackgroundJob(){
        if (!isJobAlreadyScheduled(context)){
            handleScheduledPeriodicDataSyncJob();
            Log.e(getClass().getSimpleName(),"============Background task is scheduled=============");
        }else{
            cancelPeriodicBackgroundDataSyncJob();
            schedulePeriodicBackgroundJob();
            Log.e(getClass().getSimpleName(),"There is already a background service running.");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isJobAlreadyScheduled(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService( JOB_SCHEDULER_SERVICE ) ;

        boolean hasBeenScheduled = false ;

        for ( JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == MESSAGE_SYNC_JOB_ID ) {
                hasBeenScheduled = true ;
                break ;
            }
        }

        return hasBeenScheduled ;
    }

    private void handleScheduledPeriodicDataSyncJob() {
        ComponentName componentName = new ComponentName(context, MuzimaJobScheduler.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            int resultCode = JobScheduler.RESULT_FAILURE;
            JobInfo mUzimaJobInfo;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                mUzimaJobInfo = new JobInfo
                        .Builder(MESSAGE_SYNC_JOB_ID, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setMinimumLatency(MUZIMA_JOB_PERIODIC)
                        .build();
            }else {
                mUzimaJobInfo = new JobInfo
                        .Builder(MESSAGE_SYNC_JOB_ID, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setOverrideDeadline(MUZIMA_JOB_PERIODIC)
                        .build();
            }


            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                resultCode = jobScheduler.schedule(mUzimaJobInfo);
                Log.e(getClass().getSimpleName(),"============Background task is scheduled level 2=============");

            }

            if (resultCode == JobScheduler.RESULT_SUCCESS)
                Log.e(getClass().getSimpleName(),"============Background task is scheduled resultCode success=============");
            else
                Log.e(getClass().getSimpleName(),"============Background task is scheduled failed =============");
        }

    }

    private void cancelPeriodicBackgroundDataSyncJob() {
        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler)context.getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(MESSAGE_SYNC_JOB_ID);
            Toast.makeText(context, "mUzima data sync was cancelled.", Toast.LENGTH_SHORT).show();

        }
    }
}
