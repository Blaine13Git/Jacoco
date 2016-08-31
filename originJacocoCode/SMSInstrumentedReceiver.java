package codecoverage.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSInstrumentedReceiver extends BroadcastReceiver {
	public static String TAG = "M3SMSInstrumentedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		FinishListener mListener = new JacocoInstrumentation();
		if (mListener != null) {
			mListener.dumpIntermediateCoverage("/sdcard/coverage.ec");
		}
	}

}
