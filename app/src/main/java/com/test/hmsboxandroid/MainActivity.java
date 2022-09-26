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
import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;
import com.huawei.hms.push.HmsMessaging;



public class MainActivity extends AppCompatActivity {


    //private static final String TAG = "BVBMapViewDemoActivity";
    private static final String TAG = "BVBMLKITOCR";
    Button BSelectImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtain Token Push HCM
        this.getToken();
        this.subscribe("helloMagic");

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
        startTakePhotoActivity(this, callback);
    }

    private void startTakePhotoActivity(Object object, MLGcrCapture.Callback callback) {
        // Create a general card recognition configurator that can be used to configure languages recognized.
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().setLanguage("zh").create();
        // Create a general card recognition UI configurator.
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                // Set the color of the scanning box.
                .setScanBoxCornerColor(Color.BLUE)
                // Set the prompt text in the scanning box. It is recommended that the text contain less than 30 characters.
                .setTipText("Taking picture, align edges")
                .create();
        // Method 1: Create a general card recognition processor based on the customized card recognition UI configurator.
        //MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);
        // Method 2: Use the default UI to create a general card recognition processor.
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig);
        // Bind the general card recognition processor to the processing result callback function.
        ocrManager.capturePhoto(this, object, callback);
    }

    private MLGcrCapture.Callback callback = new MLGcrCapture.Callback() {
        // This method requires the following status codes:
        // MLGcrCaptureResult.CAPTURE_CONTINUE: The returned result does not meet the requirements (for example, no result is returned or the returned result is incorrect). In camera stream or picture taking mode, the recognition continues.
        // MLGcrCaptureResult.CAPTURE_STOP: The returned result meets the requirements and the recognition stops.
        @Override
        public int onResult(MLGcrCaptureResult result, Object o){
            // Process the recognition result. Implement post-processing logic based on your use case to extract valid information and return the status code.
            if (result != null) {// Check whether a result is returned.
                // Recognition result processing logic.
                Log.i(TAG, "result ML OCR3:" + result.text.getStringValue());
                //if (!isMatch(result)) {// Check whether the processing result meets the requirements. Implement the isMatch method based on your use case.
                return MLGcrCaptureResult.CAPTURE_CONTINUE;// The processing result does not meet the requirements, and the recognition continues.
                //}
                // Process the results that meet the requirements.
            }
            return MLGcrCaptureResult.CAPTURE_STOP;// The processing ends, and the recognition exits.
        }
        @Override
        public void onCanceled(){
            // Processing for recognition request cancelation.
            Log.i(TAG, "canceled ML OCR:" );

        }
        // Callback method used when no text is recognized or a system exception occurs during recognition.
        // retCode: result code.
        // bitmap: bank card image that fails to be recognized.
        @Override
        public void onFailure(int retCode, Bitmap bitmap){
            // Exception handling.
            Log.i(TAG, "failure ML OCR:" + retCode);

        }
        @Override
        public void onDenied(){
            // Processing for recognition request deny scenarios, for example, the camera is unavailable.
            Log.i(TAG, "denied ML OCR:" );

        }
    };




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