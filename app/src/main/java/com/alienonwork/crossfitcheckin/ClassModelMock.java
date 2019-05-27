package com.alienonwork.crossfitcheckin;

import com.alienonwork.crossfitcheckin.repository.entity.ClassCrossfit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassModelMock {
    public static HashMap<String, List<ClassCrossfit>> getData() {
        HashMap<String, List<ClassCrossfit>> scheduleListData = new HashMap<>();

        List<ClassCrossfit> turmas = new ArrayList<>();
        turmas.add(new ClassCrossfit("Crossfit", "6:00"));
        turmas.add(new ClassCrossfit("Crossfit", "7:00"));
        turmas.add(new ClassCrossfit("Crossfit", "8:00"));
        turmas.add(new ClassCrossfit("Crossfit", "9:00"));

        scheduleListData.put("Segunda", turmas);
        scheduleListData.put("Terça", turmas);
        scheduleListData.put("Quarta", turmas);

        return scheduleListData;
    }
}