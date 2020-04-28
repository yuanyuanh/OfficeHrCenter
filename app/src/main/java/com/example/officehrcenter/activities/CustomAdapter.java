package com.example.officehrcenter.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.officehrcenter.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> {

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nameText;
        TextView timeText;
        TextView msgText;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.activity_profitem, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_profitem, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.time);
            viewHolder.msgText = (TextView) convertView.findViewById(R.id.msg);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.nameText.setText("To meet: " + dataModel.getName());
        viewHolder.timeText.setText("Time: " + dataModel.getTime());
        if(dataModel.getMsg().equals("")){
            viewHolder.msgText.setText("No message");
        }else{
            viewHolder.msgText.setText("Message: " + dataModel.getMsg());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}

