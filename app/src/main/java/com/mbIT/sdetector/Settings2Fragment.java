package com.mbIT.sdetector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
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
        Button button3 = (Button) rootView.findViewById(R.id.changeInfoBtn);
        button3.setOnClickListener(this::onClick);

        return rootView;
    }

    // ID 변경, PW 변경, 개인 정보 변경 버튼 클릭 이벤트
    public void onClick(View view) {

        MainActivity activity = (MainActivity) getActivity();

        switch (view.getId()) {
            case R.id.changeIdBtn:
                activity.moveToIDSettings(); // ID 변경 창으로 이동
                break;
            case R.id.changePwBtn:
                activity.moveToPWSettings(); // PW 변경 창으로 이동
                break;
            case R.id.changeInfoBtn: // 개인 정보 변경 창으로 이동
                activity.moveToINFOSettings();
                break;
        }
    }
}
