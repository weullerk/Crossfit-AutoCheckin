package com.alienonwork.crossfitcheckin.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.alienonwork.crossfitcheckin.repository.CfCheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entity.ClassCrossfit;

import java.util.List;

public class ClassCrossfitViewModel extends AndroidViewModel {

    private LiveData<List<ClassCrossfit>> classCrossfitLiveData;

    public ClassCrossfitViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ClassCrossfit>> getClassCrossfit() {
        if (classCrossfitLiveData == null) {
            classCrossfitLiveData = CfCheckinDatabaseAccessor
                    .getInstance(getApplication())
                    .classCrossfitDAO()
                    .listClassesLiveData(1, 2);
        }
        return classCrossfitLiveData;
    }

    // TODO: 04/06/2019 call worker populate db with class list
    public void loadClassCrossfit() {

    }
}
