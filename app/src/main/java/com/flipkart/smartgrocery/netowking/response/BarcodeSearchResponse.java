package com.flipkart.smartgrocery.netowking.response;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BarcodeSearchResponse {

    List<ProductModel> products;
}
