package com.ziv.flexilogger;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Schedules and performs log upload to Firebase Realtime Database. */
final class CloudUploader {

    private static final String TAG = "FlexiCloud";

    static void enqueue(Context ctx) {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .setBackoffCriteria(
                        androidx.work.BackoffPolicy.EXPONENTIAL,
                        15,
                        TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(ctx).enqueue(req);
    }

    /** Reads the last ~50 lines and pushes them under /apps/{app}/logs/{user}/{session}. */
    public static class UploadWorker extends Worker {

        public UploadWorker(@NonNull Context c, @NonNull WorkerParameters p) {
            super(c, p);
        }

        @NonNull
        @Override
        public Result doWork() {
            FlexiLoggerConfig cfg = Flexilogger.getConfig();
            if (cfg == null || !cfg.enableCloud) return Result.success();

            File logFile = Flexilogger.getLogFile(getApplicationContext());
            if (!logFile.exists()) return Result.success();

            List<String> lines;
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(new java.io.FileInputStream(logFile)))) {
                lines = new java.util.ArrayList<>();
                String ln;
                while ((ln = br.readLine()) != null) lines.add(ln);
                if (lines.size() > 50)
                    lines = lines.subList(lines.size() - 50, lines.size());
            } catch (Exception e) {
                Log.e(TAG, "read error", e);
                return Result.failure();
            }

            String uid  = Flexilogger.getUserId()    != null ? Flexilogger.getUserId()    : "anon";
            String sess = Flexilogger.getSessionId() != null ? Flexilogger.getSessionId() : "sess";

            // ---- NEW: create /meta/createdAt once ---------------------------------
            DatabaseReference appRoot = FirebaseDatabase.getInstance()
                    .getReference("apps")
                    .child(cfg.appName);

            appRoot.child("meta").child("createdAt").setValue(ServerValue.TIMESTAMP);
            // ----------------------------------------------------------------------

            DatabaseReference logPath = appRoot
                    .child("logs")
                    .child(uid)
                    .child(sess);

            for (String raw : lines) {
                // raw format: [YYYY-MM-DD HH:mm:ss] [LEVEL] [TAG] message
                try {
                    // 1. split the raw line
                    int firstClose = raw.indexOf(']');
                    int secondOpen = raw.indexOf('[', firstClose + 1);
                    int secondClose = raw.indexOf(']', secondOpen + 1);
                    int thirdOpen  = raw.indexOf('[', secondClose + 1);
                    int thirdClose = raw.indexOf(']', thirdOpen + 1);

                    String tsStr  = raw.substring(1, firstClose);                // YYYY-MM-DD HH:mm:ss
                    String lvlStr = raw.substring(secondOpen + 1, secondClose);  // INFO / DEBUG / ERROR
                    String tagStr = raw.substring(thirdOpen + 1, thirdClose);    // e.g. Crash

                    long   tsSec  = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .parse(tsStr).getTime() / 1000;

                    String msg    = raw.substring(thirdClose + 2);               // skip "] "

                    DatabaseReference objRef = logPath.push();
                    objRef.setValue(new LogEntry(tsSec, lvlStr, tagStr, msg));
                } catch (Exception parseErr) {
                    Log.w(TAG, "skip unparsable line: " + raw);
                }
            }
            Log.d(TAG, "Uploaded " + lines.size() + " lines");
            return Result.success();
        }
    }
    @SuppressWarnings("unused")
    private static class LogEntry {
        public long   ts;
        public String level;
        public String tag;
        public String msg;
        LogEntry(long ts, String level, String tag, String msg) {
            this.ts = ts; this.level = level; this.tag = tag; this.msg = msg;
        }
    }
}


