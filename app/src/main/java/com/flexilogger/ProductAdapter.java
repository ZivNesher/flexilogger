package com.flexilogger;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ziv.flexilogger.Flexilogger;
import java.util.List;




public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public enum Mode {
        SHOP, CART
    }

    private final List<Product> productList;
    private final Context context;
    private final Mode mode;

    public ProductAdapter(Context ctx, List<Product> products, Mode mode) {
        this.context = ctx;
        this.productList = products;
        this.mode = mode;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, priceText;
        Button viewBtn, buyBtn, removeBtn;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            nameText = itemView.findViewById(R.id.product_name);
            priceText = itemView.findViewById(R.id.product_price);
            viewBtn = itemView.findViewById(R.id.btn_view);
            buyBtn = itemView.findViewById(R.id.btn_buy);
            removeBtn = itemView.findViewById(R.id.btn_remove);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.nameText.setText(product.getName());
        holder.priceText.setText(product.getPrice());

        Glide.with(context).load(product.getImageUrl()).into(holder.imageView);

        if (mode == Mode.SHOP) {
            holder.viewBtn.setVisibility(View.VISIBLE);
            holder.buyBtn.setVisibility(View.VISIBLE);
            holder.removeBtn.setVisibility(View.GONE);

            holder.viewBtn.setOnClickListener(v -> {
                Flexilogger.log(context, "ProductView", "User viewed product: " + product.getName(), Flexilogger.LogLevel.INFO);
                showProductDialog(product);
            });

            holder.buyBtn.setOnClickListener(v -> {
                CartManager.addToCart(product);
                Flexilogger.log(context, "Cart", "User added to cart: " + product.getName(), Flexilogger.LogLevel.INFO);
                Toast.makeText(context, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            });

        } else if (mode == Mode.CART) {
            holder.viewBtn.setVisibility(View.GONE);
            holder.buyBtn.setVisibility(View.GONE);
            holder.removeBtn.setVisibility(View.VISIBLE);

            holder.removeBtn.setOnClickListener(v -> {
                CartManager.removeFromCart(product);
                Flexilogger.log(context, "Cart", "User removed from cart: " + product.getName(), Flexilogger.LogLevel.INFO);
                Toast.makeText(context, product.getName() + " removed", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void showProductDialog(Product product) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_product_detail, null);
        ImageView img = dialogView.findViewById(R.id.dialog_product_image);
        TextView name = dialogView.findViewById(R.id.dialog_product_name);
        TextView price = dialogView.findViewById(R.id.dialog_product_price);
        TextView desc = dialogView.findViewById(R.id.dialog_product_description);

        Glide.with(context).load(product.getImageUrl()).into(img);
        name.setText(product.getName());
        price.setText(product.getPrice());
        desc.setText("This is a high-quality " + product.getName().toLowerCase() + ". Perfect for daily use.");

        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }
}
