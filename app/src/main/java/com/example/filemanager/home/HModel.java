package com.example.filemanager.home;

import androidx.lifecycle.ViewModel;

public class HModel extends ViewModel {

    private String content;

    public HModel() {
        content = "This is home fragment.";
    }

    String getText() {
        return content;
    }
}