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

public class IntroFragment extends Fragment implements ConfirmSubmitDelegate {
    //implements ConfirmCaptureListener, ConfirmSubmitDelegate {

    private String TAG = "IntroFragment";
	private Activity mActivity = null;
    private Button mTryButton = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		mActivity = getActivity();
        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        mTryButton = (Button)v.findViewById(R.id.check_id_button);
        mTryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				ConfirmCapture.getInstance().beginCapture(mActivity);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ConfirmTaskManager.getInstance().deregisterConfirmCaptureListener(ConfirmCapture.TAG);
    }

	public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
		doSubmit(payload);
	}

	public void onConfirmCaptureDidCancel() {

	}

    private void doSubmit(ConfirmPayload payload) {
        String apiKey = "{YOUR_API_KEY}";

        ConfirmSubmitTask task = new ConfirmSubmitTask(payload, apiKey);
        task.delegate = this;
        task.execute();

		setButtonVisibility(false);
        showToast("Submitting images please be patient...");
    }

	// ------------------- ConfirmSubmitDelegate methods -------------------
	public void onConfirmSubmitError(final String error) {
		Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
		showToast(error);
		setButtonVisibility(true);
	}

	public void onConfirmSubmitSuccess(final io.confirm.confirmsdk.IdModel model) {
		if (mActivity != null) {
				if (model.didPass() || model.didFail())
					showResults(model);
				else
					showToast("ID Verification could not take place.");
				setButtonVisibility(true);
		}
	}

    // ------------------- demo stuff -------------------
    private void showResults(final io.confirm.confirmsdk.IdModel model) {
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