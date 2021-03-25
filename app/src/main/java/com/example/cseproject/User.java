package com.example.cseproject;

public class User {
    public String name,email,phone,type;

    public User(){}

    public User(String name, String email, String phone,String type) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.type=type;
    }

    public User(String name,String email) {
        this.name=name;
        this.email=email;
    }
}
