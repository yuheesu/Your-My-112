package com.androidtutorialpoint.googlemapsnearbyplaces;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    TextView txtGPS;
    Button btn_setting, btn_start, btn_map;
    ToggleButton tb;
    static final int READ_BLOCK_SIZE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_setting = (Button)findViewById(R.id.btn_setting);
        btn_map = (Button)findViewById(R.id.btn_map);
        btn_start = (Button)findViewById(R.id.btn_start);
        tb = (ToggleButton)findViewById(R.id.toggleButton);
        txtGPS=(TextView)findViewById(R.id.txtGPS);

        txtGPS.setText("위치정보 미수신중");


        //구조요청 버튼 버튼
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRescue (v);
            }
        });

        //설정 버튼 : 메시지설정, 연락처설정, 위험인물 등록
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼을 눌렀을때 SettingActivity로 이동
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });

        //설정 버튼 : 메시지설정, 연락처설정, 위험인물 등록
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼을 눌렀을때 SettingActivity로 이동
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
            }
        });

        //위치 탐색 버튼
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tb.isChecked()) {
                    findLoc(true);
                }
                else {
                    findLoc(false);
                }
            }
        });

        findLoc(true);  //앱 시작시 실행
    }

    //위치 탐색
    public void findLoc(boolean start)
    {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            if(start) {
                txtGPS.setText("위치 수신중..");
                // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);
            }
            else {
                txtGPS.setText("위치정보 미수신중");
                lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
            }
        }catch(SecurityException ex){
        }
    }

    //구조요청
    //(1)GPS 위치 불러오기,  (2-1)메시지/(2-2)연락처 불러오기,  (3)문자 전송
    public void requestRescue (View v){
        //(1)GPS 위치 불러오기--------------------------------------------------------------
        String strLocation=txtGPS.getText().toString();
        String location="", locationNum="", URL="";  //경도, 위도

        if(strLocation.equals("위치 수신중..") || strLocation.equals("위치정보 미수신중"))
        {
            location="위치를 확인할 수 없습니다";
        }
        else{
            StringTokenizer stok = new StringTokenizer(strLocation, "\n");    //문자열 끊기

            locationNum = stok.nextToken();     //경도,위도
            location = stok.nextToken();    //주소

            URL="http://maps.google.com/?q="+locationNum;
        }


        //(2)사용자 지정 메시지, 저장된 연락처 불러오기-------------------------------------
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View messageView = inflater.inflate(R.layout.activity_message, null);
        TextView txtMessage = (TextView)messageView.findViewById(R.id.txtMessage);

        //초기값
        String message=txtMessage.getText().toString();
        String phone="";
        String stringMsg = "";
        String stringPhone = "";
        int countContactsLine=0;    //연락처에서 몇번째 줄까지 읽었는지 저장

        //(2-1)******메시지 불러오기
        try {
            FileInputStream fileMessage = openFileInput("mytextfile.txt");    //사용자지정 메시지
            InputStreamReader InputMessage = new InputStreamReader(fileMessage);

            char[] inputMessageBuffer = new char[READ_BLOCK_SIZE];
            int charMessage;

            while ((charMessage = InputMessage.read(inputMessageBuffer)) > 0) {
                // char to string conversion
                String readMessage = String.copyValueOf(inputMessageBuffer, 0, charMessage);
                stringMsg += readMessage;
            }
            InputMessage.close();

        } catch (Exception e) { //파일 스트림 Exception
            e.printStackTrace();
        }

        //(2-2)******전화번호 불러오기
        try {
            FileInputStream filePhone = openFileInput("Contacts_list.txt");   //연락처 목록
            InputStreamReader InputPhone = new InputStreamReader(filePhone);

            char[] inputPhoneBuffer = new char[READ_BLOCK_SIZE];
            int charPhone;

            while ((charPhone = InputPhone.read(inputPhoneBuffer)) > 0) {
                // char to string conversion
                String readPhone = String.copyValueOf(inputPhoneBuffer, 0, charPhone);
                stringPhone += readPhone;
            }
            InputPhone.close();

        } catch (Exception e) { //파일 스트림 Exception
            e.printStackTrace();
        }


        //(3)문자 전송----------------------------------------------------------------------
        //사용자가 입력한 메시지가 있으면, 그 메시지를 저장(오류:사용자 메시지를 꼭 지정해줘야 전송됨)
        if (! stringMsg.equals("")) message = stringMsg;

        //사용자가 입력한 번호가 없으면 112에만 문자 전송(테스트를 위해 토스트 메시지로만 확인. 실제 전송x)
        if (stringPhone.equals("")) {
            Toast.makeText(getApplicationContext(), "저장된 번호x \n ->112에만 전송", Toast.LENGTH_LONG).show();
        }
        else
        {   //사용자가 입력한 번호로 문자 전송
            StringTokenizer stok = new StringTokenizer(stringPhone, "\n");    //문자열 끊기

            while (stok.hasMoreTokens()) {
                countContactsLine++;
                phone = stok.nextToken();     //문자를 떼어내서 저장

                //(이름, 전화번호, 메일)중에서 전화번호를 읽어와 문자 전송
                if (countContactsLine == 2) {
                    SendSMS(phone, message + "\n-현재 위치: \n" + location);
                    if(!URL.equals("")) SendSMS(phone, "-구글맵: " + URL); //URL있으면 전송

                    countContactsLine = -1;   //3번째 줄인 메일 건너뛰기
                }
            }

            Toast.makeText(getApplicationContext(), "메시지를 전송하였습니다.", Toast.LENGTH_LONG).show();
        }
    }


    //문자 전송 메소드------------------------------------------------------------------------------
    public void SendSMS(String phoneNum, String message) {
        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(phoneNum, null, message, null, null);
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            //txtGPS.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
            //        + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);
            getLocation(latitude,longitude);
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    //경도, 위도를 주소로 변경
    public void getLocation(double lat, double lng){
        String str = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    str = address.get(0).getAddressLine(0).toString();
                }
            }
        } catch (IOException e) {
            Log.e("MainActivity", "주소를 찾지 못하였습니다.");
            e.printStackTrace();
        }

        //위도 경도를 이용해 텍스트뷰에 주소를 표시
        txtGPS.setText(lat+","+lng+"\n" + str);

    }


}
