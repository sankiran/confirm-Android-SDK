Confirm Android Sample

create new project - single Activity

added Button to layout

added onClickListener to Button

File Menu -> New -> New Module -> import .aar package
tap the […] button to locate the sdk-all-release.aar

added ‘implements ConfirmCaptureDelegate, ConfirmSubmitDelegate’
add ConfirmCapture creation, delegate, beginCapture call

add ConfirmCaptureDelegate methods
add ConfirmSubmitDelegate methods

add to AndroidManifest.xml

within <manifest>

	<uses-permission android:name="android.permission.CAMERA" />
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.VIBRATE"/>

	<uses-feature android:name="android.hardware.camera" />
	<uses-feature android:name="android.hardware.camera.autofocus" />

within <application>

        <activity
            android:name="io.confirm.confirmsdk.ConfirmCameraActivity"
            android:screenOrientation="portrait" />


[REMOVE THIS FILE BEFORE REPO GOES PUBLIC]