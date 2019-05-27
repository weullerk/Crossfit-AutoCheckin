package com.alienonwork.crossfitcheckin.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.alienonwork.crossfitcheckin.ClassModelMock;
import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.adapters.ScheduleExpandableListAdapter;
import com.alienonwork.crossfitcheckin.repository.entity.ClassCrossfit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ScheduleFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private List<String> mDays = new ArrayList<String>();
    private HashMap<String, List<ClassCrossfit>> mClasses = new HashMap<String, List<ClassCrossfit>>();
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

        HashMap<String, List<ClassCrossfit>> scheduleListData = ClassModelMock.getData();
        List<String> days = new ArrayList<>(scheduleListData.keySet());

        for (String day: days) {
            mDays.add(day);
            mClasses.put(day, scheduleListData.get(day));
        }
    }
}
