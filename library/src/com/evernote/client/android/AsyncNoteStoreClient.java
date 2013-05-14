/*
 * Copyright 2012 Evernote Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, mClient
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    mClient list of conditions and the following disclaimer in the documentation
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
package com.evernote.client.android;

import com.evernote.edam.notestore.NoteStore;
import com.evernote.thrift.protocol.TProtocol;

/**
 * An Async wrapper for {@link NoteStore.Client}
 * Use these methods with a {@link OnClientCallback} to get make network requests
 *
 * @author @tylersmithnet
 */
public class AsyncNoteStoreClient {

  protected String mAuthenticationToken;
  protected final NoteStore.Client mClient;

  AsyncNoteStoreClient(TProtocol iprot, TProtocol oprot, String authenticationToken) {
    mClient = new NoteStore.Client(iprot, oprot);
    mAuthenticationToken = authenticationToken;
  }

  /**
   * If direct access to the Note Store is needed, all of these calls are synchronous
   * @return {@link NoteStore.Client}
   */
  public NoteStore.Client getClient() {
    return mClient;
  }

  /**
   * @return authToken inserted into calls
   */
  String getAuthenticationToken() {
    return mAuthenticationToken;
  }

  void setAuthToken(String authenticationToken) {
    mAuthenticationToken = authenticationToken;
  }
}
