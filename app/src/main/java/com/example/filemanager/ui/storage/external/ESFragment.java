package com.example.filemanager.ui.storage.external;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.filemanager.R;

public class ESFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ESModel externalStorageViewModel = new ViewModelProvider(this).get(ESModel.class);
        View root = inflater.inflate(R.layout.fragment_external_storage, container, false);
        final TextView textView = root.findViewById(R.id.text_external_storage);
        externalStorageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
