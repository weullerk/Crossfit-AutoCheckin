package com.alienonwork.crossfitcheckin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.models.ClassModel;

import java.util.HashMap;
import java.util.List;

public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {

    List<String> daysList;
    HashMap<String, List<ClassModel>> classList;


    public ScheduleExpandableListAdapter(List<String> daysList, HashMap<String, List<ClassModel>> classList) {
        this.daysList = daysList;
        this.classList = classList;
    }

    @Override
    public int getGroupCount() {
        return this.daysList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.classList.get(this.daysList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.daysList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.classList.get(this.daysList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_group, parent, false);
        }
        TextView dayTextView = convertView.findViewById(R.id.schedule_day_textview);
        dayTextView.setText((String) this.getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ClassModel classModel = (ClassModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_child, parent, false);
        }
        TextView classTextView = convertView.findViewById(R.id.schedule_class_textview);
        classTextView.setText(String.format("%s - %s", classModel.getDescription(), classModel.getHour()));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
