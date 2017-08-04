<a name=top>![Confirm logo](https://s3-us-west-2.amazonaws.com/confirm.public/web-images/confirm-logo_43x34.png) **Confirm.io**</a>

*To return to the starting README doc, <a href=../README.md>click here</a>.*

## Confirm SDK - `AndroidManifest.xml` modifications


Modify your app’s `AndroidManifest.xml` to include the following permissions:

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

<br>
Make the following changes to the activity calling the SDK. In this example, `MainActivity.java` is the Activity that calls the SDK, so it looks like this:

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

*<a href=#top>Return to top</a>*