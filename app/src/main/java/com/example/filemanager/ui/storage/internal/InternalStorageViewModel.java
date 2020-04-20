package com.example.filemanager.ui.storage.internal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InternalStorageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InternalStorageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is internal storage fragment.");
    }

    LiveData<String> getText() {
        return mText;
    }
}