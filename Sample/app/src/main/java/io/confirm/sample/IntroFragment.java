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
import io.confirm.confirmsdk.ConfirmCustomProxy;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmCaptureListener;
import io.confirm.confirmsdk.ConfirmSubmitListener;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.models.FaceVerifyResponse;
import io.confirm.confirmsdk.models.IdModel;

public class IntroFragment extends Fragment
		implements ConfirmCaptureListener, ConfirmSubmitListener, ConfirmCustomProxy {
	private String TAG = "IntroFragment";

	// Must be initialized before using ConfirmSDK
	private Activity mActivity = null;
	private ConfirmCaptureListener mCaptureListener = null;
	private ConfirmSubmitListener mSubmitListener = null;
	private ConfirmCustomProxy mCustomProxy = null;

	private Button mTryButton = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Note: mActivity, mCaptureListener, and mSubmitListener
		MUST be initialized before using ConfirmSDK */
		mActivity = getActivity();
		mCaptureListener = this;
		mSubmitListener = this;
		mCustomProxy = this;

		return inflater.inflate(R.layout.fragment_intro, container, false);
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		mTryButton = (Button) v.findViewById(R.id.check_id_button);
		mTryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				///* Use enableFacialMatch() to turn on the facial match feature */
				ConfirmCapture.getInstance().enableFacialMatch();
				/* Use enableBackAuth() to turn on the back-only authentication feature */
				//ConfirmCapture.getInstance().enableBackAuth();
				/* Optional: Use setCustomProxy() to enable support for custom interstitial views*/
				ConfirmCapture.getInstance().setCustomProxy(mCustomProxy);
				/* Optional: Use setCountryCode() to set a country code in ISO 3166-1 alpha-3 format */
				ConfirmCapture.getInstance().setCountryCode("USA");
				/* Optional: Use setDocumentType() to set document type. (Note: Please see README for information) */
				ConfirmCapture.getInstance().setDocumentType(ConfirmCapture.ConfirmDocumentType.ID1);
				/* This is how we begin a ConfirmSDK capture session */
				ConfirmCapture.getInstance().beginCapture(mCaptureListener, mActivity);
			}
		});
	}

	/**
	 * Callback from ConfirmSDK when the capture is completed.
	 * @param payload Object which retains the details of the capture to be submitted to the Confirm API.
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

		ConfirmCapture.getInstance().cleanup(); // Purge details of the capture
	}

	/**
	 * Callback from ConfirmSDK after submitting the result and received the result.
	 * @param idModel
	 * @param faceModel
	 */
	@Override
	public void onConfirmSubmitSuccess(final IdModel idModel, final FaceVerifyResponse faceModel) {
		if (mActivity != null) {
			if (idModel.didPass()) {
				// Request completed - document deemed authentic
				showResults(idModel, faceModel);
			}
			else if (idModel.didFail()) {
				// Request completed - document deemed potentially fraudulent
				showResults(idModel, faceModel);
			}
			else {
				// Request completed, but Confirm was unable to provide an authentication status for
				// the document. This is usually due to image or document damage
				// Failure
				showResults(idModel, faceModel);
			}
			setButtonVisibility(true);
		}
		ConfirmCapture.getInstance().cleanup(); // Purge details of the capture
	}

	/**
	 * Callback from ConfirmSDK when submission is cancelled.
	 */
	@Override
	public void onConfirmSubmitCancel() {
		setButtonVisibility(true);
	}

	/**
	 * Callback from ConfirmSDK when uploading process started.
	 */
	@Override
	public void onConfirmUploadProgressStart() {

	}

	/**
	 * Callback from ConfirmSDK when uploading process finished.
	 */
	@Override
	public void onConfirmUploadProgressFinish() {

	}

	/**
	 * Callback from ConfirmSDK during uploading process.
	 * @param progress Start to end range is in [0.0, 1.0].
	 */
	@Override
	public void onConfirmUploadProgressStatus(float progress) {

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

	/**
	 * Show results once the request is completed.
	 * @param idModel
	 * @param faceModel
	 */
	private void showResults(final IdModel idModel, FaceVerifyResponse faceModel) {
		FragmentManager fm = getActivity().getFragmentManager();
		ResultFragment fragment =
				(ResultFragment) fm.findFragmentById(R.id.result_fragment);

		if (fragment == null) {
			try {
				fragment = ResultFragment.newInstance(idModel, faceModel);
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

	/**
	 * Set custom instruction view UI.
	 * @param side
	 * @return NULL if no custom view. Otherwise, return corresponding fragment.
	 */
	@Override
	public Fragment getViewForInstruction(ConfirmCapture.ConfirmCameraSide side) {
		Fragment fragment = null;
		switch (side) {
			case Front:
				InstructionFragment frontInstruction = new InstructionFragment();
				fragment = frontInstruction;
				break;
			case Back:
				// Add custom Fragment in here
				break;
			default:
				break;
		}
		return fragment;
	}

	/**
	 * Set custom review view UI.
	 * @param side
	 * @return NULL if no custom view. Otherwise, return corresponding fragment.
	 */
	@Override
	public Fragment getViewForReview(ConfirmCapture.ConfirmCameraSide side) {
		Fragment fragment = null;
		switch (side) {
			case Front:
				// Add custom Fragment in here
				break;
			case Back:
				ReviewFragment backReview = new ReviewFragment();
				fragment = backReview;
				break;
			case Selfie:
				ReviewFragment selfieReview = new ReviewFragment();
				fragment = selfieReview;
				break;
			default:
				break;
		}
		return fragment;
	}
}