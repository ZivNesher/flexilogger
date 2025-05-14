package com.flexilogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.ziv.flexilogger.Flexilogger;

import java.util.ArrayList;
import java.util.List;

public class ShoppingDemoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<Product> products;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_demo);

        recyclerView = findViewById(R.id.product_recycler);
        backBtn = findViewById(R.id.btn_back);

        // Sample products with images and prices
        products = new ArrayList<>();
        products.add(new Product("101", "Wireless Headphones", "$99.99", "https://via.placeholder.com/300x200.png?text=Headphones"));
        products.add(new Product("102", "Smart Watch", "$149.99", "https://via.placeholder.com/300x200.png?text=Smart+Watch"));
        products.add(new Product("103", "Bluetooth Speaker", "$89.99", "https://via.placeholder.com/300x200.png?text=Speaker"));
        products.add(new Product("104", "Laptop Stand", "$39.99", "https://via.placeholder.com/300x200.png?text=Laptop+Stand"));
        products.add(new Product("105", "Running Shoes", "$129.99", "https://via.placeholder.com/300x200.png?text=Shoes"));
        products.add(new Product("106", "Leather Wallet", "$49.99", "https://via.placeholder.com/300x200.png?text=Wallet"));

        adapter = new ProductAdapter(this, products);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        recyclerView.setAdapter(adapter);

        backBtn.setOnClickListener(v -> {
            Flexilogger.log(this, "Shopping", "User clicked BACK", Flexilogger.LogLevel.INFO);
            finish();
        });
    }
}
