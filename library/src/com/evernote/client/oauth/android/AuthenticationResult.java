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

import android.content.SharedPreferences;
import android.util.Log;

/**
 * A container class for the results of a successful OAuth authorization with
 * the Evernote service.
 *
 * @author @tylersmithnet
 */
public class AuthenticationResult {

  private String LOGTAG = "AuthenticatonResult";

  private String mAuthToken;
  private String mNoteStoreUrl;
  private String mWebApiUrlPrefix;
  private String mEvernoteHost;
  private int mUserId;

  private int mBusinessId;
  private String mBusinessNoteStoreUrl;
  private long mBusinessAuthTokenExpiration;


  public AuthenticationResult(SharedPreferences pref) {
    restore(pref);
  }

  /**
   * Create a new AuthenticationResult.
   *
   * @param authToken An Evernote authentication token.
   * @param noteStoreUrl The URL of the Evernote NoteStore for the authenticated user.
   * @param webApiUrlPrefix The URL of misc. Evernote web APIs for the authenticated user.
   * @param evernoteHost the Evernote Web URL provided from the bootstrap process
   * @param userId The numeric ID of the Evernote user.
   */
  public AuthenticationResult(String authToken, String noteStoreUrl,
    String webApiUrlPrefix, String evernoteHost, int userId) {

    this(authToken, noteStoreUrl, webApiUrlPrefix, evernoteHost, userId, null, -1, -1);
  }

  /**
   * Create a new AuthenticationResult.
   *
   * @param authToken An Evernote authentication token.
   * @param noteStoreUrl The URL of the Evernote NoteStore for the authenticated user.
   * @param webApiUrlPrefix The URL of misc. Evernote web APIs for the authenticated user.
   * @param evernoteHost the Evernote Web URL provided from the bootstrap process
   * @param userId The numeric ID of the Evernote user.
   * @param userId The numeric ID of the Evernote user.
   * @param businessNoteStoreUrl The URL of the Evernote BusinessNoteStore for the authenticated user's Business Account.
   * @param businessAuthTokenExpiration The epoch time for business auth token expiraton
   *
   */

  public AuthenticationResult(String authToken, String noteStoreUrl, String webApiUrlPrefix, String evernoteHost, int userId,
      String businessNoteStoreUrl, int businessId, long businessAuthTokenExpiration) {
    this.mAuthToken = authToken;
    this.mNoteStoreUrl = noteStoreUrl;
    this.mWebApiUrlPrefix = webApiUrlPrefix;
    this.mEvernoteHost = evernoteHost;
    this.mUserId = userId;
    this.mBusinessId = businessId;
    this.mBusinessNoteStoreUrl = businessNoteStoreUrl;
    this.mBusinessAuthTokenExpiration = businessAuthTokenExpiration;
  }

  void persist(SharedPreferences pref) {
    Log.d(LOGTAG, "persisting Authentication results to SharedPreference");
    SharedPreferences.Editor editor = pref.edit();

    editor.putString(Preferences.KEY_AUTHTOKEN, mAuthToken);
    editor.putString(Preferences.KEY_NOTESTOREURL, mNoteStoreUrl);
    editor.putString(Preferences.KEY_WEBAPIURLPREFIX, mWebApiUrlPrefix);
    editor.putString(Preferences.KEY_EVERNOTEHOST, mEvernoteHost);
    editor.putInt(Preferences.KEY_USERID, mUserId);
    editor.putInt(Preferences.KEY_BUSINESSID, mBusinessId);
    editor.putString(Preferences.KEY_BUSINESSNOTESTOREURL, mBusinessNoteStoreUrl);
    editor.putLong(Preferences.KEY_BUSINESSTOKENEXPIRATION, mBusinessAuthTokenExpiration);

    Preferences.save(editor);
  }

  void restore(SharedPreferences pref) {
    Log.d(LOGTAG, "restoring Authentication results from SharedPreference");
    mAuthToken = pref.getString(Preferences.KEY_AUTHTOKEN, null);
    mNoteStoreUrl = pref.getString(Preferences.KEY_NOTESTOREURL, null);
    mWebApiUrlPrefix = pref.getString(Preferences.KEY_WEBAPIURLPREFIX, null);
    mEvernoteHost = pref.getString(Preferences.KEY_EVERNOTEHOST, null);
    mUserId = pref.getInt(Preferences.KEY_USERID, -1);
    mBusinessId = pref.getInt(Preferences.KEY_BUSINESSID, -1);
    mBusinessNoteStoreUrl = pref.getString(Preferences.KEY_BUSINESSNOTESTOREURL, null);
    mBusinessAuthTokenExpiration = pref.getLong(Preferences.KEY_BUSINESSTOKENEXPIRATION, 0);
  }

  void clear(SharedPreferences pref) {
    Log.d(LOGTAG, "clearing Authentication results from SharedPreference");
    SharedPreferences.Editor editor = pref.edit();

    editor.remove(Preferences.KEY_AUTHTOKEN);
    editor.remove(Preferences.KEY_NOTESTOREURL);
    editor.remove(Preferences.KEY_WEBAPIURLPREFIX);
    editor.remove(Preferences.KEY_EVERNOTEHOST);
    editor.remove(Preferences.KEY_USERID);
    editor.remove(Preferences.KEY_BUSINESSID);
    editor.remove(Preferences.KEY_BUSINESSNOTESTOREURL);
    editor.remove(Preferences.KEY_BUSINESSTOKENEXPIRATION);

    Preferences.save(editor);
  }



  /**
   * @return the authentication token that will be used to make authenticated API requests.
   */
  public String getAuthToken() {
    return mAuthToken;
  }

  /**
   * @return the URL that will be used to access the NoteStore service.
   */
  public String getNoteStoreUrl() {
    return mNoteStoreUrl;
  }

  /**
   * @return the URL prefix that can be used to access non-Thrift API endpoints.
   */
  public String getWebApiUrlPrefix() {
    return mWebApiUrlPrefix;
  }

  /**
   *
   * @return the Evernote Web URL provided from the bootstrap process
   */
  public String getEvernoteHost() {
    return mEvernoteHost;
  }

  /**
   * @return the numeric user ID of the user who authorized access to their Evernote account.
   */
  public int getUserId() {
    return mUserId;
  }


  /**
   *
   * @return The Business ID
   */
  public int getBusinessId() {
    return mBusinessId;
  }

  /**
   * Set the Business Id
   */
  void setBusinessId(int mBusinessId) {
    this.mBusinessId = mBusinessId;
  }

  /**
   * @return the URL that will be used to access the BusinessNoteStore service.
   */
  public String getBusinessNoteStoreUrl() {
    return mBusinessNoteStoreUrl;
  }

  /**
   * Set the BusinessNoteStore Url
   */
  void setBusinessNoteStoreUrl(String mBusinessNoteStoreUrl) {
    this.mBusinessNoteStoreUrl = mBusinessNoteStoreUrl;
  }

  /**
   * @return the BusinessNoteStore Authorizaton token's expiration time (epoch)
   */
  public long getBusinessAuthTokenExpiration() {
    return mBusinessAuthTokenExpiration;
  }

  /**
   * Set the BusinessNoteStore Authorizaton token's expiration time  (epoch)
   */
  void setBusinessAuthTokenExpiration(long mBusinessAuthTokenExpiration) {
    this.mBusinessAuthTokenExpiration = mBusinessAuthTokenExpiration;
  }
}
