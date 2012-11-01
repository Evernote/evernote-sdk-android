Evernote SDK for Android version 0.2.3
=============================================

Evernote API version 1.22

Overview
--------
This SDK contains wrapper code used to call the Evernote Cloud API from Android apps and a __beta__ SDK for authenticating to Evernote using OAuth. Note that the OAuth SDK components may change quickly over the next couple of weeks.

The SDK also contains two samples. The code in sample/HelloEDAM demonstrates the basic use of the SDK to access the Evernote Cloud API. The code in sample/HelloEvernote demonstrates direct integration with [Evernote for Android](https://play.google.com/store/apps/details?id=com.evernote) using Intents.

Prerequisites
-------------
In order to use the code in this SDK, you need to obtain an API key from http://dev.evernote.com/documentation/cloud. You'll also find full API documentation on that page.

In order to run the sample code, you need a user account on the sandbox service where you will do your development. Sign up for an account at https://sandbox.evernote.com/Registration.action 

Known Issues
------------
The current version of the SDK performs OAuth network requests on the main thread, resulting in a NetworkOnMainThreadException when the targetSDK is set to 11 or higher.

Getting Started - HelloEDAM
---------------------------
The sample application HelloEDAM demonstrates how to use the Evernote SDK for Android to authentication to the Evernote service using OAuth, then access the user's Evernote account. To run the sample project:

1. Open Eclipse
2. From the File menu, choose Import
3. Under General, select "Existing Projects into Workspace" and click Next
4. Choose "Select root directory" and click Browse
5. Select the HelloEDAM directory and click Open
6. Click Finish
7. From the Package Explorer, expand the src directory and open com.evernote.android.sample.HelloEDAM.java
8. Scroll down and fill in your Evernote API consumer key and secret.
8. Open the sample application's AndroidManifest.xml
8. In the EvernoteOAuthActivity intent-filter, replace "your key" with your API consumer key. For example, if your consumer key is "foobar", the value should be "en-foobar".
9. Build and run the project

Using the SDK in your app
-------------------------
It only takes a few steps to add OAuth authentication to Evernote into your Android app. Here are the steps you'll want to borrow from the sample app.

### Include the code

Open your application's project properties and add lib/evernote-client-android.jar and lib/scribe-1.3.0-jar to the Java build path.

### Modify your AndroidManifest.xml

As part of the OAuth process, your app needs to know to handle certain URLs. Add the following activity to your app's AndroidManifest.xml file, replacing "your key" with your particular Evernote consumer key:

        <activity
          android:name="com.evernote.client.oauth.android.EvernoteOAuthActivity"
          android:launchMode="singleTask"
          android:configChanges="orientation|keyboard">
          <intent-filter>
            <!-- Change this to be en- followed by your consumer key -->
            <data android:scheme="en-your key" />
            <action android:name="android.intent.action.VIEW" />                
            <category android:name="android.intent.category.BROWSABLE"/>
            <category android:name="android.intent.category.DEFAULT" /> 
          </intent-filter>
        </activity>

### Set up an EvernoteSession

When your app starts, create an EvernoteSession object that has all of the information that is needed to authenticate to Evernote.

```java
   private void setupSession() {
     // TODO reused the cached the authentication token if there is one
     ApplicationInfo info = 
       new ApplicationInfo(CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_HOST, 
           APP_NAME, APP_VERSION);
     session = new EvernoteSession(info, getTempDir());
     updateUi();
   }
```
### Give the user a way to initiate authentication

In our sample app, we have a "Sign in to Evernote" button that initiates the authentication process. You might choose to do something similar, or you might simply initiate authentication the first time that the user tries to access Evernote-related functionality.

```java
public void startAuth(View view) {
     if (session.isLoggedIn()) {
       session.logOut();
     } else {
       session.authenticate(this);
     }
     updateUi();
   }  
```
### Complete authentication in onResume

Complete the authentication process in your activity's onResume method by calling EvernoteSession.completeAuthentication.

```java
   @Override
   public void onResume() {
     super.onResume(); 
     if (!session.completeAuthentication()) {
       // We only want to do this when we're resuming after authentication...
       Toast.makeText(this, "Evernote login failed", Toast.LENGTH_LONG).show();
     }
```
### Cache the resulting authentication token

You should store the authentication token so that the user doesn't need to authenticate again the next time that they use your app.

### Use the session's NoteStore to make Evernote API calls

After you've authenticated, the EvernoteSession will have a valid authentication token. Use the session to get a NoteStore or UserStore client object, and pass the session's authentication token when calling NoteStore/UserStore methods. See saveImage() in the sample application for an example of creating a new note using the API. Browse the JavaDoc at http://dev.evernote.com/documentation/reference/javadoc/

Getting Started - HelloEvernote
-------------------------------
Android apps can use Intents to integrate directly with Evernote for Android when it is installed. The code in sample/HelloEvernote demonstrates the basics.

1. Open Eclipse
2. From the File menu, choose Import
3. Under General, select "Existing Projects into Workspace" and click Next
4. Choose "Select root directory" and click Browse
5. Select the HelloEvernote directory and click Open
6. Click Finish
7. Install Evernote for Android on the device or emulator where you will run the sample project
8. Build and run the project

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
