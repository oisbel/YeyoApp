package com.yeyolotto.www.yeyo.utilities;

import android.net.Uri;
import android.util.Base64;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    final static String Base_URL = "http://www.yeyolotto.com"; //3.18.176.39

    final static String BASE_CREATE_USER_URL = Base_URL + "/adduser";

    final static String BASE_GET_USER_URL = Base_URL + "/getuser";

    final static String BASE_EDIT_USER_URL = Base_URL + "/edituser";

    final static String BASE_TIROS_URL = Base_URL + "/tiros"; // to get the 60 last tiros

    final static String BASE_LAST_TIROS_URL = Base_URL + "/tiros"; // to get the last count tiros : /tiros/<int:count>

    final static String BASE_ALL_TIROS_URL = Base_URL + "/tiros/all";

    private static String buildBasicAuthorizationString(String username, String password) {

        String credentials = username + ":" + password;
        return "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));
    }

    /**
     * Builds the URL used to query yeyolotto.
     * @return The URL to use to query the yeyolotto create user.
     */
    public static URL buildCreateUserUrl() {
        Uri builtUri = Uri.parse(BASE_CREATE_USER_URL).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**
     * Returns the entire result from the HTTP response after create user.
     *
     * @param url The URL to fetch the HTTP response from.
     * @param jsonParam The json object to sent it with the request.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String geCreateUserFromHttpUrl(URL url, JSONObject jsonParam) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setRequestProperty("Accept","application/json");
        urlConnection.setRequestProperty("accept-charset", "UTF-8");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
        out.write(jsonParam.toString().getBytes("UTF-8"));
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in, "UTF-8");
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Builds the URL used to query yeyolotto.
     * @return The URL to use to query the yeyolotto create user.
     */
    public static URL buildGetUserUrl() {
        Uri builtUri = Uri.parse(BASE_GET_USER_URL).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**
     * Returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getUserFromHttpUrl(URL url, String username, String password) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        String basicAuth = buildBasicAuthorizationString(username, password);
        urlConnection.setRequestProperty("Authorization", basicAuth);
        urlConnection.setRequestProperty("Accept","application/json");

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Builds the URL used to query yeyolotto.
     * @return The URL to use to query the yeyolotto edit user.
     */
    public static URL buildEditUserUrl(int user_id) {
        Uri builtUri = Uri.parse(BASE_EDIT_USER_URL + "/" +user_id).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**
     * Returns the entire result from the HTTP response after edit user.
     *
     * @param url The URL to fetch the HTTP response from.
     * @param jsonParam The json object to sent it with the request.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getEditUserFromHttpUrl(URL url, JSONObject jsonParam,
                                                String username, String password) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        String basicAuth = buildBasicAuthorizationString(username, password);
        urlConnection.setRequestProperty("Authorization", basicAuth);
        urlConnection.setRequestProperty("Accept","application/json");
        urlConnection.setRequestProperty("accept-charset", "UTF-8");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
        out.write(jsonParam.toString().getBytes("UTF-8"));
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in,"UTF_8");
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Builds the URL used to query yeyolotto.
     *
     * @return The URL to use to query the yeyolotto get tiros.
     */
    public static URL buildTirosUrl() {
        Uri builtUri = Uri.parse(BASE_TIROS_URL).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**
     * Returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getTirosFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept","application/json");

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Builds the URL used to query yeyolotto.
     *
     * @return The URL to use to query the yeyolotto get reports.
     */
    public static URL buildLastTirosUrl(int count) {
        Uri builtUri = Uri.parse(BASE_LAST_TIROS_URL + "/" + count).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**
     * Returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getLastTirosFromHttpUrl(URL url, String username, String password) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        String basicAuth = buildBasicAuthorizationString(username, password);
        urlConnection.setRequestProperty("Authorization", basicAuth);
        urlConnection.setRequestProperty("Accept","application/json");

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Builds the URL used to query yeyolotto.
     *
     * @return The URL to use to query the yeyolotto get reports.
     */
    public static URL buildAllTirosUrl() {
        Uri builtUri = Uri.parse(BASE_ALL_TIROS_URL).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**
     * Returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getAllTirosFromHttpUrl(URL url, String username, String password) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        String basicAuth = buildBasicAuthorizationString(username, password);
        urlConnection.setRequestProperty("Authorization", basicAuth);
        urlConnection.setRequestProperty("Accept","application/json");

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
