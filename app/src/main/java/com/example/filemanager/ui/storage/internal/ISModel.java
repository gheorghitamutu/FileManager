package com.example.filemanager.ui.storage.internal;

import androidx.lifecycle.ViewModel;

import java.io.File;

class ISModel extends ViewModel {

    private String filepath;
    private String filename;
    private boolean isDirectory;

    ISModel(File file) {
        this.filepath = file.getPath();
        this.filename = file.getName();
        this.isDirectory = file.isDirectory();
    }

    String getFilepath() {
        return filepath;
    }

    String getFilename() {
        return filename;
    }

    boolean isDirectory() {
        return isDirectory;
    }
}