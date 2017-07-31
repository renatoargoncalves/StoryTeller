package br.com.realtest.storyteller.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Locale;

import br.com.realtest.storyteller.events.OnErrorSpeaking;
import br.com.realtest.storyteller.events.OnStartSpeaking;
import br.com.realtest.storyteller.events.OnStopSpeaking;
import br.com.realtest.storyteller.listeners.SpeechActivityDetected;

/**
 * Created by renato.rezende on 31/07/2017.
 */

public class MyApplication extends Application implements
        TextToSpeech.OnInitListener {

    private final static String TAG = "MyApplication";
    private static final String SP_KEY = "sharedStoryTeller";
    private Context context;
    private TextToSpeech tts;
    private HashMap<String, String> params = new HashMap<String, String>();
    private SpeechActivityDetected speechActivityDetected;
    private AudioManager audioManager;
    private float speechRate = 1;
    private int streamVolume = 0;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "onCreate()");
        try {
            sharedPreferences = getApplicationContext().getSharedPreferences(SP_KEY, MODE_PRIVATE);
            speechRate = sharedPreferences.getFloat("speechRate", 1);
            Log.i(TAG, "onCreate(): speechRate: " + sharedPreferences.getFloat("speechRate", 1));

            tts = new TextToSpeech(this, this);

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "inglesparaviagem");
            speechActivityDetected = new SpeechActivityDetected();
        } catch (Exception e) {
            Log.e(TAG, "onCreate()" + e.getMessage());
        }
    }

    @Override
    public void onInit(int status) {
        try {
            Log.i(TAG, "onInit()");
            if (status == TextToSpeech.SUCCESS) {
                tts.setSpeechRate(speechRate);

                if (isLanguageAvaialbe(tts.setLanguage(new Locale("pt", "POR")))) {
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String s) {
                            Log.i(TAG, "onStart()");
                            EventBus.getDefault().post(new OnStartSpeaking());
                        }

                        @Override
                        public void onDone(String s) {
                            Log.i(TAG, "onDone()");
                            EventBus.getDefault().post(new OnStopSpeaking());
                        }

                        @Override
                        public void onError(String s) {
                            Log.i(TAG, "onError()");
                            EventBus.getDefault().post(new OnErrorSpeaking());
                        }
                    });
                } else {
                    Toast.makeText(this, "Desculpe, o text-to-speech idioma Portugês-br não está disponível.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Há um problema com seu mecanismo text-to-speech.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "onInit: " + e.getMessage());
        }
    }

    public boolean isLanguageAvaialbe(int i) {
        if (i == TextToSpeech.LANG_MISSING_DATA || i == TextToSpeech.LANG_NOT_SUPPORTED)
            return false;
        else
            return true;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TextToSpeech getTts() {
        return tts;
    }

    public void setTts(TextToSpeech tts) {
        this.tts = tts;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public SpeechActivityDetected getSpeechActivityDetected() {
        return speechActivityDetected;
    }

    public void setSpeechActivityDetected(SpeechActivityDetected speechActivityDetected) {
        this.speechActivityDetected = speechActivityDetected;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public float getSpeechRate() {
        return speechRate;
    }

    public void setSpeechRate(float speechRate) {
        this.speechRate = speechRate;
    }

    public int getStreamVolume() {
        return streamVolume;
    }

    public void setStreamVolume(int streamVolume) {
        this.streamVolume = streamVolume;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
