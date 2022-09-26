
package com.test.hmsboxandroid;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.mlkit.sample.camera.FrameMetadata;
import com.mlkit.sample.views.overlay.GraphicOverlay;
//import com.techtools.myage.camera.FrameMetadata;
//import com.techtools.myage.graphic.CameraImageGraphic;
//import com.techtools.myage.graphic.LocalFaceGraphic;
//import com.techtools.myage.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class LocalFaceTransactor extends BaseTransactor<List<MLFace>> {
    private static final String TAG = "LocalFaceTransactor";

    private final MLFaceAnalyzer detector;
    private boolean isOpenFeatures;
    private Context mContext;

    public int age = 0;
    public TextView mage_tv;

    private int previous_age = 0;
    private TextView mcountdown_tv;
    CountDownTimer aCounter;
    boolean isPause = false;
    long millis_lef = 0;
    boolean counter_running = false;
    boolean counter_finished = false;
    String sex = "None";
    int max_age = 0, min_age = 0;
    SharedPreferences pref;
    Activity mactivity;

    public LocalFaceTransactor(Activity activity, MLFaceAnalyzerSetting options, final Context context, boolean isOpenFeatures,
                               TextView age_tv, TextView countdown_tv) {

        this.detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(options);
        this.isOpenFeatures = isOpenFeatures;
        this.mContext = context;

        this.mage_tv = age_tv;

        this.mcountdown_tv = countdown_tv;
        this.mactivity = activity;
        timer = new Timer();
        pref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        aCounter = new CountDownTimer(10000, 1000){
            public void onTick(long millisUntilFinished){
                if (millisUntilFinished / 1000 >=0)
                    mcountdown_tv.setText((int) (millisUntilFinished / 1000)+"");
                counter_running = true;
                millis_lef = millisUntilFinished;
            }

            public void onFinish(){
                mcountdown_tv.setText("Done");
                counter_running = false;
                Toast.makeText(context, "Your Age is : " + max_age, Toast.LENGTH_SHORT).show();
                counter_finished = true;


                save_to_preflist(max_age + ", " + sex + ", " + get_date_time());

            }
        };
        //aCounter.start();

    }



    @Override
    public void stop() {
        Log.e(TAG, "LocalFaceTransactor stop: " );
        try {
            this.detector.stop();
            aCounter.cancel();
        } catch (IOException e) {
            Log.e(LocalFaceTransactor.TAG, "Exception thrown while trying to close face transactor: " + e.getMessage());
        }
    }



    @Override
    protected Task<List<MLFace>> detectInImage(MLFrame image) {
        Log.e(TAG, "detectInImage: " );
        return this.detector.asyncAnalyseFrame(image);
    }


    int sensibility = 0;
    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d("toby", "Total HMSFaceProc graphicOverlay start");

        if(faces.size()<1){

            mage_tv.setText("Center your face to the camera !");
            mage_tv.setTextSize(18);


            if(aCounter != null){
                aCounter.cancel();
                isPause = true;
                counter_running = false;
            }

        }else{
            for (MLFace face : faces) {
                Log.e(TAG, "Age: " + face.getFeatures().getAge() );
                age = face.getFeatures().getAge();
                if (face.getFeatures().getAge() > previous_age) {
                    previous_age = age;
                    age = face.getFeatures().getAge();
                }else
                    age = previous_age;

                sex = (face.getFeatures().getSexProbability() > 0.5f) ? "Female" : "Male";

                if (age > max_age){
                    max_age = age;
//                    sensibility++;
//                    if (sensibility > 1) {
//                        max_age = age;
//                        sensibility = 0;
//                    }
                }
                else if(age < min_age)
                    min_age = age;

                mage_tv.setText(face.getFeatures().getAge()+" Years " + sex);
                mage_tv.setTextSize(30);


                if(!counter_running && millis_lef==0 && !counter_finished)
                    aCounter.start();
                else if(!counter_running && millis_lef > 0 && !counter_finished){
                    continue_counter(millis_lef, mContext);
                }

            }
        }




    }

    public void continue_counter(long ms_lef, final Context mcontext){
        aCounter = new CountDownTimer(ms_lef, 1000){
            public void onTick(long millisUntilFinished){
                if (millisUntilFinished / 1000 >=0)
                    mcountdown_tv.setText((int) (millisUntilFinished / 1000)+"");
                counter_running = true;
                millis_lef = millisUntilFinished;
            }

            public void onFinish(){
                mcountdown_tv.setText("Done!");
                counter_running = false;
                Toast.makeText(mcontext, "Your Age is : " + max_age, Toast.LENGTH_SHORT).show();
                counter_finished = true;

                save_to_preflist(max_age + ", " + sex + ", " + get_date_time());

// OLD SAVE:
//                Set<String> in_StringSet = pref.getStringSet("Stringset", new LinkedHashSet<String>());
//                //Set<String> in_StringSet = new LinkedHashSet<String>(hs);
//
//                LinkedHashSet<String> linked_list = new LinkedHashSet<String>(in_StringSet);
//                linked_list.add(max_age + ", " + sex + ", " + get_date_time() );
//
//               // in_StringSet.add(max_age + ", " + sex + ", " + get_date_time() );
//                pref.edit().putStringSet("Stringset", linked_list).apply();

            }
        };
        aCounter.start();
    }


    public void save_to_preflist(String str){
        String saved_json_string = pref.getString("Stringset", "[]");
        //Convert to LinkedHashSet (support order):
        LinkedHashSet<String> linked_hash_map_list = new Gson().fromJson(saved_json_string, new TypeToken<LinkedHashSet<String>>(){}.getType());

        assert linked_hash_map_list != null;
        linked_hash_map_list.add(str);

        // convert map back to JSON String
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create();
        Type type = new TypeToken<LinkedHashSet<Integer>>(){}.getType();
        String json = gson.toJson(linked_hash_map_list, type);

        pref.edit().putString("Stringset", json).apply();
    }


    static int interval;
    static Timer timer;
    private static final int setInterval() {
        if (interval == 1)
            timer.cancel();
        return --interval;
    }


    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.d("toby", "LocalFaceTransactor Total HMSFaceProc graphicOverlay onFailure");
        Log.e(LocalFaceTransactor.TAG, "Face detection failed: " + e.getMessage());
    }

    @Override
    public boolean isFaceDetection() {
        Log.e(TAG, "LocalFaceTransactor isFaceDetection: " );
        return true;
    }

    public String get_date_time(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy'\n'HH:mm:ss");
        return df.format(Calendar.getInstance().getTime());
    }


    
}
