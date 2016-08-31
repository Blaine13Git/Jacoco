package codecoverage;

import android.annotation.SuppressLint;
import android.util.Log;

import com.twl.qichechaoren.activity.SplashActivity;

@SuppressLint("NewApi")
public class InstrumentedActivity extends SplashActivity {

	public static String TAG = "IntrumentedPlayer";

	private FinishListener mListener;

	public void setFinishListener(FinishListener listener) {
		mListener = listener;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG + ".InstrumentedActivity", "onDestroy()");
		//super.finish();
		if (mListener != null) {
			mListener.onActivityFinished();
		}
	}

}
