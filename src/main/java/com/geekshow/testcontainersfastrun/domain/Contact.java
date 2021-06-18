package com.geekshow.testcontainersfastrun.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Contact {

    @Id
    private String id;
    private String name;
    private String email;
    private String address;
    private String phone;

    public String getId() {
        return id;
    }

    public Contact id(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Contact name(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Contact email(String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Contact address(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Contact phone(String phone) {
        this.phone = phone;
        return this;
    }
}
