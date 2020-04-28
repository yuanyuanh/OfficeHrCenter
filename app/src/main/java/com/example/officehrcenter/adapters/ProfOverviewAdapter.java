package com.example.officehrcenter.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.officehrcenter.R;
import com.example.officehrcenter.objects.ProfOverviewDataModel;
import com.example.officehrcenter.objects.ProfileDataModel;

import java.util.ArrayList;

public class ProfOverviewAdapter extends ArrayAdapter<ProfOverviewDataModel> {

    private ArrayList<ProfOverviewDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nameText;
        TextView officeText;
    }

    public ProfOverviewAdapter(ArrayList<ProfileDataModel> data, Context context) {
        super(context, R.layout.profoverview_item, data);
        this.dataSet = data;
        this.mContext=context;
    }


}
