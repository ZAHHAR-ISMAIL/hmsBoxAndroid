package com.example.mapkit_module_example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;

import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;

public class LunchOCR {
    private static final String TAG = "BVBMLKITOCR";
    //Button BSelectImage;

    public void imageChooser(Context cxt) {
        startTakePhotoActivity( cxt,this, callback);
    }

    private void startTakePhotoActivity(Context cxt, Object object, MLGcrCapture.Callback callback) {
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
        ocrManager.capturePhoto(cxt, object, callback);
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
}
