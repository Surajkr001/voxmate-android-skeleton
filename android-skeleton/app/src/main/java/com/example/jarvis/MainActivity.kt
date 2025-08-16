package com.voxmate.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer.createSpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import com.voxmate.app.model.Message

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var btnSpeak: Button
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var tts: TextToSpeech
    private val client = OkHttpClient()

    private lateinit var rvChat: RecyclerView
    private lateinit var adapter: ConversationAdapter
    private val messages = mutableListOf<Message>()

    private val prefsName = "voxmate_prefs"
    private val messagesKey = "messages_json"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // permission result handled here
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSpeak = findViewById(R.id.btnSpeak)
        rvChat = findViewById(R.id.rvChat)

        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        tts = TextToSpeech(this, this)

        adapter = ConversationAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter

        loadMessages()

        btnSpeak.setOnClickListener {
            startListening()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        }
    }

    private fun startListening() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = createSpeechRecognizer(this)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    addSystemMessage("Speech recognition error: $error")
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val transcript = matches?.firstOrNull() ?: ""
                    if (transcript.isNotBlank()) {
                        addUserMessage(transcript)
                        sendToServer(transcript)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer?.startListening(intent)
        } else {
            addSystemMessage("Speech recognition not available")
        }
    }

    private fun sendToServer(userInput: String) {
        val url = "http://10.0.2.2:5000/v1/chat" // emulator localhost mapping; use localhost when running server on host
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val json = JSONObject().apply {
            put("userId", "demo-user")
            put("sessionId", "demo-session")
            put("input", userInput)
        }

        val body = json.toString().toRequestBody(mediaType)
        val request = Request.Builder().url(url).post(body).build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = client.newCall(request).execute()
                val respBody = resp.body?.string() ?: ""
                val obj = JSONObject(respBody)
                val text = obj.optString("text", "(no response)")

                runOnUiThread {
                    addSystemMessage(text)
                    speak(text)
                }
            } catch (e: Exception) {
                runOnUiThread { addSystemMessage("Network error: ${e.message}") }
            }
        }
    }

    private fun speak(text: String) {
    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "voxmate_response")
    }

    private fun addUserMessage(text: String) {
        val m = Message(text, true)
        adapter.add(m)
        rvChat.scrollToPosition(adapter.itemCount - 1)
        saveMessages()
    }

    private fun addSystemMessage(text: String) {
        val m = Message(text, false)
        adapter.add(m)
        rvChat.scrollToPosition(adapter.itemCount - 1)
        saveMessages()
    }

    private fun saveMessages() {
        val arr = JSONArray()
        for (m in messages) {
            val o = JSONObject()
            o.put("text", m.text)
            o.put("isUser", m.isUser)
            arr.put(o)
        }
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit().putString(messagesKey, arr.toString()).apply()
    }

    private fun loadMessages() {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val raw = prefs.getString(messagesKey, null) ?: return
        try {
            val arr = JSONArray(raw)
            val list = mutableListOf<Message>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(Message(o.getString("text"), o.getBoolean("isUser")))
            }
            adapter.setAll(list)
        } catch (e: Exception) {
            // ignore parse errors
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        tts.shutdown()
    }
}
