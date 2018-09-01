/*
 * Copyright (c) 2014 - 2018. The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 *  this code in a for-profit venture,please contact the copyright holder.
 */
package com.muzima.adapters.cohort;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import com.muzima.R;
import com.muzima.adapters.ListAdapter;
import com.muzima.api.model.Cohort;
import com.muzima.controller.CohortController;
import com.muzima.utils.Fonts;

/**
 * Responsible for displaying Cohorts as list.
 */
public abstract class CohortsAdapter extends ListAdapter<Cohort> {
    final CohortController cohortController;
    BackgroundListQueryTaskListener backgroundListQueryTaskListener;

    CohortsAdapter(Context context, int textViewResourceId, CohortController cohortController) {
        super(context, textViewResourceId);
        this.cohortController = cohortController;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(
                    R.layout.item_cohorts_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setTextToName(getItem(position).getName());
        return convertView;
    }

    public void setBackgroundListQueryTaskListener(BackgroundListQueryTaskListener backgroundListQueryTaskListener) {
        this.backgroundListQueryTaskListener = backgroundListQueryTaskListener;
    }

    class ViewHolder {
        private final CheckedTextView name;
        private final ImageView downloadedImage;

        ViewHolder(View convertView) {
            this.downloadedImage = convertView.findViewById(R.id.downloadImg);
            this.name = convertView
                    .findViewById(R.id.cohort_name);
        }

        void displayDownloadImage() {
            downloadedImage.setVisibility(View.VISIBLE);
        }

        void hideDownloadImage() {
            downloadedImage.setVisibility(View.GONE);
        }

        void setTextToName(String text) {
            name.setText(text);
            name.setTypeface(Fonts.roboto_medium(getContext()));
        }
    }
}
