package com.evernote.client.android;

import android.os.Handler;
import android.os.Looper;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.*;
import com.evernote.edam.type.*;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TProtocol;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * An Async wrapper for {@link NoteStore.Client}
 * Use these methods with a {@link OnClientCallback} to get make network requests
 *
 * @author @tylersmithnet
 */
public class AsyncNoteStoreClient extends NoteStore.Client implements AsyncClientInterface {

  private final ExecutorService mThreadExecutor;
  private final String mAuthenticationToken;
  private final Handler mUIHandler;

  AsyncNoteStoreClient(TProtocol prot, String authenticationToken) {
    super(prot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
    mAuthenticationToken = authenticationToken;
    mUIHandler = new Handler(Looper.getMainLooper());
  }

  AsyncNoteStoreClient(TProtocol iprot, TProtocol oprot, String authenticationToken) {
    super(iprot, oprot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
    mAuthenticationToken = authenticationToken;
    mUIHandler = new Handler(Looper.getMainLooper());
  }

  public <T> void execute(final OnClientCallback<T, Exception> callback, final String function, final Object... args) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          Class[] classes = new Class[args.length];
          for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
          }

          Method  method = AsyncNoteStoreClient.this.getClass().getMethod(function, classes.length > 0 ? classes : null);

          final T answer = (T) method.invoke(AsyncNoteStoreClient.this, args);

          mUIHandler.post(new Runnable() {
            @Override
            public void run() {
              callback.onResultsReceived(answer);
            }
          });

        } catch (final Exception ex) {
          mUIHandler.post(new Runnable() {
            @Override
            public void run() {
              callback.onErrorReceived(ex);
            }
          });
        }
      }
    });
  }

  /**
   * Custom Business Helper Methods
   */

  /**
   * Helper method to create a note asynchronously in a business notebook
   *
   * @param note
   * @param businessNotebook
   * @param callback
   */
  public void createBusinessNote(final Note note, final LinkedNotebook businessNotebook, final OnClientCallback<Note, Exception> callback) {
    execute(callback, "createBusinessNote", mAuthenticationToken, note, businessNotebook);
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
  public Note createBusinessNote(Note note, LinkedNotebook businessNotebook) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
    SharedNotebook notebook = getSharedNotebookByAuth(mAuthenticationToken);
    note.setNotebookGuid(notebook.getNotebookGuid());
    return note;
  }

  /**
   * Helper method to list business notebooks asynchronously
   *
   * @param callback
   */
  public void listBusinessNotebooks(final OnClientCallback<List<LinkedNotebook>, Exception> callback) {
    execute(callback, "listBusinessNotebooks", mAuthenticationToken);
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
  public List<LinkedNotebook> listBusinessNotebooks() throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
    List<LinkedNotebook> linkedNotebooks = new ArrayList<LinkedNotebook>();
    for (LinkedNotebook notebook : listLinkedNotebooks(mAuthenticationToken)) {
      if (notebook.isSetBusinessId()) {
        linkedNotebooks.add(notebook);
      }
    }
    return linkedNotebooks;
  }

