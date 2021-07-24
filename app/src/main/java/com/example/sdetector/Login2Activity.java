package com.example.sdetector;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class Login2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        getSupportActionBar().setTitle("회원가입");

        CheckBox checkBox = findViewById(R.id.checkBox); // 전체 동의
        CheckBox checkBox1 = findViewById(R.id.checkBox1); // 이용 약관 동의
        CheckBox checkBox2 = findViewById(R.id.checkBox2); // 개인정보 수집 동의
        CheckBox checkBox3 = findViewById(R.id.checkBox3); // 사용자 접근 동의
        CheckBox checkBox4 = findViewById(R.id.checkBox4); // 알림 수신 동의(선택)

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox4.setChecked(true);
                } else {
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                }
            }
        });

        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()
                        && checkBox4.isChecked()) {
                    checkBox.setChecked(true);
                }
            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()
                        && checkBox4.isChecked()) {
                    checkBox.setChecked(true);
                }
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()
                        && checkBox4.isChecked()) {
                    checkBox.setChecked(true);
                }
            }
        });

        checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()
                        && checkBox4.isChecked()) {
                    checkBox.setChecked(true);
                }
            }
        });

        TextView textView1 = findViewById(R.id.textView1);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login2Activity.this);
                builder.setTitle("서비스 이용 약관");
                builder.setMessage(R.string.arg_textView1);
                builder.setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 처리
                            }
                        });
                builder.show();

            }
        });

        TextView textView2 = findViewById(R.id.textView2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login2Activity.this);
                builder.setTitle("개인정보 수집 및 이용");
                builder.setMessage(R.string.arg_textView2);
                builder.setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 처리

                            }
                        });
                builder.show();

            }
        });

        /*TextView textView3 = findViewById(R.id.textView3);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login2Activity.this);
                builder.setTitle("사용자 접근권한");
                builder.setMessage(R.string.arg_textView3);
                builder.setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 처리

                            }
                        });
                builder.show();

            }
        });*/

        TextView textView4 = findViewById(R.id.textView4);
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login2Activity.this);
                builder.setTitle("알림 수신");
                builder.setMessage(R.string.arg_textView4);
                builder.setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 처리

                            }
                        });
                builder.show();

            }
        });

        Button backBtn = (Button) findViewById(R.id.buttonBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent);
            }
        });

        Button nextBtn = (Button) findViewById(R.id.buttonNext);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        Login3Activity.class);
                startActivity(intent);
            }
        });

    }
}