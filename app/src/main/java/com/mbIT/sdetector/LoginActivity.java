package com.mbIT.sdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = (Button) findViewById(R.id.buttonLogin);
        loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);
            }
        });

        Button findBtn = (Button) findViewById(R.id.buttonFind);
        findBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),
                        Login4Activity.class);
                startActivity(intent);
            }
        });

        Button signupBtn = (Button) findViewById(R.id.buttonSignup);
        signupBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),
                        Login2Activity.class);
                startActivity(intent);
            }
        });

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {
                    //
                    System.out.println("oAuthToken is " + oAuthToken);
                }
                if (throwable != null) {
                    //
                    System.out.println("throwable is not null");
                }
                checkKakaoLogin();
                return null;
            }
        };

        ImageButton kakaoBtn = (ImageButton) findViewById(R.id.buttonLoginKakao);
        kakaoBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                }
                else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
            }
        });
    }

    private void checkKakaoLogin() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    System.out.println("로그인 상태");
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    LoginActivity.this.finish();
                    startActivity(intent);
                }
                else {
                    // 로그아웃 상태.
                    System.out.println("로그아웃 상태");
                }
                return null;
            }
        });
    }
}