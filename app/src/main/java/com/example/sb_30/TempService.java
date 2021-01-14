package com.example.sb_30;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TempService extends Service {
    @Override // 서비스가 최초 생성될 때만 호출
    public void onCreate() {
        Log.d("TempService", "TempService Start!!!!!!!!!!!!");
    }

    @Override // startService()로 서비스를 시작할 때 호출
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TempService3333", "TempService Start!!!!!!!!!!!!");
        return START_STICKY;
    }

    @Override // bindService()로 바인딩을 실행할 때 호출
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override // unbindService()로 바인딩을 해제할 때 호출
    public boolean onUnbind(Intent intent) {

        return false;
    }

    @Override // 이미 onUnbind()가 호출된 후에 bindService()로 바인딩을 실행할 때 호출
    public void onRebind(Intent intent) {

    }

    @Override // 서비스가 소멸될 때 호출
    public void onDestroy() {

    }
}
