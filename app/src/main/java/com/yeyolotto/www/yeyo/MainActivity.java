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
import com.yeyolotto.www.yeyo.utilities.UpdateTirosQueryTask;

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

        // see if was the parent is the splash activity, so I dont have to update the tiros
        if(!("splash".equals(parentActivity)) && !user_email.isEmpty())
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
        JSONObject parametersJSON = new JSONObject();
        int position = DataUtils.getTirosCount(mDbHelper.getReadableDatabase());
        if(position<0){ // algo salio mal con sql
            return;
        }
        try {
            parametersJSON.put("email", email);
            parametersJSON.put("password", pass);
            parametersJSON.put("position", position);
        }catch (JSONException e){
            e.printStackTrace();
        }
        new UpdateTirosQueryTask(mDbHelper,parametersJSON).Update();
    }
}
