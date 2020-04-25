package com.example.filemanager.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment.");
    }

    LiveData<String> getText() {
        return mText;
    }
}