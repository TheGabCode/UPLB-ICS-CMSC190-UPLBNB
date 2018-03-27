package com.cmsc190.ics.uplbnb;

/**
 * Created by Dell on 15 Feb 2018.
 */

public class User {
    public String email;
    public String password;
    public String first_name;
    public String last_name;
    public String user_type;
    public String number;
    public String id;

    public User(){}

    public User(String email,String password,String first_name,String last_name,String user_type,String number,String id){
        this.email = email;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_type = user_type;
        this.number = number;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public String getFullname(){
        return first_name + " " + last_name;
    }
}
