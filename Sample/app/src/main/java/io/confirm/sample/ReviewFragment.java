package io.confirm.sample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.confirm.confirmsdk.ConfirmCapture;

/**
 * Custom Review Fragment
 */
public class ReviewFragment extends Fragment{
	public ReviewFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_review, container, false);
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		// Register retake button
		Button retakeButton = (Button)v.findViewById(R.id.reviewRetakeButton);
		retakeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ConfirmCapture.getInstance().cancelVerification();
			}
		});

		// Register use button
		Button useButton = (Button)v.findViewById(R.id.reviewUseButton);
		useButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ConfirmCapture.getInstance().advanceVerification();
			}
		});
	}
}