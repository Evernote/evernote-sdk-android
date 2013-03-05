package com.evernote.client.oauth.android.client;

import com.evernote.client.oauth.android.EvernoteSession;
import com.evernote.edam.notestore.*;
import com.evernote.edam.type.*;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.protocol.TProtocol;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * An Async wrapper for {@link NoteStore.Client}
 * Use these methods with a {@link OnClientCallback} to get make network requests
 *
 * @author @tylersmithnet
 */
public class AsyncNoteStoreClient extends NoteStore.Client {

  ExecutorService mThreadExecutor;
  public String mAuthenticationToken;

  public AsyncNoteStoreClient(TProtocol prot, String authenticationToken) {
    super(prot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
    mAuthenticationToken = authenticationToken;
  }

  public AsyncNoteStoreClient(TProtocol iprot, TProtocol oprot, String authenticationToken) {
    super(iprot, oprot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
    mAuthenticationToken = authenticationToken;
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getSyncState(String)
   */
  public void getSyncState(final OnClientCallback<SyncState, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {

      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getSyncState(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client##getSyncStateWithMetrics(com.evernote.edam.notestore.ClientUsageMetrics, OnClientCallback)
   */
  public void getSyncStateWithMetrics(final ClientUsageMetrics clientMetrics, final OnClientCallback<SyncState, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getSyncStateWithMetrics(mAuthenticationToken, clientMetrics));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }


  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getSyncChunk(String, int, int, boolean)
   */
  public void getSyncChunk(final int afterUSN, final int maxEntries, final boolean fullSyncOnly, final OnClientCallback<SyncChunk, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getSyncChunk(mAuthenticationToken, afterUSN, maxEntries, fullSyncOnly));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }


  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getFilteredSyncChunk(String, int, int, com.evernote.edam.notestore.SyncChunkFilter)
   */
  public void getFilteredSyncChunk(final int afterUSN, final int maxEntries, final SyncChunkFilter filter, final OnClientCallback<SyncChunk, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getFilteredSyncChunk(mAuthenticationToken, afterUSN, maxEntries, filter));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getLinkedNotebookSyncState(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void getLinkedNotebookSyncState(final LinkedNotebook linkedNotebook, final OnClientCallback<SyncState, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getLinkedNotebookSyncState(mAuthenticationToken, linkedNotebook));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }


  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getLinkedNotebookSyncChunk(String, com.evernote.edam.type.LinkedNotebook, int, int, boolean)
   */
  public void getLinkedNotebookSyncChunk(final LinkedNotebook linkedNotebook, final int afterUSN, final int maxEntries, final boolean fullSyncOnly, final OnClientCallback<SyncChunk, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getLinkedNotebookSyncChunk(mAuthenticationToken, linkedNotebook, afterUSN, maxEntries, fullSyncOnly));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#listNotebooks(String)
   */
  public void listNotebooks(final OnClientCallback<List<Notebook>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.listNotebooks(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNotebook(String, String)
   */
  public void getNotebook(final String guid, final OnClientCallback<Notebook, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNotebook(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getDefaultNotebook(String)
   */
  public void getDefaultNotebook(final OnClientCallback<Notebook, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getDefaultNotebook(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#createNotebook(String, com.evernote.edam.type.Notebook)
   */
  public void createNotebook(final Notebook notebook, final OnClientCallback<Notebook, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.createNotebook(mAuthenticationToken, notebook));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#updateNotebook(String, com.evernote.edam.type.Notebook)
   */
  public void updateNotebook(final Notebook notebook, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.updateNotebook(mAuthenticationToken, notebook));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeNotebook(String, String)
   */
  public void expungeNotebook(final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeNotebook(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#listTags(String)
   */
  public void listTags(final OnClientCallback<List<Tag>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.listTags(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#listTagsByNotebook(String, String)
   */
  public void listTagsByNotebook(final String notebookGuid, final OnClientCallback<List<Tag>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.listTagsByNotebook(mAuthenticationToken, notebookGuid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getTag(String, String)
   */
  public void getTag(final String guid, final OnClientCallback<Tag, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getTag(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#createTag(String, com.evernote.edam.type.Tag)
   */
  public void createTag(final Tag tag, final OnClientCallback<Tag, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.createTag(mAuthenticationToken, tag));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#updateTag(String, com.evernote.edam.type.Tag)
   */
  public void updateTag(final Tag tag, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.updateTag(mAuthenticationToken, tag));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#untagAll(String, String)
   */
  public void untagAll(final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          AsyncNoteStoreClient.super.untagAll(mAuthenticationToken, guid);
          callback.onResultsReceived(null);
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeTag(String, String)
   */
  public void expungeTag(final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeTag(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#listSearches(String)
   */
  public void listSearches(final OnClientCallback<List<SavedSearch>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.listSearches(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getSearch(String, String)
   */
  public void getSearch(final String guid, final OnClientCallback<SavedSearch, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getSearch(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#createSearch(String, com.evernote.edam.type.SavedSearch)
   */
  public void createSearch(final SavedSearch search, final OnClientCallback<SavedSearch, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.createSearch(mAuthenticationToken, search));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#updateSearch(String, com.evernote.edam.type.SavedSearch)
   */
  public void updateSearch(final SavedSearch search, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.updateSearch(mAuthenticationToken, search));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeSearch(String, String)
   */
  public void expungeSearch(final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeSearch(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#findNotes(String, com.evernote.edam.notestore.NoteFilter, int, int)
   */
  public void findNotes(final NoteFilter filter, final int offset, final int maxNotes, final OnClientCallback<NoteList, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.findNotes(mAuthenticationToken, filter, offset, maxNotes));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#findNoteOffset(String, com.evernote.edam.notestore.NoteFilter, String)
   */
  public void findNoteOffset(final NoteFilter filter, final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.findNoteOffset(mAuthenticationToken, filter, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#findNotesMetadata(String, com.evernote.edam.notestore.NoteFilter, int, int, com.evernote.edam.notestore.NotesMetadataResultSpec)
   */
  public void findNotesMetadata(final NoteFilter filter, final int offset, final int maxNotes, final NotesMetadataResultSpec resultSpec, final OnClientCallback<NotesMetadataList, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.findNotesMetadata(mAuthenticationToken, filter, offset, maxNotes, resultSpec));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#findNoteCounts(String, com.evernote.edam.notestore.NoteFilter, boolean)
   */
  public void findNoteCounts(final NoteFilter filter, final boolean withTrash, final OnClientCallback<NoteCollectionCounts, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.findNoteCounts(mAuthenticationToken, filter, withTrash));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNote(String, String, boolean, boolean, boolean, boolean)
   */
  public void getNote(final String guid, final boolean withContent, final boolean withResourcesData, final boolean withResourcesRecognition, final boolean withResourcesAlternateData, final OnClientCallback<Note, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNote(mAuthenticationToken, guid, withContent, withResourcesData, withResourcesRecognition, withResourcesAlternateData));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNoteApplicationData(String, String)
   */
  public void getNoteApplicationData(final String guid, final OnClientCallback<LazyMap, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNoteApplicationData(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNoteApplicationDataEntry(String, String, String)
   */
  public void getNoteApplicationDataEntry(final String guid, final String key, final OnClientCallback<String, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNoteApplicationDataEntry(mAuthenticationToken, guid, key));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#setNoteApplicationDataEntry(String, String, String, String)
   */
  public void setNoteApplicationDataEntry(final String guid, final String key, final String value, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.setNoteApplicationDataEntry(mAuthenticationToken, guid, key, value));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#unsetNoteApplicationDataEntry(String, String, String)
   */
  public void unsetNoteApplicationDataEntry(final String guid, final String key, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.unsetNoteApplicationDataEntry(mAuthenticationToken, guid, key));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNoteContent(String, String)
   */
  public void getNoteContent(final String guid, final OnClientCallback<String, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNoteContent(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNoteSearchText(String, String, boolean, boolean)
   */
  public void getNoteSearchText(final String guid, final boolean noteOnly, final boolean tokenizeForIndexing, final OnClientCallback<String, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNoteSearchText(mAuthenticationToken, guid, noteOnly, tokenizeForIndexing));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceSearchText(String, String)
   */
  public void getResourceSearchText(final String guid, final OnClientCallback<String, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceSearchText(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNoteTagNames(String, String)
   */
  public void getNoteTagNames(final String guid, final OnClientCallback<List<String>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNoteTagNames(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#createNote(String, com.evernote.edam.type.Note)
   */
  public void createNote(final Note note, final OnClientCallback<Note, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.createNote(mAuthenticationToken, note));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#updateNote(String, com.evernote.edam.type.Note)
   */
  public void updateNote(final Note note, final OnClientCallback<Note, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.updateNote(mAuthenticationToken, note));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#deleteNote(String, String)
   */
  public void deleteNote(final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.deleteNote(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeNote(String, String)
   */
  public void expungeNote(final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeNote(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeNotes(String, java.util.List)
   */
  public void expungeNotes(final List<String> noteGuids, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeNotes(mAuthenticationToken, noteGuids));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeInactiveNotes(String)
   */
  public void expungeInactiveNotes(final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeInactiveNotes(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#copyNote(String, String, String)
   */
  public void copyNote(final String noteGuid, final String toNotebookGuid, final OnClientCallback<Note, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.copyNote(mAuthenticationToken, noteGuid, toNotebookGuid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#listNoteVersions(String, String)
   */
  public void listNoteVersions(final String noteGuid, final OnClientCallback<List<NoteVersionId>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.listNoteVersions(mAuthenticationToken, noteGuid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getNoteVersion(String, String, int, boolean, boolean, boolean)
   */
  public void getNoteVersion(final String noteGuid, final int updateSequenceNum, final boolean withResourcesData, final boolean withResourcesRecognition, final boolean withResourcesAlternateData, final OnClientCallback<Note, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getNoteVersion(mAuthenticationToken, noteGuid, updateSequenceNum, withResourcesData, withResourcesRecognition, withResourcesAlternateData));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResource(String, String, boolean, boolean, boolean, boolean)
   */
  public void getResource(final String guid, final boolean withData, final boolean withRecognition, final boolean withAttributes, final boolean withAlternateData, final OnClientCallback<Resource, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResource(mAuthenticationToken, guid, withData, withRecognition, withAttributes, withAlternateData));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceApplicationData(String, String)
   */
  public void getResourceApplicationData(final String guid, final OnClientCallback<LazyMap, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceApplicationData(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceApplicationDataEntry(String, String, String)
   */
  public void getResourceApplicationDataEntry(final String guid, final String key, final OnClientCallback<String, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceApplicationDataEntry(mAuthenticationToken, guid, key));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#setResourceApplicationDataEntry(String, String, String, String)
   */
  public void setResourceApplicationDataEntry(final String guid, final String key, final String value, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.setResourceApplicationDataEntry(mAuthenticationToken, guid, key, value));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#unsetResourceApplicationDataEntry(String, String, String)
   */
  public void unsetResourceApplicationDataEntry(final String guid, final String key, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.unsetResourceApplicationDataEntry(mAuthenticationToken, guid, key));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#updateResource(String, com.evernote.edam.type.Resource)
   */
  public void updateResource(final Resource resource, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.updateResource(mAuthenticationToken, resource));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceData(String, String)
   */
  public void getResourceData(final String guid, final OnClientCallback<byte[], Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceData(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceByHash(String, String, byte[], boolean, boolean, boolean)
   */
  public void getResourceByHash(final String noteGuid, final byte[] contentHash, final boolean withData, final boolean withRecognition, final boolean withAlternateData, final OnClientCallback<Resource, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceByHash(mAuthenticationToken, noteGuid, contentHash, withData, withRecognition, withAlternateData));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceRecognition(String, String)
   */
  public void getResourceRecognition(final String guid, final OnClientCallback<byte[], Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceRecognition(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceAlternateData(String, String)
   */
  public void getResourceAlternateData(final String guid, final OnClientCallback<byte[], Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceAlternateData(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getResourceAttributes(String, String)
   */
  public void getResourceAttributes(final String guid, final OnClientCallback<ResourceAttributes, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getResourceAttributes(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getPublicNotebook(int, String)
   */
  public void getPublicNotebook(final int userId, final String publicUri, final OnClientCallback<Notebook, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getPublicNotebook(userId, publicUri));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#createSharedNotebook(String, com.evernote.edam.type.SharedNotebook)
   */
  public void createSharedNotebook(final SharedNotebook sharedNotebook, final OnClientCallback<SharedNotebook, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.createSharedNotebook(mAuthenticationToken, sharedNotebook));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#updateSharedNotebook(String, com.evernote.edam.type.SharedNotebook)
   */
  public void updateSharedNotebook(final SharedNotebook sharedNotebook, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.updateSharedNotebook(mAuthenticationToken, sharedNotebook));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#sendMessageToSharedNotebookMembers(String, String, String, java.util.List)
   */
  public void sendMessageToSharedNotebookMembers(final String notebookGuid, final String messageText, final List<String> recipients, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.sendMessageToSharedNotebookMembers(mAuthenticationToken, notebookGuid, messageText, recipients));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#listSharedNotebooks(String)
   */
  public void listSharedNotebooks(final OnClientCallback<List<SharedNotebook>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.listSharedNotebooks(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeSharedNotebooks(String, java.util.List)
   */
  public void expungeSharedNotebooks(final List<Long> sharedNotebookIds, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeSharedNotebooks(mAuthenticationToken, sharedNotebookIds));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#createLinkedNotebook(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void createLinkedNotebook(final LinkedNotebook linkedNotebook, final OnClientCallback<LinkedNotebook, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.createLinkedNotebook(mAuthenticationToken, linkedNotebook));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#updateLinkedNotebook(String, com.evernote.edam.type.LinkedNotebook)
   */
  public void updateLinkedNotebook(final LinkedNotebook linkedNotebook, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.updateLinkedNotebook(mAuthenticationToken, linkedNotebook));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#listLinkedNotebooks(String)
   */
  public void listLinkedNotebooks(final OnClientCallback<List<LinkedNotebook>, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.listLinkedNotebooks(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#expungeLinkedNotebook(String, String)
   */
  public void expungeLinkedNotebook(final String guid, final OnClientCallback<Integer, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.expungeLinkedNotebook(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#authenticateToSharedNotebook(String, String)
   */
  public void authenticateToSharedNotebook(final String shareKey, final OnClientCallback<AuthenticationResult, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.authenticateToSharedNotebook(shareKey, mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#getSharedNotebookByAuth(String)
   */
  public void getSharedNotebookByAuth(final OnClientCallback<SharedNotebook, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.getSharedNotebookByAuth(mAuthenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#emailNote(String, com.evernote.edam.notestore.NoteEmailParameters)
   */
  public void emailNote(final NoteEmailParameters parameters, final OnClientCallback<Void, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          AsyncNoteStoreClient.super.emailNote(mAuthenticationToken, parameters);
          callback.onResultsReceived(null);
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#shareNote(String, String)
   */
  public void shareNote(final String guid, final OnClientCallback<String, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.shareNote(mAuthenticationToken, guid));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#stopSharingNote(String, String)
   */
  public void stopSharingNote(final String guid, final OnClientCallback<Void, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          AsyncNoteStoreClient.super.stopSharingNote(mAuthenticationToken, guid);
          callback.onResultsReceived(null);
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#authenticateToSharedNote(String, String)
   */
  public void authenticateToSharedNote(final String guid, final String noteKey, final OnClientCallback<AuthenticationResult, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.authenticateToSharedNote(guid, noteKey));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   * @param {@link OnClientCallback} providing an interface to the calling code
   *
   * @see NoteStore.Client#findRelated(String, com.evernote.edam.notestore.RelatedQuery, com.evernote.edam.notestore.RelatedResultSpec)
   */
  public void findRelated(final RelatedQuery query, final RelatedResultSpec resultSpec, final OnClientCallback<RelatedResult, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncNoteStoreClient.super.findRelated(mAuthenticationToken, query, resultSpec));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }
}
