<a name=top>![Confirm logo](https://s3-us-west-2.amazonaws.com/confirm.public/web-images/confirm-logo_43x34.png) **Confirm.io**</a>

*To return to the starting README doc, <a href=../README.md>click here</a>.*

## Confirm SDK - Custom Views
Quick Jump<br>
1. [Using the `ConfirmCustomProxy` object](#confirmcustomproxy) <br>
2. [The custom Instruction view](#custom-instruction) <br>
3. [The custom Review view](#custom-review)

<hr><br>

## <a name=confirmcustomproxy></a>Using the `ConfirmCustomProxy` object

`ConfirmCustomProxy` is an optional class you may leverage to include custom views for the instruction and review screens. These views are interstitial, appearing before and/or after capture events. If the class is not implemented, no custom view is used between capture events. 

Example usage:
*A code snippet is provided. You can view the full source by <a href=https://github.com/confirm-io/confirm-Android-SDK-staging/blob/master/Sample/app/src/main/java/io/confirm/sample/IntroFragment.java target=_blank>clicking here</a>.*

```java
import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmCustomProxy;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmCaptureListener;
import io.confirm.confirmsdk.ConfirmSubmitListener;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.models.FaceVerifyResponse;
import io.confirm.confirmsdk.models.IdModel;

public class SampleActivity extends AppCompatActivity
		implements ConfirmCaptureListener, ConfirmSubmitListener, ConfirmCustomProxy {

	private String TAG = "SampleActivity";

	// Must be initialized before using ConfirmSDK
	private Activity mActivity = null;
	private ConfirmCaptureListener mCaptureListener = null;
	private ConfirmSubmitListener mSubmitListener = null;
	private ConfirmCustomProxy mCustomProxy = null;
	
	private Button mTryButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Note: mActivity, mCaptureListener, and mSubmitListner
		MUST be initialized before using ConfirmSDK */
		mActivity = getActivity();
		mCaptureListener = this;
		mSubmitListener = this;
		mCustomProxy = this;

		setContentView(com.bundle.sample.R.layout.activity_sample);

		mTryButton = (Button)findViewById(com.bundle.sample.R.id.button);

		mTryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view){
				/* Use enableFacialMatch() to turn on the facial match feature */
				ConfirmCapture.getInstance().enableFacialMatch();
				/* Optional: Use setCustomProxy() to turn on custom view */
				ConfirmCapture.getInstance().setCustomProxy(mCustomProxy);
				/* This is where we start the ConfirmSDK capture session */
				ConfirmCapture.getInstance().beginCapture(mListener, mActivity);
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
}
```

*<a href=#top>Return to top</a>*
<hr><br>

## <a name=custom-instruction></a>The custom Instruction view

The Instruction view can use two methods to interact with ConfirmSDK:

- `ConfirmCapture.getInstance().advanceInstructions()` - tells the SDK to continue to the capture.
- `ConfirmCapture.getInstance().cancelInstructions()` - tells the SDK to cancel the current capture.

Example usage:
*A code snippet is provided. You can view the full source by <a href=https://github.com/confirm-io/confirm-Android-SDK-staging/blob/master/Sample/app/src/main/java/io/confirm/sample/InstructionFragment.java target=_blank>clicking here</a>.*

```java
/**
 * InstructionFragment.java
 */
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
// ...
```

<br><br>
Example usage (from the class implementing the `ConfirmCustomProxy` interface):
*A code snippet is provided. You can view the full source by <a href=https://github.com/confirm-io/confirm-Android-SDK-staging/blob/master/Sample/app/src/main/java/io/confirm/sample/IntroFragment.java target=_blank>clicking here</a>.*

```java
/**
 * IntroFragment.java
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
// ...
```

If the method returns `null`, then there will not be a custom view prior to a capture event. In that case, the SDK will move forward to the next capture event.

*Note: The sample code above shows that the custom instruction view is only used for the front capture.*

*<a href=#top>Return to top</a>*
<hr><br>

## <a name=custom-review></a>The Custom Review view

The Review view can use two methods to interact with ConfirmSDK:

- `ConfirmCapture.getInstance().advanceVerification()` - tells the SDK to accept the image and continue.
- `ConfirmCapture.getInstance().cancelVerification()` - tells the SDK to perform a new capture (retake).

Example usage:
*A code snippet is provided. You can view the full source by <a href=https://github.com/confirm-io/confirm-Android-SDK-staging/blob/master/Sample/app/src/main/java/io/confirm/sample/ReviewFragment.java target=_blank>clicking here</a>.*

```java
/**
 * ReviewFragment.java
 */
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
// ...
```
<br><br>
Example usage (from the class implementing the `ConfirmCustomProxy` interface):

*A code snippet is provided. You can view the full source by <a href=https://github.com/confirm-io/confirm-Android-SDK-staging/blob/master/Sample/app/src/main/java/io/confirm/sample/IntroFragment.java target=_blank>clicking here</a>.*

```java
/**
 * IntroFragment.java
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
// ...
```

If the method returns `null`, then there will not be a custom view prior to a capture event. In that case, the SDK will move forward to the next capture event.

*Note: The sample code above shows that the custom review view is only used for the back and selfie capture.*

*<a href=#top>Return to top</a>*