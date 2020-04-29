package com.example.officehrcenter.adapters;

import android.content.Context;
import android.R.color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.officehrcenter.R;
import com.example.officehrcenter.objects.ProfileDataModel;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<ProfileDataModel> {

    private ArrayList<ProfileDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nameText;
        TextView timeText;
        TextView msgText;
    }

    public ProfileAdapter(ArrayList<ProfileDataModel> data, Context context) {
        super(context, R.layout.profile_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ProfileDataModel profileDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.profile_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.name);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.time);
            viewHolder.msgText = (TextView) convertView.findViewById(R.id.msg);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        if(profileDataModel.getName().equals("")){
            viewHolder.nameText.setText("Personal activity");
        }else{
            viewHolder.nameText.setText("To meet: " + profileDataModel.getName());
        }
        viewHolder.timeText.setText("Time: " + profileDataModel.getTime());
        if(profileDataModel.getMsg().equals("")){
            viewHolder.msgText.setText("No note");
        }else{
            viewHolder.msgText.setText("Note: " + profileDataModel.getMsg());
        }
        // Return the completed view to render on screencon
        return convertView;
    }
}

