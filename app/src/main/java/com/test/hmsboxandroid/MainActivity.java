package com.test.hmsboxandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
//import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
//import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
//import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
//import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
//import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;
import com.huawei.hms.push.HmsMessaging;

import com.example.mapkit_module_example.LunchOCR;


public class MainActivity extends AppCompatActivity {


    //private static final String TAG = "BVBMapViewDemoActivity";
    private static final String TAG = "BVBMLKITOCR";
    Button BSelectImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtain Token Push HCM
       // this.getToken();
       // this.subscribe("helloMagic");

        BSelectImage = findViewById(R.id.BSelectImage);
        //IVPreviewImage = findViewById(R.id.IVPreviewImage);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }
        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

    }


    void imageChooser() {
        //startTakePhotoActivity(this, callback);
        LunchOCR ocrTmp = new LunchOCR();
        ocrTmp.imageChooser(this);
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