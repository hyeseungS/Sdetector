package com.example.sdetector;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class Settings2Fragment extends Fragment {
    private ViewGroup rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings2, container, false);

        Button button1 = (Button) rootView.findViewById(R.id.changeIdBtn);
        button1.setOnClickListener(this::onClick);
        Button button2 = (Button) rootView.findViewById(R.id.changePwBtn);
        button2.setOnClickListener(this::onClick);
        Button button3 = (Button) rootView.findViewById(R.id.changeIdBtn);
        button3.setOnClickListener(this::onClick);

        return rootView;
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.changeIdBtn:

                break;
            case R.id.changePwBtn:

                break;
            case R.id.changeInfoBtn:

                break;
        }
    }
}
