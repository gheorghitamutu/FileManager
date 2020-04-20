package com.example.filemanager.ui.storage.internal;

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

public class InternalStorageFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InternalStorageViewModel internalStorageViewModel = new ViewModelProvider(this).get(InternalStorageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_internal_storage, container, false);
        final TextView textView = root.findViewById(R.id.text_internal_storage);
        internalStorageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
