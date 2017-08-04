package io.confirm.sample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.confirm.confirmsdk.ConfirmCapture;

/**
 * Custom Instruction Fragment
 */
public class InstructionFragment extends Fragment {
	public InstructionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_instruction, container, false);
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		// Register start/scan button
		Button scanButton = (Button)v.findViewById(R.id.instructionScanButton);
		scanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ConfirmCapture.getInstance().advanceInstructions();
			}
		});

		// Register back button
		Button backButton = (Button)v.findViewById(R.id.instructionCancelButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ConfirmCapture.getInstance().cancelInstructions();
			}
		});
	}
}
