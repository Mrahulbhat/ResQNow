# Google Services Setup Guide for ResQNow

## Overview
Google Services (including Firebase) have been added to your project. Follow these steps to complete the setup.

---

## ✅ Already Completed in Code

The following changes have been made to your project:

1. ✅ Added `com.google.gms.google-services` plugin to root `build.gradle.kts`
2. ✅ Applied the plugin in `app/build.gradle.kts`
3. ✅ Added Firebase dependencies to `libs.versions.toml`:
   - Firebase Analytics
   - Firebase Firestore
   - Firebase Cloud Messaging
   - Firebase Authentication

---

## 📋 Next Steps - Manual Configuration Required

### **Step 1: Create Firebase Project**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a new project"
3. Enter your project name: "ResQNow"
4. Configure Google Analytics (optional but recommended)
5. Click "Create project"

### **Step 2: Register Android App**

1. In Firebase Console, click the Android icon
2. Fill in the following details:
   - **Android Package Name**: `com.resqnow.app`
   - **App nickname**: ResQNow (optional)
   - **SHA-1 certificate fingerprint**: See below
3. Click "Register app"

### **Step 3: Get SHA-1 Certificate Fingerprint**

Run this command in PowerShell in your project directory:

```powershell
# Windows - using keytool from Java
$JAVA_HOME = (Get-Command java).Source | Split-Path -Parent | Split-Path -Parent
& "$JAVA_HOME\bin\keytool.exe" -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android | Select-String "SHA1"
```

Or if you have Android SDK installed:
```powershell
keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android | Select-String "SHA1"
```

Copy the SHA-1 value and paste it into Firebase Console.

### **Step 4: Download google-services.json**

1. In Firebase Console, after registering the app, click "Download google-services.json"
2. Copy the file to: `app/google-services.json`
3. **Important**: The file must be placed in the `app/` directory (same level as `build.gradle.kts`)

---

## 📁 Project Structure After Setup

```
ResQNow/
├── app/
│   ├── google-services.json  ← PLACE FILE HERE
│   ├── build.gradle.kts
│   └── src/
└── build.gradle.kts
```

---

## 🚀 Firebase Services Available in Your App

### 1. **Firebase Analytics**
Track user events and app usage:
```kotlin
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

val analytics = FirebaseAnalytics.getInstance(context)
analytics.logEvent("emergency_detected") {
    param("location", "home")
}
```

### 2. **Firebase Firestore**
Real-time database for emergency records:
```kotlin
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

val db = Firebase.firestore
db.collection("emergencies")
    .document(userId)
    .set(mapOf("status" to "active", "timestamp" to System.currentTimeMillis()))
```

### 3. **Firebase Cloud Messaging (FCM)**
Send push notifications:
```kotlin
import com.google.firebase.messaging.FirebaseMessaging

FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        // Save token to your backend
    }
}
```

### 4. **Firebase Authentication**
User authentication:
```kotlin
import com.google.firebase.auth.FirebaseAuth

val auth = FirebaseAuth.getInstance()
auth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = task.result?.user
        }
    }
```

---

## 🔧 Gradle Configuration

Your gradle files now include:

**`build.gradle.kts` (root)**
```kotlin
id("com.google.gms.google-services") version "4.4.2" apply false
```

**`app/build.gradle.kts`**
```kotlin
id("com.google.gms.google-services")

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
}
```

---

## ✨ Testing Your Setup

After adding `google-services.json`, build and run:

```powershell
./gradlew build
```

If successful, Firebase is properly integrated!

---

## 📚 Useful Resources

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Android Documentation](https://firebase.google.com/docs/android/setup)
- [Firebase Kotlin Documentation](https://firebase.google.com/docs/database/kotlin-start)
- [Firebase Firestore Guide](https://firebase.google.com/docs/firestore)

---

## 🐛 Troubleshooting

### Issue: "google-services plugin not found"
**Solution**: Ensure Google Services plugin is added to root `build.gradle.kts` with the correct version.

### Issue: "google-services.json not found"
**Solution**: Make sure `google-services.json` is placed in the `app/` directory.

### Issue: Gradle build fails with dependency conflicts
**Solution**: The Firebase BOM (Bill of Materials) manages version compatibility. Ensure it's declared first in dependencies.

---

## 🎯 Recommended for Your ResQNow App

Given your emergency detection app, consider using:

1. **Firestore** - Store emergency incidents and user data
2. **Cloud Messaging** - Send alerts to emergency contacts
3. **Authentication** - Secure user accounts
4. **Analytics** - Track detection accuracy and usage patterns


