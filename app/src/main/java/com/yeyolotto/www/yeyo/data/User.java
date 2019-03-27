package com.yeyolotto.www.yeyo.data;

public class User {
    private int id;
    private String nombre;
    private String email;
    private String email_usa;
    private String password;

    public User(int id, String nombre, String email, String email_usa, String password)
    {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.email_usa = email_usa;
        this.password = password;
    }

    public int getId()
    {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getEmail_usa() {
        if (email_usa.equals(""))
                return "None";
        return email_usa;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail_usa(String email_usa) {
        this.email_usa = email_usa;
    }
}
