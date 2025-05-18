package com.flexilogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ziv.flexilogger.Flexilogger;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private boolean purchaseCompleted = false;
    private Handler handler = new Handler();
    private Runnable abandonRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        RecyclerView recyclerView = findViewById(R.id.cart_recycler);
        Button completeBtn = findViewById(R.id.btn_complete_purchase);
        Button clearBtn = findViewById(R.id.btn_clear_cart);
        Button backBtn = findViewById(R.id.btn_back);
        List<Product> cartItems = CartManager.getCartItems();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ProductAdapter adapter = new ProductAdapter(this, cartItems, ProductAdapter.Mode.CART);
        recyclerView.setAdapter(adapter);



        completeBtn.setOnClickListener(v -> {
            Flexilogger.log(this, "Cart", "User completed the purchase", Flexilogger.LogLevel.INFO);
            Toast.makeText(this, "Purchase completed!", Toast.LENGTH_SHORT).show();
            purchaseCompleted = true;
            CartManager.clearCart();
            finish();
        });

        clearBtn.setOnClickListener(v -> {
            CartManager.clearCart();
            Flexilogger.log(this, "Cart", "User cleared the cart", Flexilogger.LogLevel.INFO);
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
            finish();
        });
        backBtn.setOnClickListener(v -> {
            Flexilogger.log(this, "Cart", "User clicked BACK", Flexilogger.LogLevel.INFO);
            finish();
        });

        // Start 30-second timeout
        abandonRunnable = () -> {
            if (!purchaseCompleted && !CartManager.isEmpty()) {
                Flexilogger.log(this, "Cart", "User abandoned the cart without purchasing", Flexilogger.LogLevel.ERROR);
            }
        };
        handler.postDelayed(abandonRunnable, 30_000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(abandonRunnable);
    }


}
