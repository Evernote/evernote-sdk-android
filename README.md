Evernote SDK for Android version 1.0
====================================

Evernote API version 1.23

Notice
------
This is a non-backwards compatible udpate.  Please git pull before following setup instructions.

Overview
--------
This SDK wraps the [Evernote Cloud API ](http://dev.evernote.com/documentation/cloud/) and provides OAuth authentication functionality. The SDK is provided as an Android Library project that can be included in your application.

Prerequisites
-------------
In order to use the code in this SDK, you need to obtain an API key from http://dev.evernote.com/documentation/cloud. You'll also find full API documentation on that page.

In order to run the sample code, you need a user account on the sandbox service where you will do your development. Sign up for an account at https://sandbox.evernote.com/Registration.action

The instructions below assume that you are developing your Android app in Eclipse and have the [latest Android development tools](http://developer.android.com/tools/sdk/eclipse-adt.html).

Running the sample app from Eclipse
-----------------------------------
The sample application HelloEDAM demonstrates how to use the Evernote SDK for Android to authentication to the Evernote service using OAuth, then access the user's Evernote account. To build and run the sample project from Eclipse:

1. Open Eclipse
2. From the File menu, choose New and then Project...
3. Under Android, select "Android Project from Existing Code" and click Next
4. Click Browse
5. Select the SDK root directory (the directory containing this README) and click Open
6. Click Finish
7. From the Package Explorer, expand the HelloEDAM project's src directory and open com.evernote.android.sample.HelloEDAM.java
8. At the top of HelloEDAM.java, fill in your Evernote API consumer key and secret.
9. Build and run the project

The sample application allows you to authenticate to Evernote, select an image from your device's image gallery, and then save that image into Evernote as a new note.

Using the SDK in your app
-------------------------
There are two ways to include the SDK in your project: by including and building the Android Library Project or by using Maven.

### Include the Android Library Project in your Eclipse workspace

1. Import the Android Library Project
   1. Open Eclipse
   2. From the File menu, choose New and then Project...
   3. Under Android, select "Android Project from Existing Code" and click Next
   4. Click Browse
   5. Select the library directory and click Open
   6. Click Finish
1. Add the Android Library Project as a dependency in your app
   7. Right-click on your project and choose "Properties"
   8. In the Android section, in the Library area, click Add...
   9. Select library from the list and click OK
   10. Click Java Build Path and then select the Projects tab
   11. Click Add...
   12. Select Library and click OK
   13. Click OK

### Use Maven

If you build your app using Maven, you can simply add the Evernote SDK for Android as a dependency in your pom.xml.

1. Add the Evernote SDK for Android as a dependency:

```
	<dependency>
		<groupId>com.evernote</groupId>
		<artifactId>android-sdk</artifactId>
		<version>1.0.1</version>
		<type>apklib</type>
	</dependency>
```

### Modify your AndroidManifest.xml

The SDK's OAuth functionality is implemented as an Android Activity that must be declared in your app's AndroidManifest.xml. Simply copy and paste the following snippet into your AndroidManifest.xml within the application section:

        <activity
                android:name="com.evernote.client.oauth.android.EvernoteOAuthActivity" />

### Set up an EvernoteSession

Define your app credentials (key, secret, and host).  See http://dev.evernote.com/documentation/cloud/

```java
	private static final String CONSUMER_KEY = "Your consumer key";
	private static final String CONSUMER_SECRET = "Your consumer secret";
	private static final String EVERNOTE_HOST = EvernoteSession.HOST_SANDBOX;
```

When your app starts, initialize the EvernoteSession singleton that has all of the information that is needed to authenticate to Evernote.

```java
    mEvernoteSession = EvernoteSession.init(this, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_HOST, null);
```

### Give the user a way to initiate authentication

In our sample app, we have a "Sign in to Evernote" button that initiates the authentication process. You might choose to do something similar, or you might simply initiate authentication the first time that the user tries to access Evernote-related functionality.

```java
    mEvernoteSession.authenticate(this);
```
### Complete authentication in onActivityResult

You can check whether authentication was successful by watching for the Evernote OAuth Activity in onActivityResult. If authentication is successful, you can start using the Evernote API.

```java
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      switch(requestCode) {
        // Update UI when oauth activity returns result
        case EvernoteSession.REQUEST_CODE_OAUTH:
          if (resultCode == Activity.RESULT_OK) {
            // Authentication was successful, do what you need to do in your app
          }
          break;
      }
    }
```

### Get a NoteStore.Client from the EvernoteSession and make Evernote API calls

After you've authenticated, the EvernoteSession will have a valid authentication token. Use the session to get a NoteStore or UserStore client object, and pass the session's authentication token when calling NoteStore/UserStore methods. See saveImage() in the sample application for an example of creating a new note using the API. Browse the JavaDoc at http://dev.evernote.com/documentation/reference/javadoc/

License
=======
    Copyright (c) 2007-2012 by Evernote Corporation, All rights reserved.

    Use of the source code and binary libraries included in this package
    is permitted under the following terms:

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

        1. Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
        2. Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
