 package com.example.sb_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

 public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    TextView tv_sender;
    TextView tv_content;
    TextView tv_date;

    TextView tv_year;
    TextView tv_mon;
    TextView tv_day;

    Button btn_sms;
    Button btn_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String year;
        String month;
        String days;

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
        btn_date = findViewById(R.id.btn_date);

        Calendar cal = Calendar.getInstance();

        tv_year = findViewById(R.id.tv_year);
        tv_mon = findViewById(R.id.tv_mon);
        tv_day = findViewById(R.id.tv_day);

        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH)+1);
        days = String.valueOf(cal.get(Calendar.DATE));

        // 월, 일을 2자리로 변환
        if( month.length() == 1 ){
            month = "0" + month;
        }

        if( days.length() == 1 ){
            days = "0" + days;
        }

        tv_year.setText(year);
        tv_mon.setText(month);
        tv_day.setText(days);

        btn_sms.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPermission();
            }
        });

        btn_date.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();

                new DatePickerDialog(MainActivity.this, mDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
            }
        });

        Log.d("mainActivity", "mainActivity Start!!!!");

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

    // 문자 내역 읽어오기
    public int readSMSMessage() {
        //List<Message> arrayList = new ArrayList<Message>();
        Uri allMessage = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage, new String[]{"_id", "thread_id", "address", "person", "date", "body"},null, null,"date DESC");

        LinearLayout ll = (LinearLayout)findViewById(R.id.add_sms);

        ll.removeAllViews();

        int cnt = 0;

        while (c.moveToNext()) {
            Message msg = new Message();

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
            msg.setTimestamp(timestamp);

            String body = c.getString(5);
            msg.setBody(body);

            int getDate = Integer.parseInt("" + tv_year.getText() + tv_mon.getText() + tv_day.getText());
            int getSmsDate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date(msg.getTimestamp())));

            if(getDate - getSmsDate > 0 ){
                break;
            }

            cnt ++;

            // SMS 번호 동적 레이아웃
            TextView tv_count = new TextView(this);
            tv_count.setText("SMS #" + cnt);
            ll.addView(tv_count);

            // SMS 발신 번호 동적 레이아웃
            LinearLayout senderLL = new LinearLayout(this);
            senderLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            senderLL.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv_sender_label = new TextView(this);
            TextView tv_sender_value = new TextView(this);

            tv_sender_label.setText("발신 번호 : ");
            tv_sender_value.setText(msg.getAddress());

            senderLL.addView(tv_sender_label);
            senderLL.addView(tv_sender_value);

            ll.addView(senderLL);

            // SMS 발신 내용 동적 레이아웃
            LinearLayout contentLL = new LinearLayout(this);
            contentLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentLL.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv_content_label = new TextView(this);
            TextView tv_content_value = new TextView(this);

            tv_content_label.setText("SMS 내용 : ");
            tv_content_value.setText(msg.getBody());

            contentLL.addView(tv_content_label);
            contentLL.addView(tv_content_value);

            ll.addView(contentLL);

            // SMS 수신 시간 동적 레이아웃
            LinearLayout dateLL = new LinearLayout(this);
            dateLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            dateLL.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv_date_label = new TextView(this);
            TextView tv_date_value = new TextView(this);

            tv_date_label.setText("수신 시간 : ");
            tv_date_value.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(msg.getTimestamp())));

            dateLL.addView(tv_date_label);
            dateLL.addView(tv_date_value);

            ll.addView(dateLL);
        }
        return 0;
    }

    // 문자내역 읽어오기 권한 체크
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

     DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
             tv_year = findViewById(R.id.tv_year);
             tv_mon = findViewById(R.id.tv_mon);
             tv_day = findViewById(R.id.tv_day);

            String year;
            String month;
            String days;

             year = String.valueOf(yy);
             month = String.valueOf(mm+1);
             days = String.valueOf(dd);

            // 월, 일을 2자리로 변환
            if( month.length() == 1 ){
                month = "0" + month;
            }

            if( days.length() == 1 ){
                days = "0" + days;
            }

             tv_year.setText(year);
             tv_mon.setText(month);
             tv_day.setText(days);
         }
     };
}