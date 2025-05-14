package com.ziv.flexilogger;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flexilogger {

    public enum LogLevel {
        DEBUG, INFO, ERROR
    }

    private static final String LOG_FILE_NAME = "flexi_logs.txt";
    private static String globalUserId = null;
    private static String globalSessionId = null;

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
            case DEBUG: Log.d(tag, message); break;
            case INFO: Log.i(tag, message); break;
            case ERROR: Log.e(tag, message); break;
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
    }

    public static File getLogFile(Context context) {
        return new File(context.getFilesDir(), LOG_FILE_NAME);
    }
}
