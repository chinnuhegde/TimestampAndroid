# TimeStamp ⏱️

**TimeStamp** is a native Android application built with Kotlin for automated, real-time numeric data logging and report management. It acts as a continuous background monitor that captures sequential data points exactly once per second, stamps them with precise time data, and stores them securely in a local database for future analysis. 

This repository serves as a complete example of cleanly separating UI, database, and background logic in native Android development.

## 🚀 Key Features

* **Automated Data Logging:** One-click start/stop functionality initiates a background Coroutine that logs an incrementing value every single second.
* **Resumable State:** The counter remembers the last recorded value within a session lifecycle, ensuring counts pick up exactly where they paused when "Start" is hit again.
* **Persistent SQLite Database:** Robust, offline-first data retention using `SQLiteOpenHelper`. Maintains two normalized tables: one for Report metadata and one for individual, timestamped records.
* **Real-Time UI Updates:** The dashboard dynamically updates live duration, total record counts, and streams the captured data directly into a `RecyclerView`.
* **Report History & Analytics:** A dedicated history dashboard where users can view all past sessions, including start/end times and total duration.
* **CSV Export & Share Integration:** Compiles any recorded session into a properly formatted `.csv` file. Utilizes Android's `FileProvider` to trigger the native OS share sheet (Email, Google Drive, etc.).

## 🛠️ Technology Stack

* **Language:** Kotlin
* **UI toolkit:** Android XML (ConstraintLayout)
* **Concurrency:** Kotlin Coroutines (`lifecycleScope`, `Dispatchers.IO`)
* **Local Storage:** SQLite (Relational DB)
* **Data Presentation:** `RecyclerView` with custom Adapters
* **Inter-Process Communication:** Android `Intent`, `FileProvider`

## 📱 Screenshots

*(You can add screenshots here by uploading them to your GitHub repository and linking them like this: `![Dashboard](link_to_image)`)*

## ⚙️ How to Build and Run

1. Clone this repository:
   ```bash
   git clone https://github.com/YourUsername/TimeStamp.git
   ```
2. Open the project in **Android Studio**.
3. Allow Gradle to sync and download the required dependencies (Coroutines, RecyclerView).
4. Click the green **Run** button to deploy the app to an Android emulator or a physical device.

## 📁 Project Structure

* `MainActivity.kt`: Handles the primary UI, live data display, and the background Coroutine timer.
* `DatabaseHelper.kt`: Manages SQLite database creation, upgrades, and all CRUD operations.
* `Models.kt`: Contains the Kotlin Data Classes (`Report`, `Record`).
* `ReportActivity.kt` / `ReportDetailsActivity.kt`: Handles fetching historical data from the DB and the CSV export logic.
* `Adapters`: Connects the data models to the XML `RecyclerViews`.


