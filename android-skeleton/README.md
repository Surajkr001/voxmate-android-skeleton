VoxMate Android Skeleton

- This folder contains a minimal Android app skeleton demonstrating:
- Push-to-talk using Android SpeechRecognizer
- Text-to-Speech playback
- A mock network call to a `/v1/chat` endpoint

It's intended as a starting point. Open `android-skeleton` in Android Studio to import the Gradle project.

What's included:
- `app/` - Android app module with `MainActivity.kt` and layout
- `functions/` - simple Express-based stub for `/v1/chat` (run locally or adapt to Cloud Functions)

Notes:
- This is a lightweight demo using only free libraries and can be extended to call Hugging Face / Replicate or your chosen AI endpoint.
