package com.yeyolotto.www.yeyo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yeyolotto.www.yeyo.data.User;
import com.yeyolotto.www.yeyo.databinding.ActivityUserDataBinding;
import com.yeyolotto.www.yeyo.utilities.DataUtils;

public class UserDataActivity extends AppCompatActivity {

    ImageButton closeBT;
    TextView nameTV;
    User mUser;
    Button logoutBT;
    ActivityUserDataBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        // Set the content view to the layout for data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_data);

        closeBT = findViewById(R.id.closeBT);
        nameTV = findViewById(R.id.nameTV);
        logoutBT = findViewById(R.id.logoutBT);

        mUser = DataUtils.loadUserData(this);
        if(mUser == null || mUser.getId() == -1) {
            finish();
            return;
        }

        displayUserData();

        closeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),logoutActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Bind each attribute in the views to the corresponding data
     */
    private void displayUserData(){
        mBinding.emailUsaTV.setText(mUser.getEmail_usa());
        mBinding.nameTV.setText(mUser.getNombre());
        mBinding.emailTV.setText(mUser.getEmail());
    }
}
