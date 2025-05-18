package com.flexilogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ziv.flexilogger.Flexilogger;


public class MainActivity extends AppCompatActivity {

    private Button logDebugBtn, logInfoBtn, logErrorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Flexilogger.setUserId("user_1234");
        Flexilogger.setSessionId("session_xyz");



        Button viewLogsBtn = findViewById(R.id.btn_view_logs);
        Button shopDemoBtn = findViewById(R.id.btn_shop_demo);


        shopDemoBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ShoppingDemoActivity.class));
        });
        viewLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LogViewerActivity.class));
            }
        });
        Button errorBtn = findViewById(R.id.btn_trigger_error);
        errorBtn.setOnClickListener(v -> {
            try {
                // simulate crash
                String nullStr = null;
                nullStr.length(); // throws NullPointerException
            } catch (Exception e) {
                Flexilogger.log(MainActivity.this, "Crash", "ERROR: " + e.getMessage(), Flexilogger.LogLevel.ERROR);
                Toast.makeText(this, "Simulated error captured in log", Toast.LENGTH_SHORT).show();
            }
        });


    }


}
