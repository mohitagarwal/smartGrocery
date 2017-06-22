package com.flipkart.smartgrocery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.netowking.response.ProductModel;

import java.util.List;

public class ProductListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private Context context;
    private List<ProductModel> products;

    public ProductListAdapter(List<ProductModel> products, Context context) {
        this.products = products;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
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
        viewHolder.container = view.findViewById(R.id.productItemContainer);
        viewHolder.titleView = (TextView) view.findViewById(R.id.title);
        viewHolder.imageView = (ImageView) view.findViewById(R.id.productImage);
        viewHolder.quantityView = (TextView) view.findViewById(R.id.quantity);
        viewHolder.discountView = (TextView) view.findViewById(R.id.discount);
        viewHolder.priceView = (TextView) view.findViewById(R.id.price);
        view.setTag(viewHolder);
    }

    private void setupView(ProductViewHolder viewHolder, ProductModel productModel) {
        viewHolder.titleView.setText(productModel.getTitle());
        Glide.with(context).load(productModel.getImageUrl().get(0).get(productModel.getImageUrl().get(0).size() -1).getUrl()).into(viewHolder.imageView);
        viewHolder.quantityView.setText(productModel.getQuantity() + "");
        viewHolder.discountView.setText(productModel.getDiscount() + "");
        viewHolder.priceView.setText(productModel.getPrice() + "");
        
        viewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
                // TODO: 22/06/17  
            }
        });
    }

    private static class ProductViewHolder {
        private View container;
        private ImageView imageView;
        private TextView titleView;
        private TextView quantityView;
        private TextView priceView;
        private TextView discountView;
    }
    
}
