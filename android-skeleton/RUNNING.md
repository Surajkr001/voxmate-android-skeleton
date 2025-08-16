Running the VoxMate Android Skeleton (local)

This file explains how to run the mock server and the Android app locally using only free tools.

1) Start the mock server

Open PowerShell and run the helper script:

```pwsh
cd "c:\Users\suraj\Desktop\HTMLCSSJSPractice\android-skeleton"
.\start-functions.ps1
```

This will install dependencies (if needed) and start an Express mock server on http://localhost:5000 with a `/v1/chat` endpoint.

2) Run the Android app

- Open the `android-skeleton` folder in Android Studio (or run `open-android-studio.ps1` from PowerShell to try to open it automatically).
- Start an Android emulator (recommended). The app uses `10.0.2.2:5000` to reach the host machine when running in the default Android emulator.

Notes and troubleshooting
- If you're running the app on a physical device, replace the server URL in `MainActivity.kt` with your machine IP (e.g., `http://192.168.1.100:5000/v1/chat`).
- The mock server is purposely lightweight and uses only free packages (Express + CORS).
- To deploy the mock to Firebase Functions (free tier), see `android-skeleton/functions/firebase_index.js` and `firebase.json` — you'll need to install the Firebase CLI and initialize the project with your Firebase account.

Next steps
- Add a Gradle wrapper for fully reproducible builds (I can add this automatically if you want).
- Replace the mock backend with a free Hugging Face demo or a deployed Firebase function for cloud-based AI responses.
