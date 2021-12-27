package com.example.pirconevigate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

public class Karyl3starActivity extends AppCompatActivity implements View.OnClickListener {


    //fab 애니메이션 및 변수
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private SharedPreferences character_select;


    private String TAG = "VideoActivity"; // 비디오 재생용 액티비티

    private VideoView mVideoview; // 영상 재생은 비디오뷰를 사용하도록 한다.


    /// 이미지 버튼 입력
    ImageButton bt_internet;
    ImageButton bt_navi;
    ImageButton bt_bluetooth;
    ImageButton bt_musicplayer;
    ImageButton bt_geniemusic;
    ImageButton bt_youtube;
    ImageButton bt_twitch;
    ImageButton bt_dc;

    // 세팅 버튼 입력
    ImageButton bt_setting;

    // 이미지 뷰 입력

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


    Timer timer;
    TimerTask timerTask;


    int year;
    int month;
    int date;

    int woy;
    int wom;

    int doy;
    int dom;
    int dow;

    int hour12;
    static int hour24=0;
    int minute;
    int second;

    int milliSecond;
    int timeZone;
    int lastDate;

    int character_int;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyl_3star);

        // 액션바가 그래도 보인다면 강제로 추가 하도록 한다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 비디오 액티비티
        mVideoview = (VideoView) findViewById(R.id.video_view);
        //play video
        mVideoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.karyl)); // 영상 (raw 폴더에 저장 하도록)

        mVideoview.start();
        //loop
        mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true); // 동영상 무한 반복을 위한 루프 설정
            }
        });

        // 시간에 따른 배경화면 바꾸는 변수 설정

        //디지털 시계 이미지 변수 설정
        img_month10 = (ImageView)findViewById(R.id.time_month10);
        img_month0 = (ImageView)findViewById(R.id.time_month0);

        img_day10 = (ImageView)findViewById(R.id.time_day10);
        img_day0 = (ImageView)findViewById(R.id.time_day0);

        img_week = (ImageView)findViewById(R.id.font_week);

        img_hour10 = (ImageView)findViewById(R.id.time_hour10);
        img_hour0 = (ImageView)findViewById(R.id.time_hour0);

        img_min10 = (ImageView)findViewById(R.id.time_min10);
        img_min0 = (ImageView)findViewById(R.id.time_min0);

        img_sec10 = (ImageView)findViewById(R.id.time_sec10);
        img_sec0 = (ImageView)findViewById(R.id.time_sec0);


        timer = new Timer(); //타이머로 소스를 반복 시키자 ㅇㅋ
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timerzone(); // 1초의 시간 표시를 위한 함수수
            }
        };
        timer.schedule(timerTask, 0, 1000); // 1초




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
        bt_internet = (ImageButton)findViewById(R.id.btxml_internet);


        // 세팅 버튼 id 연결
        bt_setting = (ImageButton)findViewById(R.id.btxml_setting);

        //버튼 클릭 리스너 입력
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);

        //SharedPreferences 데이터 저장
        character_select = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = character_select.edit();
        editor.putInt("ActiveID",4);
        editor.commit();



        // 터치를 이용해야 눌렀을때 이미지가 변경되는 작업을 수월하게 진행 할 수 있다.
        // 인터넷 이미지 버튼 연결 및 클릭 명령
        bt_internet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "인터넷 버튼 선택", Toast.LENGTH_SHORT).show();

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
        bt_navi = (ImageButton)findViewById(R.id.btxml_navi);
        bt_navi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "네비게이션 버튼 선택", Toast.LENGTH_SHORT).show();

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
        bt_bluetooth = (ImageButton)findViewById(R.id.btxml_bluetooth);
        bt_bluetooth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "블루투스 버튼 선택", Toast.LENGTH_SHORT).show();

                }
                else if (action == MotionEvent.ACTION_UP) {
                    Intent bluetoothmode = new Intent();
                    bluetoothmode.setComponent(new ComponentName("com.ts.MainUI", "com.ts.bt.BtConnectActivity"));
                    startActivity(bluetoothmode);
                    finish();
                }
                return false;

            }


        });

        // 뮤직 플레이어 이미지 버튼 연결 및 클릭 명령
        bt_musicplayer = (ImageButton)findViewById(R.id.btxml_player);
        bt_musicplayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "뮤직플레이어 버튼 선택", Toast.LENGTH_SHORT).show();

                }
                else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.massivcode.androidmusicplayer");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 지니뮤직 이미지 버튼 연결 및 클릭 명령
        bt_geniemusic = (ImageButton)findViewById(R.id.btxml_genie);
        bt_geniemusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "지니뮤직 버튼 선택", Toast.LENGTH_SHORT).show();

                }
                else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.ktmusic.geniemusic");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 디씨 이미지 버튼 연결 및 클릭 명령
        bt_dc = (ImageButton)findViewById(R.id.btxml_dc);
        bt_dc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "디씨 버튼 선택", Toast.LENGTH_SHORT).show();

                }
                else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.dcinside.app");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 유튜브 이미지 버튼 연결 및 클릭 명령
        bt_youtube = (ImageButton)findViewById(R.id.btxml_youtube);
        bt_youtube.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "유튜브 버튼 선택", Toast.LENGTH_SHORT).show();

                }
                else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 트위치 이미지 버튼 연결 및 클릭 명령
        bt_twitch = (ImageButton)findViewById(R.id.btxml_twitch);
        bt_twitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {

                    Toast.makeText(Karyl3starActivity.this, "트위치 버튼 선택", Toast.LENGTH_SHORT).show();

                }
                else if (action == MotionEvent.ACTION_UP) {
                    Intent app_musicplayer = getPackageManager().getLaunchIntentForPackage("tv.twitch.android.app");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_musicplayer);
                    finish();
                }
                return false;

            }
        });

        // 세팅 이미지 버튼 연결 및 클릭 명령
        bt_setting = (ImageButton)findViewById(R.id.btxml_setting);
        bt_setting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                //ACTION_DOWN = 버튼을 누르다
                if (action == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(Karyl3starActivity.this, "세팅 버튼 선택", Toast.LENGTH_SHORT).show();
                }
                else if (action == MotionEvent.ACTION_UP) {
                    Intent app_setting = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    // 모든 문제는 getActivity 를 사용해야 한다. (상속 개념)
                    startActivity(app_setting);
                    finish();
                }
                return false;

            }
        });



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
                Intent char_kokkoro = new Intent(Karyl3starActivity.this, MainKokkoroActivity.class);
                char_kokkoro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                timer.cancel(); //타이머 동작 중지
                timer.purge(); // 타이머 동작 완전 제거
                timer = null; // 타이머 빈값 처리
                startActivity(char_kokkoro);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.fab2:
                anim();
                Toast.makeText(this, "캬루 로 선택 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent char_karyl = new Intent(Karyl3starActivity.this, Karyl3starActivity.class);
                char_karyl.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_karyl);
                timer.cancel();
                timer.purge();
                timer = null;
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.fab3:
                anim();
                Toast.makeText(this, "페코린느 로 선택 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent char_peco = new Intent(Karyl3starActivity.this, MainPecoActivity.class);
                char_peco.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                timer.cancel();
                timer.purge();
                timer = null;
                startActivity(char_peco);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.fab4:
                anim();
                Toast.makeText(this, "1성으로 변신!", Toast.LENGTH_SHORT).show();
                Intent char_3tar = new Intent(Karyl3starActivity.this, MainKarylActivity.class);
                char_3tar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                timer.cancel();
                timer.purge();
                timer = null;
                startActivity(char_3tar);
                finish();
                overridePendingTransition(0, 0);
                break;

        }

    }

    public void timerzone(){
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

        System.out.println("오늘은 " + year +"년 " + month+1 + "월" + date +"일");
        System.out.println("오늘은 올해의 " + woy +"째주, 이번달의 " + wom + "째주. " + date +"일");
        System.out.println("오늘은 이번 해의 " + doy +"일이자, 이번 달의 " + dom + "일. 요일은 " + dow +"일 (1:일요일)");
        System.out.println("현재 시각은 " + hour12 +":"+ minute + ":"+ second +", 24시간으로 표현하면 " + hour24+":"+ minute + ":"+ second);
        System.out.println("오늘은 " + year +"년 " + month+1 + "월" + date +"일");
        System.out.println("1000분의 1초 (0~999): " + milliSecond);
        System.out.println("timeZone (-12~+12): " + timeZone/(60*60*1000)); // 1000분의 1초를 시간으로 표시하기 위해 60*60*1000
        System.out.println("이 달의 마지막 날: " + lastDate);


        // 시간 변경에 따른 디지털 시계 숫자 변경용 코드

        //달 (10자리)
        if(month>=9)
            img_month0.setImageResource(R.drawable.font_k_1);
        else
            img_month0.setImageResource(R.drawable.font_k_0);

        //달 (1자리)
        switch(month%10){
            case 0: img_month0.setImageResource(R.drawable.font_k_1);
                break;
            case 1: img_month0.setImageResource(R.drawable.font_k_2);
                break;
            case 2: img_month0.setImageResource(R.drawable.font_k_3);
                break;
            case 3: img_month0.setImageResource(R.drawable.font_k_4);
                break;
            case 4: img_month0.setImageResource(R.drawable.font_k_5);
                break;
            case 5: img_month0.setImageResource(R.drawable.font_k_6);
                break;
            case 6: img_month0.setImageResource(R.drawable.font_k_7);
                break;
            case 7: img_month0.setImageResource(R.drawable.font_k_8);
                break;
            case 8: img_month0.setImageResource(R.drawable.font_k_9);
                break;
            case 9: img_month0.setImageResource(R.drawable.font_k_0);
                break;
        }

        //일 (10자리)
        switch(date/10){
            case 0: img_day10.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_day10.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_day10.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_day10.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_day10.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_day10.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_day10.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_day10.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_day10.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_day10.setImageResource(R.drawable.font_k_9);
                break;
        }

        //일 (1자리)
        switch(date%10){
            case 0: img_day0.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_day0.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_day0.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_day0.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_day0.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_day0.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_day0.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_day0.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_day0.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_day0.setImageResource(R.drawable.font_k_9);
                break;
        }


        //요일
        switch(dow%10){
            case 1: img_week.setImageResource(R.drawable.font_week);
                break;
            case 2: img_week.setImageResource(R.drawable.week_k_mon);
                break;
            case 3: img_week.setImageResource(R.drawable.week_k_tues);
                break;
            case 4: img_week.setImageResource(R.drawable.week_k_wednes);
                break;
            case 5: img_week.setImageResource(R.drawable.week_k_thurs);
                break;
            case 6: img_week.setImageResource(R.drawable.week_k_fri);
                break;
            case 7: img_week.setImageResource(R.drawable.week_satur);
                break;
        }


        //시간 (10자리)
        switch(hour24/10){
            case 0: img_hour10.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_hour10.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_hour10.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_hour10.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_hour10.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_hour10.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_hour10.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_hour10.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_hour10.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_hour10.setImageResource(R.drawable.font_k_9);
                break;
        }

        //시간 (1자리)
        switch(hour24%10){
            case 0: img_hour0.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_hour0.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_hour0.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_hour0.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_hour0.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_hour0.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_hour0.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_hour0.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_hour0.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_hour0.setImageResource(R.drawable.font_k_9);
                break;
        }

        //분 (10자리)
        switch(minute/10){
            case 0: img_min10.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_min10.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_min10.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_min10.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_min10.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_min10.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_min10.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_min10.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_min10.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_min10.setImageResource(R.drawable.font_k_9);
                break;
        }

        //분 (1자리)
        switch(minute%10){
            case 0: img_min0.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_min0.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_min0.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_min0.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_min0.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_min0.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_min0.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_min0.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_min0.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_min0.setImageResource(R.drawable.font_k_9);
                break;
        }

        //초 (10자리)
        switch(second/10){
            case 0: img_sec10.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_sec10.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_sec10.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_sec10.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_sec10.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_sec10.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_sec10.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_sec10.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_sec10.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_sec10.setImageResource(R.drawable.font_k_9);
                break;
        }

        //초 (1자리)
        switch(second%10){
            case 0: img_sec0.setImageResource(R.drawable.font_k_0);
                break;
            case 1: img_sec0.setImageResource(R.drawable.font_k_1);
                break;
            case 2: img_sec0.setImageResource(R.drawable.font_k_2);
                break;
            case 3: img_sec0.setImageResource(R.drawable.font_k_3);
                break;
            case 4: img_sec0.setImageResource(R.drawable.font_k_4);
                break;
            case 5: img_sec0.setImageResource(R.drawable.font_k_5);
                break;
            case 6: img_sec0.setImageResource(R.drawable.font_k_6);
                break;
            case 7: img_sec0.setImageResource(R.drawable.font_k_7);
                break;
            case 8: img_sec0.setImageResource(R.drawable.font_k_8);
                break;
            case 9: img_sec0.setImageResource(R.drawable.font_k_9);
                break;
        }

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

