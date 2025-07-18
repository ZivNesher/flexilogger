# FlexiLogger

**FlexiLogger** is a lightweight and efficient logging library for Android. It allows developers to log structured messages with levels, tags, and timestamps, and send them to **Firebase Realtime Database**. Logs are then visualized through a real-time, web-based dashboard.

🔗 **View logs in the web dashboard:** [https://github.com/ZivNesher/flexiloggerWEB](https://github.com/ZivNesher/flexiloggerWEB)

---

## ✅ Features

- Log levels: `DEBUG`, `INFO`, `ERROR`
- Automatic timestamping
- User ID and Session ID tagging
- Sends logs to Firebase Realtime Database
- [Optional] Local fallback to file (`flexi_logs.txt`)
- Web dashboard provides:
  - Pie charts by `tag`, `user`, and `level`
  - Realtime log table with filters and color coding
  - App-level password protection

---

## 🚀 Integration

1. Add the `flexilogger` module to your Android project or import via GitHub:

<details>
<summary>Gradle (local module)</summary>

```groovy
implementation project(":flexilogger")
```

</details>

<details>
<summary>JitPack (optional)</summary>

Add to your root `build.gradle`:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Then in app-level `build.gradle`:

```groovy
implementation 'com.github.ZivNesher:Flexilogger:1.0.0'
```

</details>

2. Initialize FlexiLogger in your Application or main Activity:

```java
Flexilogger.setUserId("user_1234");
Flexilogger.setSessionId("session_5678");

FlexiLoggerConfig config = new FlexiLoggerConfig.Builder("myAppName").build();
Flexilogger.init(getApplicationContext(), config);
```

---

## ✍️ Usage

### Logging

```java
Flexilogger.log(context, "LoginScreen", "User pressed login", Flexilogger.LogLevel.INFO);
Flexilogger.log(context, "Cart", "Empty cart", Flexilogger.LogLevel.DEBUG);
Flexilogger.log(context, "API", "Server error", Flexilogger.LogLevel.ERROR);
```

---

## 🔍 Viewing Logs (Web Dashboard)

Logs are sent to:

```
apps/
  └── myAppName/
      └── logs/
          └── user_1234/
              └── session_5678/
                  └── log_xyz123: {
                        tag: "LoginScreen",
                        msg: "User pressed login",
                        level: "INFO",
                        ts: 1723459123
                  }
```

The **dashboard** at  
👉 [https://github.com/ZivNesher/flexiloggerWEB](https://github.com/ZivNesher/flexiloggerWEB)  
displays these logs using:

- 🥧 Pie charts by `tag`, `user ID`, and `level`
- 📋 Interactive table with search and filters
- 🔐 App-level password auth (set in Firebase under `/meta/passwordHash`)

---

## 📁 Optional: Local File Logging

In addition to Firebase, logs are also saved locally (optional fallback):

- File: `flexi_logs.txt`
- Location: App's internal storage directory
- Useful for offline inspection
- You may implement a share/export activity if needed

---

## 🧪 Troubleshooting

- **Dashboard shows no logs**  
  - Ensure `Flexilogger.init()` is called
  - Confirm app name matches Firebase DB path
  - Verify Firebase Realtime Database rules allow writes

- **Log not saved locally?**  
  - Check context passed to `log()` is valid
  - Ensure internal storage is accessible

---

## 🧩 Built With

- Android (Java)
- Firebase Realtime Database
- Optional: MUI + React dashboard

---

## 📜 License

MIT License  
Free to use, extend, and integrate into open-source or commercial apps.

---

## 🙌 Author

**Ziv Nesher**  
[github.com/ZivNesher](https://github.com/ZivNesher)

---
