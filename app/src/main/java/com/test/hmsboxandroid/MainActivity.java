package com.test.hmsboxandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.sms.ReadSmsManager;
import com.huawei.hms.support.sms.common.ReadSmsConstant;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Hash_code", new HashManager().getHashValue(this));

        MyHMSBroadcastReceiver mySMSBroadcastReceiver=new MyHMSBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ReadSmsConstant.READ_SMS_BROADCAST_ACTION);
        this.registerReceiver(mySMSBroadcastReceiver, filter);
        Task<Void> task = ReadSmsManager.start(this);
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("SMS_ENABLED", "onComplet ");
                }
            }
        });



        // First, check if the app has permission to send SMS messages
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission not granted, request it
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.SEND_SMS},
//                    11);
//        } else {
//            // Permission granted, send SMS message
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage("+212706531149", null, "[#] Your verification code is 102030 tcHLlT5HJlx", null, null);
//        }

    }



}