package com.evernote.android.sample;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.evernote.client.conn.ApplicationInfo;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.client.oauth.android.EvernoteSession;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.util.EDAMUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
  private static final String CONSUMER_KEY = "frostbite7217-7708";
  private static final String CONSUMER_SECRET = "44fc68c4d16dca1f";

  /***************************************************************************
   * Change these values as needed to use this code in your own application. *
   ***************************************************************************/

  // Name of this application, for logging
  private static final String TAG = "HelloEDAM";

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
  private EvernoteSession mEvernoteSession;
  
  // UI elements that we update
  private Button mBtnAuth;
  private Button mBtnSave;
  private Button mBtnSelect;
  private ImageView mImageView;
  
  // The path to and MIME type of the currently selected image from the gallery
  private class ImageData {
    public Bitmap imageBitmap;
    public String filePath;
    public String mimeType;
    public String fileName;
  }

  private ImageData mImageData;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mBtnAuth = (Button) findViewById(R.id.auth_button);
    mBtnSelect = (Button) findViewById(R.id.select_button);
    mBtnSave = (Button) findViewById(R.id.save_button);
    mImageView = (ImageView) findViewById(R.id.image);

    if(getLastNonConfigurationInstance() != null) {
      mImageData = (ImageData) getLastNonConfigurationInstance();
      mImageView.setImageBitmap(mImageData.imageBitmap);
    }

    setupSession();
  }

  @Override
  public void onResume() {
    super.onResume();
    updateUi();
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    return mImageData;
  }

  /**
   * Setup the EvernoteSession used to access the Evernote API.
   */
  private void setupSession() {
    ApplicationInfo info = 
      new ApplicationInfo(CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_HOST, 
          APP_NAME, APP_VERSION);

    // Retrieve persisted authentication information
    mEvernoteSession = EvernoteSession.getInstance(this, info);
  }
  
  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateUi() {
    if (mEvernoteSession.isLoggedIn()) {
      mBtnAuth.setText(R.string.label_log_out);
      if(mImageData != null && !TextUtils.isEmpty(mImageData.filePath)) {
        mBtnSave.setEnabled(true);
      } else {
        mBtnSave.setEnabled(false);
      }
      mBtnSelect.setEnabled(true);
    } else {
      mBtnAuth.setText(R.string.label_log_in);
      mBtnSave.setEnabled(false);
      mBtnSelect.setEnabled(false);
    }
  }
  
  /**
   * Called when the user taps the "Log in to Evernote" button.
   * Initiates the Evernote OAuth process, or logs out if the user is already
   * logged in.
   */
  public void startAuth(View view) {
    if (mEvernoteSession.isLoggedIn()) {
      mEvernoteSession.logOut(getApplicationContext());
    } else {
      mEvernoteSession.authenticate(HelloEDAM.this);
    }
    updateUi();
  }

  /***************************************************************************
   * The remaining code in this class simply demonstrates the use of the     *
   * Evernote API once authnetication is complete. You don't need any of it  *
   * in your application.                                                    *
   ***************************************************************************/

  /**
   * Called when the control returns from an activity that we launched.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch(requestCode) {
      //Update UI when oauth activity returns result
      case EvernoteSession.REQUEST_CODE_OAUTH:
        if(resultCode == Activity.RESULT_OK) {
          updateUi();
        }
        break;
      //Grab image data when picker returns result
      case SELECT_IMAGE:
        if (resultCode == Activity.RESULT_OK) {
          new ImageSelector().execute(data);
        }
        break;
    }
  }

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
    //TODO: thread this
    //TODO: Abstract header/footer info
    //TODO: Clean up error catching
    if (mEvernoteSession.isLoggedIn() && mImageData != null && mImageData.filePath != null) {
      String f = mImageData.filePath;
      try {
        // Hash the data in the image file. The hash is used to refernece the
        // file in the ENML note content.
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        FileData data = new FileData(EDAMUtil.hash(in), new File(f));
        in.close();
        
        // Create a new Resource
        Resource resource = new Resource();
        resource.setData(data);
        resource.setMime(mImageData.mimeType);
        
        // Create a new Note
        Note note = new Note();
        note.setTitle("Android test note");
        note.addToResources(resource);
        
        // Set the note's ENML content. Learn about ENML at 
        // http://dev.evernote.com/documentation/cloud/chapters/ENML.php
        String content = 
          NOTE_PREFIX +
          "<p>This note was uploaded from Android. It contains an image.</p>" +
          "<en-media type=\"" + mImageData.mimeType + "\" hash=\"" +
          EDAMUtil.bytesToHex(resource.getData().getBodyHash()) + "\"/>" +
          NOTE_SUFFIX;
        note.setContent(content);
        
        // Create the note on the server. The returned Note object
        // will contain server-generated attributes such as the note's
        // unique ID (GUID), the Resource's GUID, and the creation and update time.
        Note createdNote = mEvernoteSession.createNoteStore().createNote(mEvernoteSession.getAuthToken(), note);

        Toast.makeText(this, R.string.msg_image_saved, Toast.LENGTH_LONG).show();
      } catch (Throwable t) {
        // It's generally bad form to catch Throwable, but for this simple demo, 
        // we want to trap and log all errors.
        Toast.makeText(this, R.string.err_creating_note, Toast.LENGTH_LONG).show();
        Log.e(TAG, getString(R.string.err_creating_note), t);
      }  
    }
  }

  /**
   * Called when control returns from the image gallery picker.
   * Loads the image that the user selected.

   */
  private class ImageSelector extends AsyncTask<Intent, Void, ImageData> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    /**
     * The callback from the gallery contains a pointer into a table.
     * Look up the appropriate record and pull out the information that we need,
     * in this case, the path to the file on disk, the file name and the MIME type.
     * @param intents
     * @return
     */
    @Override
    protected ImageData doInBackground(Intent... intents) {
      if(intents == null || intents.length == 0) {
        return null;
      }

      Uri selectedImage = intents[0].getData();
      String[] queryColumns = {
          MediaStore.Images.Media._ID,
          MediaStore.Images.Media.DATA,
          MediaStore.Images.Media.MIME_TYPE,
          MediaStore.Images.Media.DISPLAY_NAME };

      Cursor cursor = null;
      ImageData image = null;
      try {
        cursor = getContentResolver().query(selectedImage, queryColumns, null, null, null);
        if(cursor.moveToFirst()) {
          image = new ImageData();
          long imageId= cursor.getLong(cursor.getColumnIndex(queryColumns[0]));

          image.filePath = cursor.getString(cursor.getColumnIndex(queryColumns[1]));
          image.mimeType = cursor.getString(cursor.getColumnIndex(queryColumns[2]));
          image.fileName = cursor.getString(cursor.getColumnIndex(queryColumns[3]));

          Uri imageUri = ContentUris.withAppendedId(
              MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
          Bitmap tempBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

          Point size = new Point();
          getWindowManager().getDefaultDisplay().getSize(size);
          int dimen = size.x < size.y ? size.x : size.y;

          image.imageBitmap = Bitmap.createScaledBitmap(tempBitmap, dimen, dimen, true);
          tempBitmap.recycle();

        }
      }catch (Exception e) {
        Log.e(TAG, "Error retrieving image");
      }finally {
        if (cursor != null) {
          cursor.close();
        }
      }
      return image;
    }

    /**
     * Sets the image to the background and enables saving it to evernote
     * @param image
     */
    @Override
    protected void onPostExecute(ImageData image) {
      if(image == null) {
        Toast.makeText(getApplicationContext(), R.string.err_image_selected, Toast.LENGTH_SHORT).show();
        return;
      }

      if(image.imageBitmap != null) {
        mImageView.setImageBitmap(image.imageBitmap);
      }

      if (mEvernoteSession.isLoggedIn()) {
        mBtnSave.setEnabled(true);
      }

      mImageData = image;
    }
  }


}
