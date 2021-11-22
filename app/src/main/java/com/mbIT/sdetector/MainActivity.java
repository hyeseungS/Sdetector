package com.mbIT.sdetector;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //인터넷 서버 통신 코드
    private static String ID_ADDRESS = "52.78.165.117";   //매번 ip주소 바꿔줄 것
    private static String TAG = "phptest";

    private AppBarConfiguration mAppBarConfiguration;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private NavController navController;
    @Override

    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            View v = getCurrentFocus();

            if (v instanceof EditText) {

                Rect outRect = new Rect();

                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {

                    v.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }

            }

        }

        return super.dispatchTouchEvent( event );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_report, R.id.navigation_diary,
                R.id.navigation_settings).setDrawerLayout(drawer).build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        BottomNavigationView bottom_nav_view = findViewById(R.id.bottom_nav_view); // 아래 Navigation
        NavigationUI.setupWithNavController(bottom_nav_view, navController);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu); // 상단바의 알림 버튼 메뉴 추가
        return true;
    }

    // 햄버거 버튼
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // HomeFragment(홈)에서 Graph3Fragment(일간, 주간, 월간 앱 사용 시간)로 이동
    public void moveToDetail(){
        navController.navigate(R.id.action_navigation_home_to_more_graph);
    }

    // DiaryFragment(감정 일기) --> DiaryListFragment(일기 목록)
    public void moveToDiaryList(){
        navController.navigate(R.id.action_navigation_diary_to_diary_list);
    }

    // SettingsFragment(환경 설정) --> Settings2Fragment(회원 정보)
    public void moveToMEMSettings(){
        navController.navigate(R.id.action_navigation_settings_to_mem_settings);
    }
    // SettingsFragment(환경 설정) --> Settings3Fragment(이용 약관 확인)
    public void moveToTACSettings(){
        navController.navigate(R.id.action_navigation_settings_to_tnc_settings);
    }
    // Settings2Fragment(회원 정보) --> Settings2_1Fragment(ID 변경)
    public void moveToIDSettings(){
        navController.navigate(R.id.action_mem_settings_to_mem_settingsID);
    }
    // Settings2Fragment(회원 정보) --> Settings2_2Fragment(PW 변경)
    public void moveToPWSettings(){
        navController.navigate(R.id.action_mem_settings_to_mem_settingsPW);
    }
    // Settings2Fragment(회원 정보) --> Settings2_3Fragment(개인 정보 변경)
    public void moveToINFOSettings(){
        navController.navigate(R.id.action_mem_settings_to_mem_settingsINFO);
    }

    public void showDatePicker(View view) {

        mDateSetListener = (datePicker, yy, mm, dd) -> {
            TextView tv = findViewById(R.id.DatetextView);
            tv.setText(String.format("%d년 %d월 %d일", yy, mm + 1, dd));
        };

            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, mDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
        };

    public void backLogin(View v) {
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent);
            }

        });

    }

}


