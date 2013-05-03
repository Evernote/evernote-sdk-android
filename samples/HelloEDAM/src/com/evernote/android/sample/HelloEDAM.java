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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.InvalidAuthenticationException;

/**
 * This simple Android app demonstrates how to integrate with the
 * Evernote API (aka EDAM).
 * <p/>
 * In this sample, the user authorizes access to their account using OAuth
 * <p/>
 * class created by @tylersmithnet
 */
public class HelloEDAM extends ParentActivity {

  // Name of this application, for logging
  private static final String LOGTAG = "HelloEDAM";

  // UI elements that we update
  private Button mLoginButton;
  private Button mLogoutButton;
  private ListView mListView;
  private ArrayAdapter mAdapter;

  //Listener to act on clicks
  private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      switch(position) {
        case 0:
          startActivity(new Intent(getApplicationContext(), ImagePicker.class));
          break;
        case 1:
          startActivity(new Intent(getApplicationContext(), SimpleNote.class));
          break;
        case 2:
          startActivity(new Intent(getApplicationContext(), SearchNotes.class));
      }
    }
  };



  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mLoginButton = (Button) findViewById(R.id.login);
    mLogoutButton = (Button) findViewById(R.id.logout);
    mListView = (ListView) findViewById(R.id.list);
    mAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1,
        android.R.id.text1,
        getResources().getStringArray(R.array.esdk__main_list));

    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(mItemClickListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    updateAuthUi();
  }

  /**
   * Update the UI based on Evernote authentication state.
   */
  private void updateAuthUi() {
    //show login button if logged out
    mLoginButton.setEnabled(!mEvernoteSession.isLoggedIn());

    //Show logout button if logged in
//    mLogoutButton.setEnabled(mEvernoteSession.isLoggedIn());

    //disable clickable elements until logged in
    mListView.setEnabled(mEvernoteSession.isLoggedIn());
  }

  /**
   * Called when the user taps the "Log in to Evernote" button.
   * Initiates the Evernote OAuth process
   */

  public void login(View view) {
    mEvernoteSession.authenticate(this);
  }

  /**
   * Called when the user taps the "Log in to Evernote" button.
   * Clears Evernote Session and logs out
   */
  public void logout(View view) {
    try {
      mEvernoteSession.logOut(this);
    } catch (InvalidAuthenticationException e) {
      Log.e(LOGTAG, "Tried to call logout with not logged in", e);
    }
    updateAuthUi();
  }

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
          updateAuthUi();
        }
        break;
    }
  }
}
