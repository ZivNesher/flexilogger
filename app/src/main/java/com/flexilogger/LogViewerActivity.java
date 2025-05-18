package com.flexilogger;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import com.ziv.flexilogger.Flexilogger;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogViewerActivity extends AppCompatActivity {

    private TextView logTextView;
    private EditText searchBar;
    private Spinner filterSpinner;
    private Button shareBtn, clearBtn, backBtn;
    private List<String> allLogs = new ArrayList<>();
    private List<String> filteredLogs = new ArrayList<>();
    private Handler autoRefreshHandler = new Handler();
    private Runnable autoRefreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        logTextView = findViewById(R.id.log_text_view);
        searchBar = findViewById(R.id.search_bar);
        filterSpinner = findViewById(R.id.filter_spinner);
        shareBtn = findViewById(R.id.btn_share_logs);
        clearBtn = findViewById(R.id.btn_clear_logs);
        backBtn = findViewById(R.id.btn_go_back);

        setupFilterSpinner();
        loadLogs();

        logTextView.setOnLongClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Log", logTextView.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Logs copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        shareBtn.setOnClickListener(v -> showShareOptions());
        clearBtn.setOnClickListener(v -> clearLogs());
        backBtn.setOnClickListener(v -> {
            Flexilogger.log(this, "LogViewer", "User clicked BACK", Flexilogger.LogLevel.INFO);
            finish();
        });

        autoRefreshRunnable = () -> {
            loadLogs();
            autoRefreshHandler.postDelayed(autoRefreshRunnable, 5000);
        };
        autoRefreshHandler.post(autoRefreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
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

        filteredLogs = new ArrayList<>();
        for (String line : allLogs) {
            boolean matchesSearch = line.toLowerCase().contains(searchText);
            boolean matchesLevel = selectedFilter.equals("ALL") || line.contains("[" + selectedFilter + "]");
            if (matchesSearch && matchesLevel) {
                filteredLogs.add(line);
            }
        }

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

    private void showShareOptions() {
        String[] options = {"Share as TXT", "Export as JSON", "Export as ZIP"};
        new AlertDialog.Builder(this)
                .setTitle("Export Logs")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: shareLogFile(); break;
                        case 1: exportLogsAsJson(); break;
                        case 2: exportLogsAsZip(); break;
                    }
                }).show();
    }

    private void shareLogFile() {
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

    private void exportLogsAsJson() {
        File output = new File(getFilesDir(), "logs_" + System.currentTimeMillis() + ".json");

        JSONArray jsonArray = new JSONArray(filteredLogs);
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(jsonArray.toString(2));
            writer.flush();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save JSON", Toast.LENGTH_SHORT).show();
            return;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", output);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share JSON log file"));
    }

    private void exportLogsAsZip() {
        File output = new File(getFilesDir(), "logs_" + System.currentTimeMillis() + ".zip");
        File logFile = Flexilogger.getLogFile(this);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output))) {
            ZipEntry entry = new ZipEntry("flexi_logs.txt");
            zos.putNextEntry(entry);

            FileInputStream fis = new FileInputStream(logFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            fis.close();
            zos.closeEntry();
        } catch (IOException e) {
            Toast.makeText(this, "ZIP export failed", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", output);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share ZIP log file"));
    }
}
