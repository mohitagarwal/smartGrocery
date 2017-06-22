package com.flipkart.smartgrocery.netowking.response;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductModel {

    private String title;

    @SerializedName("image_url")
    private String imageUrl;

    private int price;

    private String fsn;

    private String category;

    private int quantity;

    private String unit;

    private int discount;
}
