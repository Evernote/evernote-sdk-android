package com.evernote.android.sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.evernote.android.sample.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

/**
 * This simple Android Activity demonstrates how to integrate with 
 * Evernote for Android using Intents. Evernote for Android must be
 * installed on your device for this sample app to work. You can
 * download Evernote for Android from the Android Market:
 * https://market.android.com/details?id=com.evernote
 */
public class HelloEvernote extends Activity {
  
  // The directory on disk where we store application data
  private static final String APP_DATA_PATH = "/Android/data/com.evernote.android.sample/files/";
  
  // The prefix for an ENEX file, up to the note title
  private static final String ENEX_PREFIX_PART_ONE = 
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE en-export SYSTEM \"http://xml.evernote.com/pub/evernote-export.dtd\">" +
    "<en-export><note><title>";
  
  // The prefix for an ENEX file, after the note title but before the note content  
  private static final String ENEX_PREFIX_PART_TWO = "</title><content><![CDATA[";
  
  // The suffix for an ENEX file, after the note content
  private static final String ENEX_SUFFIX =
    "]]></content></note></en-export>";
  
  // The ENML prefix for note content
  private static final String NOTE_PREFIX = 
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" +
    "<en-note>";
  
  // The ENML suffix for note content
  private static final String NOTE_SUFFIX = "</en-note>";
  
  // Activity result request codes
  private static final int SHARE_IMAGE = 1;
  private static final int NEW_NOTE_WITH_CONTENT_AND_ATTACHMENT = 2;
  
  // Names of Evernote-specific Intent actions and extras
  public static final String ACTION_NEW_NOTE             = "com.evernote.action.CREATE_NEW_NOTE";
  public static final String ACTION_NEW_SNAPSHOT         = "com.evernote.action.NEW_SNAPSHOT";
  public static final String ACTION_NEW_VOICE_NOTE       = "com.evernote.action.NEW_VOICE_NOTE";
  public static final String ACTION_NEW_SEARCH           = "com.evernote.action.SEARCH";
  public static final String ACTION_VIEW_NOTE            = "com.evernote.action.VIEW_NOTE";
  public static final String ACTION_SEARCH_NOTES         = "com.evernote.action.SEARCH_NOTES";
  
  public static final String EXTRA_NOTE_GUID             = "NOTE_GUID";
  public static final String EXTRA_FULL_SCREEN           = "FULL_SCREEN";
  public static final String EXTRA_TAGS                  = "TAG_NAME_LIST";
  public static final String EXTRA_NOTEBOOK_GUID         = "NOTEBOOK_GUID";
  public static final String EXTRA_SOURCE_URL            = "SOURCE_URL";
  public static final String EXTRA_SOURCE_APP            = "SOURCE_APP";
  public static final String EXTRA_AUTHOR                = "AUTHOR";
  public static final String EXTRA_QUICK_SEND            = "QUICK_SEND";
  
