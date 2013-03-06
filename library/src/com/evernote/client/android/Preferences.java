package com.evernote.client.android;

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
class Preferences {

  // Keys for values persisted in our shared preferences
  static final String KEY_AUTHTOKEN = "evernote.mAuthToken";
  static final String KEY_NOTESTOREURL = "evernote.notestoreUrl";
  static final String KEY_WEBAPIURLPREFIX = "evernote.webApiUrlPrefix";
  static final String KEY_USERID = "evernote.userId";
  static final String KEY_EVERNOTEHOST = "evernote.mEvernoteHost";
  static final String KEY_BUSINESSID = "evernote.businessId";
  static final String KEY_BUSINESSNOTESTOREURL = "evernote.businessNoteStoreUrl";
  static final String KEY_BUSINESSTOKENEXPIRATION = "evernote.businessTokenExpiration";

  static final String PREFERENCE_NAME = "evernote.preferences";

  /**
   *
   * @return the {@link SharedPreferences} object to the private space
   */
  static SharedPreferences getPreferences(Context ctx) {
    return ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
  }

  /**
   * Saves {@link SharedPreferences.Editor} using a non-blocking method on Gingerbread and up
   * Saves {@link SharedPreferences.Editor} using a blocking method below Gingerbread
   */
  @TargetApi(9)
  static void save(SharedPreferences.Editor editor) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      editor.apply();
    } else {
      editor.commit();
    }
  }

}
