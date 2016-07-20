#![Confirm logo](https://s3-us-west-2.amazonaws.com/confirm.public/web-images/confirm-logo_43x34.png) Confirm.io (BETA)

[Confirm.io](http://www.confirm.io/) provides simple, safe, and secure mobile ID authentication solutions. Our cloud API and paired image capture SDK empower applications to more seamlessly collect customer information and authenticate the identity of their users. 

This SDK requires an API key issued by Confirm.io in order to submit documents to our cloud. If you wish to test out the SDK, [please contact Confirm](http://www.confirm.io/#!contact/i66dd) to receive your demo API key.

## Requirements

* Rear-facing camera
* Android SDK version 21 or later (Support of earlier SDK versions coming soon!)
* armeabi-v7a, arm64-v8, x86, or x86_64 processor

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

<!-- Confirm SDK - required -->
<application ...>
  <activity android:name="io.confirm.confirmsdk.ConfirmCameraActivity"/>
</application>
```

## Integration

The SDK is split into two core components:

1. `ConfirmCapture` - Intelligent document image capture of both the front and back of the user's ID.
2. `ConfirmSubmit` - Submission of captured imagery and data to api.confirm.io (requires API key)

### Capturing the document

Capturing a document requires the use of `ConfirmCapture`. This utility is responsible for opening the video stream, overlaying the framing and responsive user messaging, detecting if the document is aligned with the frame, and triggering automatic capture. As it completes, `ConfirmCapture` will populate a `ConfirmPayload` object which retains the details of the capture to be submitted to the Confirm API. 

#### Sample
```java
public class IntroFragment extends Fragment implements ConfirmCaptureDelegate, ConfirmSubmitDelegate { {
  private ConfirmCapture confirmCapture = null;
  
  @Override
  public void onViewCreated(View v, Bundle savedInstanceState) {
    // ConfirmCapture will be opened on the button click
    Button button = (Button)v.findViewById(R.id.try_now_button);
    final IntroFragment self = this;

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mConfirmCapture = new ConfirmCapture();
        mConfirmCapture.delegate = self;

        // the second parameter is the ID of the view that we want to put our UX into
        mConfirmCapture.beginCapture(getActivity(), R.id.container);
      }
    });
  }
  
  // ------------------- ConfirmCaptureDelegate methods -------------------
  @Override
  public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
    // ConfirmCapture completed, and payload is ready to be sent to Confirm's cloud
    doSubmit(payload); // Method contents shown below
  }
  
  @Override
  public void onConfirmCaptureDidCancel() {
    // Capture was dismissed without capturing an image
  }
}
```

### Submit to Confirm.io's API

After both the front and back of the ID have been captured, the payload can then be sent to Confirm.io's cloud API for data extraction and authentication. 

Before submission to the Confirm API, developers must obtain an API key from Confirm's team directly. It is highly recommended that mobile developers leverage Confirm's consumer key authentication protocols when submitting images directly to Confirm's API. To learn more, [please visit our documentation](https://confirm.readme.io/docs/authentication).

The code containing verification API is located in the object `ConfirmSubmit`.

To set the API key:

```obj-c
ConfirmSubmitTask task = new ConfirmSubmitTask(payload, "{YOUR_API_KEY_HERE");
```

#### Sample

```java
// ...
private void doSubmit(ConfirmPayload payload) {
  String apiKey = "4bfb21eb-7139-4820-95f7-e12d06556a2d";
  ConfirmSubmitTask task = new ConfirmSubmitTask(payload, apiKey);
  task.delegate = this;
  task.execute();
}

// ------------------- ConfirmSubmitDelegate methods -------------------
public void onConfirmSubmitError(final String error) {
  Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
}

public void onConfirmSubmitSuccess(final IdModel model) {
  final Activity activity = getActivity();
  if (activity != null) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (model.didPass())
          // Request completed - document deamed authentic
        else if (model.didFail())
          // Request completed - document deamed potentially fraudulent
        else
          // Request completed, but Confirm was unable to provide an authentication status for
          // the document. This is usually due to image or document damage
      }
    });
  }
}
// ...
```
