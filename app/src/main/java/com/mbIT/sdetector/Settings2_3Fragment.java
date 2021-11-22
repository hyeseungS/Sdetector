package com.mbIT.sdetector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Settings2_3Fragment extends Fragment {
    private ViewGroup rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings2_3, container, false);

        Button button = (Button) rootView.findViewById(R.id.changeINFOBtn);
        button.setOnClickListener(this::onClick);

        return rootView;
    }

    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.changeINFOBtn:

                break;
        }
    }
}