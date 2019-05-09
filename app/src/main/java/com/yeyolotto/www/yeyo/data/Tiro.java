package com.yeyolotto.www.yeyo.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Tiro {
    private JSONObject tiroJSON;
    private int id;
    private String fecha;
    private String hora;
    private String tiro;

    public Tiro(JSONObject tiro)
    {
        tiroJSON = tiro;
        try
        {
            id = tiroJSON.getInt("id");
            fecha = tiroJSON.getString("fecha");
            hora = tiroJSON.getString("hora");
            this.tiro = tiroJSON.getString("tiro").replace("\n","");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public Tiro(int id, String fecha, String hora, String tiro){
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.tiro = tiro;
    }

    public JSONObject getTiroJSON() {
        return tiroJSON;
    }

    public int getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getTiro() {
        return tiro;
    }
}
