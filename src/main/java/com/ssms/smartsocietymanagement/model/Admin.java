package com.ssms.smartsocietymanagement.model;

public class Admin {
    private String Ad_id;
    private String name;
    private String Ad_username;
    private String Ad_password;
    private String Ad_email;

    public Admin(String ad_id, String name, String ad_username, String ad_password, String ad_email) {
        Ad_id = ad_id;
        this.name = name;
        Ad_username = ad_username;
        Ad_password = ad_password;
        Ad_email = ad_email;
    }

    public void setId(String ad_id) {
        Ad_id = ad_id;
    }
    public String getId() {  return Ad_id; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAd_username() {
        return Ad_username;
    }
    public void setAd_username(String ad_username) {
        Ad_username = ad_username;
    }

    public String getAd_password() {
        return Ad_password;
    }
    public void setAd_password(String ad_password) {
        Ad_password = ad_password;
    }

    public String getAd_email() {return Ad_email;}
    public void setAd_email(String ad_email) {Ad_email = ad_email;}


}