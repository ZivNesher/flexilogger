package com.flexilogger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import android.view.View;
import android.content.Intent;
import android.net.Uri;

import com.ziv.flexilogger.Flexilogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.core.content.FileProvider;

public class LogViewerActivity extends AppCompatActivity {

    private TextView logTextView;
    private EditText searchBar;
    private Spinner filterSpinner;
    private List<String> allLogs = new ArrayList<>();
    private List<String> filteredLogs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        logTextView = findViewById(R.id.log_text_view);
        searchBar = findViewById(R.id.search_bar);
        filterSpinner = findViewById(R.id.filter_spinner);
        Button shareBtn = findViewById(R.id.btn_share_logs);
        Button clearBtn = findViewById(R.id.btn_clear_logs);
        Button backBtn = findViewById(R.id.btn_go_back);

        setupFilterSpinner();
        loadLogs();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        shareBtn.setOnClickListener(v -> shareLogs());
        clearBtn.setOnClickListener(v -> clearLogs());
        backBtn.setOnClickListener(v -> {
            Flexilogger.log(this, "LogViewer", "User clicked BACK", Flexilogger.LogLevel.INFO);
            finish();
        });
    }

    private void setupFilterSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"ALL", "INFO", "DEBUG", "ERROR"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);
    }

    private void loadLogs() {
        allLogs.clear();
        File logFile = Flexilogger.getLogFile(this);
        if (!logFile.exists()) {
            logTextView.setText("No log file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allLogs.add(line);
            }
        } catch (IOException e) {
            allLogs.add("Error reading log file: " + e.getMessage());
        }

        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchBar.getText().toString().toLowerCase();
        String selectedFilter = filterSpinner.getSelectedItem().toString();

        filteredLogs = allLogs.stream()
                .filter(line -> {
                    boolean matchesSearch = line.toLowerCase().contains(searchText);
                    boolean matchesLevel = selectedFilter.equals("ALL") || line.contains("[" + selectedFilter + "]");
                    return matchesSearch && matchesLevel;
                })
                .collect(Collectors.toList());

        StringBuilder display = new StringBuilder();
        int lineNumber = 1;
        for (String line : filteredLogs) {
            if (line.contains("[ERROR]")) {
                display.append(String.format("%03d. 🔴 %s\n", lineNumber++, line));
            } else if (line.contains("[INFO]")) {
                display.append(String.format("%03d. 🔵 %s\n", lineNumber++, line));
            } else if (line.contains("[DEBUG]")) {
                display.append(String.format("%03d. ⚫ %s\n", lineNumber++, line));
            } else {
                display.append(String.format("%03d. ⚪ %s\n", lineNumber++, line));
            }
        }

        logTextView.setText(display.toString());
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
                allLogs.clear();
                applyFilters();
            } else {
                Toast.makeText(this, "Failed to clear logs", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
