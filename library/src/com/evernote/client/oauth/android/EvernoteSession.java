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
package com.evernote.client.oauth.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.evernote.client.conn.mobile.TEvernoteHttpClient;
import com.evernote.client.oauth.EvernoteAuthToken;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.UserStore;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;

import java.io.File;
import java.util.Locale;

/**
 * Represents a session with the Evernote web service API. Used to authenticate
 * to the service via OAuth and obtain a NoteStore.Client object used to make
 * authenticated API calls.
 * 
 */
public class EvernoteSession {

  //TODO: Revise docs

  // Keys for values persisted in our shared preferences 
  protected static final String KEY_AUTHTOKEN = "evernote.mAuthToken";
  protected static final String KEY_NOTESTOREURL = "evernote.notestoreUrl";
  protected static final String KEY_WEBAPIURLPREFIX = "evernote.webApiUrlPrefix";
  protected static final String KEY_USERID = "evernote.userId";
  public static final int REQUEST_CODE_OAUTH = 1010101;

  protected static final String PREFERENCE_NAME = "evernote.preferences";

  private String mConsumerKey;
  private String mConsumerSecret;
  private String mConsumerHost;
  private String mUserAgentString;

  private AuthenticationResult mAuthenticationResult;
  private File mDataDirectory;

  private static EvernoteSession sInstance = null;

  //TODO: revise need for appInfo in singleton
  /**
   * Use to acquire a singleton instance of the EvernoteSession for authentication
   * @param ctx
   * @return
   */
  public static EvernoteSession init(Context ctx, String consumerKey, String consumerSecret, String consumerHost) {
    if(sInstance == null) {
      sInstance = new EvernoteSession(ctx, consumerKey, consumerSecret, consumerHost);
    }
    return sInstance;
  }

  /**
   * Used to access the instantiated instance without an application object
   * @return
   */
  public static EvernoteSession getInstance() {
    return sInstance;
  }


  /**
   * Private constructor, not to be called from outside
   * @param ctx
   * @param consumerKey
   * @param consumerSecret
   * @param consumerHost
   */
  private EvernoteSession(Context ctx, String consumerKey, String consumerSecret, String consumerHost) {
    mConsumerKey = consumerKey;
    mConsumerSecret = consumerSecret;
    mConsumerHost = consumerHost;
    setUserAgentString(ctx);

    SharedPreferences pref = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    this.mAuthenticationResult = getAuthenticationResult(pref);
    mDataDirectory = ctx.getFilesDir();
  }

  /**
   * Restore an AuthenticationResult from shared preferences.
   * @return The restored AuthenticationResult, or null if the preferences
   * did not contain the required information.
   */
  private AuthenticationResult getAuthenticationResult(SharedPreferences prefs) {
    String authToken = prefs.getString(KEY_AUTHTOKEN, null);
    String notestoreUrl = prefs.getString(KEY_NOTESTOREURL, null);
    String webApiUrlPrefix = prefs.getString(KEY_WEBAPIURLPREFIX, null);
    int userId = prefs.getInt(KEY_USERID, -1);

    if (TextUtils.isEmpty(authToken) ||
        TextUtils.isEmpty(notestoreUrl) ||
        TextUtils.isEmpty(webApiUrlPrefix) ||
        userId == -1) {
      return null;
    }
    return new AuthenticationResult(authToken, notestoreUrl, webApiUrlPrefix, userId);
  }

  /**
   * Check whether the session has valid authentication information
   * that will allow successful API calls to be made.
   */
  public boolean isLoggedIn() {
    return mAuthenticationResult != null;
  }

  /**
   * Clear all stored authentication information.
   */
  public void logOut(Context ctx) {
    mAuthenticationResult = null;
    
    // Removed cached authentication information
    Editor editor = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
    editor.remove(KEY_AUTHTOKEN);
    editor.remove(KEY_NOTESTOREURL);
    editor.remove(KEY_WEBAPIURLPREFIX);
    editor.remove(KEY_USERID);

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      editor.apply();
    } else {
      editor.commit();
    }

