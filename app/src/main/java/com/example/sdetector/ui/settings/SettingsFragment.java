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
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

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
        Button logoutBtn = (Button) rootView.findViewById(R.id.logoutBtn);

        // 회원 정보 버튼 클릭 시 Settings2Fragment로 이동
       memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) getActivity();
                activity.moveToMEMSettings();

            }
        });

       // 이용 약관 확인 버튼 클릭 시 Settings3Fragment로 이동
        tacButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) getActivity();
                activity.moveToTACSettings();

            }
        });

        // 알림 수신 여부 Toggle Button 이벤트
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

       // 로그아웃 버튼 클릭 시 로그인 화면으로 이동
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                    @Override
                    public Unit invoke(User user, Throwable throwable) {
                        if (user != null) { // 카카오 로그인 상태
                            UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                                @Override
                                public Unit invoke(Throwable throwable) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    //                         -> getContext() ?
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET ?
                                    startActivity(intent);
                                    return null;
                                }
                            });
                        }
                        else { // 카카오 이외 로그인 (또는 로그아웃) 상태
                            // 로그아웃 처리 후
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            //                         -> getContext() ?
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET ?
                            startActivity(intent);
                        }
                        return null;
                    }
                });
            }
        });
        return rootView;
    }

}

