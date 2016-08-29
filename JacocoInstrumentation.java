package codeCoverage;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JacocoInstrumentation extends Instrumentation implements FinishListener {
    public static String TAG = "JacocoInstrumentation:";

    public static String code_coverage_file = "";
    private final Bundle mResults = new Bundle();
    private Intent mIntent;
    private static final boolean LOGD = true;
    private boolean mCoverage = true;
    private String mCoverageFilePath;

    @Override
    public void onCreate(Bundle arguments) {
        Log.d(TAG, "onCreate(" + arguments + ")");
        super.onCreate(arguments);
        SimpleDateFormat startTime = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        Calendar calendar = Calendar.getInstance();
        String myTime = startTime.format(calendar.getTime());

        File codeCoverageDir = new File("/sdcard/codeCoverage");
        if(!codeCoverageDir .exists()  && !codeCoverageDir .isDirectory()){
            codeCoverageDir .mkdir();
        }

        code_coverage_file = codeCoverageDir+"/coverage_" + myTime + ".ec";

        File file = new File(code_coverage_file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "异常 : " + e);
                e.printStackTrace();
            }
        }
        if (arguments != null) {
            mCoverageFilePath = arguments.getString("coverageFile");
        }

        mIntent = new Intent(getTargetContext(), InstrumentedActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start();
    }

    @Override
    public void onStart() {
        if (LOGD)
            Log.d(TAG, "onStart()");
        super.onStart();

        Looper.prepare();
        InstrumentedActivity activity = (InstrumentedActivity) startActivitySync(mIntent);
        activity.setFinishListener(this);
    }

    private void generateCoverageReport() {
        Log.d(TAG, "generateCoverageReport():" + getCoverageFilePath());
        OutputStream out = null;
        try {
            out = new FileOutputStream(getCoverageFilePath(), false);
            Object agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null);

            out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class)
                    .invoke(agent, false));
        } catch (Exception e) {
            Log.d(TAG, e.toString(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getCoverageFilePath() {
        if (mCoverageFilePath == null) {
            return code_coverage_file;
        } else {
            return mCoverageFilePath;
        }
    }

    private boolean setCoverageFilePath(String filePath) {
        if (filePath != null && filePath.length() > 0) {
            mCoverageFilePath = filePath;
            return true;
        }
        return false;
    }

    @Override
    public void onActivityFinished() {
        if (LOGD)
            Log.d(TAG, "onActivityFinished()");
        if (mCoverage) {
            generateCoverageReport();
        }
        finish(Activity.RESULT_OK, mResults);
    }

    @Override
    public void dumpIntermediateCoverage(String filePath) {
        // TODO Auto-generated method stub
        if (LOGD) {
            Log.d(TAG, "Intermidate Dump Called with file name :" + filePath);
        }
        if (mCoverage) {
            if (!setCoverageFilePath(filePath)) {
                if (LOGD) {
                    Log.d(TAG, "Unable to set the given file path:" + filePath + " as dump target.");
                }
            }
            generateCoverageReport();
            setCoverageFilePath(code_coverage_file);
        }
    }

}
