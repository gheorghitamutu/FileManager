package com.example.filemanager.notepad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.filemanager.MainActivity;
import com.example.filemanager.Manager;
import com.example.filemanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class NFragment extends Fragment {
    private MainActivity ma;
    private EditText etFileContent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        NModel model = new ViewModelProvider(this).get(NModel.class);
        View root = inflater.inflate(R.layout.fragment_notepad, container, false);
        etFileContent = root.findViewById(R.id.file_content);
        etFileContent.setText(model.getFileContent());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        try {
            ma = (MainActivity) Manager.getActivity();
        } catch (ClassNotFoundException |
                NoSuchMethodException |
                InvocationTargetException |
                IllegalAccessException |
                NoSuchFieldException e) {
            e.printStackTrace();
        }

        BottomNavigationView bnv = Objects.requireNonNull(ma).findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_cancel:
                        Toast.makeText(ma, "Cancel", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_nav_ok:

                        String content = etFileContent.getText().toString();
                        if (Manager.saveInFile(content)) {
                            Toast.makeText(ma, "Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ma, "Failed to save the file!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                ma.goToPreviousFragment();
                return true;
            }
        });
    }
}
