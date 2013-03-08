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

import com.evernote.edam.notestore.*;
import com.evernote.edam.type.*;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.protocol.TProtocol;

import java.util.List;

/**
 * An Async wrapper for {@link NoteStore.Client}
 * Use these methods with a {@link OnClientCallback} to get make network requests
 *
 * @author @tylersmithnet
 */
public class AsyncNoteStoreClient extends NoteStore.Client {

  protected final String mAuthenticationToken;

  AsyncNoteStoreClient(TProtocol prot, String authenticationToken) {
    super(prot);
    mAuthenticationToken = authenticationToken;
  }

  AsyncNoteStoreClient(TProtocol iprot, TProtocol oprot, String authenticationToken) {
    super(iprot, oprot);
    mAuthenticationToken = authenticationToken;
  }

  String getAuthenticationToken() {
    return mAuthenticationToken;
  }


  /**
   * Async wrappers for NoteStore.Client Methods
   */

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSyncState(String)
   */
  public void getSyncState(OnClientCallback<SyncState> callback) {
    AsyncReflector.execute(this, callback, "getSyncState", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client##getSyncStateWithMetrics(com.evernote.edam.notestore.ClientUsageMetrics, OnClientCallback)
   */
  public void getSyncStateWithMetrics(ClientUsageMetrics clientMetrics, OnClientCallback<SyncState> callback) {
    AsyncReflector.execute(this, callback, "getSyncStateWithMetrics", mAuthenticationToken, clientMetrics);
  }


  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSyncChunk(String, int, int, boolean)
   */
  public void getSyncChunk(int afterUSN, int maxEntries, boolean fullSyncOnly, OnClientCallback<SyncChunk> callback) {
    AsyncReflector.execute(this, callback, "getSyncChunk", mAuthenticationToken, afterUSN, maxEntries, fullSyncOnly);
  }


  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getFilteredSyncChunk(String, int, int, com.evernote.edam.notestore.SyncChunkFilter)
   */
  public void getFilteredSyncChunk(int afterUSN, int maxEntries, SyncChunkFilter filter, OnClientCallback<SyncChunk> callback) {
    AsyncReflector.execute(this, callback, "getFilteredSyncChunk", mAuthenticationToken, afterUSN, maxEntries, filter);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getLinkedNotebookSyncState(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void getLinkedNotebookSyncState(LinkedNotebook linkedNotebook, OnClientCallback<SyncState> callback) {
    AsyncReflector.execute(this, callback, "getLinkedNotebookSyncState", mAuthenticationToken, linkedNotebook);
  }


  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getLinkedNotebookSyncChunk(String, com.evernote.edam.type.LinkedNotebook, int, int, boolean)
   */
  public void getLinkedNotebookSyncChunk(LinkedNotebook linkedNotebook, int afterUSN, int maxEntries, boolean fullSyncOnly, OnClientCallback<SyncChunk> callback) {
    AsyncReflector.execute(this, callback, "getLinkedNotebookSyncChunk", mAuthenticationToken, linkedNotebook, afterUSN, maxEntries, fullSyncOnly);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listNotebooks(String)
   */
  public void listNotebooks(OnClientCallback<List<Notebook>> callback) {
    AsyncReflector.execute(this, callback, "listNotebooks", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNotebook(String, String)
   */
  public void getNotebook(String guid, OnClientCallback<Notebook> callback) {
    AsyncReflector.execute(this, callback, "getNotebook", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getDefaultNotebook(String)
   */
  public void getDefaultNotebook(OnClientCallback<Notebook> callback) {
    AsyncReflector.execute(this, callback, "getDefaultNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createNotebook(String, com.evernote.edam.type.Notebook)
   */
  public void createNotebook(Notebook notebook, OnClientCallback<Notebook> callback) {
    AsyncReflector.execute(this, callback, "createNotebook", mAuthenticationToken, notebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateNotebook(String, com.evernote.edam.type.Notebook)
   */
  public void updateNotebook(Notebook notebook, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "updateNotebook", mAuthenticationToken, notebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeNotebook(String, String)
   */
  public void expungeNotebook(String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeNotebook", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listTags(String)
   */
  public void listTags(OnClientCallback<List<Tag>> callback) {
    AsyncReflector.execute(this, callback, "listTags", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listTagsByNotebook(String, String)
   */
  public void listTagsByNotebook(String notebookGuid, OnClientCallback<List<Tag>> callback) {
    AsyncReflector.execute(this, callback, "listTagsByNotebook", mAuthenticationToken, notebookGuid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getTag(String, String)
   */
  public void getTag(String guid, OnClientCallback<Tag> callback) {
    AsyncReflector.execute(this, callback, "getTag", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createTag(String, com.evernote.edam.type.Tag)
   */
  public void createTag(Tag tag, OnClientCallback<Tag> callback) {
    AsyncReflector.execute(this, callback, "createTag", mAuthenticationToken, tag);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateTag(String, com.evernote.edam.type.Tag)
   */
  public void updateTag(Tag tag, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "updateTag", mAuthenticationToken, tag);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#untagAll(String, String)
   */
  public void untagAll(String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "untagAll", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeTag(String, String)
   */
  public void expungeTag(String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeTag", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listSearches(String)
   */
  public void listSearches(OnClientCallback<List<SavedSearch>> callback) {
    AsyncReflector.execute(this, callback, "listSearches", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSearch(String, String)
   */
  public void getSearch(String guid, OnClientCallback<SavedSearch> callback) {
    AsyncReflector.execute(this, callback, "getSearch", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createSearch(String, com.evernote.edam.type.SavedSearch)
   */
  public void createSearch(SavedSearch search, OnClientCallback<SavedSearch> callback) {
    AsyncReflector.execute(this, callback, "createSearch", mAuthenticationToken, search)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateSearch(String, com.evernote.edam.type.SavedSearch)
   */
  public void updateSearch(SavedSearch search, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "updateSearch", mAuthenticationToken, search);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeSearch(String, String)
   */
  public void expungeSearch(String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeSearch", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNotes(String, com.evernote.edam.notestore.NoteFilter, int, int)
   */
  public void findNotes(NoteFilter filter, int offset, int maxNotes, OnClientCallback<NoteList> callback) {
    AsyncReflector.execute(this, callback, "findNotes", mAuthenticationToken, filter, offset, maxNotes);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNoteOffset(String, com.evernote.edam.notestore.NoteFilter, String)
   */
  public void findNoteOffset(NoteFilter filter, String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "findNoteOffset", mAuthenticationToken, filter, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNotesMetadata(String, com.evernote.edam.notestore.NoteFilter, int, int, com.evernote.edam.notestore.NotesMetadataResultSpec)
   */
  public void findNotesMetadata(NoteFilter filter, int offset, int maxNotes, NotesMetadataResultSpec resultSpec, OnClientCallback<NotesMetadataList> callback) {
    AsyncReflector.execute(this, callback, "findNotesMetadata", mAuthenticationToken, filter, offset, maxNotes, resultSpec);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNoteCounts(String, com.evernote.edam.notestore.NoteFilter, boolean)
   */
  public void findNoteCounts(NoteFilter filter, boolean withTrash, OnClientCallback<NoteCollectionCounts> callback) {
    AsyncReflector.execute(this, callback, "findNoteCounts", mAuthenticationToken, filter, withTrash);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNote(String, String, boolean, boolean, boolean, boolean)
   */
  public void getNote(String guid, boolean withContent, boolean withResourcesData, boolean withResourcesRecognition, boolean withResourcesAlternateData, OnClientCallback<Note> callback) {
    AsyncReflector.execute(this, callback, "getNote", mAuthenticationToken, guid, withContent, withResourcesData, withResourcesRecognition, withResourcesAlternateData);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteApplicationData(String, String)
   */
  public void getNoteApplicationData(String guid, OnClientCallback<LazyMap> callback) {
    AsyncReflector.execute(this, callback, "getNoteApplicationData", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteApplicationDataEntry(String, String, String)
   */
  public void getNoteApplicationDataEntry(String guid, String key, OnClientCallback<String> callback) {
    AsyncReflector.execute(this, callback, "getNoteApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#setNoteApplicationDataEntry(String, String, String, String)
   */
  public void setNoteApplicationDataEntry(String guid, String key, String value, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "setNoteApplicationDataEntry", mAuthenticationToken, guid, key, value);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#unsetNoteApplicationDataEntry(String, String, String)
   */
  public void unsetNoteApplicationDataEntry(String guid, String key, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "unsetNoteApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteContent(String, String)
   */
  public void getNoteContent(String guid, OnClientCallback<String> callback) {
    AsyncReflector.execute(this, callback, "getNoteContent", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteSearchText(String, String, boolean, boolean)
   */
  public void getNoteSearchText(String guid, boolean noteOnly, boolean tokenizeForIndexing, OnClientCallback<String> callback) {
    AsyncReflector.execute(this, callback, "getNoteSearchText", mAuthenticationToken, guid, noteOnly, tokenizeForIndexing);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceSearchText(String, String)
   */
  public void getResourceSearchText(String guid, OnClientCallback<String> callback) {
    AsyncReflector.execute(this, callback, "getResourceSearchText", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteTagNames(String, String)
   */
  public void getNoteTagNames(String guid, OnClientCallback<List<String>> callback) {
    AsyncReflector.execute(this, callback, "getNoteTagNames", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createNote(String, com.evernote.edam.type.Note)
   */
  public void createNote(Note note, OnClientCallback<Note> callback) {
    AsyncReflector.execute(this, callback, "createNote", mAuthenticationToken, note);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateNote(String, com.evernote.edam.type.Note)
   */
  public void updateNote(Note note, OnClientCallback<Note> callback) {
    AsyncReflector.execute(this, callback, "updateNote", mAuthenticationToken, note);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#deleteNote(String, String)
   */
  public void deleteNote(String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "deleteNote", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeNote(String, String)
   */
  public void expungeNote(String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeNote", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeNotes(String, java.util.List)
   */
  public void expungeNotes(List<String> noteGuids, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeNotes", mAuthenticationToken, noteGuids)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeInactiveNotes(String)
   */
  public void expungeInactiveNotes(OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeInactiveNotes", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#copyNote(String, String, String)
   */
  public void copyNote(String noteGuid, String toNotebookGuid, OnClientCallback<Note> callback) {
    AsyncReflector.execute(this, callback, "copyNote", mAuthenticationToken, noteGuid, toNotebookGuid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listNoteVersions(String, String)
   */
  public void listNoteVersions(String noteGuid, OnClientCallback<List<NoteVersionId>> callback) {
    AsyncReflector.execute(this, callback, "listNoteVersions", mAuthenticationToken, noteGuid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteVersion(String, String, int, boolean, boolean, boolean)
   */
  public void getNoteVersion(String noteGuid, int updateSequenceNum, boolean withResourcesData, boolean withResourcesRecognition, boolean withResourcesAlternateData, OnClientCallback<Note> callback) {
    AsyncReflector.execute(this, callback, "getNoteVersion", mAuthenticationToken, noteGuid, updateSequenceNum, withResourcesData, withResourcesRecognition, withResourcesAlternateData);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResource(String, String, boolean, boolean, boolean, boolean)
   */
  public void getResource(String guid, boolean withData, boolean withRecognition, boolean withAttributes, boolean withAlternateData, OnClientCallback<Resource> callback) {
    AsyncReflector.execute(this, callback, "getResource", mAuthenticationToken, guid, withData, withRecognition, withAttributes, withAlternateData)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceApplicationData(String, String)
   */
  public void getResourceApplicationData(String guid, OnClientCallback<LazyMap> callback) {
    AsyncReflector.execute(this, callback, "getResourceApplicationData", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceApplicationDataEntry(String, String, String)
   */
  public void getResourceApplicationDataEntry(String guid, String key, OnClientCallback<String> callback) {
    AsyncReflector.execute(this, callback, "getResourceApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#setResourceApplicationDataEntry(String, String, String, String)
   */
  public void setResourceApplicationDataEntry(String guid, String key, String value, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "setResourceApplicationDataEntry", mAuthenticationToken, guid, key, value);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#unsetResourceApplicationDataEntry(String, String, String)
   */
  public void unsetResourceApplicationDataEntry(String guid, String key, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "unsetResourceApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateResource(String, com.evernote.edam.type.Resource)
   */
  public void updateResource(Resource resource, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "updateResource", mAuthenticationToken, resource);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceData(String, String)
   */
  public void getResourceData(String guid, OnClientCallback<byte[]> callback) {
    AsyncReflector.execute(this, callback, "getResourceData", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceByHash(String, String, byte[], boolean, boolean, boolean)
   */
  public void getResourceByHash(String noteGuid, byte[] contentHash, boolean withData, boolean withRecognition, boolean withAlternateData, OnClientCallback<Resource> callback) {
    AsyncReflector.execute(this, callback, "getResourceByHash", mAuthenticationToken, noteGuid, contentHash, withData, withRecognition, withAlternateData);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceRecognition(String, String)
   */
  public void getResourceRecognition(String guid, OnClientCallback<byte[]> callback) {
    AsyncReflector.execute(this, callback, "getResourceRecognition", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceAlternateData(String, String)
   */
  public void getResourceAlternateData(String guid, OnClientCallback<byte[]> callback) {
    AsyncReflector.execute(this, callback, "getResourceAlternateData", mAuthenticationToken, guid)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceAttributes(String, String)
   */
  public void getResourceAttributes(String guid, OnClientCallback<ResourceAttributes> callback) {
    AsyncReflector.execute(this, callback, "getResourceAttributes", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getPublicNotebook(int, String)
   */
  public void getPublicNotebook(int userId, String publicUri, OnClientCallback<Notebook> callback) {
    AsyncReflector.execute(this, callback, "getPublicNotebook", mAuthenticationToken, userId, publicUri);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createSharedNotebook(String, com.evernote.edam.type.SharedNotebook)
   */
  public void createSharedNotebook(SharedNotebook sharedNotebook, OnClientCallback<SharedNotebook> callback) {
    AsyncReflector.execute(this, callback, "createSharedNotebook", mAuthenticationToken, sharedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateSharedNotebook(String, com.evernote.edam.type.SharedNotebook)
   */
  public void updateSharedNotebook(SharedNotebook sharedNotebook, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "updateSharedNotebook", mAuthenticationToken, sharedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#sendMessageToSharedNotebookMembers(String, String, String, java.util.List)
   */
  public void sendMessageToSharedNotebookMembers(String notebookGuid, String messageText, List<String> recipients, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "sendMessageToSharedNotebookMembers", mAuthenticationToken, notebookGuid, messageText, recipients);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listSharedNotebooks(String)
   */
  public void listSharedNotebooks(OnClientCallback<List<SharedNotebook>> callback) {
    AsyncReflector.execute(this, callback, "listSharedNotebooks", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeSharedNotebooks(String, java.util.List)
   */
  public void expungeSharedNotebooks(List<Long> sharedNotebookIds, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeSharedNotebooks", mAuthenticationToken, sharedNotebookIds);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createLinkedNotebook(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void createLinkedNotebook(LinkedNotebook linkedNotebook, OnClientCallback<LinkedNotebook> callback) {
    AsyncReflector.execute(this, callback, "createLinkedNotebook", mAuthenticationToken, linkedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateLinkedNotebook(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void updateLinkedNotebook(LinkedNotebook linkedNotebook, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "updateLinkedNotebook", mAuthenticationToken, linkedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listLinkedNotebooks(String)
   */
  public void listLinkedNotebooks(OnClientCallback<List<LinkedNotebook>> callback) {
    AsyncReflector.execute(this, callback, "listLinkedNotebooks", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeLinkedNotebook(String, String)
   */
  public void expungeLinkedNotebook(String guid, OnClientCallback<Integer> callback) {
    AsyncReflector.execute(this, callback, "expungeLinkedNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#authenticateToSharedNotebook(String, String)
   */
  public void authenticateToSharedNotebook(String shareKey, OnClientCallback<AuthenticationResult> callback) {
    AsyncReflector.execute(this, callback, "authenticateToSharedNotebook", mAuthenticationToken, shareKey);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSharedNotebookByAuth(String)
   */
  public void getSharedNotebookByAuth(OnClientCallback<SharedNotebook> callback) {
    AsyncReflector.execute(this, callback, "getSharedNotebookByAuth", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#emailNote(String, com.evernote.edam.notestore.NoteEmailParameters)
   */
  public void emailNote(NoteEmailParameters parameters, OnClientCallback<Void> callback) {
    AsyncReflector.execute(this, callback, "emailNote", mAuthenticationToken, parameters);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#shareNote(String, String)
   */
  public void shareNote(String guid, OnClientCallback<String> callback) {
    AsyncReflector.execute(this, callback, "shareNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#stopSharingNote(String, String)
   */
  public void stopSharingNote(String guid, OnClientCallback<Void> callback) {
    AsyncReflector.execute(this, callback, "stopSharingNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#authenticateToSharedNote(String, String)
   */
  public void authenticateToSharedNote(String guid, String noteKey, OnClientCallback<AuthenticationResult> callback) {
    AsyncReflector.execute(this, callback, "authenticateToSharedNote", mAuthenticationToken, guid, noteKey);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findRelated(String, com.evernote.edam.notestore.RelatedQuery, com.evernote.edam.notestore.RelatedResultSpec)
   */
  public void findRelated(RelatedQuery query, RelatedResultSpec resultSpec, OnClientCallback<RelatedResult> callback) {
    AsyncReflector.execute(this, callback, "findRelated", mAuthenticationToken, query, resultSpec);
  }
}
