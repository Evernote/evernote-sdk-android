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
package com.evernote.client.android;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.SharedNotebook;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.TException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Helper class to manage Frequent Business operations
 * These use mutliple NoteStores
 */
public class BusinessHelper {


  /**
   * Helper method to create a note asynchronously in a business notebook
   *
   * @param note
   * @param businessNotebook
   * @param callback
   */
  public static void createBusinessNote(final Note note, final LinkedNotebook businessNotebook, final OnClientCallback<Note> callback) {
    AsyncReflector.execute(BusinessHelper.class, callback, "createBusinessNote", note, businessNotebook);

  }

  /**
   * Helper method to create a note synchronously in a business notebook
   *
   * @param note
   * @param businessNotebook
   * @return
   * @throws com.evernote.edam.error.EDAMUserException
   *
   * @throws com.evernote.edam.error.EDAMSystemException
   *
   * @throws com.evernote.thrift.TException
   * @throws com.evernote.edam.error.EDAMNotFoundException
   *
   */
  public static Note createBusinessNote(Note note, LinkedNotebook businessNotebook) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
    AsyncNoteStoreClient noteStoreClient = EvernoteSession.getOpenSession().getClientFactory().createNoteStoreClient();
    SharedNotebook notebook = noteStoreClient.getClient().getSharedNotebookByAuth(noteStoreClient.getAuthenticationToken());
    note.setNotebookGuid(notebook.getNotebookGuid());
    return note;
  }

  /**
   * Helper method to list business notebooks asynchronously
   *
   * @param callback
   */
  public static void listBusinessNotebooks(final OnClientCallback<List<LinkedNotebook>> callback) {
    AsyncReflector.execute(BusinessHelper.class, callback, "listBusinessNotebooks");
  }

  /**
   * Helper method to list business notebooks synchronously
   *
   * @return
   * @throws EDAMUserException
   * @throws EDAMSystemException
   * @throws TException
   * @throws EDAMNotFoundException
   */
  public static List<LinkedNotebook> listBusinessNotebooks() throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
    AsyncNoteStoreClient noteStoreClient = EvernoteSession.getOpenSession().getClientFactory().createNoteStoreClient();

    List<LinkedNotebook> linkedNotebooks = new ArrayList<LinkedNotebook>();
    for (LinkedNotebook notebook : noteStoreClient.getClient().listLinkedNotebooks(noteStoreClient.getAuthenticationToken())) {
      if (notebook.isSetBusinessId()) {
        linkedNotebooks.add(notebook);
      }
    }
    return linkedNotebooks;
  }

  /**
   * Create Business Notebook from a Notebook
   *
   * Asynchronous call
   *
   * @param callback
   */
  public static void createBusinessNotebook(Notebook notebook, OnClientCallback<LinkedNotebook> callback) {
    AsyncReflector.execute(BusinessHelper.class, callback, "createBusinessNotebook", notebook);
  }

  /**
   * Create Business Notebook from a Notebook
   *
   * Synchronous call
   *
   * @return {@link LinkedNotebook} with guid from server
   */
  public static LinkedNotebook createBusinessNotebook(Notebook notebook) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

    AsyncNoteStoreClient businessNoteStore = EvernoteSession.getOpenSession().getClientFactory().createBusinessNoteStoreClient();
    AsyncNoteStoreClient noteStoreClient = EvernoteSession.getOpenSession().getClientFactory().createNoteStoreClient();

    Notebook businessNotebook = businessNoteStore.getClient().createNotebook(businessNoteStore.getAuthenticationToken(), notebook);
    SharedNotebook sharedNotebook = businessNotebook.getSharedNotebooks().get(0);
    LinkedNotebook linkedNotebook = new LinkedNotebook();
    linkedNotebook.setShareKey(sharedNotebook.getShareKey());
    linkedNotebook.setShareName(businessNotebook.getName());
    linkedNotebook.setUsername(EvernoteSession.getOpenSession().getAuthenticationResult().getBusinessUser().getUsername());
    linkedNotebook.setShardId(EvernoteSession.getOpenSession().getAuthenticationResult().getBusinessUser().getShardId());

    return noteStoreClient.getClient().createLinkedNotebook(noteStoreClient.getAuthenticationToken(), linkedNotebook);
  }

  /**
   * Providing a LinkedNotebook referencing a Business notebook, perform a delete
   *
   * Asynchronous call
   * @param callback
   */
  public static void deleteBusinessNotebook(LinkedNotebook businessNotebook, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(BusinessHelper.class, callback, "deleteBusinessNotebook", businessNotebook);
  }

  /**
   * Providing a LinkedNotebook referencing a Business notebook, perform a delete
   *
   * Synchronous call
   *
   * @return guid of notebook deleted
   */
  public static int deleteBusinessNotebook(LinkedNotebook linkedNotebook) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

    AsyncNoteStoreClient businessNoteStore = EvernoteSession.getOpenSession().getClientFactory().createBusinessNoteStoreClient();
    AsyncNoteStoreClient sharedNoteStore = EvernoteSession.getOpenSession().getClientFactory().createNoteStoreClient(linkedNotebook.getNoteStoreUrl());
    AsyncNoteStoreClient noteStore = EvernoteSession.getOpenSession().getClientFactory().createNoteStoreClient();
    AuthenticationResult sharedAuthKey = sharedNoteStore.getClient().authenticateToSharedNotebook(linkedNotebook.getShareKey(), noteStore.getAuthenticationToken());

    SharedNotebook sharedNotebook = sharedNoteStore.getClient().getSharedNotebookByAuth(sharedAuthKey.getAuthenticationToken());
    Long[] ids = {sharedNotebook.getId()};
    businessNoteStore.getClient().expungeSharedNotebooks(businessNoteStore.getAuthenticationToken(), Arrays.asList(ids));
    return noteStore.getClient().expungeLinkedNotebook(noteStore.getAuthenticationToken(), linkedNotebook.getGuid());
  }

  /**
   * Will return the {@link Notebook} associated with the {@link LinkedNotebook} from the business account
   *
   * Asynchronous call
   *
   * @param linkedNotebook
   * @param callback
   */
  public static void getCorrespondingBusinessNotebook(LinkedNotebook linkedNotebook, OnClientCallback<Notebook> callback) {
    AsyncReflector.execute(BusinessHelper.class, callback, "getCorrespondingBusinessNotebook", linkedNotebook);
  }

  /**
   * Will return the {@link Notebook} associated with the {@link LinkedNotebook} from the business account
   *
   * Synchronous call
   *
   * @param linkedNotebook
   */
  public static Notebook getCorrespondingBusinessNotebook(LinkedNotebook linkedNotebook) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

    AsyncNoteStoreClient businessNoteStore = EvernoteSession.getOpenSession().getClientFactory().createBusinessNoteStoreClient();
    AsyncNoteStoreClient sharedNoteStore = EvernoteSession.getOpenSession().getClientFactory().createNoteStoreClient(linkedNotebook.getNoteStoreUrl());
    AsyncNoteStoreClient noteStoreClient = EvernoteSession.getOpenSession().getClientFactory().createNoteStoreClient();
    AuthenticationResult sharedAuthKey = sharedNoteStore.getClient().authenticateToSharedNotebook(linkedNotebook.getShareKey(), noteStoreClient.getAuthenticationToken());

    SharedNotebook sharedNotebook = sharedNoteStore.getClient().getSharedNotebookByAuth(sharedAuthKey.getAuthenticationToken());
    return businessNoteStore.getClient().getNotebook(businessNoteStore.getAuthenticationToken(), sharedNotebook.getNotebookGuid());
  }

  /**
   * Checks writable permissions of {@link LinkedNotebook} on Business account
   *
   * Asynchronous call
   *
   * @param linkedNotebook
   * @param callback
   */
  public static void isBusinessNotebookWritable(LinkedNotebook linkedNotebook, OnClientCallback<Boolean> callback) {
    AsyncReflector.execute(BusinessHelper.class, callback, "isBusinessNotebookWritable", linkedNotebook);
  }
  /**
   * Checks writable permissions of {@link LinkedNotebook} on Business account
   *
   * Synchronous call
   *
   * @param linkedNotebook
   */
  public static boolean isBusinessNotebookWritable(LinkedNotebook linkedNotebook) throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {
    Notebook notebook = getCorrespondingBusinessNotebook(linkedNotebook);
    return !notebook.getRestrictions().isNoCreateNotes();
  }

  /**
   *
   * Asynchronous call
   *
   */
  public void isBusinessUser(final OnClientCallback<Boolean> callback) {
    AsyncReflector.execute(BusinessHelper.class, callback, "isBusinessUser");
  }

  /**
   *
   * Synchronous call
   *
   *
   * @return the result of a user belonging to a business account
   */
  public boolean isBusinessUser() throws TException, EDAMUserException, EDAMSystemException {
    AsyncUserStoreClient userStoreClient = EvernoteSession.getOpenSession().getClientFactory().createUserStoreClient();
    return userStoreClient.getClient().getUser(userStoreClient.getAuthenticationToken()).getAccounting().isSetBusinessId();
  }

}
