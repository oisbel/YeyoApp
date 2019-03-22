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

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    /**
     * Get the user data form SharePreferences
     */
    public static User loadUserData(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new User(sharedPreferences.getInt("id",-1),
                sharedPreferences.getString("nombre",""),
                sharedPreferences.getString("email",""),
                sharedPreferences.getString("email_usa",""),
                sharedPreferences.getString("password",""));
    }

    public static void clearUserData(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id","-1");
        editor.putString("nombre","");
        editor.putString("email","");
        editor.putString("email_usa","");
        editor.putString("password","");
        editor.apply();
    }

    /**
     * Guarda los datos de usuarios que se acaban de cambiar en el servidor
     * @param context
     * @param jsonData
     */
    public static void SaveUserData(Context context, JSONObject jsonData){
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