package com.alienonwork.crossfitcheckin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassModelMock {
    public static HashMap<String, List<ClassModel>> getData() {
        HashMap<String, List<ClassModel>> scheduleListData = new HashMap<>();

        List<ClassModel> turmas = new ArrayList<>();
        turmas.add(new ClassModel("Crossfit", "6:00"));
        turmas.add(new ClassModel("Crossfit", "7:00"));
        turmas.add(new ClassModel("Crossfit", "8:00"));
        turmas.add(new ClassModel("Crossfit", "9:00"));

        scheduleListData.put("Segunda", turmas);
        scheduleListData.put("Terça", turmas);
        scheduleListData.put("Quarta", turmas);

        return scheduleListData;
    }
}