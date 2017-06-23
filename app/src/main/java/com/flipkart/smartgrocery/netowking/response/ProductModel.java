package com.flipkart.smartgrocery.netowking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class ProductModel {

    private String title;

    @SerializedName("images")
    private List<List<ImageModel>> imageUrl;

    private int price;

    private String fsn;

    @SerializedName("vertical")
    private String category;

    private int quantity;

    private int discount;

    @SerializedName("id_attributes")
    private List<IdAttributes> attributes;
}
