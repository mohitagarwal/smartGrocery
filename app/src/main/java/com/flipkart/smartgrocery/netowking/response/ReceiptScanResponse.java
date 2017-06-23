package com.flipkart.smartgrocery.netowking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReceiptScanResponse {

    @SerializedName("your_cost")
    private String yourCost;

    @SerializedName("fk_cost")
    private String fkCost;

    private String saving;

    @SerializedName("saving_percent")
    private String savingPercent;

    private List<ProductModel> products;
}
