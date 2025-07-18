package com.ziv.flexilogger;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flexilogger {
    private static FlexiLoggerConfig cfg;

    public enum LogLevel {
        DEBUG, INFO, ERROR
    }

    public static void init(Context ctx, FlexiLoggerConfig config) {
        cfg = config;
    }

    private static final String LOG_FILE_NAME = "flexi_logs.txt";
    private static final String FILTERED_LOGCAT_FILE = "filtered_logcat.txt";

    private static String globalUserId = null;
    private static String globalSessionId = null;

    public static FlexiLoggerConfig getConfig()    { return cfg; }
    public static String getUserId()               { return globalUserId; }
    public static String getSessionId()            { return globalSessionId; }

    public static void setUserId(String userId) {
        globalUserId = userId;
    }

    public static void setSessionId(String sessionId) {
        globalSessionId = sessionId;
    }

    public static void log(Context context, String tag, String message, LogLevel level) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        StringBuilder logEntry = new StringBuilder();
        logEntry.append("[").append(timestamp).append("] ")
                .append("[").append(level.name()).append("] ")
                .append("[").append(tag).append("] ");

        if (globalUserId != null) {
            logEntry.append("[userId=").append(globalUserId).append("] ");
        }

        if (globalSessionId != null) {
            logEntry.append("[sessionId=").append(globalSessionId).append("] ");
        }

        logEntry.append(message).append("\n");

        // Print to Logcat
        switch (level) {
            case DEBUG: Log.d("Flexilogger", logEntry.toString()); break;
            case INFO: Log.i("Flexilogger", logEntry.toString()); break;
            case ERROR: Log.e("Flexilogger", logEntry.toString()); break;
        }

        // Append to file
        try {
            File file = new File(context.getFilesDir(), LOG_FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            writer.append(logEntry.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("Flexilogger", "Error writing to log file", e);
        }

        if (cfg != null && cfg.enableCloud) {
            CloudUploader.enqueue(context.getApplicationContext());
        }
    }

    public static File getLogFile(Context context) {
        return new File(context.getFilesDir(), LOG_FILE_NAME);
    }

    // Capture only Flexilogger logs from Logcat
    public static void collectOwnLogcatLogs(Context context) {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d Flexilogger:D *:S");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            File file = new File(context.getFilesDir(), FILTERED_LOGCAT_FILE);
            FileWriter writer = new FileWriter(file, true);

            String line;
            while ((line = reader.readLine()) != null) {
                writer.append(line).append("\n");
            }

            writer.flush();
            writer.close();
            reader.close();
        } catch (IOException e) {
            Log.e("Flexilogger", "Error reading filtered Logcat", e);
        }
    }

    public static File getFilteredLogFile(Context context) {
        return new File(context.getFilesDir(), FILTERED_LOGCAT_FILE);
    }
}
