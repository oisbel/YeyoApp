package com.yeyolotto.www.yeyo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yeyolotto.www.yeyo.utilities.DataUtils;
import com.yeyolotto.www.yeyo.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    ImageButton backIB;
    ProgressBar loadingPB;
    EditText emailET;
    EditText passwordET;
    Button loginBT;
    Boolean loginStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backIB = findViewById(R.id.backIB);
        loadingPB = findViewById(R.id.loadingProgressBar);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBT = findViewById(R.id.loginButton);

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeGetUserQuery();
            }
        });


        backIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Crea el objeto JSON con email y pass del
     * usuario que se intenta logear, y ejecuta el asynctask
     */
    private void makeGetUserQuery(){
        if(!checkEntries()) return;
        JSONObject userDataJSON = new JSONObject();
        try{
            userDataJSON.put("email", emailET.getText());
            userDataJSON.put("password", passwordET.getText());
            new GetUserQueryTask().execute(userDataJSON);
        }catch (JSONException e){
            e.printStackTrace();
        }

        if(loginStatus) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Verifica que se hayan entrado los campos correctamente
     * @return
     */
    private boolean checkEntries(){
        if(!DataUtils.validateEmail(emailET.getText().toString())){
            emailET.setError(getResources().getString(R.string.invalid_email));
            return false;
        }
        if(passwordET.getText().toString().equalsIgnoreCase("")){
            passwordET.setError(getResources().getString(R.string.mandatory_error));
            return false;
        }
        return true;
    }

    /**
     * Guarda el usuario logeado usando SharePreferences
     * @param userDataJSON
     */
    private void SaveUser(JSONObject userDataJSON){
        if(userDataJSON != null)
            DataUtils.SaveUserData(this, userDataJSON);

    }

    private void showLoading(){
        loadingPB.setVisibility(View.VISIBLE);
        loginBT.setText("");
    }

    private void showLogin(){
        loadingPB.setVisibility(View.INVISIBLE);
        loginBT.setText(getString(R.string.loginLabel));
    }

    private void ShowErrorMessage(){
        Toast toast = Toast.makeText(this, R.string.login_user_error_message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void ShowInvalidCredentialsMessage(){
        Toast toast = Toast.makeText(this, R.string.invalid_user_credential, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Ejecuta el pedido de logear un usuario pasandole el header authorization
     * que seria el objeto json con email y password
     */
    public class GetUserQueryTask extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            if(jsonObjects.length == 0) return null;
            JSONObject result = null;

            JSONObject jsonData = jsonObjects[0];
            URL getUserUrl = NetworkUtils.buildGetUserUrl();
            String getUserJSONResult = null;
            String user_name = "";
            String pass = "";

            try{
                user_name = jsonData.getString("email");
                pass = jsonData.getString("password");
            }catch (JSONException e){
                e.printStackTrace();
            }

            try {
                getUserJSONResult = NetworkUtils.getUserFromHttpUrl(getUserUrl, user_name, pass);
            }catch (IOException e){
                e.printStackTrace();
                return result;
            }

            try {
                result = new JSONObject(getUserJSONResult);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            showLogin();
            if(jsonObject != null){
                String status = "fail";
                try{
                    status = jsonObject.getString("status");
                    if(status == "fail"){
                        ShowInvalidCredentialsMessage();
                    }else{
                        // sucess
                        SaveUser(jsonObject);
                        loginStatus = true;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    ShowErrorMessage();
                }
            } else {
                ShowErrorMessage();
            }
        }
    }

}
