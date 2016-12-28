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
import io.confirm.confirmsdk.ConfirmCaptureListener;
import io.confirm.confirmsdk.ConfirmSubmitListener;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.models.IdModel;

public class IntroFragment extends Fragment
		implements ConfirmCaptureListener, ConfirmSubmitListener {
    private String TAG = "IntroFragment";

	// Must be initialized before using ConfirmSDK
	private Activity mActivity = null;
	private ConfirmCaptureListener mCaptureListener = null;
	private ConfirmSubmitListener mSubmitListener = null;

    private Button mTryButton = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Note: mActivity, mCaptureListener, and mSubmitListner
		MUST be initialized before using ConfirmSDK */
		mActivity = getActivity();
		mCaptureListener = this;
		mSubmitListener = this;

        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        mTryButton = (Button)v.findViewById(R.id.check_id_button);
        mTryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				/* This is where we start the ConfirmSDK capture session */
				ConfirmCapture.getInstance().beginCapture(mCaptureListener, mActivity);
            }
        });
    }

	/**
	 * Callback from ConfirmSDK when the capture is completed.
	 * @param payload Object which retains the details of the capture to be submitted to the Confirm API
	 */
	@Override
	public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
		doSubmit(payload);
	}

	/**
	 * Callback from ConfirmSDK when the capture is dismissed.
	 */
	@Override
	public void onConfirmCaptureDidCancel() {
		// When capture is dismissed.
	}

	/**
	 * Callback from ConfirmSDK after submitting the result and received an error.
	 * @param error Error message
	 */
	@Override
	public void onConfirmSubmitError(final String error) {
		Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
		showToast(error);
		setButtonVisibility(true);
	}

	/**
	 * Callback from ConfirmSDK after submitting the result and received the result.
	 * @param model
	 */
	@Override
	public void onConfirmSubmitSuccess(final IdModel model) {
		if (mActivity != null) {
			if (model.didPass()) {
				// Request completed - document deemed authentic
				showResults(model);
			}
			else if (model.didFail()) {
				// Request completed - document deemed potentially fraudulent
				showResults(model);
			}
			else {
				// Request completed, but Confirm was unable to provide an authentication status for
				// the document. This is usually due to image or document damage
				// Failure
				showToast("ID Verification could not take place.");
			}
			setButtonVisibility(true);
		}
	}

	/**
	 * Submit payload object to Confirm  API.
	 * @param payload
	 */
    private void doSubmit(ConfirmPayload payload) {
        String apiKey = "{YOUR_API_KEY_HERE}"; // Please put valid API key in here.

        ConfirmSubmitTask task = new ConfirmSubmitTask(mSubmitListener, payload, apiKey);
        task.execute();

		setButtonVisibility(false);
        showToast("Submitting images please be patient...");
    }

    // ------------------- sample app part -------------------
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