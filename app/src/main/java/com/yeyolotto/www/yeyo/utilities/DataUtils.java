package com.yeyolotto.www.yeyo.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yeyolotto.www.yeyo.data.User;

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
     * Guarda los datos de usuario que se acaba de logear en el servidor
     * @param context
     * @param jsonData
     */
    public static void SaveUserData(Context context, JSONObject jsonData){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putInt("id", jsonData.getInt("id"));
            editor.putString("nombre", jsonData.getString("nombre"));
            editor.putString("email", jsonData.getString("email"));
            editor.putString("email_usa", jsonData.getString("email_usa"));
            editor.putString("password", jsonData.getString("password"));
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
}
