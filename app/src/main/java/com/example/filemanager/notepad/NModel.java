package com.example.filemanager.notepad;

import androidx.lifecycle.ViewModel;

import com.example.filemanager.Manager;

public class NModel extends ViewModel {
    private String fileContent;

    public NModel() {
        fileContent = Manager.getStringFromCurrentFile();
    }

    String getFileContent() {
        return fileContent;
    }
}
