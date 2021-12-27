package com.example.pirconevigate;

import static android.app.SearchManager.QUERY;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;




import java.util.Timer;
import java.util.TimerTask;

import static com.example.pirconevigate.Recognition.SpeakString;
import static com.example.pirconevigate.Recognition.speakready;

import com.example.pirconevigate.Constants;

import org.w3c.dom.Text;

public class MainKokkoroActivity extends AppCompatActivity implements View.OnClickListener {


    //fab 애니메이션 및 변수
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private SharedPreferences character_select;


    /// 이미지 버튼 입력
    ImageButton bt_internet;
    ImageButton bt_navi;
    ImageButton bt_bluetooth;
    ImageButton bt_musicplayer;
    ImageButton bt_geniemusic;
    ImageButton bt_youtube;
    ImageButton bt_twitch;
    ImageButton bt_dc;
    ImageButton img_face;

    // 세팅 버튼 입력
    ImageButton bt_setting;

    // 이미지 뷰 입력
    ImageView bt_emotion; // 감정 표현 이미지뷰

    ImageView img_background; // 시간에 따른 백그라운드 이미지뷰


    //시간에 따른 디지털 시계 이미지뷰
    ImageView img_month10;
    ImageView img_month0;

    ImageView img_day10;
    ImageView img_day0;

    //ImageView img_week; 이건 지금은 안쓰임 좀있다 풀고 요일 설정 하자.

    ImageView img_hour10;
    ImageView img_hour0;

    ImageView img_min10;
    ImageView img_min0;

    ImageView img_sec10;
    ImageView img_sec0;

    ImageView img_week;

    // 콧코로 음성인식 레디 상태 확인용 이미지뷰
    ImageView img_speak_ready;

    //콧코로 음성인식 중에 입 모양 움직이게 하기
    ImageView img_face_speak;

    //콧코로 상시 눈 깜빡임 모드 활성화
    ImageView img_face_blink;

    ImageView img_dark_back;
    ImageView img_dark_back_kokkoro;
    ImageView img_dark_back_kokkoro_face;
    ImageView img_dark_back_kokkoro_blink;
    ImageView img_dark_back_kokkoro_lip;
    ImageView img_dark_back_kokkoro_lip_mode;
    //착각이 있을 듯 싶어서 그러는데 대화상자 만드는 위치임
    ImageView img_dark_back_kokkoro_speak;
    ImageView img_dark_back_kokkoro_speak_text;

    //날씨 안내용
    ImageView weather_now;

    ImageView weather_kokkoro_speak_text;
    ImageView weather_kokkoro_speak_eye;
    ImageView weather_kokkoro_speak_mouth;

    ImageView weather_kokkoro;
    ImageView img_tempView;
    ImageView img_humView;
    ImageView img_weatherpanlel;
    ImageView img_stamp;

    TextView dateView;

    TextView tempView;

    TextView humView;

    TextView weatherText;

    static RequestQueue requestQueue;

    static RequestQueue iftttQueue;

    double currentLatitude;
    double currentLongitude;

    //여기까지

    TextView speaktext;

    MediaPlayer mediaPlayer;



    int year;
    int month;
    int date;

    int woy;
    int wom;

    int doy;
    int dom;
    int dow;

    int hour12;
    static int hour24 = 0;
    int minute;
    int second;

    int milliSecond;
    int timeZone;
    int lastDate;

    int character_int;

    final int PERMISSION = 1;

    // 콧코로 음성 인식 변수
    int kokkoro_voice = Constants.kokkoro_voice_off;
    int kokkoro_count = Constants.kokkoro_conunt_ready;

    // 콧코로 메뉴선택용
    int move_to_voice=Constants.move_to_voice_null;

