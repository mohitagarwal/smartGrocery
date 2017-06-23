package com.flipkart.smartgrocery.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.flipkart.smartgrocery.netowking.response.ProductModel;

import java.util.List;

/**
 * Created by satyanarayana.p on 23/06/17.
 */

@Table(name = "ShoppingCart", id = "id")
public class ShoppingCart extends Model {

    @Column(name = "title")
    public String title;

    @Column(name = "imageUrl")
    public String imageUrl;

    @Column(name = "price")
    public Integer price;

    @Column(name = "vertical")
    public String vertical;

    @Column(name = "quantity")
    public Integer quantity;

    @Column(name = "fsn")
    public String fsn;

    @Column(name = "discount_price")
    public String discountPrice;

    @Column(name = "attributes")
    public String attributes;

    public ShoppingCart() {
        super();
    }

    public ShoppingCart(ProductModel productModel) {
        super();
        this.title = productModel.getTitle();
        if (productModel.getImageUrl() != null
                && !productModel.getImageUrl().isEmpty()
                && productModel.getImageUrl().get(0) != null
                && !productModel.getImageUrl().get(0).isEmpty()) {
            this.imageUrl = productModel.getImageUrl().get(0).get(productModel.getImageUrl().get(0).size() - 1).getUrl();
        }
        this.price = productModel.getPrice();
        this.vertical = productModel.getCategory();
        this.quantity = 1;
        this.discountPrice = productModel.getDiscountedPrice();
        this.fsn = productModel.getFsn();
        StringBuilder sb = new StringBuilder();
        if (productModel.getAttributes() != null && productModel.getAttributes().size() != 0) {
            for (int i = 0; i < Math.min(productModel.getAttributes().size(), 2); i++) {
                sb.append(productModel.getAttributes().get(i).buildJSON());
            }
            this.attributes = sb.toString();
        }
    }

    public static List<ShoppingCart> getAll() {
        return new Select()
                .from(ShoppingCart.class)
                .execute();
    }

    public static void deleteAll() {
        new Delete().from(ShoppingCart.class).execute();
    }


}

