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
	private static final String RESULT_NOT_AVAILABLE = "N/A";

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
		LinearLayout resultLayout = (LinearLayout)v.findViewById(R.id.resultLayout);

		mCloseButton = (Button)v.findViewById(R.id.close_button);
		mCloseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getFragmentManager().popBackStack();
			}
		});
		mStatusLabel = (TextView)v.findViewById(R.id.status_label);

		// Face recognition
		mFaceMatchLayout = (LinearLayout)v.findViewById(R.id.facial_recognition_layout);
		if (mFaceModel == null)
			mFaceMatchLayout.setVisibility(View.GONE);
		else {
			mFaceMatchLayout.setVisibility(View.VISIBLE);
			mFaceMatchScoreLabel = (TextView) v.findViewById(R.id.face_match_score_label);
			mFaceMatchScoreLabel.setText(mFaceModel.getMatchScoreString());
		}

		if (mIdModel.isFullAuth()) {
			mStatusLabel.setText(mIdModel.getStatus());

			resultLayout.setVisibility(View.VISIBLE);
			if (mIdModel.getIdentity() != null) {
				IdBioModel bio = mIdModel.getIdentity().getBio();

				mFirstNameLabel = (TextView) v.findViewById(R.id.first_name);
				mFirstNameLabel.setText(bio.getFirstName());
				mLastNameLabel = (TextView) v.findViewById(R.id.last_name);
				mLastNameLabel.setText(bio.getLastName());
				mAddressLabel = (TextView) v.findViewById(R.id.address);
				mAddressLabel.setText(bio.getAddress());
				mCityLabel = (TextView) v.findViewById(R.id.city);
				mCityLabel.setText(bio.getCity());
				mStateLabel = (TextView) v.findViewById(R.id.state);
				mStateLabel.setText(bio.getState());
				mZipLabel = (TextView) v.findViewById(R.id.zip);
				mZipLabel.setText(bio.getZIP());

				SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

				mDOBLabel = (TextView) v.findViewById(R.id.dob);
				if (bio.getDOB() != null)
					mDOBLabel.setText(df.format(bio.getDOB()));
			}
			if (mIdModel.getIdentity() != null) {
				IdClassificationModel classification = mIdModel.getIdentity().getClassification();
				mIssTypeLabel = (TextView) v.findViewById(R.id.iss_type);
				mIssTypeLabel.setText(classification.getType());
				mIssStateLabel = (TextView) v.findViewById(R.id.iss_state);
				mIssStateLabel.setText(classification.getState());
			}
			if (mIdModel.getIdentity() != null) {
				SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				IdIssuanceModel issuance = mIdModel.getIdentity().getIssuance();

				mNumberLabel = (TextView) v.findViewById(R.id.number);
				mNumberLabel.setText(issuance.getNumber());
				mDateIssuedLabel = (TextView) v.findViewById(R.id.issued);
				if (issuance.getIssued() != null)
					mDateIssuedLabel.setText(df.format(issuance.getIssued()));
				else
					mDateIssuedLabel.setText(RESULT_NOT_AVAILABLE);
				mExpirationLabel = (TextView) v.findViewById(R.id.expiration);
				if (issuance.getExpiration() != null)
					mExpirationLabel.setText(df.format(issuance.getExpiration()));
				else
					mExpirationLabel.setText(RESULT_NOT_AVAILABLE);

				mServerLabel = (TextView) v.findViewById(R.id.server_header);
				mFailureLayout = (LinearLayout) v.findViewById(R.id.failure_layout);
				mFailureLabel = (TextView) v.findViewById(R.id.failure);

				if (mIdModel.getFailureReasons().size() > 0) {
					mFailureLabel.setText(mIdModel.getFailureReasons().get(0));
				} else {
					mServerLabel.setVisibility(View.INVISIBLE);
					mFailureLayout.setVisibility(View.INVISIBLE);
				}
			}
		}
		else {
			resultLayout.setVisibility(View.GONE);
			mStatusLabel.setText(mIdModel.getBackAuth().getRecommendation());
		}
	}
}
