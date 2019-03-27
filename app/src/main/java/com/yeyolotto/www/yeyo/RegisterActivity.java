package com.yeyolotto.www.yeyo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yeyolotto.www.yeyo.utilities.DataUtils;
import com.yeyolotto.www.yeyo.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    EditText nombreET;
    EditText emailET;
    EditText passwordET;
    ProgressBar loadingProgressBar;
    Button registerButton;

    // Objeto JSON con los datos del nuevo usuario a crear
    JSONObject userDataJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nombreET = findViewById(R.id.nombreET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        registerButton = findViewById(R.id.registerButton);

        userDataJSON = new JSONObject();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCreateUserQuery();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void ShowUserExistMessage(){
        Toast toast = Toast.makeText(this, R.string.user_exist_message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void ShowCreatedUserMessage(){
        Toast toast = Toast.makeText(this, R.string.user_created, Toast.LENGTH_LONG);
        toast.show();
    }

    private void ShowErrorMessage(){
        Toast toast = Toast.makeText(this, R.string.create_user_error_message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void showLoading(){
        loadingProgressBar.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.INVISIBLE);
    }

    private void showLogin(){
        loadingProgressBar.setVisibility(View.INVISIBLE);
        registerButton.setVisibility(View.VISIBLE);
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
        if(!DataUtils.validateName(nombreET.getText().toString())){
            nombreET.setError(getResources().getString(R.string.invalid_name));
            return false;
        }
        if(passwordET.getText().toString().equalsIgnoreCase("")){
            passwordET.setError(getResources().getString(R.string.mandatory_error));
            return false;
        }
        return true;
    }

    /**
     * Guarda el usuario creado usando SharePreferences
     * @param user_id
     */
    private void SaveUser(int user_id){
        if(userDataJSON != null)
            DataUtils.SaveUserData(this, userDataJSON,user_id);

    }

    private void OpenMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Crea el objeto JSON que se va a mandar con el request de crear
     * el usuario y ejecuta el asynctask
     */
    private void makeCreateUserQuery(){
        if(!checkEntries()) return;
        try {
            userDataJSON.put("nombre", nombreET.getText());
            userDataJSON.put("email", emailET.getText());
            userDataJSON.put("password", passwordET.getText());
        }catch (JSONException e){
            e.printStackTrace();
        }

        new CreateUserQueryTask().execute(userDataJSON);
    }

    /**
     * Ejecuta el pedido de crear un usuario nuevo pasandole el objeto json
     * con toda la informacion del usuario
     */
    public class CreateUserQueryTask extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            if(jsonObjects == null || jsonObjects.length == 0) return null;
            JSONObject jsonData = jsonObjects[0];
            URL createUserUrl = NetworkUtils.buildCreateUserUrl();
            String createUserJSONResult = null;
            JSONObject result = null;
            try {
                createUserJSONResult = NetworkUtils.geCreateUserFromHttpUrl(createUserUrl,jsonData);
            }catch (IOException e){
                e.printStackTrace();
                return result;
            }
            try {
                result = new JSONObject(createUserJSONResult);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            showLogin();
            if(jsonObject != null){
                if(jsonObject.has("message")){
                    ShowUserExistMessage();
                }
                if(jsonObject.has("email")){
                    // sucess
                    ShowCreatedUserMessage();
                    int user_id = -1;
                    try{
                        user_id = jsonObject.getInt("id");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    if(user_id != -1){
                        SaveUser(user_id);
                        OpenMainActivity();
                    }
                }
            } else {
                ShowErrorMessage();
            }
        }
    }

}
