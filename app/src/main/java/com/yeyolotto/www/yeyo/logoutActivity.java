package com.yeyolotto.www.yeyo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yeyolotto.www.yeyo.utilities.DataUtils;

public class logoutActivity extends AppCompatActivity {

    Button discardLogoutBT;
    Button confirmLogoutBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        discardLogoutBT = findViewById(R.id.discardLogoutBT);
        confirmLogoutBT = findViewById(R.id.confirmLogoutBT);

        discardLogoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmLogoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear user data from sharepreferences
                Context context = getApplicationContext();
                DataUtils.clearUserData(context);

                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
            }
        });
    }
}
