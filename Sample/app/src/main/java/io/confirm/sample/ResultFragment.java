package io.confirm.sample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import io.confirm.confirmsdk.models.*;

public class ResultFragment extends Fragment {
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_MODEL = "result_model";

	private IdModel mIdModel;
	private FaceVerifyResponse mFaceModel;

	private Button  mCloseButton;

	private TextView mStatusLabel;

	private LinearLayout mFaceMatchLayout = null;
	private TextView mFaceMatchScoreLabel = null;

	private TextView mFirstNameLabel;
	private TextView mLastNameLabel;
	private TextView mAddressLabel;
	private TextView mCityLabel;
	private TextView mStateLabel;
	private TextView mZipLabel;
	private TextView mDOBLabel;

	private TextView mIssTypeLabel;
	private TextView mIssStateLabel;

	private TextView mNumberLabel;
	private TextView mDateIssuedLabel;
	private TextView mExpirationLabel;

	private TextView mServerLabel;
	private LinearLayout mFailureLayout;
	private TextView mFailureLabel;

	public ResultFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param idModel ID model
	 * @param faceModel Face model
	 * @return A new instance of fragment ResultFragment.
	 */
	public static ResultFragment newInstance(IdModel idModel, FaceVerifyResponse faceModel) {
		ResultFragment fragment = new ResultFragment();
		fragment.mIdModel = idModel;
		fragment.mFaceModel = faceModel;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_result, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {

		mCloseButton = (Button)v.findViewById(R.id.close_button);
		mCloseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getFragmentManager().popBackStack();
			}
		});
		mStatusLabel = (TextView)v.findViewById(R.id.status_label);
		mStatusLabel.setText(mIdModel.status);

		// Face recognition
		mFaceMatchLayout = (LinearLayout)v.findViewById(R.id.facial_recognition_layout);
		if (mFaceModel == null)
			mFaceMatchLayout.setVisibility(View.GONE);
		else {
			mFaceMatchLayout.setVisibility(View.VISIBLE);
			mFaceMatchScoreLabel = (TextView) v.findViewById(R.id.face_match_score_label);
			mFaceMatchScoreLabel.setText(mFaceModel.getMatchScoreString());
		}

		IdBioModel bio = mIdModel.identity.bio;
		IdClassificationModel classification = mIdModel.identity.classification;
		IdIssuanceModel issuance = mIdModel.identity.issuance;

		mFirstNameLabel = (TextView)v.findViewById(R.id.first_name);
		mFirstNameLabel.setText(bio.firstName);
		mLastNameLabel = (TextView)v.findViewById(R.id.last_name);
		mLastNameLabel.setText(bio.lastName);
		mAddressLabel = (TextView)v.findViewById(R.id.address);
		mAddressLabel.setText(bio.address);
		mCityLabel = (TextView)v.findViewById(R.id.city);
		mCityLabel.setText(bio.city);
		mStateLabel = (TextView)v.findViewById(R.id.state);
		mStateLabel.setText(bio.state);
		mZipLabel = (TextView)v.findViewById(R.id.zip);
		mZipLabel.setText(bio.zip);

		SimpleDateFormat df =  new SimpleDateFormat("MM/dd/yyyy");

		mDOBLabel = (TextView)v.findViewById(R.id.dob);
		if (bio.dob != null)
			mDOBLabel.setText(df.format(bio.dob));

		mIssTypeLabel = (TextView)v.findViewById(R.id.iss_type);
		mIssTypeLabel.setText(classification.type);
		mIssStateLabel = (TextView)v.findViewById(R.id.iss_state);
		mIssStateLabel.setText(classification.state);

		mNumberLabel = (TextView)v.findViewById(R.id.number);
		mNumberLabel.setText(issuance.number);
		mDateIssuedLabel = (TextView)v.findViewById(R.id.issued);
		if (issuance.issued != null)
			mDateIssuedLabel.setText(df.format(issuance.issued));
		else
			mDateIssuedLabel.setText("N/A");
		mExpirationLabel = (TextView)v.findViewById(R.id.expiration);
		if (issuance.expiration != null)
			mExpirationLabel.setText(df.format(issuance.expiration));
		else
			mExpirationLabel.setText("N/A");

		mServerLabel = (TextView)v.findViewById(R.id.server_header);
		mFailureLayout = (LinearLayout)v.findViewById(R.id.failure_layout);
		mFailureLabel = (TextView)v.findViewById(R.id.failure);

		if (mIdModel.failureReasons.size() > 0) {
			mFailureLabel.setText(mIdModel.failureReasons.get(0));
		} else {
			mServerLabel.setVisibility(View.INVISIBLE);
			mFailureLayout.setVisibility(View.INVISIBLE);
		}
	}
}
