package com.flexilogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ziv.flexilogger.Flexilogger;


public class MainActivity extends AppCompatActivity {

    private Button logDebugBtn, logInfoBtn, logErrorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Flexilogger.setUserId("user_1234");
        Flexilogger.setSessionId("session_xyz");



        logDebugBtn = findViewById(R.id.btn_debug);
        logInfoBtn = findViewById(R.id.btn_info);
        logErrorBtn = findViewById(R.id.btn_error);
        Button viewLogsBtn = findViewById(R.id.btn_view_logs);
        Button shopDemoBtn = findViewById(R.id.btn_shop_demo);

        logDebugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flexilogger.log(MainActivity.this, "MainActivity", "Debug log clicked", Flexilogger.LogLevel.DEBUG);
            }
        });
        shopDemoBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ShoppingDemoActivity.class));
        });

        logInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flexilogger.log(MainActivity.this, "MainActivity", "Info log clicked", Flexilogger.LogLevel.INFO);
            }
        });

        logErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flexilogger.log(MainActivity.this, "MainActivity", "Error log clicked", Flexilogger.LogLevel.ERROR);
            }
        });

        viewLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LogViewerActivity.class));
            }
        });

    }
}
