package com.flipkart.smartgrocery.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.adapters.ProductListAdapter;
import com.flipkart.smartgrocery.models.ShoppingCart;
import com.flipkart.smartgrocery.netowking.response.IdAttributes;
import com.flipkart.smartgrocery.netowking.response.ImageModel;
import com.flipkart.smartgrocery.netowking.response.ProductModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by satyanarayana.p on 23/06/17.
 */

public class ShoppingCartActivity extends AppCompatActivity {

    private ListView productsListView;
    private TextView totalAmountView;
    private TextView totalDiscountView;
    private TextView totalPayableView;
    private Button checkoutButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        productsListView = (ListView) findViewById(R.id.cart_list);
        totalAmountView = (TextView) findViewById(R.id.totalAmount);
        totalDiscountView = (TextView) findViewById(R.id.discountAmount);
        totalPayableView = (TextView) findViewById(R.id.payableAmount);
        checkoutButton = (Button) findViewById(R.id.pay);
        displayList();
    }

    private void displayList() {
        int totalAmount = 0;
        int totalDiscount = 0;
        List<ShoppingCart> cartList = ShoppingCart.getAll();
        List<ProductModel> productModels = new ArrayList<>();
        for (int i = 0; i < cartList.size(); i++) {
            ShoppingCart cart = cartList.get(i);
            ProductModel model = new ProductModel();
            model.setTitle(cart.title);
            ImageModel imageModel = new ImageModel();
            imageModel.setUrl(cart.imageUrl);
            List<ImageModel> im = new ArrayList<>();
            im.add(imageModel);
            List<List<ImageModel>> im2 = new ArrayList<>();
            im2.add(im);
            model.setImageUrl(im2);
            model.setPrice(cart.price);
            model.setCategory(cart.vertical);
            model.setQuantity(cart.quantity);
            model.setFsn(cart.fsn);
            model.setDiscount(cart.discount);
            ArrayList<String> attrs = new ArrayList<String>(Arrays.asList(cart.attributes.split(" , ")));
            ArrayList<IdAttributes> idAttributes = new ArrayList<>();
            Gson gson = new Gson();
            if (idAttributes != null && idAttributes.size() != 0) {
                for (int j = 0; j < Math.min(idAttributes.size(), 2); j++) {
                    idAttributes.add(gson.fromJson(attrs.get(i), IdAttributes.class));
                }
            }
            model.setAttributes(idAttributes);
            productModels.add(model);
            totalAmount += model.getPrice() * model.getQuantity();
            totalDiscount += model.getDiscount() * model.getQuantity();
        }
        int totalPayable = totalAmount - totalDiscount;
        ProductListAdapter adapter = new ProductListAdapter(productModels, this);
        productsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        totalAmountView.setText(totalAmount+"");
        totalDiscountView.setText(totalDiscount+"");
        totalPayableView.setText(totalPayable+"");

    }



}
