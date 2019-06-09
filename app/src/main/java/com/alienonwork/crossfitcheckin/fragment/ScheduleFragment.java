package com.alienonwork.crossfitcheckin.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.adapters.ScheduleExpandableListAdapter;
import com.alienonwork.crossfitcheckin.repository.entities.ClassCrossfit;
import com.alienonwork.crossfitcheckin.viewmodel.ClassCrossfitViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;


public class ScheduleFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    ClassCrossfitViewModel classCrossfitViewModel;
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
        mExpandableListView.setChoiceMode(CHOICE_MODE_SINGLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        classCrossfitViewModel = ViewModelProviders.of(getActivity()).get(ClassCrossfitViewModel.class);
        classCrossfitViewModel.getClassCrossfit().observe(this, new Observer<List<ClassCrossfit>>() {
            @Override
            public void onChanged(List<ClassCrossfit> classCrossfits) {

            }
        });
    }
}
