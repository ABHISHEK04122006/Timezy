# Privacy Policy for Timezy

**Effective Date:** October 22, 2026  
**Last Updated:** October 22, 2026  
**Application Name:** Timezy (Countdown & Widget)  
**Package Identifier:** `com.timezy.widget`  

Thank you for choosing **Timezy**. Your privacy is of the utmost importance to us. This Privacy Policy describes how your information is handled when you use the Timezy mobile application.

---

## 1. Overview & Offline Architecture

Timezy is designed from the ground up as a **100% offline, privacy-first application**. 

- We do **not** collect, store, transmit, or sell your personal data.
- The application does **not** request or utilize the Android internet permission (`android.permission.INTERNET`).
- All countdown events, titles, descriptions, target dates, and customization preferences remain strictly and exclusively on your physical device in private local storage.

---

## 2. Information We Collect

### Personal Data
Timezy does **not** collect any personally identifiable information (such as your name, email address, physical address, phone number, or payment details).

### Event and User Input Data
Any data you enter into the application (such as event names, notes, milestone dates, and visual themes) is stored locally using Android SQLite (Room Database) and Jetpack DataStore on your device. We have no access to this data.

### Analytics, Advertising & Telemetry
Timezy does **not** contain any third-party analytics libraries (such as Google Analytics or Firebase Analytics), advertising SDKs (such as Google AdMob), or crash reporting trackers. No data is sent to external servers.

---

## 3. Device Permissions Used

Timezy requests only the minimum device permissions strictly required for its core local functionality:

1. **`POST_NOTIFICATIONS` (Android 13+)**:
   - Used solely to display countdown arrival notifications and reminders that you have explicitly scheduled.
2. **`SCHEDULE_EXACT_ALARM` / Alarm Manager**:
   - Used to wake the system at the exact date and time you set for your event reminders.
3. **`RECEIVE_BOOT_COMPLETED`**:
   - Used to restore and reschedule your active event reminders if your device is restarted or rebooted.
4. **`VIBRATE`**:
   - Used to provide optional haptic feedback when a countdown alert triggers.

---

## 4. Third-Party Services

Timezy does not integrate with, transmit data to, or share data with any third-party services or APIs.

---

## 5. Children’s Privacy

Because Timezy does not collect any personal information whatsoever, it does not knowingly collect or solicit personal data from children under the age of 13 (or under the applicable age of consent in your jurisdiction), in full compliance with the Children's Online Privacy Protection Act (COPPA) and the General Data Protection Regulation (GDPR).

---

## 6. Data Retention and Deletion

All event data is retained on your device for as long as you choose to keep it. You have complete control over your data:
- You can edit or delete individual events at any time within the app.
- You can clear all data or uninstall the application at any time using your device's Android Application Settings, which will permanently remove all stored local databases and preferences.

---

## 7. Changes to This Privacy Policy

We may update our Privacy Policy from time to time. Any changes will be published directly to this page with an updated "Last Updated" date.

---

## 8. Contact Us

If you have any questions, suggestions, or concerns regarding this Privacy Policy or the Timezy application, please contact us at:

- **Developer**: Abhishek
- **GitHub Repository**: [https://github.com/ABHISHEK04122006/Timezy](https://github.com/ABHISHEK04122006/Timezy)