  public static final String TYPE_TEXT                   = "text/plain";
  public static final String TYPE_ENEX                   = "application/enex";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }
  
  /**
   * Bring up an empty "New Note" activity in Evernote for Android.
   */
  public void newNote(View view) {
    Intent intent = new Intent();
    intent.setAction(ACTION_NEW_NOTE);
    try {
      startActivity(intent);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
    } 
  }
  
  /**
   * Bring up a "New Note" activity in Evernote for Android with the note content 
   * and title prepopulated with values that we specify. 
   */
  public void newNoteWithContent(View view) {
    String text = "This is a sample text file.\nThis is line two.";
    String title = "New Note with Content";
    
    Intent intent = new Intent();
    intent.setAction(ACTION_NEW_NOTE);

    // Set the note's title and plaintext content
    intent.putExtra(Intent.EXTRA_TITLE, title);
    intent.putExtra(Intent.EXTRA_TEXT, text);

    try {
      startActivity(intent);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
    } 
  }
  
  /**
   * Called when the user taps the "Create new note with content & file" button.
   * 
   * Sends the user to the image gallery to choose an image to share.
   * After they choose an image, Android will call "onActivityResult", 
   * which will allow us to retrieve the image that the user chose.
   * We then call newNoteWithContentAndAttachment to share that image, 
   * along with note title & content, with Evernote for Android.
   */
  public void startNewNoteWithContentAndAttachment(View view) {
    Intent intent = new Intent(Intent.ACTION_PICK, 
                               MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    startActivityForResult(intent, NEW_NOTE_WITH_CONTENT_AND_ATTACHMENT);
  }

  /**
   * Bring up a "New Note" activity in Evernote for Android with an image
   * that we specify attached to the note, as well as the note content 
   * and title prepopulated with values that we specify. In this code, 
   * we also demonstrate setting a number of other note attributes,
   * including tags, notebook, sourceUrl and sourceApplication.
   */
  public void newNoteWithContentAndAttachment(Shareable file) {
    String text = "This is a sample text file.\nThis is line two.";
    String title = "New Note with Content and Attachment";
    
    Intent intent = new Intent();
    intent.setAction(ACTION_NEW_NOTE);
    
    // Set the note's title and plaintext content
    intent.putExtra(Intent.EXTRA_TITLE, title);
    intent.putExtra(Intent.EXTRA_TEXT, text);

    // Add tags, which will be created if they don't exist
    ArrayList<String> tags = new ArrayList<String>();
    tags.add("tagOne");
    tags.add("tagTwo");
    intent.putExtra(EXTRA_TAGS, tags);
    
    // If we knew the GUID of a notebook that we wanted to put the new note in, we could set it here
    //String notebookGuid = "d7c41948-f4aa-46e1-a818-e6ff73877145";
    //intent.putExtra(EXTRA_NOTE_GUID, notebookGuid);
    
    // Set the note's author, souceUrl and sourceApplication attributes.
    // To learn more, see
    // http://www.evernote.com/about/developer/api/ref/Types.html#Struct_NoteAttributes
    intent.putExtra(EXTRA_AUTHOR, "Seth Hitchings");
    intent.putExtra(EXTRA_SOURCE_URL, "http://www.evernote.com/about/developer/android.php");
    intent.putExtra(EXTRA_SOURCE_APP, "EvernoteAndroidIntentDemo{This is my app's custom data}");
    
    // If you set QUICK_SEND to true, Evernote for Android will automatically "save"
    // the new note. The user will see the "New note" activity briefly, then
    // return to your application.
    //intent.putExtra(EXTRA_QUICK_SEND, true);
    
    // Add file(s) to be attached to the note
    ArrayList<Uri> uriList = new ArrayList<Uri>();
    uriList.add(file.uri);
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM , uriList);
    try {
      startActivity(intent);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
    } 
  }

  /**
   * Bring up a search results dialog in Evernote for Android containing
   * the results of a query that we specify. In this sample the query 
   * searches for all notes with the tag "test". The full search grammer
   * can be found in Appendix C of the Evernote API overview at 
   * http://www.evernote.com/about/developer/api/evernote-api.htm.
   */
  public void doSearch(View view) {
    String query = "tag:test";
    
    Intent intent = new Intent();
    intent.setAction(ACTION_SEARCH_NOTES);
    intent.putExtra(SearchManager.QUERY, query);
    try {
      startActivity(intent);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
    } 
  }
  
  /**
   * Display a specific note, identified by GUID, in Evernote for Android.
   * Users can then edit the note by tapping the menu button and tapping "Edit".
   *
   * Note that this will not work for you unless you pass the GUID of a note
   * that is in the account of the user who is signed into Evernote for Android.
   */
  public void viewNote(View view) {
    String noteGuid = "63781605-3c3d-4e56-90a8-8be5e3ae7eee";
    boolean hideTitleBar = true;
    
    Intent intent = new Intent();
    intent.setAction(ACTION_VIEW_NOTE);
    intent.putExtra(EXTRA_NOTE_GUID, noteGuid);
    intent.putExtra(EXTRA_FULL_SCREEN, hideTitleBar);
    try {
      startActivity(intent);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
    } 
  }

  /**
   * Create a new note by creating an Evernote export file (ENEX) and sharing it with 
   * Evernote for Android. The export file contains the full definition of a note, 
   * including the title, content, included resources (aka attachments), and note 
   * attributes such as location. The DTD defining the export format can be found at
   * http://xml.evernote.com/pub/evernote-export.dtd.
   * 
   * The activity will not display any Evernote for Android user interface.
   * Instead, it will validate that the ENEX is formatted correctly, then
   * queue the new note for upload.
   * 
   * Note that if your ENEX content isn't valid, you may not receive an error from
   * Evernote for Android. Instead, the next time Evernote for Android tries to 
   * sync, the user will be notified that the note failed to upload. Be careful
   * to ensure that the note body is valid ENML.
   */
  public void createNoteUsingEnex(View view) {
    String noteTitle = "Sample Note Title";
    String noteContent = "<h1>This is a headline</h1>" +
                         "<p>This is a paragraph.</p>" +
                         "<en-todo/>This is a checkbox";
    
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == false) {
      Toast.makeText(this, R.string.err_no_storage, Toast.LENGTH_SHORT).show();
      return;
    }

    File appDir = new File(Environment.getExternalStorageDirectory(), APP_DATA_PATH);
    File enexFile = new File(appDir, "sample.enex");
    
    // Create a properly formatted ENEX file that defines the note we want created
    try {
      if (appDir.exists() == false) {
        appDir.mkdirs();
      }
      if (enexFile.exists()) {
        enexFile.delete();
      } 
      if (enexFile.createNewFile() == false) {
        Toast.makeText(this, R.string.err_create_enml_file, Toast.LENGTH_LONG).show();
      } else {
        PrintWriter out = new PrintWriter(new FileWriter(enexFile));
        out.print(ENEX_PREFIX_PART_ONE);
        out.print(noteTitle);
        out.print(ENEX_PREFIX_PART_TWO);
        out.println(NOTE_PREFIX);
        out.println(noteContent);
        out.println(NOTE_SUFFIX);
        out.println(ENEX_SUFFIX);
        out.close();
      }
    } catch (IOException iox) {
      Toast.makeText(this, R.string.err_create_enml_file, Toast.LENGTH_LONG).show();
      return;
    }

    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    // Unlike sharing a file attachment, sharing an ENEX requires that you pass the file 
    // in the data field, not the stream extra
    intent.setDataAndType(Uri.fromFile(enexFile), TYPE_ENEX);
    try {
      startActivity(Intent.createChooser(intent, getString(R.string.label_share_using)));
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
    } 
    Toast.makeText(this, R.string.msg_enex_success, Toast.LENGTH_SHORT).show();
  }

  /**
   * Called when the user taps the "Share Image" button.
   * 
   * Sends the user to the image gallery to choose an image to share.
   * After they choose an image, Android will call "onActivityResult", 
   * which will allow us to retrieve the image that the user chose.
   * We can then share that image with Evernote for Android.
   */
  public void startShareImage(View view) {
    Intent intent = new Intent(Intent.ACTION_PICK, 
                               MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    startActivityForResult(intent, SHARE_IMAGE);
  }

  /**
   * Called when the control returns from an activity that we launched.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == SHARE_IMAGE) {
        // Callback from our 'startShareImage' action
        shareImage(endSelectImage(data));
      } else if (requestCode == NEW_NOTE_WITH_CONTENT_AND_ATTACHMENT) {
        // Callback from our 'startNewNoteWithContentAndAttachment' action
        newNoteWithContentAndAttachment(endSelectImage(data));
      }
    }
  }

  /**
   * Called when control returns from the image gallery picker. 
   * Obtains the Uri and MIME type of the selected image from the gallery. 
   */
  private Shareable endSelectImage(Intent data) {
    // The callback from the gallery contains a pointer into a table.
    // Look up the appropriate record and pull out the information that we need,
    // in this case, the path to the file on disk and the MIME type. 
    Uri selectedImage = data.getData();
    String[] queryColumns = { MediaStore.Images.Media.DATA, 
                              MediaStore.Images.Media.MIME_TYPE};
    Cursor cursor = getContentResolver().query(selectedImage, queryColumns, null, null, null);
    cursor.moveToFirst();
    
    String filePath = cursor.getString(cursor.getColumnIndex(queryColumns[0]));
    String mimeType = cursor.getString(cursor.getColumnIndex(queryColumns[1]));
    cursor.close();

    return new Shareable(Uri.fromFile(new File(filePath)), mimeType);
  }
  
  /**
   * Bring up a "New Note" activity in Evernote for Android with an image
   * that we specify attached to the note.
   */
  public void shareImage(Shareable file) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.setType(file.mimeType);
    intent.putExtra(Intent.EXTRA_STREAM, file.uri);
    try {
      startActivity(Intent.createChooser(intent, getString(R.string.label_share_using)));
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
    } 
  }
  
  /**
   * Simple container class to hold a File URI and the associated file's MIME type.
   */
  private class Shareable {
    private Uri uri;
    private String mimeType;
    private Shareable(Uri uri, String mimeType) {
      this.uri = uri;
      this.mimeType = mimeType;
    }
  }
}