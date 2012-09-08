package com.evernote.android.sample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;

import com.evernote.edam.util.EDAMUtil;
import com.evernote.client.conn.mobile.FileData;

import com.evernote.client.conn.ApplicationInfo;
import com.evernote.client.oauth.android.EvernoteSession;
import com.evernote.android.sample.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This simple Android app demonstrates how to integrate with the 
 * Evernote Cloud API (aka EDAM) to create a note.
 * 
 * In this sample, the user authorizes access to their account using OAuth
 * and chooses an image from the device's image gallery. The image is then 
 * saved directly to Evernote using the Cloud API.
 */
public class HelloEDAM extends Activity {
  
  /***************************************************************************
   * You MUST change the following values to run this sample application.    *
   ***************************************************************************/
  
  // Your Evernote API key. See http://dev.evernote.com/documentation/cloud/
  // Please obfuscate your code to help keep these values secret.
  private static final String CONSUMER_KEY = "Your Consumer Key";
  private static final String CONSUMER_SECRET = "Your Consumer Secret";

  /***************************************************************************
   * Change these values as needed to use this code in your own application. *
   ***************************************************************************/

  // Name of this application, for logging
  private static final String TAG = "HelloEDAM";
  
  // A directory on disk where your application stores temporary data
  private static final String APP_DATA_PATH = 
    "/Android/data/com.evernote.android.sample/temp/";

  // Change to "www.evernote.com" to use the Evernote production service 
  // instead of the sandbox
  private static final String EVERNOTE_HOST = "sandbox.evernote.com";
  
  private static final String APP_NAME = "Evernote Android Sample";  
  private static final String APP_VERSION = "1.0";

  /***************************************************************************
   * The following values are simply part of the demo application.           *
   ***************************************************************************/
  
  // Activity result request codes
  private static final int SELECT_IMAGE = 1;

  // The ENML preamble to every Evernote note. 
  // Note content goes between <en-note> and </en-note>
  private static final String NOTE_PREFIX = 
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" +
    "<en-note>";

  // The ENML postamble to every Evernote note 
  private static final String NOTE_SUFFIX = "</en-note>";

  // Used to interact with the Evernote web service
  private EvernoteSession session;
  
  // UI elements that we update
  private Button btnAuth;
  private Button btnSave;
  private Button btnSelect;
  private TextView msgArea;
  
  // The path to and MIME type of the currently selected image from the gallery
  private String filePath;
  private String mimeType;
  private String fileName;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    msgArea = (TextView)findViewById(R.id.message);
    btnAuth = (Button) findViewById(R.id.auth_button);
    btnSelect = (Button) findViewById(R.id.select_button);
    btnSave = (Button) findViewById(R.id.save_button);

