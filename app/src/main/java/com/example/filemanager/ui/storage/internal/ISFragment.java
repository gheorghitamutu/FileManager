package com.example.filemanager.ui.storage.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.Manager;
import com.example.filemanager.R;
import com.example.filemanager.ui.storage.options.ODialog;
import com.example.filemanager.ui.storage.options.OModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// TODO: refresh fragment with another root (you may have to use Manager)
// https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
public class ISFragment extends Fragment {

    private RecyclerView rv;
    private ArrayList<ISModel> models;
    private LinearLayout llNoMedia;
    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_internal_storage, container, false);

        TextView tvFilepath = rootView.findViewById(R.id.filepath);
        llNoMedia = rootView.findViewById(R.id.noMedia);

        rv = rootView.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager rv_lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rv_lm);

        models = new ArrayList<>();
        ISAdapter rvAdapter = new ISAdapter(models);
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
            ISModel model = new ISModel(file);
            models.add(model);
        }

        Collections.sort(models, new Comparator<ISModel>() {
            public int compare(ISModel o1, ISModel o2) {
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
        // TODO: actual action (popup buttons for do action and cancel)
        final String message = item.toString() + " " + Manager.getCurrentPath() + "/" + Manager.getCurrentFile();
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();

        boolean actionResult = false;
        switch (item.getOption()) {
            case "Copy":
                break;
            case "Move":
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
