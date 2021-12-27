package com.example.pirconevigate;

import android.os.Environment;

public class Constants {





    public static final String CATEGORY_FITNESS 		= "01";
    public static final String CATEGORY_SILVER 			= "02";

    //콧코로 메뉴 이동 모드 활성화
    public static final int move_to_menu_null = 0;
    public static final int move_to_menu_hidden = 1;



    //콧코로 음성인식 모드 활성화
    public static final int move_to_voice_null = 0;
    public static final int move_to_voice_bluetooth	= 1;
    public static final int move_to_voice_youtube= 2;
    public static final int move_to_voice_youtube_search = 3;
    public static final int move_to_voice_search = 4;
    public static final int move_to_voice_navi = 5;
    public static final int move_to_voice_sex = 6;
    public static final int move_to_voice_ride = 7;
    public static final int move_to_voice_weather = 8;

    public static final int move_to_living_room_on = 9;
    public static final int move_to_living_room_off = 10;
    public static final int move_to_air_circulator_on = 11;
    public static final int move_to_air_circulator_off = 12;
    public static final int move_to_livingroom_temp = 13;


    // 이건 음성인식이나 일반이나 동시에 사용 할 수 있다.
    public static final int not_to_move = 99;

    //콧코로 보이스가 켜졌는지 확인 하는 곳
    public static final int kokkoro_voice_off	= 0;
    public static final int kokkoro_voice_on	= 1;

    //사용자 음성 10초동안 받아지는지 대기하는 장소
    public static final int kokkoro_conunt_ready	= 0;
    public static final int kokkoro_conunt_max	= 10;


    public static final int sex_off			= 0;
    public static final int sex_on 		    = 1;
    public static final int BT_MESSAGE_TOAST 			= 5;
}
