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
import android.os.Build;
import android.text.TextUtils;
import com.evernote.client.conn.ApplicationInfo;
import com.evernote.client.conn.mobile.TEvernoteHttpClient;
import com.evernote.edam.notestore.NoteStore;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;

import java.io.File;

/**
 * Represents a session with the Evernote web service API. Used to authenticate
 * to the service via OAuth and obtain a NoteStore.Client object used to make
 * authenticated API calls.
 * 
 * To authenticate to Evernote, create an instance of this class using
 * {@link #EvernoteSession(ApplicationInfo, File)}, then call 
 * {@link #authenticate(Context)}, which will start an asynchronous
 * authentication Activity. When your calling Activity resumes,
 * call {@link #completeAuthentication(SharedPreferences)} to see whether authentication
 * was successful.
 * 
 * If you already have cached Evernote authentication credentials as a result
 * of a previously successful authentication, create an instance of this class
 * using @link {@link #EvernoteSession(ApplicationInfo, AuthenticationResult, File)}.
 * 
 * Once you have an authenticated instance of this class, call 
 * {@link #createNoteStore()} to obtain a NoteStore.Client object, which can be used
 * to make Evernote API calls.
 */
public class EvernoteSession {

  // Keys for values persisted in our shared preferences 
  protected static final String KEY_AUTHTOKEN = "evernote.mAuthToken";
  protected static final String KEY_NOTESTOREURL = "evernote.notestoreUrl";
  protected static final String KEY_WEBAPIURLPREFIX = "evernote.webApiUrlPrefix";
  protected static final String KEY_USERID = "evernote.userId";

  private ApplicationInfo mApplicationInfo;
  private AuthenticationResult mAuthenticationResult;
  private File mTempDir;

  /**
   * Create a new EvernoteSession that is not initially authenticated.
   * To authenticate, call {@link #authenticate(Context)}.
   * 
   * @param applicationInfo The information required to authenticate.
   * @param tempDir A directory in which temporary files can be created.
   */
  public EvernoteSession(ApplicationInfo applicationInfo, File tempDir) {
    this.mApplicationInfo = applicationInfo;
    this.mTempDir = tempDir;
  }
  
  /**
   * Create a new Evernote session using saved information 
   * from a previous successful authentication. 
   */
  public EvernoteSession(ApplicationInfo applicationInfo, 
      AuthenticationResult sessionInfo, File tempDir) {
    this(applicationInfo, tempDir);
    this.mAuthenticationResult = sessionInfo;
  }

  /**
   * Create a new Evernote session, using saved information 
   * from a previous successful authentication if available. 
   */
  public EvernoteSession(ApplicationInfo applicationInfo, 
      SharedPreferences sessionInfo, File tempDir) {
    this(applicationInfo, tempDir);
    this.mAuthenticationResult = getAuthenticationResult(sessionInfo);
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
  public void logOut(SharedPreferences prefs) {
    mAuthenticationResult = null;
    
    // Removed cached authentication information
    Editor editor = prefs.edit();
    editor.remove(KEY_AUTHTOKEN);
    editor.remove(KEY_NOTESTOREURL);
    editor.remove(KEY_WEBAPIURLPREFIX);
    editor.remove(KEY_USERID);

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      editor.apply();
    } else {
      editor.commit();
    }

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
          mApplicationInfo.getUserAgent(), mTempDir);
    TBinaryProtocol protocol = new TBinaryProtocol(transport);
    return new NoteStore.Client(protocol, protocol);  
  }

  /**
   * Start the OAuth authentication process. Obtains an OAuth request token
   * from the Evernote service and redirects the user to the web browser
   * to authorize access to their Evernote account.
   */
  public void authenticate(Context context) {
    // Create an activity that will be used for authentication
    Intent intent = new Intent(context, EvernoteOAuthActivity.class);
    intent.putExtra(EvernoteOAuthActivity.EXTRA_EVERNOTE_HOST, mApplicationInfo.getEvernoteHost());
    intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_KEY, mApplicationInfo.getConsumerKey());
    intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_SECRET, mApplicationInfo.getConsumerSecret());
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    context.startActivity(intent);
  }
}
