package com.yeyolotto.www.yeyo.utilities;

import android.os.AsyncTask;

import com.yeyolotto.www.yeyo.data.YeyoDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Ejecuta el pedido de actualizar la base de datos local con los últimos tiros
 */
public class UpdateTirosQueryTask {

    YeyoDbHelper mDbHelper;
    JSONObject parametersJSON;

    public UpdateTirosQueryTask(YeyoDbHelper mDbHelper, JSONObject parametersJSON){
        this.mDbHelper = mDbHelper;
        this.parametersJSON = parametersJSON;
    }

    public void Update(){
        new TirosQueryTask().execute(parametersJSON);
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
