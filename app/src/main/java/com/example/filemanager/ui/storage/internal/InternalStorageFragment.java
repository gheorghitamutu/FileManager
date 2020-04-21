package com.example.filemanager.ui.storage.internal;

import android.os.Bundle;
import android.os.Environment;
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

import com.example.filemanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InternalStorageFragment extends Fragment {

    private RecyclerView rv;
    private ArrayList<InternalStorageViewModel> models;
    private LinearLayout llNoMedia;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_internal_storage, container, false);

        TextView tvFilepath = rootView.findViewById(R.id.filepath);
        llNoMedia = rootView.findViewById(R.id.noMedia);

        rv = rootView.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager rv_lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rv_lm);

        models = new ArrayList<>();
        Adapter rvAdapter = new Adapter(models);
        rv.setAdapter(rvAdapter);

        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
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
            InternalStorageViewModel model = new InternalStorageViewModel(file);
            models.add(model);
        }

        Collections.sort(models, new Comparator<InternalStorageViewModel>() {
            public int compare(InternalStorageViewModel o1, InternalStorageViewModel o2) {
                if (o1.isDirectory() && !o2.isDirectory()) {
                    return -1;
                } else if (!o1.isDirectory() && o2.isDirectory()) {
                    return 1;
                }
                return o1.getFilename().compareTo(o2.getFilename());
            }
        });
    }

}
