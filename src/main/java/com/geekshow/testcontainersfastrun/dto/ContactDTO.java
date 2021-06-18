package com.geekshow.testcontainersfastrun.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactDTO {

  @JsonProperty("id")
  public final String id;

  @JsonProperty("name")
  public final  String name;

  @JsonProperty("email")
  public final String email;

  @JsonProperty("address")
  public final String address;

  @JsonProperty("phone")
  public final String phone;

  @JsonCreator
  public ContactDTO(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("email") String email,
      @JsonProperty("address") String address,
      @JsonProperty("phone") String phone) {

    this.id = id;
    this.name = name;
    this.email = email;
    this.address = address;
    this.phone = phone;
  }
}
