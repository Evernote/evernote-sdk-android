/*
 * Copyright 2012 Evernote Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.evernote.android.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.client.oauth.android.EvernoteSession;
import com.evernote.client.oauth.android.EvernoteUtil;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This simple Android app demonstrates how to integrate with the
 * Evernote API (aka EDAM).
 * <p/>
 * In this sample, the user authorizes access to their account using OAuth
 * and chooses an image from the device's image gallery. The image is then
 * saved directly to user's Evernote account as a new note.
 */
public class HelloEDAM extends Activity {

  /**
   * ************************************************************************
   * You MUST change the following values to run this sample application.    *
   * *************************************************************************
   */

  // Your Evernote API key. See http://dev.evernote.com/documentation/cloud/
  // Please obfuscate your code to help keep these values secret.
  private static final String CONSUMER_KEY = "Your consumer key";
  private static final String CONSUMER_SECRET = "Your consumer secret";

  /**
   * ************************************************************************
   * Change these values as needed to use this code in your own application. *
   * *************************************************************************
   */

  // Name of this application, for logging
  private static final String TAG = "HelloEDAM";

  // Initial development is done on Evernote's testing service, the sandbox.
  // Change to HOST_PRODUCTION to use the Evernote production service
  // once your code is complete, or HOST_CHINA to use the Yinxiang Biji
  // (Evernote China) production service.
  private static final String EVERNOTE_HOST = EvernoteSession.HOST_SANDBOX;

  /**
   * ************************************************************************
   * The following values are simply part of the demo application.           *
   * *************************************************************************
   */

  // Activity result request codes
  private static final int SELECT_IMAGE = 1;

  // Used to interact with the Evernote web service
  private EvernoteSession mEvernoteSession;

  // UI elements that we update
  private Button mBtnAuth;
  private Button mBtnSave;
  private Button mBtnSelect;
  private ImageView mImageView;
  private final int DIALOG_PROGRESS = 101;

  // The path to and MIME type of the currently selected image from the gallery
  private class ImageData {
    public Bitmap imageBitmap;
    public String filePath;
    public String mimeType;
    public String fileName;
  }

  private ImageData mImageData;

  /**
   * Called when the activity is first created.
   */
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mBtnAuth = (Button) findViewById(R.id.auth_button);
    mBtnSelect = (Button) findViewById(R.id.select_button);
    mBtnSave = (Button) findViewById(R.id.save_button);
    mImageView = (ImageView) findViewById(R.id.image);