    // sex 중복 참여 방지용 변수
    int sex_count = Constants.sex_off;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_kokkoro);

        // 날씨 퍼미션 활성화
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //위치 권한 확인

            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }



        // 액션바가 그래도 보인다면 강제로 추가 하도록 한다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 안드로이드 6.0버전 이상인지 체크해서 퍼미션 체크
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }


        // 콧코로 음성인식 준비 상태 확인

        img_speak_ready = (ImageView) findViewById(R.id.speakready);

        //콧코로 입모양 준비상태 확인
        img_face_speak =(ImageView)findViewById(R.id.img_speak);

        Glide.with(this).load(R.drawable.speak_ready_lip).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).into(img_face_speak);

        // 콧코로 눈 블링크 모드 활성화

        img_face_blink =(ImageView)findViewById(R.id.btxml_face_blink);

        Glide.with(this).load(R.drawable.kokkoro_face_blink).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(img_face_blink);

        // 콧코로 음성 인식 시 다른 콧코로가 나타나게 하는 모드
        img_dark_back = (ImageView)findViewById(R.id.dark_back);
        img_dark_back_kokkoro = (ImageView)findViewById(R.id.dark_back_kokkoro);
        img_dark_back_kokkoro_speak = (ImageView)findViewById(R.id.dark_back_speak);
        // 얜 원래 따로설정하는 시스템임
        img_dark_back_kokkoro_face = (ImageView)findViewById(R.id.dark_back_kokkoro_face);

        img_dark_back_kokkoro_blink = (ImageView)findViewById(R.id.dark_back_kokkoro_blink);
        Glide.with(this).load(R.drawable.speak_ready_blink).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(img_dark_back_kokkoro_blink);


        img_dark_back_kokkoro_lip = (ImageView)findViewById(R.id.dark_back_kokkoro_lip);
        Glide.with(this).load(R.drawable.speak_ready_lip)
                .apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).
                fitCenter().into(img_dark_back_kokkoro_lip);

        // 이것도 자막마다 따로 설정하기로 함.
        img_dark_back_kokkoro_lip_mode = (ImageView)findViewById(R.id.dark_back_kokkoro_lip_mode);

        //이건 자막마다 따로 설정하기로 함.
        img_dark_back_kokkoro_speak_text = (ImageView)findViewById(R.id.dark_back_speak_text);





        img_face_speak.setVisibility(View.GONE); // 화면에 안보이게 한다.

        // img id 연결
        img_face = (ImageButton) findViewById(R.id.btxml_face);

        //표정 연결
        bt_emotion = (ImageView) findViewById(R.id.btxml_emotion);


        // 시간에 따른 배경화면 바꾸는 변수 설정
        img_background = (ImageView) findViewById(R.id.character_background);

        //디지털 시계 이미지 변수 설정
        img_month10 = (ImageView) findViewById(R.id.time_month10);
        img_month0 = (ImageView) findViewById(R.id.time_month0);

        img_day10 = (ImageView) findViewById(R.id.time_day10);
        img_day0 = (ImageView) findViewById(R.id.time_day0);

        img_week = (ImageView) findViewById(R.id.font_week);

        img_hour10 = (ImageView) findViewById(R.id.time_hour10);
        img_hour0 = (ImageView) findViewById(R.id.time_hour0);

        img_min10 = (ImageView) findViewById(R.id.time_min10);
        img_min0 = (ImageView) findViewById(R.id.time_min0);

        img_sec10 = (ImageView) findViewById(R.id.time_sec10);
        img_sec0 = (ImageView) findViewById(R.id.time_sec0);

        //날씨용 이미지 연결

        dateView = findViewById(R.id.dateView);

        weather_kokkoro = findViewById(R.id.weather_kokkoro);

        // 날씨 gif 로딩
        weather_gif_create();

        tempView = findViewById(R.id.tx_tempView);

        img_tempView = findViewById(R.id.img_tempView);

        weatherText = findViewById(R.id.tx_weather);

        humView = findViewById(R.id.tx_humView);

        img_humView = findViewById(R.id.img_humView);

        img_weatherpanlel = findViewById(R.id.weatherpanlel);

        img_stamp = findViewById(R.id.kokkoro_stamp);


        // 현재 날씨 이미지
        weather_now = findViewById(R.id.img_weather_now);

        // 기상캐스터 콧코로 이미지 gone 처리
        weather_gone();

        /* 타이머가 과연 필요할지 점검중
        timer = new Timer(); //타이머로 소스를 반복 시키자 ㅇㅋ
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timerzone(); // 1초의 시간 표시를 위한 함수수
            }
        };

         */

        //눈깜빡임 시작
        img_face_blink.setVisibility(View.VISIBLE);

        welcome_this_voice();


        Thread timethread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    timerzone(); // 1초의 시간 표시를 위한 함수수
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timethread.start();


        // fab 애니매이션 입력
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        // fab floating 버튼 입력
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);



        // 인터넷 이미지 버튼 연결 및 클릭 명령
        bt_internet = (ImageButton) findViewById(R.id.btxml_internet);



        // 세팅 버튼 id 연결
        bt_setting = (ImageButton) findViewById(R.id.btxml_setting);

        //스피크 결과 텍스트 출력 id 연결
        speaktext = (TextView) findViewById(R.id.tx_speaktext);

        //핸들러로 데이터 알아듣게 하기.
        final Handler speakhandler = new Handler(Looper.getMainLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            speakhandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //콧코로 음성인식 준비 상태 확인
                    if(speakready=="ready"){
                        img_speak_ready.setImageResource(R.drawable.speak_ready);
                    }

                    //.speaktext.setText(SpeakString);
                    speakfiltering();
                    speakhandler.postDelayed(this, 1000);
                }
            }, 0, 1000);
        }


        //버튼 클릭 리스너 입력
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);

        //SharedPreferences 데이터 저장
        character_select = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = character_select.edit();
        editor.putInt("ActiveID", 0);
        editor.commit();


        // 터치를 이용해야 눌렀을때 이미지가 변경되는 작업을 수월하게 진행 할 수 있다.
        // 히든 버튼
        img_face.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_shy);
                    bt_emotion.setImageResource(R.drawable.char_emotion_shy);
                    Toast.makeText(MainKokkoroActivity.this, "어딜만져 페도새끼", Toast.LENGTH_SHORT).show();

                }

                //ACTION_UP = 버튼을 누르다
                else if (action == MotionEvent.ACTION_UP) {
                    // 액티비티 이동 버튼
                }
                return false;
            }
        });


        // 인터넷 이미지 버튼 연결 및 클릭 명령
        bt_internet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_internet);
                    bt_emotion.setImageResource(R.drawable.char_emotion_internet);
                    Toast.makeText(MainKokkoroActivity.this, "인터넷 버튼 선택", Toast.LENGTH_SHORT).show();

                }

                //ACTION_UP = 버튼을 누르다
                else if (action == MotionEvent.ACTION_UP) {
                    // 액티비티 이동 버튼

                    Intent app_internet = getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_internet);
                    finish();
                }
                return false;
            }
        });

        // 네비게이션 이미지 버튼 연결 및 클릭 명령
        bt_navi = (ImageButton) findViewById(R.id.btxml_navi);
        bt_navi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_navi);
                    bt_emotion.setImageResource(R.drawable.char_emotion_navi);
                    Toast.makeText(MainKokkoroActivity.this, "네비게이션 버튼 선택", Toast.LENGTH_SHORT).show();

                }

                //ACTION_UP = 버튼을 누르다
                else if (action == MotionEvent.ACTION_UP) {
                    // 액티비티 이동 버튼

                    Intent app_navi = getPackageManager().getLaunchIntentForPackage("com.skt.tmap.ku");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_navi);
                    finish();
                }
                return false;
            }
        });

        // 블루투스 이미지 버튼 연결 및 클릭 명령
        bt_bluetooth = (ImageButton) findViewById(R.id.btxml_bluetooth);
        bt_bluetooth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_bt);
                    bt_emotion.setImageResource(R.drawable.char_emotion_bluetooth);
                    Toast.makeText(MainKokkoroActivity.this, "블루투스 버튼 선택", Toast.LENGTH_SHORT).show();

                } else if (action == MotionEvent.ACTION_UP) {
                    Intent bluetoothmode = new Intent();
                    bluetoothmode.setComponent(new ComponentName("com.ts.MainUI", "com.ts.bt.BtConnectActivity"));
                    startActivity(bluetoothmode);
                    finish();
                }
                return false;

            }


        });

        // 뮤직 플레이어 이미지 버튼 연결 및 클릭 명령
        bt_musicplayer = (ImageButton) findViewById(R.id.btxml_player);
        bt_musicplayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_music);
                    bt_emotion.setImageResource(R.drawable.char_emotion_music);
                    Toast.makeText(MainKokkoroActivity.this, "뮤직플레이어 버튼 선택", Toast.LENGTH_SHORT).show();

                } else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.massivcode.androidmusicplayer");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 지니뮤직 이미지 버튼 연결 및 클릭 명령
        bt_geniemusic = (ImageButton) findViewById(R.id.btxml_genie);
        bt_geniemusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_music);
                    bt_emotion.setImageResource(R.drawable.char_emotion_music);
                    Toast.makeText(MainKokkoroActivity.this, "지니뮤직 버튼 선택", Toast.LENGTH_SHORT).show();

                } else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.ktmusic.geniemusic");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 디씨 이미지 버튼 연결 및 클릭 명령
        bt_dc = (ImageButton) findViewById(R.id.btxml_dc);
        bt_dc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_dc);   // 디씨용 이미지 수정 필요
                    bt_emotion.setImageResource(R.drawable.char_emotion_noooo); // 디씨용 이미지 수정 필요
                    Toast.makeText(MainKokkoroActivity.this, "디씨 버튼 선택", Toast.LENGTH_SHORT).show();

                } else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.dcinside.app");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 유튜브 이미지 버튼 연결 및 클릭 명령
        bt_youtube = (ImageButton) findViewById(R.id.btxml_youtube);
        bt_youtube.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_youtube);   // 유튜브용 이미지 수정 필요
                    Toast.makeText(MainKokkoroActivity.this, "유튜브 버튼 선택", Toast.LENGTH_SHORT).show();

                } else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 트위치 이미지 버튼 연결 및 클릭 명령
        bt_twitch = (ImageButton) findViewById(R.id.btxml_twitch);
        bt_twitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    img_face.setImageResource(R.drawable.char_kokkoro_youtube);   // 트위치용 이미지 수정 필요
                    Toast.makeText(MainKokkoroActivity.this, "트위치 버튼 선택", Toast.LENGTH_SHORT).show();

                } else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("tv.twitch.android.app");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 세팅 이미지 버튼 연결 및 클릭 명령
        bt_setting = (ImageButton) findViewById(R.id.btxml_setting);
        bt_setting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(MainKokkoroActivity.this, "세팅 버튼 선택", Toast.LENGTH_SHORT).show();
                } else if (action == MotionEvent.ACTION_UP) {
                    Intent app_setting = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_setting);
                    finish();
                }
                return false;

            }
        });


    }


    // 마감시 음악 반복재생 버그가 생길 수 있으므로 디스트로이 한다.
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
    }


    // fab 버튼 클릭시 동작 하는 명령어
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                Toast.makeText(this, "메인에 표시될 캐릭터를 선택해주세요", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab1:
                anim();
                Toast.makeText(this, "콧코로 로 선택 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent char_kokkoro = new Intent(MainKokkoroActivity.this, MainKokkoroActivity.class);
                char_kokkoro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_kokkoro);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.fab2:
                anim();
                Toast.makeText(this, "캬루 로 선택 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent char_karyl = new Intent(MainKokkoroActivity.this, MainKarylActivity.class);
                char_karyl.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_karyl);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.fab3:
                anim();
                Toast.makeText(this, "페코린느 로 선택 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent char_peco = new Intent(MainKokkoroActivity.this, MainPecoActivity.class);
                char_peco.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_peco);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.fab4:
                anim();
                Toast.makeText(this, "3성으로 변신!", Toast.LENGTH_SHORT).show();
                Intent char_3tar = new Intent(MainKokkoroActivity.this, Kokkoro3starActivity.class);
                char_3tar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_3tar);
                finish();
                overridePendingTransition(0, 0);
                break;

        }

    }

    public void speakfiltering() {
        try {
            if (kokkoro_voice == Constants.kokkoro_voice_off) {

                if ((SpeakString.contains("코코로") || SpeakString.contains("콧코로")) || SpeakString.contains("안녕")) {
                    what_this_voice();
                }
            }

            else if (kokkoro_voice == Constants.kokkoro_voice_on && kokkoro_count < Constants.kokkoro_conunt_max) {

                if (SpeakString.contains("유튜브") &&! (SpeakString.contains("검색"))) {
                    move_to_voice=Constants.move_to_voice_youtube;
                }

                else if (SpeakString.contains("검색")) {
                    move_to_voice=Constants.move_to_voice_search;
                }

                else if(SpeakString.contains("유튜브") && (SpeakString.contains("검색"))) {
                    move_to_voice=Constants.move_to_voice_youtube_search;
                }

                else if (SpeakString.contains("블루투스")) {
                    move_to_voice=Constants.move_to_voice_bluetooth;
                }

                else if (SpeakString.contains("네비") || SpeakString.contains("내비")) {
                    move_to_voice=Constants.move_to_voice_navi;
                }

                else if (SpeakString.contains("섹스")) {
                    move_to_voice=Constants.move_to_voice_sex;
                }

                else if (SpeakString.contains("아니야") ||SpeakString.contains("아냐") || SpeakString.contains("취소")) {
                    move_to_voice = Constants.not_to_move;
                }

                else if (SpeakString.contains("운전") || SpeakString.contains("주행")) {
                    move_to_voice = Constants.move_to_voice_ride;
                }

                else if (SpeakString.contains("날씨")) {
                    move_to_voice = Constants.move_to_voice_weather;
                }

                else if (SpeakString.contains("난방")){

                }

                else if (SpeakString.contains("거실") && SpeakString.contains("켜")){
                    move_to_voice = Constants.move_to_living_room_on;
                }

                else if (SpeakString.contains("거실") && SpeakString.contains("꺼")){
                    move_to_voice = Constants.move_to_living_room_off;
                }

                else if (SpeakString.contains("서큘") && SpeakString.contains("켜")){
                    move_to_voice = Constants.move_to_air_circulator_on;
                }

                else if (SpeakString.contains("서큘") && SpeakString.contains("꺼")){
                    move_to_voice = Constants.move_to_air_circulator_off;
                }

                else if (SpeakString.contains("보일러")){
                    move_to_voice = Constants.move_to_livingroom_temp;
                }



                //아무것도 아닌 경우
                else {
                    kokkoro_count++;
                }

                if(move_to_voice != Constants.move_to_voice_null){
                    move_to_voice_menu();
                }
                else{}
            }

            // 응답 후 10초가 넘었을 경우
            else if (kokkoro_voice == Constants.kokkoro_voice_on && kokkoro_count >= Constants.kokkoro_conunt_max) {
                //else if (kokkoro_voice == Constants.kokkoro_voice_on && kokkoro_count >= 5) { 원래 이거였음 왜 이렇게 짰는지 기억은 안남.

                move_to_voice = Constants.not_to_move;
                move_to_voice_menu();

            }


            // 음성 수신 데이터가 아예 비어버렸을경우 대비용
        } catch (NullPointerException e) {
            System.out.println("지금은 음성 수신 데이터가 비었습니다.");

            if (kokkoro_voice == Constants.kokkoro_voice_on && kokkoro_count < Constants.kokkoro_conunt_max) {
                kokkoro_count++;
            } else if (kokkoro_voice == Constants.kokkoro_voice_on && kokkoro_count >= Constants.kokkoro_conunt_max) {
                move_to_voice = Constants.not_to_move;
                move_to_voice_menu();
            }

        }

    }

    public void timerzone() {
        // 날짜 기능 구현
        Calendar today = Calendar.getInstance();

        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        date = today.get(Calendar.DATE);

        woy = today.get(Calendar.WEEK_OF_YEAR);
        wom = today.get(Calendar.WEEK_OF_MONTH);

        doy = today.get(Calendar.DAY_OF_YEAR);
        dom = today.get(Calendar.DAY_OF_MONTH);
        dow = today.get(Calendar.DAY_OF_WEEK);

        hour12 = today.get(Calendar.HOUR);
        hour24 = today.get(Calendar.HOUR_OF_DAY);
        minute = today.get(Calendar.MINUTE);
        second = today.get(Calendar.SECOND);

        milliSecond = today.get(Calendar.MILLISECOND);
        timeZone = today.get(Calendar.ZONE_OFFSET);
        lastDate = today.getActualMaximum(Calendar.DATE);

        /* 날짜 검증용
        System.out.println("오늘은 " + year + "년 " + month + 1 + "월" + date + "일");
        System.out.println("오늘은 올해의 " + woy + "째주, 이번달의 " + wom + "째주. " + date + "일");
        System.out.println("오늘은 이번 해의 " + doy + "일이자, 이번 달의 " + dom + "일. 요일은 " + dow + "일 (1:일요일)");
        System.out.println("현재 시각은 " + hour12 + ":" + minute + ":" + second + ", 24시간으로 표현하면 " + hour24 + ":" + minute + ":" + second);
        System.out.println("오늘은 " + year + "년 " + month + 1 + "월" + date + "일");
        System.out.println("1000분의 1초 (0~999): " + milliSecond);
        System.out.println("timeZone (-12~+12): " + timeZone / (60 * 60 * 1000)); // 1000분의 1초를 시간으로 표시하기 위해 60*60*1000
        System.out.println("이 달의 마지막 날: " + lastDate);

         */

        // 시간에 따른 (밤 낮 저녁) 이미지 변경 소스
        if (hour24 >= 20 || hour24 < 6) {
            img_background.setImageResource(R.drawable.bg_500512); //밤
            //System.out.println("지금 밤이래");
        } else if (hour24 >= 6 && hour24 < 16) {
            img_background.setImageResource(R.drawable.bg_500510); //아침
            //System.out.println("지금 아침이래");
        } else {
            img_background.setImageResource(R.drawable.bg_500511); //저녁
            //System.out.println("지금 저녁이래");
        }


        // 시간 변경에 따른 디지털 시계 숫자 변경용 코드

        //달 (10자리)
        if (month >= 9)
            img_month0.setImageResource(R.drawable.font_1);
        else
            img_month0.setImageResource(R.drawable.font_0);

        //달 (1자리)
        switch (month % 10) {
            case 0:
                img_month0.setImageResource(R.drawable.font_1);
                break;
            case 1:
                img_month0.setImageResource(R.drawable.font_2);
                break;
            case 2:
                img_month0.setImageResource(R.drawable.font_3);
                break;
            case 3:
                img_month0.setImageResource(R.drawable.font_4);
                break;
            case 4:
                img_month0.setImageResource(R.drawable.font_5);
                break;
            case 5:
                img_month0.setImageResource(R.drawable.font_6);
                break;
            case 6:
                img_month0.setImageResource(R.drawable.font_7);
                break;
            case 7:
                img_month0.setImageResource(R.drawable.font_8);
                break;
            case 8:
                img_month0.setImageResource(R.drawable.font_9);
                break;
            case 9:
                img_month0.setImageResource(R.drawable.font_0);
                break;
        }

        //일 (10자리)
        switch (date / 10) {
            case 0:
                img_day10.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_day10.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_day10.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_day10.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_day10.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_day10.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_day10.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_day10.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_day10.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_day10.setImageResource(R.drawable.font_9);
                break;
        }

        //일 (1자리)
        switch (date % 10) {
            case 0:
                img_day0.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_day0.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_day0.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_day0.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_day0.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_day0.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_day0.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_day0.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_day0.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_day0.setImageResource(R.drawable.font_9);
                break;
        }


        //요일
        switch (dow % 10) {
            case 1:
                img_week.setImageResource(R.drawable.font_week);
                break;
            case 2:
                img_week.setImageResource(R.drawable.week_mon);
                break;
            case 3:
                img_week.setImageResource(R.drawable.week_tues);
                break;
            case 4:
                img_week.setImageResource(R.drawable.week_wednes);
                break;
            case 5:
                img_week.setImageResource(R.drawable.week_thurs);
                break;
            case 6:
                img_week.setImageResource(R.drawable.week_fri);
                break;
            case 7:
                img_week.setImageResource(R.drawable.week_satur);
                break;
        }


        //시간 (10자리)
        switch (hour24 / 10) {
            case 0:
                img_hour10.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_hour10.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_hour10.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_hour10.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_hour10.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_hour10.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_hour10.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_hour10.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_hour10.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_hour10.setImageResource(R.drawable.font_9);
                break;
        }

        //시간 (1자리)
        switch (hour24 % 10) {
            case 0:
                img_hour0.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_hour0.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_hour0.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_hour0.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_hour0.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_hour0.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_hour0.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_hour0.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_hour0.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_hour0.setImageResource(R.drawable.font_9);
                break;
        }

        //분 (10자리)
        switch (minute / 10) {
            case 0:
                img_min10.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_min10.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_min10.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_min10.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_min10.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_min10.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_min10.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_min10.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_min10.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_min10.setImageResource(R.drawable.font_9);
                break;
        }

        //분 (1자리)
        switch (minute % 10) {
            case 0:
                img_min0.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_min0.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_min0.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_min0.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_min0.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_min0.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_min0.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_min0.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_min0.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_min0.setImageResource(R.drawable.font_9);
                break;
        }

        //초 (10자리)
        switch (second / 10) {
            case 0:
                img_sec10.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_sec10.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_sec10.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_sec10.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_sec10.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_sec10.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_sec10.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_sec10.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_sec10.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_sec10.setImageResource(R.drawable.font_9);
                break;
        }

        //초 (1자리)
        switch (second % 10) {
            case 0:
                img_sec0.setImageResource(R.drawable.font_0);
                break;
            case 1:
                img_sec0.setImageResource(R.drawable.font_1);
                break;
            case 2:
                img_sec0.setImageResource(R.drawable.font_2);
                break;
            case 3:
                img_sec0.setImageResource(R.drawable.font_3);
                break;
            case 4:
                img_sec0.setImageResource(R.drawable.font_4);
                break;
            case 5:
                img_sec0.setImageResource(R.drawable.font_5);
                break;
            case 6:
                img_sec0.setImageResource(R.drawable.font_6);
                break;
            case 7:
                img_sec0.setImageResource(R.drawable.font_7);
                break;
            case 8:
                img_sec0.setImageResource(R.drawable.font_8);
                break;
            case 9:
                img_sec0.setImageResource(R.drawable.font_9);
                break;
        }

    }
    public void what_face_visible(){
        // 전체 화면을 응답하는 콧코로로 변경하는 소스
        img_dark_back.setVisibility(View.VISIBLE);
        img_dark_back_kokkoro.setVisibility(View.VISIBLE);
        img_dark_back_kokkoro_blink.setVisibility(View.VISIBLE);
        img_dark_back_kokkoro_lip.setVisibility(View.VISIBLE);
        img_dark_back_kokkoro_face.setImageResource(R.drawable.char_kokkoro_princess_surprised);
        img_dark_back_kokkoro_face.setVisibility(View.VISIBLE);
        img_dark_back_kokkoro_speak.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.speak_test).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(img_dark_back_kokkoro_speak_text);
        img_dark_back_kokkoro_speak_text.setVisibility(View.VISIBLE);
    }

    public void end_face_visible() {
        Glide.with(this).load(R.drawable.speak_end).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(img_dark_back_kokkoro_speak_text);
        img_dark_back_kokkoro_lip_mode.setImageResource(R.drawable.kokkoro_princess_happy_lip);
        img_dark_back_kokkoro_face.setImageResource(R.drawable.char_kokkoro_princess_smile);
    }

    public void error_face_visual(){
        //자막
        Glide.with(this).load(R.drawable.speak_error).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(img_dark_back_kokkoro_speak_text);
        //입술
        img_dark_back_kokkoro_lip_mode.setImageResource(R.drawable.char_kokkoro_princess_lip_sad);
        //표정
        img_dark_back_kokkoro_face.setImageResource(R.drawable.char_kokkoro_princess_sad);

    }

    public void sex_face_visible(){
        //여기에 새로운 자막이 들어갈 예정입니다.
        //그리고 스위치에 들어간 모든 경우 전부 이미지 따로만들어야함.
        img_dark_back_kokkoro_speak_text.setVisibility(View.VISIBLE);
        img_dark_back_kokkoro_face.setImageResource(R.drawable.char_kokkoro_princess_shy);
    }


    public void all_face_invisible(){
        // 전체 화면을 응답하는 콧코로로 변경하는 소스
        img_dark_back.setVisibility(View.GONE);
        img_dark_back_kokkoro.setVisibility(View.GONE);
        img_dark_back_kokkoro_blink.setVisibility(View.GONE);
        img_dark_back_kokkoro_face.setVisibility(View.GONE);
        img_dark_back_kokkoro_speak.setVisibility(View.GONE);
        img_dark_back_kokkoro_speak_text.setVisibility(View.GONE);
        img_dark_back_kokkoro_lip.setVisibility(View.GONE);
    }

    public void welcome_this_voice(){
        // 처음 접속했을때 보이스
        mediaPlayer = MediaPlayer.create(MainKokkoroActivity.this, R.raw.welcome_voice);
        mediaPlayer.start();
        mediaPlayer.setLooping(false);
        img_face.setImageResource(R.drawable.char_kokkoro_shy); // 첫번째 응답 받았을 경우
        bt_emotion.setImageResource(R.drawable.char_emotion_shy);
        img_face_speak.setVisibility(View.VISIBLE); // 화면에 보이게 한다.

        //안녕 인사 종료 후에 음성인식 서비스 실행하도록 한다.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                img_face_speak.setVisibility(View.GONE);
                img_face.setVisibility(View.GONE);
                bt_emotion.setVisibility(View.GONE);
                startService(new Intent(MainKokkoroActivity.this,Recognition.class)); // 음성호출 서비스 온
            }
        });
    }


    public void what_this_voice(){
        stopService(new Intent(MainKokkoroActivity.this,Recognition.class)); // 음성호출 서비스 온

        SpeakString = null;
        System.out.println("음악 재생");

        //이거 변경 요청
        bt_emotion.setImageResource(R.drawable.char_emotion_question);

        mediaPlayer = MediaPlayer.create(MainKokkoroActivity.this, R.raw.what_this_voice);
        mediaPlayer.start();
        mediaPlayer.setLooping(false);

        what_face_visible();


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                startService(new Intent(MainKokkoroActivity.this,Recognition.class)); // 음성호출 서비스 온
                img_dark_back_kokkoro_lip.setVisibility(View.GONE); // 화면에 안보이게 한다.
            }

        });
        //보이스 입장 신호
        kokkoro_voice = Constants.kokkoro_voice_on;



    }

    public void move_to_voice_menu(){
        stopService(new Intent(MainKokkoroActivity.this,Recognition.class)); // 음성호출 서비스 온

        // 말한 단어 지우기랑 다시 검색단어로 돌아가게 하는 변수
        if((move_to_voice != Constants.move_to_voice_search) && (move_to_voice != Constants.move_to_voice_youtube_search)) {
            SpeakString = null;
        }
        kokkoro_voice = Constants.kokkoro_voice_off;
        kokkoro_count = Constants.kokkoro_conunt_ready;

        Glide.with(this).load(R.drawable.speak_ready_lip)
                .apply(new RequestOptions()
                        .signature(new ObjectKey("signature string"))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                ).
                fitCenter().into(img_dark_back_kokkoro_lip);

        img_dark_back_kokkoro_lip.setVisibility(View.VISIBLE);

        bt_emotion.setImageResource(R.drawable.char_emotion_answer); // 이모티콘도 변경해야함


        // 응답 완료 보이스 설정
        switch(move_to_voice){
            case Constants.move_to_voice_sex:
                if(sex_count==Constants.sex_off) {

                    sex_count = Constants.sex_on;
                    mediaPlayer = MediaPlayer.create(MainKokkoroActivity.this, R.raw.i_kill_you);
                    mediaPlayer.start();
                    mediaPlayer.setLooping(false);

                    Glide.with(this).load(R.drawable.sex)
                            .apply(new RequestOptions()
                            .signature(new ObjectKey("signature string"))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                    ).fitCenter().into(img_dark_back_kokkoro_speak_text);

                    img_dark_back_kokkoro_face.setImageResource(R.drawable.char_kokkoro_princess_angry);
                    img_dark_back_kokkoro_lip_mode.setImageResource(R.drawable.char_kokkoro_princess_lip_angry);

                }
                break;

            case Constants.not_to_move:
                mediaPlayer = MediaPlayer.create(MainKokkoroActivity.this, R.raw.error_voice);
                break;

            default:
                mediaPlayer = MediaPlayer.create(MainKokkoroActivity.this, R.raw.end_voice);
                break;
        }


        //음악 재생
        if(move_to_voice!=Constants.move_to_voice_sex) {
            mediaPlayer.start();
            mediaPlayer.setLooping(false);
        }



        //에러났을때랑 일반모드일때 비주얼 차이를 나타내기 위한 작업
        switch(move_to_voice) {

            case Constants.not_to_move :
                error_face_visual();
                break;

            case Constants.move_to_voice_null:
                error_face_visual();
                break;

            case Constants.move_to_voice_sex:
                break;


            default:
                end_face_visible();
                break;
        }

        // 이 부분은 음성인식으로 iot를 구동하기 위해 만들었다
        if(move_to_voice >= Constants.move_to_living_room_on && move_to_voice <= Constants.move_to_livingroom_temp)
        {

            iftttmode();

        }

        //여기에 음악 재생 후에 동작하도록 수정해야한다.

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                if(move_to_voice == Constants.move_to_voice_sex) {
                    System.exit(0);
                }
                else
                voice_end_to_start();

            }
        });




    }

    public void voice_end_to_start(){
        //음악이 종료 된 후에 특정 동작을 실행 시키도록 지시한다.

                all_face_invisible();
                startService(new Intent(MainKokkoroActivity.this,Recognition.class)); // 음성호출 서비스 온

                switch(move_to_voice){
                    case Constants.move_to_voice_youtube :
                        Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                        // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                        startActivity(app_musicplayer);
                        //System.exit(0);
                        break;

                    case Constants.move_to_voice_search:
                        Intent websearch = new Intent(Intent.ACTION_WEB_SEARCH);
                        websearch.putExtra(QUERY, SpeakString);
                        startActivity(websearch);
                        SpeakString = null;
                        //System.exit(0);
                        break;

                    case Constants.move_to_voice_youtube_search:
                        Intent youtubesearch = new Intent(Intent.ACTION_SEARCH);
                        youtubesearch.setPackage("com.google.android.youtube");
                        // 음성으로 받아온 데이터 이용
                        youtubesearch.putExtra(QUERY, SpeakString);
                        SpeakString = null;
                        try {
                            startActivity(youtubesearch);
                        }catch(ActivityNotFoundException e){}

                        //System.exit(0);
                        break;

                    case Constants.move_to_voice_bluetooth:
                        Intent bluetoothmode = new Intent();
                        bluetoothmode.setComponent(new ComponentName("com.ts.MainUI", "com.ts.bt.BtConnectActivity"));
                        startActivity(bluetoothmode);
                        //System.exit(0);
                        break;

                    case Constants.move_to_voice_navi:
                        Intent app_navi = getPackageManager().getLaunchIntentForPackage("com.skt.tmap.ku");
                        // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                        startActivity(app_navi);
                        //System.exit(0);
                        break;

                    case Constants.move_to_voice_ride:
                        Intent app_navi2 = getPackageManager().getLaunchIntentForPackage("com.skt.tmap.ku");
                        Intent bluetoothmode2 = new Intent();
                        bluetoothmode2.setComponent(new ComponentName("com.ts.MainUI", "com.ts.bt.BtMusicActivity"));
                        startActivity(bluetoothmode2);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        startActivity(app_navi2);
                        System.exit(0);
                        break;

                    case Constants.move_to_voice_weather :
                        weather_on();

                    }

                move_to_voice=Constants.move_to_voice_null;


    }

    //날씨 이미지 전부 제거
    public void weather_gone() {
        weather_kokkoro_speak_text.setVisibility(View.GONE);
        weather_kokkoro_speak_eye.setVisibility(View.GONE);
        weather_kokkoro_speak_mouth.setVisibility(View.GONE);
        weather_kokkoro.setVisibility(View.GONE);
        img_tempView.setVisibility(View.GONE);
        img_humView.setVisibility(View.GONE);
        weather_now.setVisibility(View.GONE);
        img_weatherpanlel.setVisibility(View.GONE);
        img_stamp.setVisibility(View.GONE);
        dateView.setVisibility(View.GONE);
        tempView.setVisibility(View.GONE);
        humView.setVisibility(View.GONE);
        weatherText.setVisibility(View.GONE);
    }

    public void weather_visible(){
        weather_kokkoro_speak_text.setVisibility(View.VISIBLE);
        weather_kokkoro_speak_eye.setVisibility(View.VISIBLE);
        weather_kokkoro_speak_mouth.setVisibility(View.VISIBLE);
        weather_kokkoro.setVisibility(View.VISIBLE);
        img_tempView.setVisibility(View.VISIBLE);
        img_humView.setVisibility(View.VISIBLE);
        weather_now.setVisibility(View.VISIBLE);
        img_weatherpanlel.setVisibility(View.VISIBLE);
        img_stamp.setVisibility(View.VISIBLE);
        dateView.setVisibility(View.VISIBLE);
        tempView.setVisibility(View.VISIBLE);
        humView.setVisibility(View.VISIBLE);
        weatherText.setVisibility(View.VISIBLE);
    }

    public void weather_on(){
        //모든 날씨 다 표현 용
        weather_visible();

        // 드디어 추가되는 미디어 플레이어
        mediaPlayer = MediaPlayer.create(MainKokkoroActivity.this, R.raw.weather_kokkoro_voice);

        // 재생 시작
        mediaPlayer.start();
        mediaPlayer.setLooping(false);

        GpsTracker gpsTracker = new GpsTracker(MainKokkoroActivity.this);

        currentLatitude = gpsTracker.getLatitude();
        currentLongitude = gpsTracker.getLongitude();

        System.out.println("위도 : "+currentLatitude);
        System.out.println("경도 : "+currentLongitude);


        //volley를 쓸 때 큐가 비어있으면 새로운 큐 생성하기
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }


        CurrentCall();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                weather_gone();
            }

        });

    }



    private void CurrentCall(){

        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+currentLatitude+"&lon="+currentLongitude+"&appid={API}&lang=kr";

        System.out.println(url);


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {

                try {

                    //api로 받은 파일 jsonobject로 새로운 객체 선언
                    JSONObject jsonObject = new JSONObject(response);


                    //도시 키값 받기
                    String city = jsonObject.getString("name");



                    //System의 현재 시간(년,월,일,시,분,초까지)가져오고 Date로 객체화함
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);

                    //년, 월, 일 형식으로. 시,분,초 형식으로 객체화하여 String에 형식대로 넣음
                    SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy년 MM월 dd일");
                    String getDay = simpleDateFormatDay.format(date);

                    //getDate에 개행을 포함한 형식들을 넣은 후 dateView에 text설정
                    String getDate = getDay + " " + city + "의 날씨는?";

                    dateView.setText(getDate);


                    //날씨 키값 받기
                    JSONArray weatherJson = jsonObject.getJSONArray("weather");
                    JSONObject weatherObj = weatherJson.getJSONObject(0);

                    //weather 변수는 날씨를 받아 오도록 설정 되어 있음.
                    String weather = weatherObj.getString("main");

                    weatherText.setText(weather);


                    // 날씨 이미지 기준점 조사

                    if(weather.contains("Thunderstorm")) {
                        weather_now.setImageResource(R.drawable.weather_thunder);
                        weatherText.setTextColor(Color.parseColor("#ea788e"));
                    }
                    else if(weather.contains("Drizzle")) {
                        weather_now.setImageResource(R.drawable.weather_drizzly);
                        weatherText.setTextColor(Color.parseColor("#8d4173"));
                    }
                    else if(weather.contains("Rain")) {
                        weather_now.setImageResource(R.drawable.weather_rainy);
                        weatherText.setTextColor(Color.parseColor("#3463d7"));
                    }
                    else if(weather.contains("Snow")) {
                        weather_now.setImageResource(R.drawable.weather_snow);
                        weatherText.setTextColor(Color.parseColor("#ff447c"));
                    }
                    else if(weather.contains("Mist") || weather.contains("Smoke") || weather.contains("Haze") || weather.contains("Dust") || weather.contains("Fog") || weather.contains("Sand") || weather.contains("Ash") || weather.contains("Squall") || weather.contains("Tornado")) {
                        weather_now.setImageResource(R.drawable.weather_mist);
                        weatherText.setTextColor(Color.parseColor("#8bb96d"));
                    }
                    else if(weather.contains("Clear")) {
                        weather_now.setImageResource(R.drawable.weather_sunny);
                        weatherText.setTextColor(Color.parseColor("#ff7243"));
                    }
                    else if(weather.contains("Clouds")) {
                        weather_now.setImageResource(R.drawable.weather_cloudy);
                        weatherText.setTextColor(Color.parseColor("#3260da"));
                    }
                    // 현재 날씨 표시 방식
                    weather_now.setVisibility(View.VISIBLE);

                    //기온 키값 받기
                    JSONObject tempK = new JSONObject(jsonObject.getString("main"));

                    //기온 받고 켈빈 온도를 섭씨 온도로 변경
                    double tempDo = (Math.round((tempK.getDouble("temp")-273.15)*100)/100.0);
                    tempView.setText(tempDo +  "°C");

                    //기온 받고 켈빈 온도를 섭씨 온도로 변경
                    double humDo = (Math.round((tempK.getDouble("humidity"))*1)/1);
                    humView.setText(humDo +  "%");








                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void weather_gif_create(){
        weather_kokkoro_speak_text = (ImageView)findViewById(R.id.weather_kokkoro_speak);
        Glide.with(this).load(R.drawable.weather_speak).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(weather_kokkoro_speak_text);


        weather_kokkoro_speak_eye = (ImageView)findViewById(R.id.weather_kokkoro_eye);
        Glide.with(this).load(R.drawable.weather_kokkoro_blink).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(weather_kokkoro_speak_eye);

        weather_kokkoro_speak_mouth = (ImageView)findViewById(R.id.weather_kokkoro_mouth);
        Glide.with(this).load(R.drawable.weather_kokkoro_speak_mouth).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).apply(new RequestOptions()
                .signature(new ObjectKey("signature string"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        ).fitCenter().into(weather_kokkoro_speak_mouth);
    }

    public void iftttmode(){


        //volley를 쓸 때 큐가 비어있으면 새로운 큐 생성하기
        if(iftttQueue == null){
            iftttQueue = Volley.newRequestQueue(getApplicationContext());}


            // 이 부분에 동작 명령어를 내리도록 한다.
        String iftttapi = "{API}";
        String startIot ="null";

        // 웹 훅으로 만들었던 닉네임을 활용하는 위치
        switch(move_to_voice){
            case Constants.move_to_living_room_on :
                startIot="switch_livingroom_on";
                break;
            case Constants.move_to_living_room_off :
                startIot="switch_livingroom_off";
                break;
            case Constants.move_to_air_circulator_on :
                startIot="air_circulator_on";
                break;
            case Constants.move_to_air_circulator_off :
                startIot="air_circulator_off";
                break;
            case Constants.move_to_livingroom_temp :
                startIot="livingroom_temp";
                break;
        }

        System.out.println("현재 설정된 iot 값 : "+startIot);

        String ifttturl="https://maker.ifttt.com/trigger/"+startIot+"/with/key/"+iftttapi;

        System.out.println("현재 설정된 ifttturl 값 : "+ifttturl);

        StringRequest iftttrequest = new StringRequest(Request.Method.GET, ifttturl, new Response.Listener<String>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                System.out.println("성공적으로 리퀘스트 성공 : " + ifttturl);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("리퀘스트 실패 : " + ifttturl);

            }
        });

        iftttrequest.setShouldCache(false);
        iftttQueue.add(iftttrequest);


    }



    // fab 애니메이션 동작 명령어
    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
        }
    }
}

