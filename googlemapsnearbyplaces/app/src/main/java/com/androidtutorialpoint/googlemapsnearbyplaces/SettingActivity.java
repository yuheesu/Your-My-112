package com.androidtutorialpoint.googlemapsnearbyplaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button btn_message = (Button)findViewById(R.id.btn_message);
        Button btn_tel = (Button)findViewById(R.id.btn_tel);
        Button btn_danger = (Button)findViewById(R.id.btn_danger);


        //메세지 등록
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼을 눌렀을때 MessageActivity로 이동
                Intent intent = new Intent(getApplicationContext(),MessageActivity.class);
                startActivity(intent);
            }
        });

        //전화번호 등록
        btn_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼을 눌렀을때 TelActivity로 이동
                Intent intent = new Intent(getApplicationContext(),TelActivity.class);
                startActivity(intent);
            }
        });


    }
}
