package com.example.pirconevigate;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.service.autofill.UserData;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static com.example.pirconevigate.App.CHANEL_ID;

public class Recognition extends RecognitionService {

    public static final int MSG_VOICE_RECO_READY = 0;
    public static final int MSG_VOICE_RECO_END = 1;
    public static final int MSG_VOICE_RECO_RESTART = 2;

    // 여기서 완성된 a의 변수를 콧코로 액티비티로 넘길 것이다.
    public static String SpeakString = null;

    public static String speakready = null ;

    private SpeechRecognizer mSrRecognizer;
    boolean mBoolVoiceRecoStarted;
    protected AudioManager mAudioManager;
    Intent itIntent;//음성인식 Intent
    boolean end = false;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate 진입");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) { //시스템에서 음성인식 서비스 실행이 가능하다면
            itIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            itIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            itIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN.toString());
            itIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
            itIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500); // 목소리가 멈춘 후 대기시간
            //itIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
            itIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            startListening();
        }
    }

    private Handler mHdrVoiceRecoState = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            System.out.println("handleMessage 진입");
            switch (msg.what) {
                case MSG_VOICE_RECO_READY:
                    System.out.println("MSG_VOICE_RECO_READY 진입");
                    break;
                case MSG_VOICE_RECO_END: {
                    System.out.println("MSG_VOICE_RECO_END 진입");
                    stopListening();
                    sendEmptyMessageDelayed(MSG_VOICE_RECO_RESTART, 1000);
                    break;
                }
                case MSG_VOICE_RECO_RESTART:
                    System.out.println("리스타트 케이스로 오는지 다시 확인");
                    startListening();
                    break;
                default:
                    System.out.println("default 진입");
                    super.handleMessage(msg);
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand 진입");
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this
                , 0, notificationIntent, 0); //알람을 눌렀을 때 해당 엑티비티로

        Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle("Service")
                .setContentText("음성인식 온")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                //.setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setCategory(NotificationCompat.CATEGORY_CALL)
                .build();
        // 이부분에서 문제 발생함
        startForeground(0, notification);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy 진입");
        end = true;
        mSrRecognizer.destroy();
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_READY); //음성인식 서비스 다시 시작
        if (mAudioManager != null) {
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        }
    }


    @Override
    protected void onStartListening(Intent recognizerIntent, Callback listener) {
        System.out.println("onStartListening 진입");

    }


    public void startListening() {
        System.out.println("startListening 진입");
        if(!end){
            System.out.println("!end 진입");
            //음성인식을 시작하기 위해 Mute
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                System.out.println("Build.VERSION.SDK_INT >= Build.VERSION_CODES.M 진입");
                if (!mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {
                    System.out.println("!mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC 진입");
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,  AudioManager.ADJUST_MUTE,0);
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE,0);
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE,0);
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE,0);
                }
            } else {
                System.out.println("else 진입");
                //mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,  AudioManager.ADJUST_UNMUTE,0);
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE,0);
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE,0);
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE,0);
            }



            if (!mBoolVoiceRecoStarted) { // 최초의 실행이거나 인식이 종료된 후에 다시 인식을 시작하려 할 때
                System.out.println("!mBoolVoiceRecoStarted 진입");
                if (mSrRecognizer == null) {
                    System.out.println("mSrRecognizer == null 진입");
                    mSrRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    mSrRecognizer.setRecognitionListener(mClsRecoListener);
                }
                mSrRecognizer.startListening(itIntent);

            }

            //음성인식 도중 효과음 차단을 위한 작업
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,  AudioManager.ADJUST_MUTE,0);
            //mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE,0);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE,0);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE,0);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
            mBoolVoiceRecoStarted = true;  //음성인식 서비스 실행 중
        }
    }

    public void stopListening() //Override 함수가 아닌 한번만 호출되는 함수 음성인식이 중단될 때
    {
        System.out.println("stopListening 진입");
        try {
            if (mSrRecognizer != null && mBoolVoiceRecoStarted) {
                mSrRecognizer.stopListening(); //음성인식 Override 중단을 호출
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mBoolVoiceRecoStarted = false;  //음성인식 종료
    }


    @Override
    protected void onCancel(Callback listener) {
        System.out.println("onCancel 진입");
        mSrRecognizer.cancel();
    }

    @Override
    protected void onStopListening(Callback listener) { //음성인식 Override 함수의 종료부분
        System.out.println("onStopListening 진입");
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_RESTART); //음성인식 서비스 다시 시작
    }

    private RecognitionListener mClsRecoListener = new RecognitionListener() {
        @Override

        public void onRmsChanged(float rmsdB) {
            System.out.println("onRmsChanged 진입");
            speakready="ready";
        }



        @Override
        public void onResults(Bundle results) {

            System.out.println("onResults 진입");
            //Recognizer KEY를 사용하여 인식한 결과값을 가져오는 코드
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            final String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Log.d("key", Arrays.toString(rs));
            System.out.println(Arrays.toString(rs));
            SpeakString = Arrays.toString(rs);
            mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);


        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            System.out.println("onReadyForSpeech 진입");
        }

        @Override
        public void onEndOfSpeech() {
            System.out.println("onEndOfSpeech 진입");

        }

        @Override
        public void onError(int intError) {
            System.out.println("onError 진입");

            switch (intError) {

                case ERROR_NETWORK_TIMEOUT:
                    //네트워크 타임아웃
                    break;
                case ERROR_NETWORK:
                    break;

                case ERROR_AUDIO:
                    //녹음 에러
                    break;
                case ERROR_SERVER:
                    //서버에서 에러를 보냄
                    break;
                case ERROR_CLIENT:
                    //클라이언트 에러
                    break;
                case ERROR_SPEECH_TIMEOUT:
                    //아무 음성도 듣지 못했을 때
                    mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);
                    break;
                case ERROR_NO_MATCH:
                    //적당한 결과를 찾지 못했을 때
                    mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);

                    break;
                case ERROR_RECOGNIZER_BUSY:
                    //RecognitionService가 바쁠 때
                    break;
                case ERROR_INSUFFICIENT_PERMISSIONS:
                    //uses - permission(즉 RECORD_AUDIO) 이 없을 때
                    break;

            }
        }
        @Override
        public void onBeginningOfSpeech() {
            System.out.println("onBeginningOfSpeech 진입");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            System.out.println("onBufferReceived 진입");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            System.out.println("onEvent 진입");
        }

        @Override
        public void onPartialResults(Bundle partialResults) { //부분 인식을 성공 했을 때
            System.out.println("onPartialResults 진입");

        }
    };
}
