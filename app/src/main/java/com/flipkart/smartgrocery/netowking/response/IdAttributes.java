package com.flipkart.smartgrocery.netowking.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IdAttributes {

    private String name;
    private String value;

    public String buildJSON(){
        return new Gson().toJson(this);
    }

}
