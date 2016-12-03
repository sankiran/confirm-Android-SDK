#![Confirm logo](https://s3-us-west-2.amazonaws.com/confirm.public/web-images/confirm-logo_43x34.png) Confirm.io

[Confirm.io](http://www.confirm.io/) provides simple, safe, and secure mobile ID authentication solutions. Our cloud API and paired image capture SDK empower applications to more seamlessly collect customer information and authenticate the identity of their users. 

This SDK requires an API key issued by Confirm.io in order to submit documents to our cloud. If you wish to test out the SDK, [please contact Confirm](http://www.confirm.io/#!contact/i66dd) to receive your demo API key.

## Requirements

* Rear-facing camera
* Android SDK version 14 or later
* 5 megapixel camera or better
* arm7 (armeabi-v7a) processor or later

## Sample app

Seeing how someone else is using the SDK is the easiest way to learn. To see the SDK in action, feel free to check out the bundled [Sample](https://github.com/confirm-io/confirm-Android-SDK/tree/master/Sample) app.

## Setup

### Manual download

1. [Download latest version of the SDK](https://github.com/confirm-io/confirm-Android-SDK/archive/master.zip)
2. Edit AndroidManifest.xml
```xml
<uses-sdk android:minSdkVersion="8" />

<!-- Permission to vibrate - recommended, allows vibration feedback on scan -->
<uses-permission android:name="android.permission.VIBRATE" />

<!-- Permission to use camera - required -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />

<!-- Permission to access a network - required --->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<application ...>
<!-- Confirm SDK - required -->
    <activity
		android:name="io.confirm.confirmsdk.ConfirmCamera1Activity"
		android:screenOrientation="landscape" />
</application>
```

## Integration

The SDK is split into two core components:

1. `ConfirmCapture` - Intelligent document image capture of both the front and back of the user's ID.
2. `ConfirmSubmit` - Submission of captured imagery and data to api.confirm.io (requires API key)

### Capturing the document

Capturing a document requires the use of `ConfirmCapture`. This utility is responsible for opening the video stream, overlaying the framing and responsive user messaging, detecting if the document is aligned with the frame, and triggering automatic capture. As it completes, `ConfirmCapture` will populate a `ConfirmPayload` object which retains the details of the capture to be submitted to the Confirm API. 

#### Sample
*Some brief code snippets are provided. See included sample app for a comprehensive example.*
```java
import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmSubmitDelegate;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.IdModel;

public class IntroFragment extends Fragment implements ConfirmSubmitDelegate {

  private String TAG = "IntroFragment";
  private Activity mActivity = null;
  private Button mTryButton = null;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    /* mActivity must be initialized before using ConfirmSDK */
    mActivity = getActivity();
    return inflater.inflate(R.layout.fragment_intro, container, false);
  }

  @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        mTryButton = (Button)v.findViewById(R.id.check_id_button);
        mTryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /* -----This is where we start the ConfirmSDK capture session ----- */
              ConfirmCapture.getInstance().beginCapture(mActivity);
            }
        });
  }
  
  /* -----ConfirmSDK Callback----- */
  /* This gets called from ConfirmSDK when capture is completed */
  public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
    // Payload is ready to be sent to Confirm's cloud
    doSubmit(payload);
  }
  
  /* This gets called from ConfirmSDK when the capture is dismissed */
  public void onConfirmCaptureDidCancel() {
    // Optional for cancel conditions (user pressed back, etc)
  }
}
```

### Submit to Confirm.io's API

After both the front and back of the ID have been captured, the payload can then be sent to Confirm.io's cloud API for data extraction and authentication. 

Before submission to the Confirm API, developers must obtain an API key from Confirm's team directly. It is highly recommended that mobile developers leverage Confirm's consumer key authentication protocols when submitting images directly to Confirm's API. To learn more, [please visit our documentation](https://confirm.readme.io/docs/authentication).

The code containing verification API is located in the object `ConfirmSubmit`.

To set the API key:

```java
ConfirmSubmitTask task = new ConfirmSubmitTask(payload, "{YOUR_API_KEY_HERE}");
```

#### Sample
*Some brief code snippets are provided. See included sample app for a comprehensive example.*
```java
// ...
private void doSubmit(ConfirmPayload payload) {
  String apiKey = "{YOUR_API_KEY_HERE}";
  ConfirmSubmitTask task = new ConfirmSubmitTask(payload, apiKey);
  task.delegate = this;
  task.execute();
}

public void onConfirmSubmitError(final String error) {
    // A submit error occured
}

public void onConfirmSubmitSuccess(final IdModel model) {
  if (mActivity != null) {
    if (model.didPass() || model.didFail())
      // Success - Use the model
    else
      // Failure or did not pass
  }
}
// ...
```