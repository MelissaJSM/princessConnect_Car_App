package com.example.pirconevigate;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {



    int character_int;

    //음성인식 퍼미션 설정


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바가 그래도 보인다면 강제로 추가 하도록 한다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // sharedpref 값 가져오는 함수
        getcharacter_select();

        //가져와서 액티비티 실행시키기
        Intent char_stage;
        switch (character_int) {
            case 0: // 콧코로 1성
                char_stage = new Intent(MainActivity.this, MainKokkoroActivity.class);
                char_stage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_stage);
                finish();
                break;
            case 1: // 캐루 1성
                char_stage = new Intent(MainActivity.this, MainKarylActivity.class);
                char_stage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_stage);
                finish();
                break;
            case 2: // 페코 1성
                char_stage = new Intent(MainActivity.this, MainPecoActivity.class);
                char_stage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_stage);
                finish();
                break;
            case 3: // 콧코로 3성
                char_stage = new Intent(MainActivity.this, Kokkoro3starActivity.class);
                char_stage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_stage);
                finish();
                break;
            case 4: // 캐루 3성
                char_stage = new Intent(MainActivity.this, Karyl3starActivity.class);
                char_stage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_stage);
                finish();
                break;
            case 5: // 페코 3성
                char_stage = new Intent(MainActivity.this, Peco3starActivity.class);
                char_stage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(char_stage);
                finish();
                break;

        }




    }

    // 저장된 캐릭터 값 가져오는 함수 (SharedPreferences)
    private void getcharacter_select() {
        try {
            SharedPreferences character_select = getSharedPreferences("UserInfo",0);
            character_int = character_select.getInt("ActiveID", 0);
        } catch (NullPointerException e) {
            character_int = 0;
        }
    }


}