    CookieSyncManager.createInstance(ctx);
    CookieManager cookieManager = CookieManager.getInstance();
    cookieManager.removeAllCookie();
  }

  /**
   * Get the authentication token that is used to make API calls
   * though a NoteStore.Client.
   *  
   * @return an authentication token, or null if {@link #isLoggedIn()}
   * is false.
   */
  public String getAuthToken() {
    if (mAuthenticationResult != null) {
      return mAuthenticationResult.getAuthToken();
    } else {
      return null;
    }
  }
  
  /**
   * Get the authentication information returned by a successful
   * OAuth authentication to the Evernote web service.
   */
  public AuthenticationResult getmAuthenticationResult() {
    return mAuthenticationResult;
  }

  /**
   * Retrieves the File directory used to store the Evernote content
   * @return File Directory
   */
  public File getDataDirectory() {
    return mDataDirectory;
  }

  /**
   * Sets the file directory to store Evernote content
   * @param fileDir
   */
  public void setDataDirectory(File fileDir) {
    mDataDirectory = fileDir;
  }
  
  /**
   * Get a new NoteStore Client. The returned client can be used for any
   * number of API calls, but is NOT thread safe.
   * 
   * @throws IllegalStateException if @link #isLoggedIn() is false.
   * @throws TTransportException if an error occurs setting up the
   * connection to the Evernote service.
   */
  public NoteStore.Client createNoteStore() throws TTransportException {
    if (!isLoggedIn()) {
      throw new IllegalStateException();
    }   
    TEvernoteHttpClient transport = 
      new TEvernoteHttpClient(mAuthenticationResult.getNoteStoreUrl(),
          mUserAgentString, mDataDirectory);
    TBinaryProtocol protocol = new TBinaryProtocol(transport);
    return new NoteStore.Client(protocol, protocol);  
  }

  public UserStore.Client createUserStore()  throws TTransportException {
    String url = "";
    if (!mConsumerHost.startsWith("http")) {
      url = mConsumerHost.contains(":") ? "http://" : "https://";
    }
    url += mConsumerHost + "/edam/user";

    TEvernoteHttpClient transport =
        new TEvernoteHttpClient(url, mUserAgentString, mDataDirectory);

    TBinaryProtocol protocol = new TBinaryProtocol(transport);
    return new UserStore.Client(protocol, protocol);

  }

  private void setUserAgentString(Context ctx) {
    // com.evernote.sample Android/216817 (en); Android/4.0.3; Xoom/15;"

    String packageName = null;
    int packageVersion = 0;
    try {
      packageName= ctx.getPackageName();
      packageVersion = ctx.getPackageManager().getPackageInfo(packageName, 0).versionCode;

    } catch (PackageManager.NameNotFoundException e) {
      Log.e("tag", e.getMessage());
    }

    String userAgent = packageName+ " Android/" +packageVersion;

    Locale locale = java.util.Locale.getDefault();
    if (locale == null) {
      userAgent += " ("+Locale.US+");";
    } else {
      userAgent += " (" + locale.toString()+ "); ";
    }
    userAgent += "Android/"+android.os.Build.VERSION.RELEASE+"; ";
    userAgent +=
        android.os.Build.MODEL + "/" + android.os.Build.VERSION.SDK + ";";
    mUserAgentString = userAgent;
  }

  public String getUserAgentString() {
    return mUserAgentString;
  }

  /**
   * Start the OAuth authentication process. Obtains an OAuth request token
   * from the Evernote service and redirects the user to the web browser
   * to authorize access to their Evernote account.
   */
  public void authenticate(Context ctx) {
    // Create an activity that will be used for authentication
    Intent intent = new Intent(ctx, EvernoteOAuthActivity.class);
    intent.putExtra(EvernoteOAuthActivity.EXTRA_EVERNOTE_HOST, mConsumerHost);
    intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_KEY, mConsumerKey);
    intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_SECRET, mConsumerSecret);

    if(ctx instanceof Activity) {
      //If this is being called from an activity, an activity can register for the result code
      ((Activity)ctx).startActivityForResult(intent, REQUEST_CODE_OAUTH);
    } else {
      //If this is being called from a service, the refresh will be handled manually
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      ctx.startActivity(intent);
    }
  }

  /**
   * Called upon completion of the oauth process to save the token into the SharedPreferences
   * @param ctx Application Context or activity
   * @param authToken Oauth EvernoteAuthToken
   */
  protected boolean persistAuthenticationToken(Context ctx, EvernoteAuthToken authToken) {
    if(ctx == null || authToken == null) {
      return false;
    }
    ctx.getExternalCacheDir();
    SharedPreferences prefs = ctx.
        getSharedPreferences(EvernoteSession.PREFERENCE_NAME, Context.MODE_PRIVATE);

    SharedPreferences.Editor editor = prefs.edit();

    editor.putString(EvernoteSession.KEY_AUTHTOKEN, authToken.getToken());
    editor.putString(EvernoteSession.KEY_NOTESTOREURL, authToken.getNoteStoreUrl());
    editor.putString(EvernoteSession.KEY_WEBAPIURLPREFIX, authToken.getWebApiUrlPrefix());
    editor.putInt(EvernoteSession.KEY_USERID, authToken.getUserId());

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      editor.apply();
    } else {
      editor.commit();
    }
    mAuthenticationResult =
        new AuthenticationResult(
            authToken.getToken(),
            authToken.getNoteStoreUrl(),
            authToken.getWebApiUrlPrefix(),
            authToken.getUserId());
    return true;
  }
}
