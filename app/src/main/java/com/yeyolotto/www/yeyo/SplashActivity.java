package com.yeyolotto.www.yeyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yeyolotto.www.yeyo.data.Tiro;
import com.yeyolotto.www.yeyo.data.TirosData;
import com.yeyolotto.www.yeyo.data.YeyoDbHelper;
import com.yeyolotto.www.yeyo.utilities.DataUtils;
import com.yeyolotto.www.yeyo.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    Button loginBT;
    TextView registerTV;
    ProgressBar loadingIndicator;

    /** Database helper that will provide us access to the database */
    private YeyoDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loginBT = findViewById(R.id.loginBT);
        registerTV = findViewById(R.id.registerTV);
        loadingIndicator = findViewById(R.id.loading_indicator);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        mDbHelper = new YeyoDbHelper(this);

        // Check if there is a user registered, load the user email
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_email = sharedPreferences.getString("email","");
        String user_password = sharedPreferences.getString("password","");

        if(user_email.equals("")){
            // No hay nadie registrado
            showLoginRegister();
        } else {
            // there is data from existing user: load last tiros and then go to main activity
            //makeTirosQuery(user_email, user_password);
            goToMainActivity();
        }

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

    /**
     * Ejecuta el hilo para descargar los tiros
     */
    private void makeTirosQuery(String email, String pass){
        JSONObject dataJSON = new JSONObject();
        try {
            dataJSON.put("email", email);
            dataJSON.put("password", pass);
            dataJSON.put("position", DataUtils.getTirosCount(mDbHelper.getReadableDatabase()));
        }catch (JSONException e){
            e.printStackTrace();
        }
        new TirosQueryTask().execute(dataJSON);
    }

    private void goToMainActivity(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Ejecuta el pedido de actualizar los tiros
     */
    public class TirosQueryTask extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(JSONObject... jsonObjects) {
            if(jsonObjects.length == 0) return null;
            JSONObject jsonData = jsonObjects[0];

            String username = "";
            String password = "";
            int position = 0;
            try{
                username = jsonData.getString("email");
                password = jsonData.getString("password");
                position = jsonData.getInt("position");
            }catch (JSONException e){
                e.printStackTrace();
            }

            URL tirosUrl = NetworkUtils.buildLastTirosUrl(position);
            // Para guardar la respuesta string en formato JSON
            String tirosJSONResult = null;
            try {
                tirosJSONResult = NetworkUtils.getLastTirosFromHttpUrl(tirosUrl, username, password);
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
            return tirosJSONResult;
        }

        @Override
        protected void onPostExecute(String tirosJSONResult) {

            if(tirosJSONResult != null && tirosJSONResult!=""
                    && DataUtils.InsertAllTirosDB(tirosJSONResult, mDbHelper.getWritableDatabase())){
                goToMainActivity();
            }else{
                showLoginRegister();
            }
        }
    }
}
