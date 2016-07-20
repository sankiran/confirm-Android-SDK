package io.confirm.sample;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.confirm.confirmsdk.IdModel;
import io.confirm.confirmsdk.IdBioModel;
import io.confirm.confirmsdk.IdClassificationModel;
import io.confirm.confirmsdk.IdIssuanceModel;

import java.text.SimpleDateFormat;

public class ConfirmResultFragment extends Fragment {

    public interface ConfirmResultDelegate {
        void onResultClosed();
    }
    ConfirmResultDelegate delegate;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MODEL = "result_model";

    private IdModel mModel;

    private Button  mCloseButton;

    private TextView mStatusLabel;

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


    public ConfirmResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param model Parameter 1.
     * @return A new instance of fragment ConfirmResultFragment.
     */
    public static ConfirmResultFragment newInstance(IdModel model) {
        ConfirmResultFragment fragment = new ConfirmResultFragment();
        fragment.mModel = model;
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
        View v = inflater.inflate(io.confirm.sample.R.layout.fragment_confirm_result, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        mCloseButton = (Button)v.findViewById(io.confirm.sample.R.id.close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().getFragmentManager().popBackStack();
               if (delegate != null)
                   delegate.onResultClosed();
            }
        });
        mStatusLabel = (TextView)v.findViewById(io.confirm.sample.R.id.status_label);
            mStatusLabel.setText(mModel.status);

        IdBioModel bio = mModel.identity.bio;
        IdClassificationModel classification = mModel.identity.classification;
        IdIssuanceModel issuance = mModel.identity.issuance;

        mFirstNameLabel = (TextView)v.findViewById(io.confirm.sample.R.id.first_name);
            mFirstNameLabel.setText(bio.firstName);
        mLastNameLabel = (TextView)v.findViewById(io.confirm.sample.R.id.last_name);
            mLastNameLabel.setText(bio.lastName);
        mAddressLabel = (TextView)v.findViewById(io.confirm.sample.R.id.address);
            mAddressLabel.setText(bio.address);
        mCityLabel = (TextView)v.findViewById(io.confirm.sample.R.id.city);
            mCityLabel.setText(bio.city);
        mStateLabel = (TextView)v.findViewById(io.confirm.sample.R.id.state);
            mStateLabel.setText(bio.state);
        mZipLabel = (TextView)v.findViewById(io.confirm.sample.R.id.zip);
            mZipLabel.setText(bio.zip);

        SimpleDateFormat df =  new SimpleDateFormat("MM/dd/yyyy");

        mDOBLabel = (TextView)v.findViewById(io.confirm.sample.R.id.dob);
        if (bio.dob != null)
            mDOBLabel.setText(df.format(bio.dob));

        mIssTypeLabel = (TextView)v.findViewById(io.confirm.sample.R.id.iss_type);
            mIssTypeLabel.setText(classification.type);
        mIssStateLabel = (TextView)v.findViewById(io.confirm.sample.R.id.iss_state);
            mIssStateLabel.setText(classification.state);

        mNumberLabel = (TextView)v.findViewById(io.confirm.sample.R.id.number);
            mNumberLabel.setText(issuance.number);
        mDateIssuedLabel = (TextView)v.findViewById(io.confirm.sample.R.id.issued);
            if (issuance.issued != null)
                mDateIssuedLabel.setText(df.format(issuance.issued));
            else
                mDateIssuedLabel.setText("N/A");
        mExpirationLabel = (TextView)v.findViewById(io.confirm.sample.R.id.expiration);
            if (issuance.expiration != null)
                mExpirationLabel.setText(df.format(issuance.expiration));
            else
                mExpirationLabel.setText("N/A");

        mServerLabel = (TextView)v.findViewById(io.confirm.sample.R.id.server_header);
        mFailureLayout = (LinearLayout)v.findViewById(io.confirm.sample.R.id.failure_layout);
        mFailureLabel = (TextView)v.findViewById(io.confirm.sample.R.id.failure);

        if (mModel.failureReasons.size() > 0) {
            mFailureLabel.setText(mModel.failureReasons.get(0));
        } else {
            mServerLabel.setVisibility(View.INVISIBLE);
            mFailureLayout.setVisibility(View.INVISIBLE);
        }
    }

}
