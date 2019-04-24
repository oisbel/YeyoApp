package com.yeyolotto.www.yeyo.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un grupo de tiros recuperados del web server
 */
public class TirosData {
    private String status;
    private List<Tiro> Tiros;

    public TirosData(String tirosJSONString) {
        Tiros = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(tirosJSONString);
            JSONObject tiros = data.getJSONObject("Tiros");
            status = tiros.getString("status");
            if(status.equals("ok")){
                JSONArray tirosList = tiros.getJSONArray("list");
                for (int i = 0; i < tirosList.length(); i++)
                {
                    Tiros.add(new Tiro(tirosList.getJSONObject(i)));
                }
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public List<Tiro> getTiros() {
        return Tiros;
    }
}
