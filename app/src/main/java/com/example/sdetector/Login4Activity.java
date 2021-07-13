package com.example.sdetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class Login4Activity extends AppCompatActivity {

    private final int idFragment = 1;
    private final int pwFragmnet = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login4);

        findViewById(R.id.idF).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                FragmentView(idFragment);
            }
        });

        findViewById(R.id.pwF).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                FragmentView(pwFragmnet);
            }
        });
        FragmentView(idFragment);
    }
    private void FragmentView(int fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
            idFragment idFragment = new idFragment();
            transaction.replace(R.id.fBox, idFragment);
            transaction.commit();
            break;

            case 2:
            pwFragment pwFragment = new pwFragment();
            transaction.replace(R.id.fBox, pwFragment);
            transaction.commit();
            break;
        }
    }
}