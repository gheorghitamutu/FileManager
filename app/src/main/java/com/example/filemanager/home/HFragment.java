package com.example.filemanager.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.filemanager.R;

public class HFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HModel model = new ViewModelProvider(this).get(HModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView tvContent = root.findViewById(R.id.text_home);
        tvContent.setText(model.getText());

        return root;
    }
}
