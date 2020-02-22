package com.alienonwork.crossfitcheckin.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.adapters.ScheduleExpandableListAdapter;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.services.CheckinService;
import com.alienonwork.crossfitcheckin.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;
import static com.alienonwork.crossfitcheckin.helpers.CheckinHelper.translateDayOfWeek;


public class ScheduleFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    ScheduleViewModel scheduleViewModel;
    private List<String> mDays = new ArrayList<String>();
    private HashMap<String, List<Schedule>> mClasses = new HashMap<String, List<Schedule>>();
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

    // todo  ao selecionar um valor, salvar esse valor
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        scheduleViewModel = ViewModelProviders.of(getActivity()).get(ScheduleViewModel.class);
        scheduleViewModel.getSchedules().observe(this, new Observer<List<Schedule>>() {
            @Override
            public void onChanged(List<Schedule> schedules) {
                if (schedules.size() > 0) {
                    // load variables mclasses and mdays
                    for(Schedule schedule : schedules) {
                        String dayName = translateDayOfWeek(schedule.getDayOfWeek());
                        if (mDays.contains(dayName)) {
                            mClasses.get(dayName).add(schedule);
                        } else {
                            List<Schedule> newScheduleList = new ArrayList<>();
                            newScheduleList.add(schedule);
                            mDays.add(dayName);
                            mClasses.put(dayName, newScheduleList);
                        }
                    }
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Boolean autoCheckinEnabled = sharedPref.getBoolean(getString(R.string.pref_auto_checkin_enabled), true);
                    if (autoCheckinEnabled) {
                        // todo start intent with command to get list of schedules
                    }
                }
            }
        });
    }
}
