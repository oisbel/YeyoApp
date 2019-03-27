package com.yeyolotto.www.yeyo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.yeyolotto.www.yeyo.data.User;
import com.yeyolotto.www.yeyo.utilities.DataUtils;

public class UserDataActivity extends AppCompatActivity {

    TextView nameTV;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        nameTV = findViewById(R.id.nameTV);

        mUser = DataUtils.loadUserData(this);
    }
}