    if (getLastNonConfigurationInstance() != null) {
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

  // using createDialog, could use Fragments instead
  @SuppressWarnings("deprecation")
  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case DIALOG_PROGRESS:
        return new ProgressDialog(HelloEDAM.this);
    }
    return super.onCreateDialog(id);
  }

  @Override
  protected void onPrepareDialog(int id, Dialog dialog) {
    switch (id) {
      case DIALOG_PROGRESS:
        ((ProgressDialog) dialog).setIndeterminate(true);
        dialog.setCancelable(false);
        ((ProgressDialog) dialog).setMessage(getString(R.string.loading));
    }
  }

  /**
   * Setup the EvernoteSession used to access the Evernote API.
   */
  private void setupSession() {

    // Retrieve persisted authentication information
    mEvernoteSession = EvernoteSession.init(this, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_HOST, null);
  }

  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateUi() {
    if (mEvernoteSession.isLoggedIn()) {
      mBtnAuth.setText(R.string.label_log_out);
      if (mImageData != null && !TextUtils.isEmpty(mImageData.filePath)) {
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
      mEvernoteSession.authenticate(this);
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
    switch (requestCode) {
      //Update UI when oauth activity returns result
      case EvernoteSession.REQUEST_CODE_OAUTH:
        if (resultCode == Activity.RESULT_OK) {
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
   * <p/>
   * Sends the user to the image gallery to choose an image to share.
   */
  public void startSelectImage(View view) {
    Intent intent = new Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    startActivityForResult(intent, SELECT_IMAGE);
  }

  /**
   * Called when the user taps the "Save Image" button.
   * <p/>
   * You probably don't want to do this on your UI thread in the
   * real world.
   * <p/>
   * Saves the currently selected image to the user's Evernote account using
   * the Evernote web service API.
   * <p/>
   * Does nothing if the Evernote API wasn't successfully initialized
   * when the activity started.
   */
  public void saveImage(View view) {
    if (mEvernoteSession.isLoggedIn() && mImageData != null && mImageData.filePath != null) {
      new EvernoteNoteCreator().execute(mImageData);
    }
  }

  private class EvernoteNoteCreator extends AsyncTask<ImageData, Void, Note> {
    // using showDialog, could use Fragments instead
    @SuppressWarnings("deprecation")
    @Override
    protected void onPreExecute() {
      showDialog(DIALOG_PROGRESS);
    }

    @Override
    protected Note doInBackground(ImageData... imageDatas) {
      if (imageDatas == null || imageDatas.length == 0) {
        return null;
      }
      ImageData imageData = imageDatas[0];


      Note createdNote = null;
      String f = imageData.filePath;
      try {
        // Hash the data in the image file. The hash is used to reference the
        // file in the ENML note content.
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        FileData data = new FileData(EvernoteUtil.hash(in), new File(f));
        in.close();

        // Create a new Resource
        Resource resource = new Resource();
        resource.setData(data);
        resource.setMime(imageData.mimeType);
        ResourceAttributes attributes = new ResourceAttributes();
        attributes.setFileName(imageData.fileName);
        resource.setAttributes(attributes);

        // Create a new Note
        Note note = new Note();
        note.setTitle("Android test note");
        note.addToResources(resource);

        // Set the note's ENML content. Learn about ENML at
        // http://dev.evernote.com/documentation/cloud/chapters/ENML.php
        String content =
            EvernoteUtil.NOTE_PREFIX +
                "<p>This note was uploaded from Android. It contains an image.</p>" +
                EvernoteUtil.createEnMediaTag(resource) +
                EvernoteUtil.NOTE_SUFFIX;

        note.setContent(content);

        // Create the note on the server. The returned Note object
        // will contain server-generated attributes such as the note's
        // unique ID (GUID), the Resource's GUID, and the creation and update time.
        createdNote = mEvernoteSession.createNoteStore().createNote(mEvernoteSession.getAuthToken(), note);
      } catch (Exception e) {
        Log.e(TAG, getString(R.string.err_creating_note), e);
      }

      return createdNote;
    }

    // using removeDialog, could use Fragments instead
    @SuppressWarnings("deprecation")
    @Override
    protected void onPostExecute(Note note) {
      removeDialog(DIALOG_PROGRESS);

      if (note == null) {
        Toast.makeText(getApplicationContext(), R.string.err_creating_note, Toast.LENGTH_LONG).show();
        return;
      }

      Toast.makeText(getApplicationContext(), R.string.msg_image_saved, Toast.LENGTH_LONG).show();
    }
  }

  /**
   * Called when control returns from the image gallery picker.
   * Loads the image that the user selected.
   */
  private class ImageSelector extends AsyncTask<Intent, Void, ImageData> {

    // using showDialog, could use Fragments instead
    @SuppressWarnings("deprecation")
    @Override
    protected void onPreExecute() {
      showDialog(DIALOG_PROGRESS);
    }

    /**
     * The callback from the gallery contains a pointer into a table.
     * Look up the appropriate record and pull out the information that we need,
     * in this case, the path to the file on disk, the file name and the MIME type.
     *
     * @param intents
     * @return
     */
    // using Display.getWidth and getHeight on older SDKs
    @SuppressWarnings("deprecation")
    @Override
    // suppress lint check on Display.getSize(Point)
    @TargetApi(16)
    protected ImageData doInBackground(Intent... intents) {
      if (intents == null || intents.length == 0) {
        return null;
      }

      Uri selectedImage = intents[0].getData();
      String[] queryColumns = {
          MediaStore.Images.Media._ID,
          MediaStore.Images.Media.DATA,
          MediaStore.Images.Media.MIME_TYPE,
          MediaStore.Images.Media.DISPLAY_NAME};

      Cursor cursor = null;
      ImageData image = null;
      try {
        cursor = getContentResolver().query(selectedImage, queryColumns, null, null, null);
        if (cursor.moveToFirst()) {
          image = new ImageData();

          image.filePath = cursor.getString(cursor.getColumnIndex(queryColumns[1]));
          image.mimeType = cursor.getString(cursor.getColumnIndex(queryColumns[2]));
          image.fileName = cursor.getString(cursor.getColumnIndex(queryColumns[3]));

          // First decode with inJustDecodeBounds=true to check dimensions
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;

          Bitmap tempBitmap = BitmapFactory.decodeFile(image.filePath, options);

          int dimen = 0;
          int x = 0;
          int y = 0;

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);

            x = size.x;
            y = size.y;
          } else {
            x = getWindowManager().getDefaultDisplay().getWidth();
            y = getWindowManager().getDefaultDisplay().getHeight();
          }

          dimen = x < y ? x : y;

          // Calculate inSampleSize
          options.inSampleSize = calculateInSampleSize(options, dimen, dimen);

          // Decode bitmap with inSampleSize set
          options.inJustDecodeBounds = false;

          tempBitmap = BitmapFactory.decodeFile(image.filePath, options);

          image.imageBitmap = Bitmap.createScaledBitmap(tempBitmap, dimen, dimen, true);
          tempBitmap.recycle();

        }
      } catch (Exception e) {
        Log.e(TAG, "Error retrieving image");
      } finally {
        if (cursor != null) {
          cursor.close();
        }
      }
      return image;
    }

    /**
     * Calculates a sample size to be used when decoding a bitmap if you don't
     * require (or don't have enough memory) to load the full size bitmap.
     * <p/>
     * <p>This function has been taken form Android's training materials,
     * specifically the section about "Loading Large Bitmaps Efficiently".<p>
     *
     * @param options   a BitmapFactory.Options object, obtained from decoding only
     *                  the bitmap's bounds.
     * @param reqWidth  The required minimum width of the decoded bitmap.
     * @param reqHeight The required minimum height of the decoded bitmap.
     * @return the sample size needed to decode the bitmap to a size that meets
     * the required width and height.
     * @see <a href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap">Load a Scaled Down Version into Memory</a>
     */
    protected int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
      // Raw height and width of image
      final int height = options.outHeight;
      final int width = options.outWidth;
      int inSampleSize = 1;

      if (height > reqHeight || width > reqWidth) {
        if (width > height) {
          inSampleSize = Math.round((float) height / (float) reqHeight);
        } else {
          inSampleSize = Math.round((float) width / (float) reqWidth);
        }
      }
      return inSampleSize;
    }

    /**
     * Sets the image to the background and enables saving it to evernote
     *
     * @param image
     */
    // using removeDialog, could use Fragments instead
    @SuppressWarnings("deprecation")
    @Override
    protected void onPostExecute(ImageData image) {
      removeDialog(DIALOG_PROGRESS);

      if (image == null) {
        Toast.makeText(getApplicationContext(), R.string.err_image_selected, Toast.LENGTH_SHORT).show();
        return;
      }

      if (image.imageBitmap != null) {
        mImageView.setImageBitmap(image.imageBitmap);
      }

      if (mEvernoteSession.isLoggedIn()) {
        mBtnSave.setEnabled(true);
      }

      mImageData = image;
    }
  }


}
