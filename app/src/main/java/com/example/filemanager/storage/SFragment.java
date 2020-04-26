package com.example.filemanager.storage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.Manager;
import com.example.filemanager.R;
import com.example.filemanager.storage.options.ODialog;
import com.example.filemanager.storage.options.OModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SFragment extends Fragment {

    private RecyclerView rv;
    private ArrayList<SModel> models;
    private LinearLayout llNoMedia;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_storage, container, false);

        TextView tvFilepath = rootView.findViewById(R.id.filepath);
        llNoMedia = rootView.findViewById(R.id.noMedia);

        rv = rootView.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager rv_lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rv_lm);

        models = new ArrayList<>();
        SAdapter rvAdapter = new SAdapter(models);
        rv.setAdapter(rvAdapter);

        String rootPath = Manager.getCurrentPath();
        tvFilepath.setText(rootPath);
        getFilesList(rootPath);

        return rootView;
    }

    private void getFilesList(String filePath) {
        File f = new File(filePath);
        File[] files = f.listFiles();

        if (files == null) {
            return;
        }

        if (files.length == 0) {
            rv.setVisibility(View.GONE);
            llNoMedia.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            llNoMedia.setVisibility(View.GONE);
        }

        for (File file : files) {
            SModel model = new SModel(file);
            models.add(model);
        }

        Collections.sort(models, new Comparator<SModel>() {
            public int compare(SModel o1, SModel o2) {
                if (o1.isDirectory() && !o2.isDirectory()) {
                    return -1;
                } else if (!o1.isDirectory() && o2.isDirectory()) {
                    return 1;
                }
                return o1.getFilename().compareTo(o2.getFilename());
            }
        });
    }

    public boolean processActionOnItem(OModel.Item item) {

        boolean actionResult = false;
        switch (item.getOption()) {
            case "Rename":
                Manager.renameFSObject(); // special case, it will handle the toast message itself
                break;
            case "Copy":
                Manager.setActionAndSource("Copy");
                actionResult = true;
                break;
            case "Move":
                Manager.setActionAndSource("Move");
                actionResult = true;
                break;
            case "Delete":
                actionResult = Manager.deleteFSObject();
                break;
            default:
                break;
        }

        ODialog.getInstance().dismiss();
        return actionResult;
    }
}
