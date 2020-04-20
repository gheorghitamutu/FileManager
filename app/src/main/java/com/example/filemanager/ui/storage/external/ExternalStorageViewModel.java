package com.example.filemanager.ui.storage.external;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExternalStorageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ExternalStorageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is external storage fragment.");
    }

    LiveData<String> getText() {
        return mText;
    }
}