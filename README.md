Evernote SDK for Android version 1.1.1
====================================

Evernote API version 1.24


Overview
--------
This SDK wraps the [Evernote Cloud API ](http://dev.evernote.com/documentation/cloud/) and provides OAuth authentication functionality. The SDK is provided as an Android Library project that can be included in your application.

Prerequisites
-------------
In order to use the code in this SDK, you need to obtain an API key from http://dev.evernote.com/documentation/cloud. You'll also find full API documentation on that page.

In order to run the sample code, you need a user account on the sandbox service where you will do your development. Sign up for an account at https://sandbox.evernote.com/Registration.action

The instructions below assume you have the latest [Android SDK](http://developer.android.com/sdk/index.html) and [API 17](http://developer.android.com/tools/revisions/platforms.html#4.2) installed. The instructions for eclipse are based on [Eclipse Juno](http://www.eclipse.org/downloads/) and [latest Android development tools](http://developer.android.com/tools/sdk/eclipse-adt.html). The instructions for Intellij are based on [Intellij IDEA 12 Community Edition](http://www.jetbrains.com/idea/download/index.html) and is our recommended IDE.


Sample App
----------
The sample application HelloEDAM demonstrates how to use the Evernote SDK for Android to authentication to the Evernote service using OAuth, then access the user's Evernote account. The sample code provides mutliple activities that show notebook listing, note creation, and resource creation in two scenarios: A plain text note creator and an image saver.

###  Running the sample app from Eclipse
To build and run the sample project from Eclipse:

1. Open Eclipse
2. From the File menu, choose New and then Project...
3. Under Android, select "Android Project from Existing Code" and click Next
4. Click Browse
5. Select the SDK root directory (the directory containing this README) and click OK
6. Click Finish
7. Right click HelloEDAM, click properties, click Java Build Path, click the Projects tab,
8. Click Add and select library, click ok to accept changes.
9. From the Package Explorer, expand the HelloEDAM project's `src` directory and open `com.evernote.android.sample.ParentActivity.java`
10. At the top of `ParentActivity.java`, fill in your Evernote API consumer key and secret.
11. Build and run the project

###  Running the sample app from Intellij
To build and run the sample project from Intellij:

1. Open Intellij
2. From the File menu, choose Import Project...
3. Select the SDK root directory (the directory containing this README) and click Open
4. Select Create project from existing sources and Click Next
5. Click Next, Next, Next, Next
6. Select Android 4.2 Google APIs and click Next
7. Click Finish
8. From the Project Explorer, expand the HelloEDAM project's `src` directory and open `com.evernote.android.sample.ParentActivity.java`
9. At the top of `ParentActivity.java`, fill in your Evernote API consumer key and secret.
10. Build and run the project


Using the SDK in your app
-------------------------
There are two ways to include the SDK in your project: by including and building the Android Library Project in your IDE or by using Maven.

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
   
### Include the Android Library Project in your Intellij workspace

1. Right click your project and choose Open Module Properties
2. Select the Plus Icon (Add) at the top and choose Import Module
3. Select the library directory and click OK   
4. Click Next, Next, Next, Next
5. Click Finish
6. Click your project and select teh Dependencies tab
7. Click the Plus Icon (Add) at the bottom and select 3 Module Dependency
8. Select library and click OK

### Use Maven

If you build your app using Maven, you can simply add the Evernote SDK for Android as a dependency in your pom.xml.

Add the Evernote SDK for Android as a dependency:

```xml
<dependency>
	<groupId>com.evernote</groupId>
	<artifactId>android-sdk</artifactId>
	<version>1.1.1</version>
	<type>apklib</type>
</dependency>
```

### Modify your `AndroidManifest.xml`

The SDK's OAuth functionality is implemented as an Android Activity that must be declared in your app's `AndroidManifest.xml`. Simply copy and paste the following snippet into your `AndroidManifest.xml` within the application section:

```xml
<activity android:name="com.evernote.client.android.EvernoteOAuthActivity" android:configChanges="orientation|keyboardHidden" />
```

### Set up an `EvernoteSession`

Define your app credentials (key, secret, and host).  See http://dev.evernote.com/documentation/cloud/

```java
private static final String CONSUMER_KEY = "Your consumer key";
private static final String CONSUMER_SECRET = "Your consumer secret";
private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
```

When your app starts, initialize the EvernoteSession singleton that has all of the information that is needed to authenticate to Evernote. The EvernoteSession instance of saved statically and does not need to be passed between activities. The better option is to run getInstance(...) in your onCreate() of the Application object or your parent Activity object.

```java
mEvernoteSession = EvernoteSession.getInstance(this, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_SERVICE);
```

### Give the user a way to initiate authentication

In our sample app, we have a "Sign in to Evernote" button that initiates the authentication process. You might choose to do something similar, or you might simply initiate authentication the first time that the user tries to access Evernote-related functionality.

```java
mEvernoteSession.authenticate(this);
```

### Evernote and Yinxiang Biji Service Bootstrapping

The Activity that completes the OAuth authentication automatically determines if the User is on the Evernote service or the Yinxiang service and configures the end points automatically.

### Complete authentication in `onActivityResult`

You can check whether authentication was successful by watching for the Evernote OAuth Activity in `onActivityResult`. If authentication is successful, you can start using the Evernote API.

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

Use the `ClientFactory` to create Async Clients
-----------------------------------------------

Calling `EvernoteSession.getClientFactory()` will give you access to `createNoteStore()`, `createUserStore()`, and `createBusinessNoteStore()`. These objects return Asynchronously wrapped `Client` objects that allow you to interact with the Evernote API over Thrift using a callback interface `OnClickCallback<T>`.

If the underlying `NoteStore.Client` or `UserStore.Client` object is needed, access via the `getClient()` method. Browse the API JavaDocs at http://dev.evernote.com/documentation/reference/javadoc/


### Create an `AsyncNoteStore` to access primary methods for personal note data
```java
mEvernoteSession.getClientFactory().createNoteStore();
```


### Create an `AsyncUserStore` to access User related methods
```java
mEvernoteSession.getClientFactory().createUserStore();
```


### Create an `AsyncBusinessNoteStoreClient` to access Business Notebooks
```java
mEvernoteSession.getClientFactory().createBusinessNoteStore();
```


### Create an `AsyncLinkedNoteStoreClient` to access shared notebooks
```java
mEvernoteSession.getClientFactory().createLinkedNoteStore(linkedNotebook);
```


Using the `AsyncNoteStoreClient` to make asynchronous API calls
---------------------------------------------------------------

### Getting list of notebooks asynchronously
```java
public void listNotebooks() throws TTransportException {
  if (mEvernoteSession.isLoggedIn()) {
    mEvernoteSession.getClientFactory().createNoteStoreClient().listNotebooks(new OnClientCallback<List<Notebook>>() {
      @Override
      public void onSuccess(final List<Notebook> notebooks) {
        List<String> namesList = new ArrayList<String>(notebooks.size());
        for (Notebook notebook : notebooks) {
          namesList.add(notebook.getName());
        }
        String notebookNames = TextUtils.join(", ", namesList);
        Toast.makeText(getApplicationContext(), notebookNames + " notebooks have been retrieved", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onException(Exception exception) {
        Log.e(LOGTAG, "Error retrieving notebooks", exception);
      }
    });
  }
}
```

### Creating a note asynchronously
```java
public void createNote(String title, String content) throws TTransportException {
  if (mEvernoteSession.isLoggedIn()) {
    Note note = new Note();
    note.setTitle(title);
    note.setContent(EvernoteUtil.NOTE_PREFIX + content + EvernoteUtil.NOTE_SUFFIX);
    mEvernoteSession.getClientFactory().createNoteStoreClient().createNote(note, new OnClientCallback<Note>() {
      @Override
      public void onSuccess(final Note data) {
        Toast.makeText(getApplicationContext(), data.getTitle() + " has been created", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onException(Exception exception) {
        Log.e(LOGTAG, "Error creating note", exception);
      }
    });
  }
}
```

### Using the `AsyncBusinessNoteStoreClient` to Access Evernote Business data

1. Check if user is member of a business
2. Create `AsyncBusinessNoteStore`
3. Call synchronous methods from a background thread or call async methods from UI thread

This notestore is not long lived, the Business authentication token expires frequently and is refreshed if needed in the `createBusinessNoteStore()` method

Example using the synchronous business methods inside a background thread to create a note in a business account

```java
new Thread(new Runnable() {
  @Override
  public void run() {
    try {
      // Is the User member of a business
      if(mEvernoteSession.getClientFactory().createUserStoreClient().isBusinessUser()) {
        //Create an AsyncBusinessNoteStoreClient, gets valid auth token
        AsyncBusinessNoteStoreClient client = mEvernoteSession.getClientFactory().createBusinessNoteStoreClient();
        List<LinkedNotebook> notebooks = client.listNotebooks();
        //If the user has any business notebooks
        if(notebooks.size() > 0) {
          //Create a note in the first one
          Note note = new Note();
          note.setTitle("New Note");
          note.setContent(EvernoteUtil.NOTE_PREFIX + "Content of Note" + EvernoteUtil.NOTE_SUFFIX);
          final Note createdNote = client.createNote(note, notebooks.get(0));
          //Update user on UI thread
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(getApplicationContext(), createdNote.getTitle() + " has been created.", Toast.LENGTH_LONG).show();
            }
          });

        }
      } else {
        Log.d(LOGTAG, "Not a business User");
      }
    } catch(Exception exception) {
      Log.e(LOGTAG, "Error received::", exception);
    }
  }
}).start();
```

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
