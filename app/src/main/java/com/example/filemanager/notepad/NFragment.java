package com.example.filemanager.notepad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.filemanager.R;

public class NFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        NModel model = new ViewModelProvider(this).get(NModel.class);
        View root = inflater.inflate(R.layout.fragment_notepad, container, false);
        final EditText etFileContent = root.findViewById(R.id.file_content);
        etFileContent.setText(model.getFileContent());

        return root;
    }

}
