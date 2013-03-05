package com.evernote.client.oauth.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 *
 * A class to manage Evernote specific preferences
 *
 * @author @tylersmithnet
 */
public class Preferences {

  // Keys for values persisted in our shared preferences
  protected static final String KEY_AUTHTOKEN = "evernote.mAuthToken";
  protected static final String KEY_NOTESTOREURL = "evernote.notestoreUrl";
  protected static final String KEY_WEBAPIURLPREFIX = "evernote.webApiUrlPrefix";
  protected static final String KEY_USERID = "evernote.userId";
  protected static final String KEY_EVERNOTEHOST = "evernote.mEvernoteHost";
  protected static final String KEY_BUSINESSID = "evernote.businessId";
  protected static final String KEY_BUSINESSNOTESTOREURL = "evernote.businessNoteStoreUrl";
  protected static final String KEY_BUSINESSTOKENEXPIRATION = "evernote.businessTokenExpiration";

  protected static final String PREFERENCE_NAME = "evernote.preferences";

  /**
   *
   * @return the {@link SharedPreferences} object to the private space
   */
  protected static SharedPreferences getPreferences(Context ctx) {
    return ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
  }

  /**
   * Saves {@link SharedPreferences.Editor} using a non-blocking method on Gingerbread and up
   * Saves {@link SharedPreferences.Editor} using a blocking method below Gingerbread
   */
  @TargetApi(9)
  protected static void save(SharedPreferences.Editor editor) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      editor.apply();
    } else {
      editor.commit();
    }
  }

}
