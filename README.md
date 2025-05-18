# FlexiLogger

FlexiLogger is a lightweight and efficient logging library for Android. It allows developers to log messages to a local file and view them through a structured and user-friendly in-app interface. The library is intended to enhance traceability and debugging even in production environments.

---

## Features

- Support for log levels: `DEBUG`, `INFO`, `ERROR`
- Persistent logging to internal file (`flexi_logs.txt`)
- Optional user and session ID tagging
- In-app log viewer with:
    - Log type filtering
    - Keyword search
    - Line numbering
    - Color-coded severity levels
    - Export to JSON and ZIP
    - Share log file via `Intent`
    - Auto-refresh every 5 seconds
    - Long-press to copy log contents

---

## Integration

1. Add the `flexilogger` module to your Android project, or copy `Flexilogger.java` directly.

2. In your app-level `build.gradle`:

```groovy
implementation project(":flexilogger")
```

3. (Optional) Initialize user/session identifiers:

```java
Flexilogger.setUserId("user_1234");
Flexilogger.setSessionId("session_5678");
```

---

## Usage

### Logging

```java
Flexilogger.log(context, "LoginScreen", "User pressed login", Flexilogger.LogLevel.INFO);
Flexilogger.log(context, "Cart", "Empty cart", Flexilogger.LogLevel.DEBUG);
Flexilogger.log(context, "API", "Server error", Flexilogger.LogLevel.ERROR);
```

### Viewing Logs

```java
startActivity(new Intent(context, LogViewerActivity.class));
```

---

## LogViewerActivity

The built-in log viewer screen supports:

- Free text search
- Log level filter (ALL, INFO, DEBUG, ERROR)
- Scrollable, copyable log output
- Line numbers
- Color-coded: red for ERROR, blue for INFO, black for DEBUG
- Export options:
    - JSON array of lines
    - ZIP file containing `flexi_logs.txt`
- Share log file via `Intent`
- Auto-refresh every 5 seconds
- Long-press to copy all logs to clipboard

---

## Customization

- Change file name by editing the `LOG_FILE_NAME` constant in `Flexilogger.java`
- Extend with additional log levels or structured log formats (e.g., JSON)
- Integrate with external systems (Firebase, Sentry, etc.) for cloud-based tracking

---

## Troubleshooting

- **Logs not written?**
    - Ensure `Flexilogger.log()` is called from a valid Context.
    - Check that the internal file directory is writable.

- **LogViewer shows no logs?**
    - Confirm that the log file exists.
    - Try exporting via "Share" to inspect content.

- **Sharing/export fails?**
    - Verify `FileProvider` is configured in `AndroidManifest.xml`

---

## License

MIT License

Free to use, extend, and integrate into commercial or open-source projects.
