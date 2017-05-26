package com.androidtutorialpoint.googlemapsnearbyplaces;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class TelActivity extends Activity {

    //연락처 불러오기
    private static final int RESULT_PICK_CONTACT = 85500;
    private TextView textView1;
    private TextView textView2;

    static final int READ_BLOCK_SIZE = 100;



    ListView list;
    ListViewAdapter adapter;
    ArrayList<ListViewItem> arrData;
    EditText txtName, txtPhone, txtMail;
    Button btnSave;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tel);

        //EditText, Button 연결
        txtName = (EditText) findViewById(R.id.txtName);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtMail = (EditText) findViewById(R.id.txtMail);
        btnSave = (Button) findViewById(R.id.btnSave);
        arrData = new ArrayList<ListViewItem>();
        textView1 = (TextView) findViewById(R.id.txtName);
        textView2 = (TextView) findViewById(R.id.txtPhone);

        //메모장에서 item 불러오기
        ReadBtn(this);

        //어댑터 생성
        adapter = new ListViewAdapter(this, arrData);


        //리스트뷰에 어댑터 연결
        list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);

        //리스트뷰 이벤트 설정
        list.setOnItemLongClickListener(new ListViewItemLongClickListener());


    }
    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }

        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);

            textView1.setText(name);
            textView2.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    // txt파일에 저장하기---------------------------------------------------------------------------
    public void WriteBtn(ListViewItem contacts) {
        String name = contacts.getName();
        String phone = contacts.getTel();
        String mail = contacts.getEmail();

        try {
            FileOutputStream fileout = openFileOutput("Contacts_list.txt", MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(name + "\n" + phone + "\n" + mail + "\n");
            outputWriter.close();

            //리스트 갱신
            arrData.add(new ListViewItem(R.mipmap.ic_launcher, name, phone, mail));
            list.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void InputData(View v) {
        String name = txtName.getText().toString();
        String phone = txtPhone.getText().toString();
        String mail = txtMail.getText().toString();

        //이름, 전화번호 입력은 필수. 메일은 생략 가능
        if (name.equals("")) {
            Toast.makeText(getBaseContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone.equals("")) {
            Toast.makeText(getBaseContext(), "전화번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        } else if (mail.equals("")) {
            mail = " ";
        }

        txtName.setText("");
        txtPhone.setText("");
        txtMail.setText("");

        WriteBtn(new ListViewItem(R.mipmap.ic_launcher, name, phone, mail));

        Toast.makeText(getBaseContext(), "연락처를 저장하였습니다", Toast.LENGTH_SHORT).show();
    }


    // txt파일 불러오기-----------------------------------------------------------------------------
    public void ReadBtn(TelActivity v) {
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("Contacts_list.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            String s_name, s_phone, s_mail;
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();


            //[Contacts_list.txt -> 리스트뷰] 연락처 목록 가져오기
            StringTokenizer stok = new StringTokenizer(s, "\n");    //문자열 끊기


            while (stok.hasMoreTokens()) {
                s_name = stok.nextToken();   //문자를 떼어내서 저장
                s_phone = stok.nextToken();
                s_mail = stok.nextToken();

                //리스트에 추가
                arrData.add(new ListViewItem(R.mipmap.ic_launcher, s_name, s_phone, s_mail));
            }

            //리스트 세팅
            //list.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //아이템 삭제-----------------------------------------------------------------------------------
    int selectedPos = -1;

    private class ListViewItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            selectedPos = position;
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(view.getContext());
            alertDlg.setTitle("연락처 삭제");

            // '삭제' 버튼 클릭
            alertDlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    arrData.remove(selectedPos);

                    // 아래 method를 호출하지 않을 경우, 삭제된 item이 화면에 계속 보여진다.
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();  // AlertDialog를 닫는다.

                    //삭제된 연락처를 제외하고, txt파일에 연락처 목록을 재입력
                    try {
                        FileOutputStream fileout = openFileOutput("Contacts_list.txt", MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

                        for (int i = 0; i < arrData.size(); i++) {
                            outputWriter.write(arrData.get(i).getName() + "\n"
                                    + arrData.get(i).getTel() + "\n"
                                    + arrData.get(i).getEmail() + "\n");
                        }
                        outputWriter.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            // '취소' 버튼 클릭
            alertDlg.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();  // AlertDialog를 닫는다.
                }
            });


            alertDlg.setMessage(String.format("연락처를 삭제하시겠습니까?"));
            alertDlg.show();
            return false;
        }

    }


}