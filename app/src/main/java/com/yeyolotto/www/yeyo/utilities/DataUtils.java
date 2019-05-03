package com.yeyolotto.www.yeyo.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;

import com.yeyolotto.www.yeyo.data.Tiro;
import com.yeyolotto.www.yeyo.data.User;
import com.yeyolotto.www.yeyo.data.YeyoContract.TiroEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtils {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_NAME_REGEX =
            Pattern.compile("^[\\p{L} .'-]+$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    public static boolean validateName(String name) {
        Matcher matcher = VALID_NAME_REGEX .matcher(name);
        return matcher.find();
    }

    /**
     * Get the user data form SharePreferences
     */
    public static User loadUserData(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPreferences.contains("id"))
            return null;
        try {
            return new User(sharedPreferences.getInt("id", -1),
                    sharedPreferences.getString("nombre", ""),
                    sharedPreferences.getString("email", ""),
                    sharedPreferences.getString("email_usa", ""),
                    sharedPreferences.getString("password", ""));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void clearUserData(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id",-1);
        editor.putString("nombre","");
        editor.putString("email","");
        editor.putString("email_usa","");
        editor.putString("password","");
        editor.apply();
    }

    /**
     * Guarda los datos de usuario que se acaba de crear en el servidor
     * @param context
     * @param jsonData
     * @param user_id
     */
    public static void SaveUserData(Context context, JSONObject jsonData, int user_id){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putInt("id", user_id);
            editor.putString("nombre", jsonData.getString("nombre"));
            editor.putString("email", jsonData.getString("email"));
            editor.putString("email_usa", "");
            editor.putString("password", jsonData.getString("password"));
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * uarda los datos de usuario que se acaba de logear en el servidor
     * @param context
     * @param jsonData
     * @param passNoHash
     */
    public static void SaveUserData(Context context, JSONObject jsonData, String passNoHash){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putInt("id", jsonData.getInt("id"));
            editor.putString("nombre", jsonData.getString("nombre"));
            editor.putString("email", jsonData.getString("email"));
            editor.putString("email_usa", jsonData.getString("email_usa"));
            editor.putString("password", passNoHash);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Edita los datos de usuario que se acaban de cambiar en el servidor
     * @param context
     * @param jsonData
     */
    public static void EditUserData(Context context, JSONObject jsonData){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            editor.putString("email_usa", jsonData.getString("email_usa"));
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param db
     * @return cantidad de tiros en la base de datos
     */
    public static int getTirosCount(SQLiteDatabase db){
        int result = -1;
        try {
            Cursor cursor = db.query(
                    TiroEntry.TABLE_NAME,   // The table to query
                    null,           // The columns to return
                    null,           // The columns for the WHERE clause
                    null,        // The values for the WHERE clause
                    null,            // Don't group the rows
                    null,             // Don't filter by row groups
                    null);           // The sort order

            result = cursor.getCount();
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }catch (SQLiteException e) {
            // database doesn't exist yet.
            return result;
        }
        return result;
    }

    /**
     * Inserta todos los tiros en la base de datos (Se usa solo una ves para hacer una copia local)
     * @param tirosJSONString String en formato JSON con todos los tiros recuperados del web server
     * @param db
     * @return true si toda la insercion fue satisfactoria
     */
    public static boolean InsertAllTirosDB(String tirosJSONString, SQLiteDatabase db){
        try{
            JSONObject data = new JSONObject(tirosJSONString);
            JSONObject tiros = data.getJSONObject("Tiros");
            String status = tiros.getString("status");
            if(status.equals("ok")){
                JSONArray tirosList = tiros.getJSONArray("list");
                for (int i = 0; i < tirosList.length(); i++)
                {
                   if(InsertTiroDB(tirosList.getJSONObject(i), db) < 0){
                       return false;
                   }
                }
            } else return false;
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Inserta los datos de un tiro en una nueva fila en la tabla correspondiente
     * @param tiro
     * @param db
     * @return el numero de fila insertado, -1 si hubo un error
     */
    public static long InsertTiroDB(JSONObject tiro, SQLiteDatabase db){

        ContentValues values = new ContentValues();
        try {
            values.put(TiroEntry.COLUMN_FECHA, tiro.getString(TiroEntry.COLUMN_FECHA));
            values.put(TiroEntry.COLUMN_HORA, tiro.getString(TiroEntry.COLUMN_HORA));
            values.put(TiroEntry.COLUMN_TIRO, tiro.getString(TiroEntry.COLUMN_TIRO));
        }catch (JSONException e){
            e.printStackTrace();
            return -2;
        }
        return db.insert(TiroEntry.TABLE_NAME, null, values);
    }
}
