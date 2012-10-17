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

import java.io.File;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.evernote.client.conn.ApplicationInfo;
import com.evernote.client.conn.mobile.TEvernoteHttpClient;
import com.evernote.client.oauth.EvernoteAuthToken;
import com.evernote.edam.notestore.NoteStore;

/**
 * Represents a session with the Evernote web service API. Used to authenticate
 * to the service via OAuth and obtain a NoteStore.Client object used to make
 * authenticated API calls.
 * 
 * To authenticate to Evernote, create an instance of this class using
 * {@link #EvernoteSession(ApplicationInfo, File)}, then call 
 * {@link #authenticate(Context)}, which will start an asynchronous
 * authentication Activity. When your calling Activity resumes,
 * call {@link #completeAuthentication()} to see whether authentication
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
  private static final String KEY_AUTHTOKEN = "evernote.authToken";
  private static final String KEY_NOTESTOREURL = "evernote.notestoreUrl";
  private static final String KEY_WEBAPIURLPREFIX = "evernote.webApiUrlPrefix";
  private static final String KEY_USERID = "evernote.userId";

  private ApplicationInfo applicationInfo;
  private AuthenticationResult authenticationResult;
  private File tempDir;

  /**
   * Create a new EvernoteSession that is not initially authenticated.
   * To authenticate, call {@link #authenticate()}.
   * 
   * @param applicationInfo The information required to authenticate.
   * @param tempDir A directory in which temporary files can be created.
   */
  public EvernoteSession(ApplicationInfo applicationInfo, File tempDir) {
    this.applicationInfo = applicationInfo;
    this.tempDir = tempDir;
  }
  
  /**
   * Create a new Evernote session using saved information 
   * from a previous successful authentication. 
   */
  public EvernoteSession(ApplicationInfo applicationInfo, 
      AuthenticationResult sessionInfo, File tempDir) {
    this(applicationInfo, tempDir);
    this.authenticationResult = sessionInfo; 
  }

  /**
   * Create a new Evernote session, using saved information 
   * from a previous successful authentication if available. 
   */
  public EvernoteSession(ApplicationInfo applicationInfo, 
      SharedPreferences sessionInfo, File tempDir) {
    this(applicationInfo, tempDir);
    this.authenticationResult = getAuthenticationResult(sessionInfo); 
  }

  /**
   * Restore an AuthenticationResult from shared preferences.
   * @return The restored AuthenticationResult, or null if the preferences
   * did not contain the required information.
   */
  private AuthenticationResult getAuthenticationResult(SharedPreferences prefs) {
    String authToken = prefs.getString(KEY_AUTHTOKEN, "");
    String notestoreUrl = prefs.getString(KEY_NOTESTOREURL, "");
    String webApiUrlPrefix = prefs.getString(KEY_WEBAPIURLPREFIX, "");
    int userId = prefs.getInt(KEY_USERID, -1);

    if (authToken.length() > 0 && notestoreUrl.length() > 0 && 
        webApiUrlPrefix.length() > 0 && (userId > 0)) {
      return new AuthenticationResult(authToken, notestoreUrl, webApiUrlPrefix, userId);
    } else {
      return null;
    }
  }

  /**
   * Check whether the session has valid authentication information
   * that will allow successful API calls to be made.
   */
  public boolean isLoggedIn() {
    return authenticationResult != null;
  }

  /**
   * Clear all stored authentication information.
   */
  public void logOut(SharedPreferences prefs) {
    authenticationResult = null;
    
    // Removed cached authentication information
    Editor editor = prefs.edit();
    editor.remove(KEY_AUTHTOKEN);
    editor.remove(KEY_NOTESTOREURL);
    editor.remove(KEY_WEBAPIURLPREFIX);
    editor.remove(KEY_USERID);
    editor.apply();
  }

  /**
   * Get the authentication token that is used to make API calls
   * though a NoteStore.Client.
   *  
   * @return an authentication token, or null if {@link #isLoggedIn()}
   * is false.
   */
  public String getAuthToken() {
    if (authenticationResult != null) {
      return authenticationResult.getAuthToken();
    } else {
      return null;
    }
  }
  
  /**
   * Get the authentication information returned by a successful
   * OAuth authentication to the Evernote web service.
   */
  public AuthenticationResult getAuthenticationResult() {
    return authenticationResult;
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
      new TEvernoteHttpClient(authenticationResult.getNoteStoreUrl(), 
          applicationInfo.getUserAgent(), tempDir);
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
    intent.putExtra(EvernoteOAuthActivity.EXTRA_EVERNOTE_HOST, applicationInfo.getEvernoteHost());
    intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_KEY, applicationInfo.getConsumerKey());
    intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_SECRET, applicationInfo.getConsumerSecret());
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    context.startActivity(intent);
  }
  
  /**
   * Complete the OAuth authentication process after the user authorizes
   * access to their Evernote account and the Evernote service redirects
   * the user back to the application.
   */
  public boolean completeAuthentication(SharedPreferences prefs) {
    boolean result = false;
    
    if (EvernoteOAuthActivity.authToken != null) {
      EvernoteAuthToken token = EvernoteOAuthActivity.authToken;
      authenticationResult = 
        new AuthenticationResult(token.getToken(), token.getNoteStoreUrl(), 
            token.getWebApiUrlPrefix(), token.getUserId());
      
      // Persist the authentication results so that the user does not
      // have to authenticate again until their token expires or is revoked.
      Editor editor = prefs.edit();
      editor.putString(KEY_AUTHTOKEN, token.getToken());
      editor.putString(KEY_NOTESTOREURL, token.getNoteStoreUrl());
      editor.putString(KEY_WEBAPIURLPREFIX, token.getWebApiUrlPrefix());
      editor.putInt(KEY_USERID, token.getUserId());
      editor.apply();
      
      result = true;
    } else {
      // If there's a pending authentication and we have no auth token, we failed
      result = !EvernoteOAuthActivity.startedAuthentication;
    }
    
    EvernoteOAuthActivity.reset();
    return result;
  }
}
