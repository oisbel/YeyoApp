package com.yeyolotto.www.yeyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.yeyolotto.www.yeyo.data.YeyoDbHelper;
import com.yeyolotto.www.yeyo.utilities.DataUtils;
import com.yeyolotto.www.yeyo.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    // Para guardar todos los fragment en memoria
    final Fragment homeFragment = new HomeFragment();
    final Fragment toolsFragment = new ToolsFragment();
    final Fragment tirosFragment = new TirosFragment();
    final Fragment playsFragment = new PlaysFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activeFragment = homeFragment;

    /** Database helper that will provide us access to the database */
    private YeyoDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNav = findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        mDbHelper = new YeyoDbHelper(this);

        // Obtener el string pasado de la activity anterior para saber quien me llamó
        String parentActivity = "login";
        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            parentActivity = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }
        // Load user, email
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_email = sharedPreferences.getString("email","");
        String user_password = sharedPreferences.getString("password","");

        if(parentActivity != "splash" && user_email!="")
            makeTirosQuery(user_email, user_password);

        fragmentManager.beginTransaction().add(R.id.container, playsFragment,"4")
                .hide(playsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, tirosFragment,"3")
                .hide(tirosFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, toolsFragment,"2")
                .hide(toolsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, homeFragment,"1")
                .commit();
    }

    private void selectFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                break;
            case R.id.navigation_tools:
                fragmentManager.beginTransaction().hide(activeFragment).show(toolsFragment).commit();
                activeFragment = toolsFragment;
                break;
            case R.id.navigation_tiros:
                fragmentManager.beginTransaction().hide(activeFragment).show(tirosFragment).commit();
                activeFragment = tirosFragment;
                break;
            case R.id.navigation_plays:
                fragmentManager.beginTransaction().hide(activeFragment).show(playsFragment).commit();
                activeFragment = playsFragment;
                break;
        }
    }

    @Override
    public void onBackPressed() {}

    /**
     * Ejecuta el hilo para descargar los tiros
     */
    private void makeTirosQuery(String email, String pass){
        JSONObject dataJSON = new JSONObject();
        int position = DataUtils.getTirosCount(mDbHelper.getReadableDatabase());
        if(position<0){ // algo salio mal con sql
            return;
        }
        try {
            dataJSON.put("email", email);
            dataJSON.put("password", pass);
            dataJSON.put("position", position);
        }catch (JSONException e){
            e.printStackTrace();
        }
        new TirosQueryTask().execute(dataJSON);
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

            if(tirosJSONResult != null && tirosJSONResult!=""){
                DataUtils.InsertAllTirosDB(tirosJSONResult, mDbHelper.getWritableDatabase());
            }
        }
    }
}
