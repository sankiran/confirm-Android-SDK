<a name=top>![Confirm logo](https://s3-us-west-2.amazonaws.com/confirm.public/web-images/confirm-logo_43x34.png) **Confirm.io**</a>

*To return to the starting README doc, <a href=../README.md >click here</a>.*

## Confirm SDK - Submissions & Progress State
Quick Jump<br>
1. [Submitting the payload](#submissions) <br>
2. [Watching the progress state](#progress-state)

<hr><br>

### <a name=submissions></a>Submitting the payload

*Code snippets are provided. See included sample app for a comprehensive example.*

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

*<a href=#top >Return to top</a>*
<hr><br>

### <a name=progress-state></a>Watching the progress state

Once the submission process begins, the following callbacks are available for progress start, status, and finish. 

The `onConfirmUploadProgressStart()` and `onConfirmUploadProgressFinish()` will each be called once. 

The `onConfirmUploadProgressStatus(float progress)` callback will be called throughout the submission upload and processing cycle. This is ideal for determinate-style progress indicators and similar UI elements.

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

*<a href=#top >Return to top</a>*