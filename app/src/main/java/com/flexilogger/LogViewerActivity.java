package com.flexilogger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.ziv.flexilogger.Flexilogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.FileProvider;


public class LogViewerActivity extends AppCompatActivity {

    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        logTextView = findViewById(R.id.log_text_view);
        Button shareBtn = findViewById(R.id.btn_share_logs);
        Button clearBtn = findViewById(R.id.btn_clear_logs);

        showLogFile();

        shareBtn.setOnClickListener(view -> shareLogs());
        clearBtn.setOnClickListener(view -> clearLogs());
    }


    private void showLogFile() {
        File logFile = Flexilogger.getLogFile(this);
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            content.append("Error reading log file: ").append(e.getMessage());
        }

        logTextView.setText(content.toString());
    }
    private void shareLogs() {
        File logFile = Flexilogger.getLogFile(this);
        if (!logFile.exists()) {
            Toast.makeText(this, "Log file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(this,
                getPackageName() + ".provider", logFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share log file via"));
    }

    private void clearLogs() {
        File logFile = Flexilogger.getLogFile(this);
        if (logFile.exists()) {
            boolean deleted = logFile.delete();
            if (deleted) {
                Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show();
                logTextView.setText("");
            } else {
                Toast.makeText(this, "Failed to clear logs", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
