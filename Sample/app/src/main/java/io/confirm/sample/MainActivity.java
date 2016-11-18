package io.confirm.sample;

import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmConstants;
import io.confirm.confirmsdk.ConfirmPayload;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

	private IntroFragment mFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "*** MainActivity::onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mFragment = new IntroFragment();

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == ConfirmConstants.INTENT_COMPLETE_CAPTURE_REQUEST_CODE) {
			ConfirmPayload payload = ConfirmCapture.getInstance().getPayload();
			mFragment.onConfirmCaptureDidComplete(payload);
		}
    }

}