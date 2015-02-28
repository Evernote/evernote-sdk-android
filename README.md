Evernote SDK for Android version 1.1.3
======================================

Evernote API version 1.25


Overview
--------
This SDK wraps the [Evernote Cloud API ](http://dev.evernote.com/documentation/cloud/) and provides OAuth authentication functionality. The SDK is provided as an Android Library project that can be included in your application.

Prerequisites
-------------
In order to use the code in this SDK, you need to obtain an API key from http://dev.evernote.com/documentation/cloud. You'll also find full API documentation on that page.

In order to run the sample code, you need a user account on the sandbox service where you will do your development. Sign up for an account at https://sandbox.evernote.com/Registration.action

The instructions below assume you have the latest [Android SDK](http://developer.android.com/sdk/index.html).


Sample App
----------
The sample application 'Evernote SDK Demo' demonstrates how to use the Evernote SDK for Android to authentication to the Evernote service using OAuth, then access the user's Evernote account. The sample code provides mutliple activities that show notebook listing, note creation, and resource creation in two scenarios: A plain text note creator and an image saver.

### Running the sample app from Android Studio
To build and run the sample project from Android Studio:

1. Open Android Studio
2. Choose Import Non-Android Studio project
3. Select the SDK root directory (the directory containing this README) and click OK
4. Add your Evernote API consumer key and secret (see below)

##### Adding Evernote API consumer key and secret
You have two different options to add your consumer key and secret.

###### gradle.properties file (preferred)

1. Open the folder `.gradle` in your user's home directory. 
2. Open or create a file called `gradle.properties`
3. Add a line `EVERNOTE_CONSUMER_KEY=Your Consumer Key`
4. Add a line `EVERNOTE_CONSUMER_SECRET=Your Consumer Secret`

###### In code

1. Open the class `com.evernote.android.sample.ParentActivity.java`
2. At the top of `ParentActivity.java`, fill in your Evernote API consumer key and secret.

Using the SDK in your app
-------------------------

### Using Gradle (work in progress)

Add the Evernote SDK for Android as a dependency in your build.gradle file.

```groovy
dependencies {
    compile 'com.evernote:android-sdk:1.1.3'
}
```

##### (Optional) Using a snapshot build for early access previews

Add Sonatype's snapshot repository in your build script.
```groovy
maven {
    url "https://oss.sonatype.org/content/repositories/snapshots"
}
```

Add the snapshot depdendency. 
```groovy
dependencies {
    compile 'com.evernote:android-sdk:1.1.3-SNAPSHOT'
}
```

### Modify your `AndroidManifest.xml`

The SDK's OAuth functionality is implemented as an Android Activity that must be declared in your app's `AndroidManifest.xml`. Simply copy and paste the following snippet into your `AndroidManifest.xml` within the application section:

```xml
<activity android:name="com.evernote.client.android.EvernoteOAuthActivity" />
<activity android:name="com.evernote.client.android.login.EvernoteLoginActivity"/>
```

### Set up an `EvernoteSession`

Define your app credentials (key, secret, and host).  See http://dev.evernote.com/documentation/cloud/

```java
private static final String CONSUMER_KEY = "Your consumer key";
private static final String CONSUMER_SECRET = "Your consumer secret";
private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
```

When your app starts, initialize the EvernoteSession singleton that has all of the information that is needed to authenticate to Evernote. The EvernoteSession instance of saved statically and does not need to be passed between activities. The better option is to build the instance in your onCreate() of the Application object or your parent Activity object.

```java
mEvernoteSession = new EvernoteSession.Builder(this)
    .setEvernoteService(EVERNOTE_SERVICE)
    .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
    .build(consumerKey, consumerSecret)
    .asSingleton();
```

### Give the user a way to initiate authentication

In our sample app, we have a "Sign in to Evernote" button that initiates the authentication process. You might choose to do something similar, or you might simply initiate authentication the first time that the user tries to access Evernote-related functionality.

The recommended approach is to use `FragmentActivity`s. Then the authentication process opens a dialog and no extra `Activity`. But normal `Activity`s are supported as well. 

```java
mEvernoteSession.authenticate(this);
```

### Evernote and Yinxiang Biji Service Bootstrapping

The Activity that completes the OAuth authentication automatically determines if the User is on the Evernote service or the Yinxiang service and configures the end points automatically.

### Complete authentication in `onActivityResult`

You can check whether authentication was successful by watching for the result in your `Activity`. If you use a `FragmentActivity`, then you should implement the `EvernoteLoginFragment.ResultCallback` interface.


```java
public class MyActivity extends Activity implements EvernoteLoginFragment.ResultCallback {

    // ...

    @Override
    public void onLoginFinished(boolean successful) {
        // handle result
    }
}    
```

If you use a normal `Activity`, you should override `onActivityResult`.

```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
        case EvernoteSession.REQUEST_CODE_LOGIN:
            if (resultCode == Activity.RESULT_OL) {
                // handle success
            } else {
                // handle failure
            }        
            break;
            
        default:
            super.onActivityResult(requestCode, resultCode, data);
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
    Copyright (c) 2007-2015 by Evernote Corporation, All rights reserved.

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
