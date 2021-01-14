package com.example.sb_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    TextView tv_sender;
    TextView tv_content;
    TextView tv_date;
    Button btn_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mainAct", "mainAct Start1111111111");
        setContentView(R.layout.activity_main);
        Log.d("mainAct", "mainAct Start!2222222222");

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        String[] permissions = {Manifest.permission.RECEIVE_SMS};

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        }else if(permissionCheck == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, "SMS 권한 설정!!!!!!!!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        tv_sender = findViewById(R.id.sender);
        tv_content = findViewById(R.id.content);
        tv_date = findViewById(R.id.date);

        btn_sms = findViewById(R.id.btn_sms);

        btn_sms.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPermission();
            }
        });

        Intent intent = getIntent();
        processCommand(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processCommand(intent);
    }

    private void processCommand(Intent intent){
        if(intent != null){
            String sender = intent.getStringExtra("sender");
            String content = intent.getStringExtra("content");
            String date = intent.getStringExtra("date");

            tv_sender.setText(sender);
            tv_content.setText(content);
            tv_date.setText(date);
        }
    }

    public int readSMSMessage() {
        //List<Message> arrayList = new ArrayList<Message>();
        Uri allMessage = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,
                new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                null, null,
                "date DESC");

        while (c.moveToNext()) {
            Message msg = new Message(); // 따로 저는 클래스를 만들어서 담아오도록 했습니다.

            long messageId = c.getLong(0);
            msg.setMessageId(String.valueOf(messageId));

            long threadId = c.getLong(1);
            msg.setThreadId(String.valueOf(threadId));

            String address = c.getString(2);
            msg.setAddress(address);

            long contactId = c.getLong(3);
            msg.setContactId(String.valueOf(contactId));

            String contactId_string = String.valueOf(contactId);
            msg.setContactId_string(contactId_string);

            long timestamp = c.getLong(4);
            msg.setTimestamp(String.valueOf(timestamp));

            String body = c.getString(5);
            msg.setBody(body);

            //arrayList.add(msg); //이부분은 제가 arraylist에 담으려고 하기떄문에 추가된부분이며 수정가능합니다.
            Toast.makeText(this, ""+msg.body, Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    private void callPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
        }else{
            readSMSMessage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSIONS_REQUEST_READ_SMS){
            readSMSMessage();
        } else{
            Toast.makeText(this, "SMS 읽기 권한 실패......", Toast.LENGTH_SHORT).show();
        }
    }
}