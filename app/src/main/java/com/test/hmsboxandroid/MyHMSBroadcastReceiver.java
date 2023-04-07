package com.test.hmsboxandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hms.common.api.CommonStatusCodes;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.sms.common.ReadSmsConstant;

public class MyHMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && ReadSmsConstant.READ_SMS_BROADCAST_ACTION.equals(intent.getAction())) {
            Status status = bundle.getParcelable(ReadSmsConstant.EXTRA_STATUS);
            if (status.getStatusCode() == CommonStatusCodes.TIMEOUT) {
                Log.i("SMS_RECIEVED", "Timeout.... ");
            } else if (status.getStatusCode() == CommonStatusCodes.SUCCESS) {
                Log.i("SMS_RECIEVED", "YES.... ");
                if (bundle.containsKey(ReadSmsConstant.EXTRA_SMS_MESSAGE)) {
                    Toast.makeText(context, bundle.getString(ReadSmsConstant.EXTRA_SMS_MESSAGE), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
