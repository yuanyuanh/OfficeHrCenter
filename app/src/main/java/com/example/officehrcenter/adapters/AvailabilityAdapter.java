package com.example.officehrcenter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.officehrcenter.R;
import com.example.officehrcenter.objects.AvailabilityDataModel;

import java.util.ArrayList;

public class AvailabilityAdapter extends ArrayAdapter<AvailabilityDataModel> {

    private ArrayList<AvailabilityDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView timeText;
        TextView availText;
    }

    public AvailabilityAdapter(ArrayList<AvailabilityDataModel> data, Context context) {
        super(context, R.layout.availability_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AvailabilityDataModel availabilityDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.availability_item, parent, false);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.availabilityDate);
            viewHolder.availText = (TextView) convertView.findViewById(R.id.availabilityStatus);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.timeText.setText("Time: " + availabilityDataModel.getStartTime() + " -- " + availabilityDataModel.getEndTime());
        if(availabilityDataModel.isAvailable()){
            viewHolder.availText.setText("Available");
            viewHolder.timeText.setTextColor(mContext.getResources().getColor(R.color.available));
            viewHolder.availText.setTextColor(mContext.getResources().getColor(R.color.available));
        }else{
            viewHolder.availText.setText("Not available");
            viewHolder.timeText.setTextColor(mContext.getResources().getColor(R.color.notAvailable));
            viewHolder.availText.setTextColor(mContext.getResources().getColor(R.color.notAvailable));
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
