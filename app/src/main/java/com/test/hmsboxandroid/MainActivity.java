package com.test.hmsboxandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;



public class MainActivity extends AppCompatActivity {


    private static final String TAG = "BVBMapViewDemoActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtain Token Push HCM
        this.getToken();
        this.subscribe("helloMagic");



    }




    private void getToken() {
       // showLog("getToken:begin");
        Log.i(TAG, "start Token BVB");

        new Thread() {
            @Override
            public void run() {
                try {

                    // read from agconnect-services.json
                    String appId = "105599089"; // p
                    String token = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    Log.i(TAG, "get token:" + token);
                    if(!TextUtils.isEmpty(token)) {
                       // sendRegTokenToServer(token);
                    }

                    Log.e("get token:", token);
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                   // showLog("get token failed, " + e);
                }
            }
        }.start();
    }

    public void subscribe(String topic) {
        try {
            // Subscribe to a topic.
            HmsMessaging.getInstance(MainActivity.this).subscribe(topic)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            // Obtain the topic subscription result.
                            if (task.isSuccessful()) {
                                Log.i(TAG, "subscribe topic successfully");
                            } else {
                                Log.e(TAG, "subscribe topic failed, return value is " + task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "subscribe failed, catch exception : " + e.getMessage());
        }
    }

}