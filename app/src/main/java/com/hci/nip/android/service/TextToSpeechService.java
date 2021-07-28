package com.nus.hci.bladeheadpiece.android.service;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechService {
    private static final String TAG = TextToSpeechService.class.getName();
    private static final Locale TTS_LANGUAGE = Locale.UK;

    private static TextToSpeechService service = null;

    private TextToSpeech textToSpeech = null;
    private Bundle textToSpeechParams = null;

    private TextToSpeechService(Context context) {
        initTextToSpeech(context);
    }

    private void initTextToSpeech(Context context) {
        textToSpeechParams = new Bundle();
        textToSpeechParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 0.5f);
        textToSpeechParams.putBoolean(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, true);

        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Log.i(TAG, "[TTS] initialization success.");
                int ttsLang = textToSpeech.setLanguage(TTS_LANGUAGE);
                if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "[TTS] The Language is not supported!");
                }

                textToSpeech.setSpeechRate(0.9f);
                textToSpeech.setPitch(0.95f);
            } else {
                Log.e(TAG, "[TTS] Initialization failed!");
            }
        }, "com.google.android.tts");

        Log.d(TAG, "[TTS] Engine:" + textToSpeech.getDefaultEngine());
    }

    public static synchronized TextToSpeechService getInstance(Context applicationContext) {
        if (service == null) {
            service = new TextToSpeechService(applicationContext);
        }
        return service;
    }

    public synchronized void stopTextToSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            textToSpeechParams = null;
        }
        service = null;
    }

    public void speakText(String text) {
        Log.d(TAG, "[TTS] speakText:" + text);
        int speechStatus = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, textToSpeechParams, null);
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e(TAG, "[TTS] Error in converting Text to Speech!");
        }
    }

}
