package com.example.officehrcenter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.officehrcenter.R;
import com.example.officehrcenter.objects.ProfOverviewDataModel;

import java.util.ArrayList;

public class ProfOverviewAdapter extends ArrayAdapter<ProfOverviewDataModel> {

    private ArrayList<ProfOverviewDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nameText;
        TextView officeText;
    }

    public ProfOverviewAdapter(ArrayList<ProfOverviewDataModel> data, Context context) {
        super(context, R.layout.profoverview_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ProfOverviewDataModel profOverviewDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ProfOverviewAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ProfOverviewAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.profoverview_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.profoverview_name);
            viewHolder.officeText = (TextView) convertView.findViewById(R.id.profoverview_office);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ProfOverviewAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.nameText.setText("Name: " + profOverviewDataModel.getName());
        viewHolder.officeText.setText("Office: " + profOverviewDataModel.getOffice());
        // Return the completed view to render on screen
        return convertView;
    }

}
