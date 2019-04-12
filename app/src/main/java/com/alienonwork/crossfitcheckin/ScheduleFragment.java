package com.alienonwork.crossfitcheckin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ScheduleFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private List<String> mDays = new ArrayList<String>();
    private HashMap<String, List<ClassModel>> mClasses = new HashMap<String, List<ClassModel>>();
    private ScheduleExpandableListAdapter mScheduleAdapter = new ScheduleExpandableListAdapter(mDays, mClasses);

    public ScheduleFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        mExpandableListView = view.findViewById(R.id.schedule_expandable_listview);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mExpandableListView.setAdapter(mScheduleAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HashMap<String, List<ClassModel>> scheduleListData = ClassModelMock.getData();
        List<String> days = new ArrayList<>(scheduleListData.keySet());

        for (String day: days) {
            mDays.add(day);
            mClasses.put(day, scheduleListData.get(day));
        }
    }
}
