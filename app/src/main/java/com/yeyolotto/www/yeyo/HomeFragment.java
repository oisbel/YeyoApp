package com.yeyolotto.www.yeyo;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeyolotto.www.yeyo.data.Tiro;
import com.yeyolotto.www.yeyo.data.YeyoDbHelper;
import com.yeyolotto.www.yeyo.utilities.DataUtils;
import com.yeyolotto.www.yeyo.utilities.NetworkUtils;
import com.yeyolotto.www.yeyo.utilities.UpdateTirosQueryTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ImageButton userBT; // boton que muestra los datos de usuario
    ImageButton updateBT; // boton para actualizar los tiros
    TextView lastDateUpdatedTV;
    TextView loadingTV; // para mostrar errores o actualizsacion satisfactoria
    ImageView occasionIV; // dia o noche del ultimo tiro
    TextView numberTV; // ultimo tiro
    TextView dateTV; // fecha del ultimo tiro

    Tiro lastTiro;

    /** Database helper that will provide us access to the database */
    private YeyoDbHelper mDbHelper;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userBT = view.findViewById(R.id.userBT);
        updateBT = view.findViewById(R.id.updateBT);
        lastDateUpdatedTV = view.findViewById(R.id.lastDateUpdatedTV);
        loadingTV = view.findViewById(R.id.loadingTV);
        occasionIV = view.findViewById(R.id.occasionIV);
        numberTV = view.findViewById(R.id.numberTV);
        dateTV = view.findViewById(R.id.dateTV);

        userBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserDataActivity.class);
                startActivity(intent);
            }
        });

        updateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Update();
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        mDbHelper = new YeyoDbHelper(getContext());

        setLastTiro();

        return view;
    }

    /**
     * Recupera el ultimo tiro de la base de datos y lo muestra
     */
    private void setLastTiro(){

        List<Tiro> list = DataUtils.GetLastTiros(1,mDbHelper.getReadableDatabase());
        if(list != null && list.size()>0) {
            lastTiro = list.get(0);
            if(lastTiro.getHora().equals("N"))
                occasionIV.setImageResource(R.drawable.ic_action_night_today);
            else
                occasionIV.setImageResource(R.drawable.ic_action_day_today);
            lastDateUpdatedTV.setText(lastTiro.getFecha());
            dateTV.setText(lastTiro.getFecha());
            numberTV.setText(lastTiro.getTiro());
        }else {
            showErrorMessage();
        }
    }

    // Actualiza los tiros en la base de datos
    private void Update(){
        // Load user, email
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String user_email = sharedPreferences.getString("email","");
        String user_password = sharedPreferences.getString("password","");

        if(!user_email.isEmpty()){
            makeTirosQuery(user_email, user_password);
        }
    }
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
        showLoadingMessage();
        // new UpdateTirosQueryTask(mDbHelper,parametersJSON).Update();
        new TirosQueryTask().execute(parametersJSON); // no pudo ser updateTiroQueryTask porque necesito onPostExecute
    }

    private void showErrorMessage(){
        loadingTV.setText(getString(R.string.loadingError));
        loadingTV.setBackgroundResource(R.color.loadingError);
        loadingTV.setVisibility(View.VISIBLE);
    }

    private void showLoadingMessage(){
        loadingTV.setVisibility(View.VISIBLE);
        loadingTV.setText(getString(R.string.loading));
        loadingTV.setBackgroundResource(R.color.loading);
    }

    private void hideLoadingMessage(){
        loadingTV.setVisibility(View.INVISIBLE);
    }

    private void UpdateTirosFragment(){


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

            if(tirosJSONResult != null && !tirosJSONResult.isEmpty()
                    && DataUtils.InsertAllTirosDB(tirosJSONResult, mDbHelper.getWritableDatabase())){
                setLastTiro();
            }
            hideLoadingMessage();
            UpdateTirosFragment();
        }
    }

}
