# FlexiLogger

**FlexiLogger** is a lightweight and easy-to-integrate logging library for Android. It allows you to log messages with custom tags and log levels directly to a persistent local file (`flexi_logs.txt`). This can be extremely helpful for debugging apps outside of the development environment — especially when user-side issues are difficult to reproduce.

---

## Features

- Write logs to a local file in internal storage
- Supports `DEBUG`, `INFO`, and `ERROR` levels
- Each log entry includes timestamp, log level, and tag
- Simple API with minimal setup
- Optional sample app to view log output on-device

---

## Installation

1. Add the `flexilogger` module to your project.

2. Include it in your `settings.gradle`:

```groovy
include ':flexilogger'
