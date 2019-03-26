package com.yeyolotto.www.yeyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    Button loginBT;
    TextView registerTV;
    ProgressBar loadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loginBT = findViewById(R.id.loginBT);
        registerTV = findViewById(R.id.registerTV);
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Check if there is a user registered, load the user email
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String user_email = sharedPreferences.getString("email","");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user_email.equals("")){
                    // No hay nadie registrado
                    showLoginRegister();
                } else {
                    // if there is data from existing user: log in
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Muestra los links para entrar y registrase(estan invisibles)
     * y hide el progress loading bar
     */
    private void showLoginRegister(){
        loginBT.setVisibility(View.VISIBLE);
        registerTV.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
    }
}