    setupSession();
  }

  @Override
  public void onResume() {
    super.onResume();

    // Complete the Evernote authentication process if necessary
    if (!session.completeAuthentication(getPreferencesForAuthData())) {
      // We only want to do this when we're resuming after authentication...
      Toast.makeText(this, "Evernote login failed", Toast.LENGTH_LONG).show();
    }
    
    updateUi();
  }

  /**
   * Evernote authentication data will be stored to this
   * SharedPreferences if we are resuming as a result of a successful OAuth
   * authorization. You may wish to pass a different SharedPreferences
   * so that Evernote settings are stored along with other settings
   * persisted by your application.
   */
  private SharedPreferences getPreferencesForAuthData() {
    return getPreferences(MODE_PRIVATE);
  }
  
  /**
   * Setup the EvernoteSession used to access the Evernote API.
   */
  private void setupSession() {
    ApplicationInfo info = 
      new ApplicationInfo(CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_HOST, 
          APP_NAME, APP_VERSION);

    // Retrieve persisted authentication information
    session = new EvernoteSession(info, getPreferencesForAuthData(), getTempDir());
    updateUi();
  }
  
  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateUi() {
    if (session.isLoggedIn()) {
      btnAuth.setText(R.string.label_log_out);
      btnSave.setEnabled(true);
      btnSelect.setEnabled(true);
    } else {
      btnAuth.setText(R.string.label_log_in);
      btnSave.setEnabled(false);
      btnSelect.setEnabled(false);
    }
  }
  
  /**
   * Called when the user taps the "Log in to Evernote" button.
   * Initiates the Evernote OAuth process, or logs out if the user is already
   * logged in.
   */
  public void startAuth(View view) {
    if (session.isLoggedIn()) {
      session.logOut(getPreferencesForAuthData());
    } else {
      session.authenticate(this);
    }
    updateUi();
  }  
  
  /**
   * Get a temporary directory that can be used by this application to store potentially
   * large files sent to and retrieved from the Evernote API.
   */
  private File getTempDir() {
    return new File(Environment.getExternalStorageDirectory(), APP_DATA_PATH);
  }

  /***************************************************************************
   * The remaining code in this class simply demonstrates the use of the     *
   * Evernote API once authnetication is complete. You don't need any of it  *
   * in your application.                                                    *
   ***************************************************************************/
  
  /**
   * Called when the user taps the "Select Image" button.
   * 
   * Sends the user to the image gallery to choose an image to share.
   */
  public void startSelectImage(View view) {
    Intent intent = new Intent(Intent.ACTION_PICK, 
                               MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    startActivityForResult(intent, SELECT_IMAGE);
  }

  /**
   * Called when the control returns from an activity that we launched.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SELECT_IMAGE) {
      // Callback from our 'startSelectImage' action
      if (resultCode == Activity.RESULT_OK) {
        endSelectImage(data);
      } 
    }
  }

  /**
   * Called when control returns from the image gallery picker.
   * Loads the image that the user selected.
   * 
   * @param data The data returned from the activity.
   */
  private void endSelectImage(Intent data) {
    // The callback from the gallery contains a pointer into a table.
    // Look up the appropriate record and pull out the information that we need,
    // in this case, the path to the file on disk, the file name and the MIME type. 
    Uri selectedImage = data.getData();
    String[] queryColumns = { MediaStore.Images.Media.DATA, 
                              MediaStore.Images.Media.MIME_TYPE, 
                              MediaStore.Images.Media.DISPLAY_NAME };
    Cursor cursor = getContentResolver().query(selectedImage, queryColumns, null, null, null);
    cursor.moveToFirst();
    this.filePath = cursor.getString(cursor.getColumnIndex(queryColumns[0]));
    this.mimeType = cursor.getString(cursor.getColumnIndex(queryColumns[1]));
    this.fileName = cursor.getString(cursor.getColumnIndex(queryColumns[2]));
    cursor.close();

    if (session.isLoggedIn()) {
      this.msgArea.setText(this.fileName);
      this.btnSave.setEnabled(true);
    }
  }

  /**
   * Called when the user taps the "Save Image" button.
   * 
   * You probably don't want to do this on your UI thread in the 
   * real world.
   * 
   * Saves the currently selected image to the user's Evernote account using
   * the Evernote web service API.
   * 
   * Does nothing if the Evernote API wasn't successfully initialized
   * when the activity started.
   */
  public void saveImage(View view) {
    if (session.isLoggedIn()) {
      String f = this.filePath;
      try {
        // Hash the data in the image file. The hash is used to refernece the
        // file in the ENML note content.
        InputStream in = new BufferedInputStream(new FileInputStream(f)); 
        FileData data = new FileData(EDAMUtil.hash(in), new File(f));
        in.close();
        
        // Create a new Resource
        Resource resource = new Resource();
        resource.setData(data);
        resource.setMime(this.mimeType);
        
        // Create a new Note
        Note note = new Note();
        note.setTitle("Android test note");
        note.addToResources(resource);
        
        // Set the note's ENML content. Learn about ENML at 
        // http://dev.evernote.com/documentation/cloud/chapters/ENML.php
        String content = 
          NOTE_PREFIX +
          "<p>This note was uploaded from Android. It contains an image.</p>" +
          "<en-media type=\"" + this.mimeType + "\" hash=\"" +
          EDAMUtil.bytesToHex(resource.getData().getBodyHash()) + "\"/>" +
          NOTE_SUFFIX;
        note.setContent(content);
        
        // Create the note on the server. The returned Note object
        // will contain server-generated attributes such as the note's
        // unique ID (GUID), the Resource's GUID, and the creation and update time.
        Note createdNote = session.createNoteStore().createNote(session.getAuthToken(), note);

        Toast.makeText(this, R.string.msg_image_saved, Toast.LENGTH_LONG).show();
      } catch (Throwable t) {
        // It's generally bad form to catch Throwable, but for this simple demo, 
        // we want to trap and log all errors.
        Toast.makeText(this, R.string.err_creating_note, Toast.LENGTH_LONG).show();
        Log.e(TAG, getString(R.string.err_creating_note), t);
      }  
    }
  }
}
