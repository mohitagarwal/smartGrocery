package com.flipkart.smartgrocery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.models.ShoppingCart;
import com.flipkart.smartgrocery.netowking.response.IdAttributes;
import com.flipkart.smartgrocery.netowking.response.ProductModel;
import com.flipkart.smartgrocery.utils.TextUtils;

import java.util.List;

public class ProductListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private Context context;
    private List<ProductModel> products;
    private boolean showActions;

    public ProductListAdapter(List<ProductModel> products, Context context) {
        this.products = products;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public ProductListAdapter(List<ProductModel> products, Context context, boolean showActions) {
        this.products = products;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.showActions = showActions;
    }


    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ProductViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_product, null);
            viewHolder = new ProductViewHolder();
            initialize(viewHolder, view);
        } else {
            viewHolder = (ProductViewHolder) view.getTag();
        }
        setupView(viewHolder, products.get(i));

        return view;
    }

    private void initialize(ProductViewHolder viewHolder, View view) {
        viewHolder.titleView = (TextView) view.findViewById(R.id.title);
        viewHolder.idAttributes = (TextView) view.findViewById(R.id.id_attributes);
        viewHolder.imageView = (ImageView) view.findViewById(R.id.productImage);
        viewHolder.quantityView = (TextView) view.findViewById(R.id.quantity);
        viewHolder.discountView = (TextView) view.findViewById(R.id.discount);
        viewHolder.priceView = (TextView) view.findViewById(R.id.price);
        viewHolder.addToCart = (Button) view.findViewById(R.id.button1);
        viewHolder.actionsView = (LinearLayout) view.findViewById(R.id.list_action);
        view.setTag(viewHolder);
    }

    private void setupView(ProductViewHolder viewHolder, final ProductModel productModel) {
        if (productModel != null) {
            if (productModel.getImageUrl() != null
                    && !productModel.getImageUrl().isEmpty()
                    && productModel.getImageUrl().get(0) != null
                    && !productModel.getImageUrl().get(0).isEmpty()) {
                viewHolder.imageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(productModel.getImageUrl().get(0).get(productModel.getImageUrl().get(0).size() - 1).getUrl()).into(viewHolder.imageView);
            } else {
                viewHolder.imageView.setVisibility(View.INVISIBLE);
            }
            viewHolder.titleView.setText(productModel.getTitle());
            /* Build list of attributes and fill the body of the container */
            StringBuilder builder = new StringBuilder();
            if (productModel.getAttributes() != null && productModel.getAttributes().size() != 0) {
                for (int i = 0; i < Math.min(productModel.getAttributes().size(), 2); i++) {
                    IdAttributes attribute = productModel.getAttributes().get(i);
                    if (attribute.getName() != null && attribute.getValue() != null) {
                        builder.append(attribute.getName())
                                .append(" : ")
                                .append(attribute.getValue())
                                .append("\n");
                    }
                }
            }
            viewHolder.idAttributes.setText(builder.toString().trim());
            if (productModel.getQuantity() != 0) {
                viewHolder.quantityView.setText(String.valueOf(productModel.getQuantity()));
                viewHolder.quantityView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.quantityView.setVisibility(View.GONE);
            }
            if (productModel.getDiscount() != 0) {
                viewHolder.discountView.setText(productModel.getDiscount() + "%");
                viewHolder.discountView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.discountView.setVisibility(View.GONE);
            }
            if (productModel.getPrice() != 0) {
                viewHolder.priceView.setText(TextUtils.getRupeeText(productModel.getPrice()));
                viewHolder.priceView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.priceView.setVisibility(View.GONE);
            }
            if(showActions){
                viewHolder.addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShoppingCart cart = new ShoppingCart(productModel);
                        cart.save();
                    }
                });
                viewHolder.actionsView.setVisibility(View.VISIBLE);
            }else {
                viewHolder.actionsView.setVisibility(View.GONE);
            }
        }
    }

    private static class ProductViewHolder {
        private ImageView imageView;
        private TextView titleView;
        private TextView idAttributes;
        private TextView quantityView;
        private TextView priceView;
        private TextView discountView;
        private LinearLayout actionsView;
        private Button addToCart;
    }

}
