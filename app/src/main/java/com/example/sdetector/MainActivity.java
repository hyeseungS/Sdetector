package com.example.sdetector;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.sdetector.ui.settings.SettingsFragment;
import com.example.sdetector.ui.diary.DiaryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private NavController navController;

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

        BottomNavigationView bottom_nav_view = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottom_nav_view, navController);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void moveToDetail(){
        navController.navigate(R.id.action_navigation_home_to_more_graph);
    }
    
    public void moveToMEMSettings(){
        navController.navigate(R.id.action_navigation_settings_to_mem_settings);
    }

    public void moveToTACSettings(){
        navController.navigate(R.id.action_navigation_settings_to_tnc_settings);
    }

    public void showDatePicker(View view) {

        mDateSetListener = (datePicker, yy, mm, dd) -> {
            TextView tv = findViewById(R.id.DatetextView);
            tv.setText(String.format("%d-%d-%d", yy, mm + 1, dd));
        };

            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, mDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
        };

    public void backLogin(View v) {
        Button logoutbtn = findViewById(R.id.logoutBtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent);
            }

        });

    }
}


