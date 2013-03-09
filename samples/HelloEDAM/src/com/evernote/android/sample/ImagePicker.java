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
/**
 * This sample shows how to select an image from the device gallery and save the attachment in Evernote
 * <p/>
 * class created by @tylersmithnet
 */
import android.annotation.TargetApi;
import android.app.Activity;
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
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ImagePicker extends ParentActivity {

  /**
   * *************************************************************************
   * The following values and code are simply part of the demo application.  *
   * *************************************************************************
   */

  private static final String LOGTAG = "ImagePicker";

  // Activity result request codes
  private static final int SELECT_IMAGE = 1;

  // The path to and MIME type of the currently selected image from the gallery
  private class ImageData {
    public Bitmap imageBitmap;
    public String filePath;
    public String mimeType;
    public String fileName;
  }
  //Instance of selected image
  private ImageData mImageData;

  //Views
  private ImageView mImageView;
  private Button mBtnSave;
  private Button mBtnSelect;


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.image_picker);

    mBtnSelect = (Button) findViewById(R.id.select_button);
    mBtnSave = (Button) findViewById(R.id.save_button);
    mImageView = (ImageView) findViewById(R.id.image);

    if (getLastNonConfigurationInstance() != null) {
      mImageData = (ImageData) getLastNonConfigurationInstance();
      mImageView.setImageBitmap(mImageData.imageBitmap);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    updateSelectionUi();
  }

  @Override
  @SuppressWarnings("deprecation")
  public Object onRetainNonConfigurationInstance() {
    return mImageData;
  }


  /**
   * Called when the control returns from an activity that we launched.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      //Grab image data when picker returns result
      case SELECT_IMAGE:
        if (resultCode == Activity.RESULT_OK) {
          new ImageSelector().execute(data);
        }
        break;
    }
  }


  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateSelectionUi() {
    if (mImageData != null && !TextUtils.isEmpty(mImageData.filePath)) {
      mBtnSave.setEnabled(true);
    } else {
      mBtnSave.setEnabled(false);
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
   * Saves the currently selected image to the user's Evernote account using
   * the Evernote web service API using an asynchronous method
   * <p/>
   * Does nothing if the Evernote API wasn't successfully initialized
   * when the activity started.
   */
  public void saveImage(View view) {
    if (mEvernoteSession.isLoggedIn() && mImageData != null && mImageData.filePath != null) {


      showDialog(DIALOG_PROGRESS);

      String f = mImageData.filePath;
      try {
        // Hash the data in the image file. The hash is used to reference the
        // file in the ENML note content.
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        FileData data = new FileData(EvernoteUtil.hash(in), new File(f));
        in.close();

        // Create a new Resource
        Resource resource = new Resource();
        resource.setData(data);
        resource.setMime(mImageData.mimeType);
        ResourceAttributes attributes = new ResourceAttributes();
        attributes.setFileName(mImageData.fileName);
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

        mEvernoteSession.getClientFactory().createNoteStoreClient().createNote(note, new OnClientCallback<Note>() {
          @Override
          public void onSuccess(Note data) {
            removeDialog(DIALOG_PROGRESS);
            Toast.makeText(getApplicationContext(), R.string.msg_image_saved, Toast.LENGTH_LONG).show();
          }

          @Override
          public void onException(Exception exception) {
            Log.e(LOGTAG, "Error saving note", exception);
            Toast.makeText(getApplicationContext(), R.string.error_saving_note, Toast.LENGTH_LONG).show();
            removeDialog(DIALOG_PROGRESS);
          }
        });
      } catch (Exception ex) {
        Log.e(LOGTAG, "Error creating notestore", ex);
        Toast.makeText(getApplicationContext(), R.string.error_creating_notestore, Toast.LENGTH_LONG).show();
        removeDialog(DIALOG_PROGRESS);
      }
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
        Log.e(LOGTAG, "Error retrieving image");
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
     *         the required width and height.
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
      updateSelectionUi();
    }
  }
}
