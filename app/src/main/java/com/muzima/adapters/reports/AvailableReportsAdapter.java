package com.muzima.adapters.reports;

import android.content.Context;
import android.util.Log;
import com.muzima.adapters.forms.FormsAdapter;
import com.muzima.controller.FormController;
import com.muzima.model.AvailableForm;
import com.muzima.model.collections.AvailableForms;
import com.muzima.tasks.FormsAdapterBackgroundQueryTask;
import com.muzima.utils.Constants;

import java.util.List;

public class AvailableReportsAdapter extends FormsAdapter<AvailableForm> {
    private static final String TAG = AvailableReportsAdapter.class.getSimpleName();

    public AvailableReportsAdapter(Context context, int textViewResourceId, FormController formController) {
        super(context, textViewResourceId, formController);
    }

    @Override
    public void reloadData() {
        new AvailableReportsAdapter.BackgroundQueryTask(this).execute();
    }

    public class BackgroundQueryTask extends FormsAdapterBackgroundQueryTask<AvailableForm> {

        public BackgroundQueryTask(FormsAdapter formsAdapter) {
            super(formsAdapter);
        }
        @Override
        protected void onPreExecute() {
            backgroundListQueryTaskListener.onQueryTaskStarted();
            AvailableReportsAdapter.this.clear();
        }
        @Override
        protected List<AvailableForm> doInBackground(Void... params) {
            AvailableForms reportTemplates = new AvailableForms();
            if (adapterWeakReference.get() != null) {
                try {
                    FormsAdapter formsAdapter = adapterWeakReference.get();
                    reportTemplates = formsAdapter.getFormController().getProviderReports();
                } catch (FormController.FormFetchException e) {
                    Log.w(TAG, "Exception occurred while fetching local provider reports ", e);
                }
            }
            return reportTemplates;
        }
        @Override
        protected void onPostExecute(List<AvailableForm> reportTemplates){
            if(reportTemplates.size() > 0){
                clear();
                addAll(reportTemplates);
                notifyDataSetChanged();
            } else {
                backgroundListQueryTaskListener.onQueryTaskFinish();
            }
        }
    }
}
