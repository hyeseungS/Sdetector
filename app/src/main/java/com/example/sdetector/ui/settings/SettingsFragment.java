package com.example.sdetector.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sdetector.LoginActivity;
import com.example.sdetector.MainActivity;
import com.example.sdetector.R;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        Button memberButton = (Button) rootView.findViewById(R.id.member_info);
        Button tacButton = (Button) rootView.findViewById(R.id.tac_info);
        ToggleButton alarmToggle = (ToggleButton) rootView.findViewById(R.id.toggle_);

        memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) getActivity();
                activity.moveToMEMSettings();

            }
        });
        tacButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) getActivity();
                activity.moveToTACSettings();

            }
        });
        alarmToggle.setOnCheckedChangeListener(
                //CompundButton.OnCheckedChangedListener을 새로 선언
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    // 첫번째 인자는 ToggleButton, 두번째 인자는 on/off에 대한 boolean값
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        String toastMessage;
                        //toggle 버튼이 on된 경우
                        if(isChecked){
                            toastMessage = "알림을 켭니다";
                        }else{
                            toastMessage = "알림을 끕니다";
                        }

                        Toast.makeText(getActivity(),toastMessage,Toast.LENGTH_SHORT).show();
                    }
                }
        );
        return rootView;
    }

}