//  public void createBusinessNotebook() {
//
//  }
//
//  public LinkedNotebook createBusinessNotebook() {
//
//  }
//
//  public void deleteBusinessNotebook() {
//
//  }
//
//  public int deleteBusinessNotebook() {
//
//  }


  /**
   * Async wrappers for NoteStore.Client Methods
   */

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSyncState(String)
   */
  public void getSyncState(OnClientCallback<SyncState, Exception> callback) {
    execute(callback, "getSyncState", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client##getSyncStateWithMetrics(com.evernote.edam.notestore.ClientUsageMetrics, OnClientCallback)
   */
  public void getSyncStateWithMetrics(ClientUsageMetrics clientMetrics, OnClientCallback<SyncState, Exception> callback) {
    execute(callback, "getSyncStateWithMetrics", mAuthenticationToken, clientMetrics);
  }


  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSyncChunk(String, int, int, boolean)
   */
  public void getSyncChunk(int afterUSN, int maxEntries, boolean fullSyncOnly, OnClientCallback<SyncChunk, Exception> callback) {
    execute(callback, "getSyncChunk", mAuthenticationToken, afterUSN, maxEntries, fullSyncOnly);
  }


  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getFilteredSyncChunk(String, int, int, com.evernote.edam.notestore.SyncChunkFilter)
   */
  public void getFilteredSyncChunk(int afterUSN, int maxEntries, SyncChunkFilter filter, OnClientCallback<SyncChunk, Exception> callback) {
    execute(callback, "getFilteredSyncChunk", mAuthenticationToken, afterUSN, maxEntries, filter);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getLinkedNotebookSyncState(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void getLinkedNotebookSyncState(LinkedNotebook linkedNotebook, OnClientCallback<SyncState, Exception> callback) {
    execute(callback, "getLinkedNotebookSyncState", mAuthenticationToken, linkedNotebook);
  }


  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getLinkedNotebookSyncChunk(String, com.evernote.edam.type.LinkedNotebook, int, int, boolean)
   */
  public void getLinkedNotebookSyncChunk(LinkedNotebook linkedNotebook, int afterUSN, int maxEntries, boolean fullSyncOnly, OnClientCallback<SyncChunk, Exception> callback) {
    execute(callback, "getLinkedNotebookSyncChunk", mAuthenticationToken, linkedNotebook, afterUSN);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listNotebooks(String)
   */
  public void listNotebooks(OnClientCallback<List<Notebook>, Exception> callback) {
    execute(callback, "listNotebooks", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNotebook(String, String)
   */
  public void getNotebook(String guid, OnClientCallback<Notebook, Exception> callback) {
    execute(callback, "getNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getDefaultNotebook(String)
   */
  public void getDefaultNotebook(OnClientCallback<Notebook, Exception> callback) {
    execute(callback, "getDefaultNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createNotebook(String, com.evernote.edam.type.Notebook)
   */
  public void createNotebook(Notebook notebook, OnClientCallback<Notebook, Exception> callback) {
    execute(callback, "createNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateNotebook(String, com.evernote.edam.type.Notebook)
   */
  public void updateNotebook(Notebook notebook, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "updateNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeNotebook(String, String)
   */
  public void expungeNotebook(String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listTags(String)
   */
  public void listTags(OnClientCallback<List<Tag>, Exception> callback) {
    execute(callback, "listTags", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listTagsByNotebook(String, String)
   */
  public void listTagsByNotebook(String notebookGuid, OnClientCallback<List<Tag>, Exception> callback) {
    execute(callback, "listTagsByNotebook", mAuthenticationToken, notebookGuid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getTag(String, String)
   */
  public void getTag(String guid, OnClientCallback<Tag, Exception> callback) {
    execute(callback, "getTag", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createTag(String, com.evernote.edam.type.Tag)
   */
  public void createTag(Tag tag, OnClientCallback<Tag, Exception> callback) {
    execute(callback, "createTag", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateTag(String, com.evernote.edam.type.Tag)
   */
  public void updateTag(Tag tag, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "updateTag", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#untagAll(String, String)
   */
  public void untagAll(String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "untagAll", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeTag(String, String)
   */
  public void expungeTag(String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeTag", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listSearches(String)
   */
  public void listSearches(OnClientCallback<List<SavedSearch>, Exception> callback) {
    execute(callback, "listSearches", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSearch(String, String)
   */
  public void getSearch(String guid, OnClientCallback<SavedSearch, Exception> callback) {
    execute(callback, "getSearch", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createSearch(String, com.evernote.edam.type.SavedSearch)
   */
  public void createSearch(SavedSearch search, OnClientCallback<SavedSearch, Exception> callback) {
    execute(callback, "createSearch", mAuthenticationToken, search)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateSearch(String, com.evernote.edam.type.SavedSearch)
   */
  public void updateSearch(SavedSearch search, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "updateSearch", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeSearch(String, String)
   */
  public void expungeSearch(String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeSearch", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNotes(String, com.evernote.edam.notestore.NoteFilter, int, int)
   */
  public void findNotes(NoteFilter filter, int offset, int maxNotes, OnClientCallback<NoteList, Exception> callback) {
    execute(callback, "findNotes", mAuthenticationToken, filter, offset, maxNotes);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNoteOffset(String, com.evernote.edam.notestore.NoteFilter, String)
   */
  public void findNoteOffset(NoteFilter filter, String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "findNoteOffset", mAuthenticationToken, filter, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNotesMetadata(String, com.evernote.edam.notestore.NoteFilter, int, int, com.evernote.edam.notestore.NotesMetadataResultSpec)
   */
  public void findNotesMetadata(NoteFilter filter, int offset, int maxNotes, NotesMetadataResultSpec resultSpec, OnClientCallback<NotesMetadataList, Exception> callback) {
    execute(callback, "findNotesMetadata", mAuthenticationToken, filter, offset, maxNotes);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findNoteCounts(String, com.evernote.edam.notestore.NoteFilter, boolean)
   */
  public void findNoteCounts(NoteFilter filter, boolean withTrash, OnClientCallback<NoteCollectionCounts, Exception> callback) {
    execute(callback, "findNoteCounts", mAuthenticationToken, filter, withTrash);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNote(String, String, boolean, boolean, boolean, boolean)
   */
  public void getNote(String guid, boolean withContent, boolean withResourcesData, boolean withResourcesRecognition, boolean withResourcesAlternateData, OnClientCallback<Note, Exception> callback) {
    execute(callback, "getNote", mAuthenticationToken, guid, withContent, withResourcesData, withResourcesRecognition, withResourcesAlternateData);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteApplicationData(String, String)
   */
  public void getNoteApplicationData(String guid, OnClientCallback<LazyMap, Exception> callback) {
    execute(callback, "getNoteApplicationData", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteApplicationDataEntry(String, String, String)
   */
  public void getNoteApplicationDataEntry(String guid, String key, OnClientCallback<String, Exception> callback) {
    execute(callback, "getNoteApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#setNoteApplicationDataEntry(String, String, String, String)
   */
  public void setNoteApplicationDataEntry(String guid, String key, String value, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "setNoteApplicationDataEntry", mAuthenticationToken, guid, key, value);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#unsetNoteApplicationDataEntry(String, String, String)
   */
  public void unsetNoteApplicationDataEntry(String guid, String key, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "unsetNoteApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteContent(String, String)
   */
  public void getNoteContent(String guid, OnClientCallback<String, Exception> callback) {
    execute(callback, "getNoteContent", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteSearchText(String, String, boolean, boolean)
   */
  public void getNoteSearchText(String guid, boolean noteOnly, boolean tokenizeForIndexing, OnClientCallback<String, Exception> callback) {
    execute(callback, "getNoteSearchText", mAuthenticationToken, guid, noteOnly, tokenizeForIndexing);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceSearchText(String, String)
   */
  public void getResourceSearchText(String guid, OnClientCallback<String, Exception> callback) {
    execute(callback, "getResourceSearchText", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteTagNames(String, String)
   */
  public void getNoteTagNames(String guid, OnClientCallback<List<String>, Exception> callback) {
    execute(callback, "getNoteTagNames", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createNote(String, com.evernote.edam.type.Note)
   */
  public void createNote(Note note, OnClientCallback<Note, Exception> callback) {
    execute(callback, "createNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateNote(String, com.evernote.edam.type.Note)
   */
  public void updateNote(Note note, OnClientCallback<Note, Exception> callback) {
    execute(callback, "updateNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#deleteNote(String, String)
   */
  public void deleteNote(String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "deleteNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeNote(String, String)
   */
  public void expungeNote(String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeNotes(String, java.util.List)
   */
  public void expungeNotes(List<String> noteGuids, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeNotes", mAuthenticationToken, noteGuids)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeInactiveNotes(String)
   */
  public void expungeInactiveNotes(OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeInactiveNotes", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#copyNote(String, String, String)
   */
  public void copyNote(String noteGuid, String toNotebookGuid, OnClientCallback<Note, Exception> callback) {
    execute(callback, "copyNote", mAuthenticationToken, noteGuid, toNotebookGuid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listNoteVersions(String, String)
   */
  public void listNoteVersions(String noteGuid, OnClientCallback<List<NoteVersionId>, Exception> callback) {
    execute(callback, "listNoteVersions", mAuthenticationToken, noteGuid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getNoteVersion(String, String, int, boolean, boolean, boolean)
   */
  public void getNoteVersion(String noteGuid, int updateSequenceNum, boolean withResourcesData, boolean withResourcesRecognition, boolean withResourcesAlternateData, OnClientCallback<Note, Exception> callback) {
    execute(callback, "getNoteVersion", mAuthenticationToken, noteGuid, updateSequenceNum, withResourcesData, withResourcesRecognition, withResourcesAlternateData);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResource(String, String, boolean, boolean, boolean, boolean)
   */
  public void getResource(String guid, boolean withData, boolean withRecognition, boolean withAttributes, boolean withAlternateData, OnClientCallback<Resource, Exception> callback) {
    execute(callback, "getResource", mAuthenticationToken, guid, withData, withRecognition, withAttributes, withAlternateData)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceApplicationData(String, String)
   */
  public void getResourceApplicationData(String guid, OnClientCallback<LazyMap, Exception> callback) {
    execute(callback, "getResourceApplicationData", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceApplicationDataEntry(String, String, String)
   */
  public void getResourceApplicationDataEntry(String guid, String key, OnClientCallback<String, Exception> callback) {
    execute(callback, "getResourceApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#setResourceApplicationDataEntry(String, String, String, String)
   */
  public void setResourceApplicationDataEntry(String guid, String key, String value, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "setResourceApplicationDataEntry", mAuthenticationToken, guid, key, value);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#unsetResourceApplicationDataEntry(String, String, String)
   */
  public void unsetResourceApplicationDataEntry(String guid, String key, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "unsetResourceApplicationDataEntry", mAuthenticationToken, guid, key);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateResource(String, com.evernote.edam.type.Resource)
   */
  public void updateResource(Resource resource, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "updateResource", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceData(String, String)
   */
  public void getResourceData(String guid, OnClientCallback<byte[], Exception> callback) {
    execute(callback, "getResourceData", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceByHash(String, String, byte[], boolean, boolean, boolean)
   */
  public void getResourceByHash(String noteGuid, byte[] contentHash, boolean withData, boolean withRecognition, boolean withAlternateData, OnClientCallback<Resource, Exception> callback) {
    execute(callback, "getResourceByHash", mAuthenticationToken, noteGuid, contentHash, withData, withRecognition, withAlternateData);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceRecognition(String, String)
   */
  public void getResourceRecognition(String guid, OnClientCallback<byte[], Exception> callback) {
    execute(callback, "getResourceRecognition", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceAlternateData(String, String)
   */
  public void getResourceAlternateData(String guid, OnClientCallback<byte[], Exception> callback) {
    execute(callback, "getResourceAlternateData", mAuthenticationToken, guid)
    ;
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getResourceAttributes(String, String)
   */
  public void getResourceAttributes(String guid, OnClientCallback<ResourceAttributes, Exception> callback) {
    execute(callback, "getResourceAttributes", mAuthenticationToken, guid);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getPublicNotebook(int, String)
   */
  public void getPublicNotebook(int userId, String publicUri, OnClientCallback<Notebook, Exception> callback) {
    execute(callback, "getPublicNotebook", mAuthenticationToken, userId, publicUri);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createSharedNotebook(String, com.evernote.edam.type.SharedNotebook)
   */
  public void createSharedNotebook(SharedNotebook sharedNotebook, OnClientCallback<SharedNotebook, Exception> callback) {
    execute(callback, "createSharedNotebook", mAuthenticationToken, sharedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateSharedNotebook(String, com.evernote.edam.type.SharedNotebook)
   */
  public void updateSharedNotebook(SharedNotebook sharedNotebook, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "updateSharedNotebook", mAuthenticationToken, sharedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#sendMessageToSharedNotebookMembers(String, String, String, java.util.List)
   */
  public void sendMessageToSharedNotebookMembers(String notebookGuid, String messageText, List<String> recipients, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "sendMessageToSharedNotebookMembers", mAuthenticationToken, notebookGuid, messageText);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listSharedNotebooks(String)
   */
  public void listSharedNotebooks(OnClientCallback<List<SharedNotebook>, Exception> callback) {
    execute(callback, "listSharedNotebooks", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeSharedNotebooks(String, java.util.List)
   */
  public void expungeSharedNotebooks(List<Long> sharedNotebookIds, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeSharedNotebooks", mAuthenticationToken, sharedNotebookIds);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#createLinkedNotebook(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void createLinkedNotebook(LinkedNotebook linkedNotebook, OnClientCallback<LinkedNotebook, Exception> callback) {
    execute(callback, "createLinkedNotebook", mAuthenticationToken, linkedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#updateLinkedNotebook(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void updateLinkedNotebook(LinkedNotebook linkedNotebook, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "updateLinkedNotebook", mAuthenticationToken, linkedNotebook);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#listLinkedNotebooks(String)
   */
  public void listLinkedNotebooks(OnClientCallback<List<LinkedNotebook>, Exception> callback) {
    execute(callback, "listLinkedNotebooks", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#expungeLinkedNotebook(String, String)
   */
  public void expungeLinkedNotebook(String guid, OnClientCallback<Integer, Exception> callback) {
    execute(callback, "expungeLinkedNotebook", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#authenticateToSharedNotebook(String, String)
   */
  public void authenticateToSharedNotebook(String shareKey, OnClientCallback<AuthenticationResult, Exception> callback) {
    execute(callback, "authenticateToSharedNotebook", mAuthenticationToken, shareKey);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#getSharedNotebookByAuth(String)
   */
  public void getSharedNotebookByAuth(OnClientCallback<SharedNotebook, Exception> callback) {
    execute(callback, "getSharedNotebookByAuth", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#emailNote(String, com.evernote.edam.notestore.NoteEmailParameters)
   */
  public void emailNote(NoteEmailParameters parameters, OnClientCallback<Void, Exception> callback) {
    execute(callback, "emailNote", mAuthenticationToken, parameters);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#shareNote(String, String)
   */
  public void shareNote(String guid, OnClientCallback<String, Exception> callback) {
    execute(callback, "shareNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#stopSharingNote(String, String)
   */
  public void stopSharingNote(String guid, OnClientCallback<Void, Exception> callback) {
    execute(callback, "stopSharingNote", mAuthenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#authenticateToSharedNote(String, String)
   */
  public void authenticateToSharedNote(String guid, String noteKey, OnClientCallback<AuthenticationResult, Exception> callback) {
    execute(callback, "authenticateToSharedNote", mAuthenticationToken, guid, noteKey);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see NoteStore.Client#findRelated(String, com.evernote.edam.notestore.RelatedQuery, com.evernote.edam.notestore.RelatedResultSpec)
   */
  public void findRelated(RelatedQuery query, RelatedResultSpec resultSpec, OnClientCallback<RelatedResult, Exception> callback) {
    execute(callback, "findRelated", mAuthenticationToken, query, resultSpec);
  }
}
