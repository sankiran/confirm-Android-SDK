package io.confirm.sample;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmSubmitDelegate;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.IdModel;

public class IntroFragment extends Fragment implements ConfirmSubmitDelegate {

    private String TAG = "IntroFragment";
	private Activity mActivity = null;
    private Button mTryButton = null;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* mActivity must be initialized before using ConfirmSDK */
		mActivity = getActivity();
        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        mTryButton = (Button)v.findViewById(R.id.check_id_button);
        mTryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				/* -----This is where we start the ConfirmSDK capture session ----- */
				ConfirmCapture.getInstance().beginCapture(mActivity);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

	/* -----ConfirmSDK Callback----- */
	/* This gets called from ConfirmSDK when capture is completed */
	public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
		// Payload is ready to be sent to Confirm's cloud
		doSubmit(payload);
	}

	/* This gets called from ConfirmSDK when the capture is dismissed */
	public void onConfirmCaptureDidCancel() {
		// Optional for cancel conditions (user pressed back, etc)
	}

    private void doSubmit(ConfirmPayload payload) {
        String apiKey = "4bfb21eb-7139-4820-95f7-e12d06556a2d";

        ConfirmSubmitTask task = new ConfirmSubmitTask(payload, apiKey);
		task.delegate = this;
        task.execute();

		setButtonVisibility(false);
        showToast("Submitting images please be patient...");
    }

	public void onConfirmSubmitError(final String error) {
		Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
		showToast(error);
		setButtonVisibility(true);
	}

	public void onConfirmSubmitSuccess(final IdModel model) {
		if (mActivity != null) {
				if (model.didPass() || model.didFail())
					showResults(model);
				else
					showToast("ID Verification could not take place.");
				setButtonVisibility(true);
		}
	}

	private void showResults(final IdModel model) {
		FragmentManager fm = getActivity().getFragmentManager();
		ResultFragment fragment =
				(ResultFragment)fm.findFragmentById(R.id.confirm_result_fragment);

		if (fragment == null) {
			try {
				fragment = ResultFragment.newInstance(model);
				fm.beginTransaction()
						.add(R.id.container, fragment)
						.addToBackStack("Results")
						.commit();
			} catch (Exception e) {
				Log.e(TAG, "Exception = " + e.getLocalizedMessage());
			} finally {

			}
		}
	}

    private void showToast(final String text) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(mActivity, text, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }

	private void setButtonVisibility(final boolean show) {
		if (mActivity != null && mTryButton != null) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (show)
						mTryButton.setVisibility(View.VISIBLE);
					else
						mTryButton.setVisibility(View.GONE);
				}
			});
		}
	}
}