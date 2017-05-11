![Confirm logo](https://s3-us-west-2.amazonaws.com/confirm.public/web-images/confirm-logo_43x34.png) Confirm.io

[Confirm.io](http://www.confirm.io/) provides simple, safe, and secure mobile ID authentication solutions. Our cloud API and paired image capture SDK empower applications to more seamlessly collect customer information and authenticate the identity of their users. 

This SDK requires an API key issued by Confirm.io in order to submit documents to our cloud. If you wish to test out the SDK, [please contact Confirm](https://www.confirm.io/sign-up/) to receive your demo API key.

## Requirements

* Rear-facing camera
* 5 megapixel camera or better
* Android device OS 4.0.1 (Ice Cream Sandwich) or higher
* ARM7 (armeabi-v7a) or ARM64 (arm64-v8a) processor (or later). Intel is not supported. For more information, [please read the guide on ABI Management located here](https://developer.android.com/ndk/guides/abis.html)
* Android Studio SDK version 14 or later

## Sample App

Seeing how someone else is using the SDK is the easiest way to learn. To see the SDK in action, feel free to check out the bundled [Sample](https://github.com/confirm-io/confirm-Android-SDK/tree/master/Sample) app.

## Setup

### Manual Download

1. [Download the latest version of the SDK](https://github.com/confirm-io/confirm-Android-SDK/archive/master.zip)
2. Edit AndroidManifest.xml

	```xml
	<uses-sdk android:minSdkVersion="14" />

	<!-- Permission to vibrate - recommended, allows vibration feedback on scan -->
	<uses-permission android:name="android.permission.VIBRATE" />

	<!-- Permission to use camera - required -->
	<uses-permission android:name="android.permission.CAMERA" />
	<uses-feature android:name="android.hardware.camera" />
	<uses-feature android:name="android.hardware.camera.autofocus" />

	<!-- Permission to access a network - required -->
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

	<!-- Permission to access a storage write - required -->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
		
	<application ...>
		<!-- Confirm SDK - required -->
		<activity
			android:name="io.confirm.confirmsdk.ConfirmCameraActivity"
			android:configChanges="orientation|screenSize"
			android:screenOrientation="landscape" />
	</application>
	```

	*Note: If you are using Android Studio, make sure `app/build.gradle` has `minSdkVersion 14` as well.*

3. In the Android Manifest, locate the activity used to call the SDK and add the following:

	`android:configChanges="orientation|screenSize"`
	
	In the sample app, `MainActivity.java` is the Activity that calls the SDK, so `AndroidManifest.xml` looks like this:

	```xml
	<activity
		android:name=".MainActivity"
		android:configChanges="orientation|screenSize"
		android:screenOrientation="portrait">
		<intent-filter>
			<action android:name="android.intent.action.MAIN"/>
			<category android:name="android.intent.category.LAUNCHER"/>
		</intent-filter>
	</activity>
	```

## Integration

The SDK is split into two core components:

1. `ConfirmCapture` - Intelligent document image capture of both the front and back of the user's ID.
2. `ConfirmSubmit` - Submission of captured imagery and data to api.confirm.io (requires API key)

### Capturing The Document

Capturing a document requires the use of `ConfirmCapture`. This utility is responsible for:

-  opening and closing the video stream
-  overlaying the frame
-  providing responsive user messaging
-  detecting if the document is aligned with the frame
-  triggering automatic capturing of the document

As it completes, `ConfirmCapture` will populate a `ConfirmPayload` object. This object retains the details of the capture for submission to the Confirm API.  

To enable facial match, use `ConfirmCapture.getInstance().enableFacialMatch();` flag to enable the capture process specific to taking a "selfie" to match to the face on the front side of the ID. Without the flag, it will not process the "selfie" mode.

#### Sample
*Some code snippets are provided. See included sample app for a comprehensive example.*
*Full source code is located in [here](https://github.com/confirm-io/confirm-Android-SDK-staging/blob/master/Sample/app/src/main/java/io/confirm/sample/IntroFragment.java).*

```java
import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmCaptureListener;
import io.confirm.confirmsdk.ConfirmSubmitListener;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.models.IdModel;

public class SampleActivity extends AppCompatActivity
		implements ConfirmCaptureListener, ConfirmSubmitListener {

	private String TAG = "SampleActivity";

	// Must be initialized before using ConfirmSDK
	private Activity mActivity = null;
	private ConfirmCaptureListener mCaptureListener = null;
	private ConfirmSubmitListener mSubmitListener = null;

	private Button mTryButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Note: mActivity, mCaptureListener, and mSubmitListner
		MUST be initialized before using ConfirmSDK */
		mActivity = getActivity();
		mCaptureListener = this;
		mSubmitListener = this;

		setContentView(com.bundle.sample.R.layout.activity_sample);

		mTryButton = (Button)findViewById(com.bundle.sample.R.id.button);

		mTryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view){
				/* Use enableFacialMatch() to turn on the facial match feature */
				ConfirmCapture.getInstance().enableFacialMatch();
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

### Submit To Confirm.io's API

After both the front and back of the ID have been captured, the payload can then be sent to Confirm.io's cloud API for data extraction and authentication. 

Before submission to the Confirm API, developers must obtain an API key from Confirm's team directly. It is highly recommended that mobile developers leverage Confirm's consumer key authentication protocols when submitting images directly to Confirm's API. To learn more, [please visit our documentation](https://confirm.readme.io/docs/authentication).

The code containing verification API is located in the object `ConfirmSubmit`.

To set the API key:

```java
ConfirmSubmitTask task = new ConfirmSubmitTask(submitListener, payload, "{YOUR_API_KEY_HERE}");
```

To cleanup the capture data after submission:

```java
ConfirmCapture.getInstance().cleanup();
```
*Note: It is important to call `cleanup` after the submission to remove unnecessary data from the device and prepare a clean environment for the next capture.*

#### Sample
*Some code snippets are provided. See included sample app for a comprehensive example.*

```java
/**
 * Submit payload object to Confirm  API.
 * @param payload
 */
private void doSubmit(ConfirmPayload payload) {
	String apiKey = "{YOUR_API_KEY_HERE}"; // Please put valid API key in here.

	// mSubmitListener must be initialized before.
	ConfirmSubmitTask task = new ConfirmSubmitTask(mSubmitListener, payload, apiKey);
	task.execute();
}

/**
 * Callback from ConfirmSDK after submitting the result and received an error.
 * @param error Error message
 */
@Override
public void onConfirmSubmitError(final String error) {
	Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
	ConfirmCapture.getInstance().cleanup(); // Purge details of the capture
}

/**
 * Callback from ConfirmSDK after submitting the result and received the result.
 * @param idModel
 * @param faceModel
 */
@Override
public void onConfirmSubmitSuccess(final IdModel idModel, final FaceVerifyResponse faceModel) {
	if (idModel.didPass()) {
		// Request completed - document deamed authentic
		showResults(idModel, faceModel);
	}
	else if (idModel.didFail()) {
		// Request completed - document deamed potentially fraudulent
		showResults(idModel, faceModel);
	}
	else {
		// Request completed, but Confirm was unable to provide an authentication status for
		// the document. This is usually due to image or document damage
	}

	ConfirmCapture.getInstance().cleanup(); // Purge details of the capture
}

/**
 * Callback from ConfirmSDK when submission is cancelled.
 */
@Override
public void onConfirmSubmitCancel() {

}
// ...
```

Once the submission process begins, the following callbacks are available for progress start, status, and finish. 

Note that the `onConfirmUploadProgressStart()` and `onConfirmUploadProgressFinish()` will each be called once. The `onConfirmUploadProgressStatus(float progress)` callback will be called throughout the submission upload/processing cycle. This is ideal for determinate-style progress indicators and similar UI elements.

```java
/**
 * Callback from ConfirmSDK when uploading process started.
 */
@Override
public void onConfirmUploadProgressStart() {

}

/**
 * Callback from ConfirmSDK during uploading process.
 * @param progress Start to end range is in [0.0, 1.0].
 */
@Override
public void onConfirmUploadProgressStatus(float progress) {

}

/**
 * Callback from ConfirmSDK when uploading process finished.
 */
@Override
public void onConfirmUploadProgressFinish() {

}
```