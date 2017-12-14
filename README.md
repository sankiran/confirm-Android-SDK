<a name=top>![Confirm logo](https://s3-us-west-2.amazonaws.com/confirm.public/web-images/confirm-logo_43x34.png) **Confirm.io**</a>

[Confirm.io](http://www.confirm.io/) provides simple, safe, and secure mobile ID authentication solutions. Our cloud API and paired image capture SDK empower applications to more seamlessly collect customer information and authenticate the identity of their users. 

This SDK requires an API key, issued by Confirm.io, in order to submit documents to our cloud. If you wish to test out the SDK, [please contact us](https://www.confirm.io/sign-up/) to receive your demo API key.
<br><br>

Quick Jump Navigation <br>
-  [Get the SDK](#get-the-sdk) <br>
-  [Configure your project](#configure) <br>
-  [Capture and submit](#capture-and-submit) <br>
-  [Optional - Customize the user experience](#customizing) <br>
-  [Optional - Back-only authentication](#backauth)

<hr><br>

## Requirements

* Rear-facing camera.
* 5 megapixel camera, or better.
* Android device OS 4.0.1 (Ice Cream Sandwich), or higher.
* ARM7 (armeabi-v7a) or ARM64 (arm64-v8a) processor (or later). Intel is not supported. For more information, [please read the guide on ABI Management located here](https://developer.android.com/ndk/guides/abis.html)
* Android Studio SDK version 14, or later.


#### Want to see a quick sample?
*Seeing a prepared sample app using the SDK is the easiest way to learn. To see the SDK in action, please [check out the bundled sample app](https://github.com/confirm-io/confirm-Android-SDK/tree/master/Sample). It is highly recommended that you compare your integration and usage against the sample.*

<hr>
<br>


## <a name=get-the-sdk></a>Get the SDK
There are two ways to get the latest SDK:

1. Grab a [fresh clone](https://github.com/confirm-io/confirm-Android-SDK) of the repository:<br>
`git clone git@github.com:confirm-io/confirm-Android-SDK.git`<br>
or<br>
`git clone https://github.com/confirm-io/confirm-Android-SDK.git`
2. Grab the [latest version](https://github.com/confirm-io/confirm-Android-SDK/archive/master.zip) of the SDK as a zip.

*<a href=#top>Return to top</a>*
<hr><br>

## <a name=configure></a>Configure your project
There are two steps to add the SDK to your project:
<br><br>

#### Decide on architecture support
The SDK is provided in an Android Archive Library (AAR) format. The AAR includes universal ARM support (both armv7 and arm64-v8a).

If you don’t want the size inherent with universal support, and want to only support a single architecture, you have a couple of options:

- [Configure your project to use APK splits](https://developer.android.com/studio/build/configure-apk-splits.html): if you go this route, you can produce copies of your APK for each architecture.
- Manual modification of the AAR: the supplied AAR is a compressed ZIP file. If you wish to strip out a specific architecture, follow these steps:
	1. Copy the AAR somewhere separate from your project.
	2. Change the file extension to `zip` (e.g. `confirmsdk-arm-release.zip`).
	3. Unzip the archive using your preferred method.
	4. Delete the zip. This avoids accidental nesting of the previous zip in the new zip you will produce.
	5. Under the `jni` folder you will see the supported architectures (i.e. `arm64-v8a`, etc). Delete the architecture(s) you don’t want.
	6. Compress/zip the contents of this folder into a new archive.
	7. Rename the archive to match the previous AAR (ex: `confirmsdk-arm-release.aar`).
<br><br>

#### Add and link the AAR
You will need to add the SDK to your project. There are few different ways to add and link the AAR. If you are using gradle, one simple method is to create a `libs` folder under your `app` folder and copy the AAR there. You can then link the AAR through your gradle file. 
<br><br>Here is an example:<br>

```java
dependencies {
    // other dependencies
    compile(name:'confirmsdk-arm-release', ext:'aar')
}
repositories {
    flatDir { dirs 'libs' }
}
```



#### Modify your app’s `AndroidManifest.xml` file
You will need to give permissions to your app and configure the launching activity. <a href=Docs/1_EditAndroidManifest.md target=_blank>Click here</a> to view the required additions.

*<a href=#top>Return to top</a>*
<hr><br>

## <a name=capture-and-submit></a>Capture and Submit

The SDK is split into two core components for capturing and submitting:

1. `ConfirmCapture` - Intelligent document image capture of both the front and back of the user's ID.
2. `ConfirmSubmit` - Submission of captured imagery and data to api.confirm.io (requires API key).


#### (Optional) Configuring the capture
*Note: configuring the document's country of origin and document type are both optional.

##### Configuring the document's country of origin
You may choose to set the document's country of origin prior to starting a capture session. To configure the country, provide an ISO (3 letter) code as follows:

```
ConfirmCapture.getInstance().setCountryCode(String countryCode);
```

You can also request an updated list of supported countries from Confirm in a hash map format:

```
import io.confirm.confirmsdk.ConfirmQueryTask;
ConfirmQueryTask queryTask = new ConfirmQueryTask();
HashMap<String, String> countryListMap = queryTask.getCountryList(mApiKey, ""); 

// Note: mApiKey is your provided API key
```

This defaults to "USA", when not provided.

*This is a convenience method. For additional information on what this method returns, see the [API reference documentation](https://confirm.readme.io/reference#coveragecountries).*

##### Configuring the document type
You may choose to set the document type prior to starting a capture session. The supported document types are provided below as `ConfirmDocumentType` enum values:

```
ID1 = Identification / Driver's License
ID3 = Passport
```

To configure the document type, provide one of the above enums as follows:

```
// Configure the session for passport capture
ConfirmCapture.getInstance().setDocumentType(ID3);
```

This defaults to ID1, when not provided.

*For additional information on the various identification card standards, please see the [IEC_7810 reference](https://en.wikipedia.org/wiki/ISO/IEC_7810).*

### Capturing the document


Capturing a document requires the use of `ConfirmCapture`. This utility is responsible for:

-  opening and closing the video stream;
-  overlaying the frame;
-  providing responsive user messaging;
-  detecting if the document is aligned with the frame; and
-  triggering automatic capture of the document.

When `ConfirmCapture` completes, it will populate a `ConfirmPayload` object. This object retains the details of the capture for submission to the Confirm API. 

If you require the captured image URLs (e.g. for use outside of the SDK), you may access them via the `ConfirmPayload` object. The payload object must *already* be populated. Consider these URLs *volatile*; they should be copied to a user-specified path prior to submitting the payload.

- `fetchFrontImageURL` - front ID image URL
- `fetchBackImageURL` - back ID image URL
- `fetchSelfieImageURL` - selfie image URL

*Optional*: You may configure the capture for international documents by using a 3-letter ISO country code. If no country is supplied, the capture will default to use “USA”. Please <a href=Docs/Supported_Countries.txt target=_blank>visit this page</a> to view an example of supported countries and codes.

Example:

`ConfirmCapture.getInstance().setCountryCode("USA");`


##### Facial Matching
To enable facial match, use the `ConfirmCapture.getInstance().enableFacialMatch();` flag. This enables the capture process specific to taking a "selfie”, matching it to the face on the front side of the ID. Without the flag, the capture will not include the "selfie" step.

##### Back-only authentication
To enable *back-only authentication*, use the `ConfirmCapture.getInstance().enableBackAuth()` flag. For more information, see the [discussion on back-only authentication](#backauth).

##### Document Type
You may configure the type of document for capture. `ConfirmCapture.ConfirmDocumentType` provides two formats:

- `ID1` - most government-issued ID cards.
- `ID3` - passports.

If no document type is supplied, the capture will default to use `ID1`. 

Example:

`ConfirmCapture.getInstance().setDocumentType(ConfirmCapture.ConfirmDocumentType.ID1);`

*Note: When the document type is set to ID3, it only requires the front image; enabling facial match will not work.*


#### Submitting the payload

After capturing both the front and back of the ID, the payload can then be sent to Confirm’s cloud API for data extraction and authentication. 

Before submission to the Confirm API, developers must first [obtain an API key](https://www.confirm.io/sign-up/). It is highly recommended that mobile developers leverage Confirm's consumer key authentication protocols when submitting images directly to Confirm's API. To learn more, [please visit our documentation](https://confirm.readme.io/docs/authentication).

The code containing the verification API is access via the `ConfirmSubmit` object.

To set the API key:

```java
ConfirmSubmitTask task = new ConfirmSubmitTask(submitListener, payload, "{YOUR_API_KEY_HERE}");
```

To cleanup the captured data after submission:

```java
ConfirmCapture.getInstance().cleanup();
```
*Note: It is important to call `cleanup` after the submission to remove unnecessary data from the device and prepare a clean environment for the next capture.*

*Optional:*
During the submission process, you may watch the progress of the submission state via `getState` on the `ConfirmSubmitTask` object. For more information, review the possible values of the enum `ConfirmSubmitState`.

<a href=Docs/2_SubmissionAndProgress.md target=_blank>Click here</a> to view instructions for handling both the submission and progress state.

*<a href=#top>Return to top</a>*
<hr><br>

## <a name=customizing></a>Optional - Customize the user experience

The SDK allows you to include custom instruction and/or review screens.

- The `Instruction` view can help guide users before performing a capture. You may wish to ask the user to prepare their document or make sure there is ample lighting prior to continuing.
- The `Review` view enables the user to see the result of a capture. The user can decide to keep the image, and continue forward, or perform a new capture.

To enable custom views, `ConfirmCustomProxy` must be registered and assigned to `ConfirmCapture` before starting the ConfirmSDK capture session.

```java
ConfirmCapture.getInstance().setCustomProxy(mCustomProxy);
```

`ConfirmCustomProxy` requires two methods to be implemented:

- `getViewForInstruction(ConfirmCapture.ConfirmCameraSide side)`
- `getViewForReview(ConfirmCapture.ConfirmCameraSide side)`

Each method can return a `Fragment` object as a custom view. You may also return `null` if you do not want to use a custom view for a specific capture side.

Please <a href=Docs/3_CustomViews.md target=_blank>click here</a> for detailed information.

*<a href=#top>Return to top</a>*
<hr><br>

## <a name=backauth></a>Optional - Back-only Authentication

The SDK has two different authentication mode: *full* and *back-only* authentication:

- The `full authentication` workflow scans the document for a full authentication process. Government-issued IDs will have both the front and back captured. Passports will have the front captured.
- The `back-only authentication` workflow processes the back of a government-issued ID for a streamlined validation.

#### Using Back-only authentication

By default, `full authentication` mode is enabled. To configure the capture session for `back-only authentication`, set the `ConfirmCapture.getInstance().enableBackAuth();` flag. This must be called before starting `beginCapture(...)` and beginning a ConfirmSDK capture session.

*Note: `back-only authentication` using the back of a government-issued ID card for quick validation. The `enableFacialMatch()` flag must not be called when this mode is configured.*

#### Receiving the result

Both authentication modes share the same model, `IdModel`, but they populate the model with different information.

To determine which information the model has, the `idModel.isFullAuth()` flag returns `true` if the model has full authentication information, or `false` if the model has back-only authentication information.

The `back-only authentication` model contains information that helps ascertain if the government-issued ID is fraudulent. The model provides this result in `recommendation`:

```java
idModel.getBackAuth().getRecommendation(); // Returns a result string
```

There are three possible flags for the recommendation:

- `accept` - we recommend that the user be accepted; no need to evaluate further.
- `reject` - we recommend that the user be outright rejected; no need to evaluate further. 
- `review` - we recommend the user resubmit their document or be evaluated through different methods; we are unable to give guidance one way or the other.

*<a href=#top>Return to top</a>*