package io.confirm.sample;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmCaptureListener;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmSubmitDelegate;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.IdModel;
import io.confirm.confirmsdk.taskmanager.ConfirmTaskManager;

public class SampleActivity extends AppCompatActivity
        implements ConfirmCaptureListener, ConfirmSubmitDelegate, // SDK support
        ConfirmResultFragment.ConfirmResultDelegate {

    private String          TAG = "SampleActivity";
    private ConfirmCapture  mConfirmCapture = null;
    private SampleActivity  self = null;
    private Button          showIDButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(io.confirm.sample.R.layout.activity_sample);

        self = this;

        showIDButton = (Button)findViewById(io.confirm.sample.R.id.button);

        showIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIDButton.setVisibility(View.INVISIBLE);
                ConfirmTaskManager.getInstance().beginConfirmaCapture(
                        ConfirmCapture.TAG, SampleActivity.this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfirmTaskManager.getInstance().deregisterConfirmCaptureListener(ConfirmCapture.TAG);
    }

    // ------------------- ConfirmCaptureListener methods -------------------
    @Override
    public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
        doSubmit(payload);
    }
    @Override
    public void onConfirmCaptureDidCancel() {
        showToast("confirm capture cancelled");
        showIDButton.setVisibility(View.VISIBLE);
    }
    @Override
    public Activity getActivity() {
        return this;
    }

    // -------------------  -------------------

    private void doSubmit(ConfirmPayload payload) {

        String apiKey = "4bfb21eb-7139-4820-95f7-e12d06556a2d";

        ConfirmSubmitTask task = new ConfirmSubmitTask(payload, apiKey);
        task.delegate = this;
        task.execute();

        showToast("Submitting images please be patient...");
    }

    // ------------------- ConfirmSubmitDelegate methods -------------------
    public void onConfirmSubmitError(final String error) {
        Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
        showToast(error);
        showIDButton.setVisibility(View.VISIBLE);
    }
    public void onConfirmSubmitSuccess(final IdModel model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (model.didPass() || model.didFail())
                    showResults(model);
                else {
                    showToast("ID Verification could not take place. (" + model.status + ")");
                    showIDButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    // ------------------- demo stuff -------------------
    private void showResults(final IdModel model) {
        FragmentManager fm = getFragmentManager();
        ConfirmResultFragment fragment = (ConfirmResultFragment)fm.findFragmentById(io.confirm.sample.R.id.confirm_result_fragment);

        if (fragment == null) {
            try {
                fragment = ConfirmResultFragment.newInstance(model);
                fragment.delegate = this;
                fm.beginTransaction()
                        .add(io.confirm.sample.R.id.container, fragment)
                        .addToBackStack("Results")
                        .commit();
            } catch (Exception e) {
                Log.e(TAG, "Exception = " + e.getLocalizedMessage());
            } finally {

            }
        }
    }

    public void onResultClosed() {
        showIDButton.setVisibility(View.VISIBLE);
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
            toast.show();
            }
        });
    }

}
