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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.List;

/**
 * This sample shows how to share an individual Evernote note.
 * <p/>
 * class created by @akagin_
 */
public class ShareNotes extends ParentActivity{

    /**
     * *************************************************************************
     * The following values and code are simply part of the demo application.  *
     * *************************************************************************
     */

    private static final String LOGTAG = "ShareNotes";

    //Data
    private List<SharingListData> notesObjects;
    private SharingListAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_notes);
        ListView mNotesListView;

        // Show user's top 10 latest notes
        notesObjects = new ArrayList<SharingListData>();

        mAdapter = new SharingListAdapter(this, 0, notesObjects);
        mNotesListView = (ListView)findViewById(R.id.list);
        mNotesListView.setAdapter(mAdapter);
        listNotesByUpdated();


        // Set a listener called when each item clicked
        mNotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String mGuid, mUrlPrefix;
            Boolean mSharedFlag;
            int mPosition;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                mGuid = notesObjects.get(mPosition).getNoteGuidData();
                mSharedFlag = notesObjects.get(mPosition).getSharedFlag();
                mUrlPrefix = mEvernoteSession.getAuthenticationResult().getWebApiUrlPrefix();

                showDialog(DIALOG_PROGRESS);
                if(!mSharedFlag) {
                    try{
                        // If a selected note isn't shared, share it, getting a shared note URL,
                        // and send it to clipboard.
                        mEvernoteSession.getClientFactory().createNoteStoreClient().shareNote(mGuid, new OnClientCallback<String>() {
                            @Override
                            public void onSuccess(String sharekey) {
                                notesObjects.get(mPosition).setSharedFlag(true);
                                String note_url = mUrlPrefix + String.format("sh/%s/%s", mGuid, sharekey);
                                copyToClipboard(note_url);

                                Toast.makeText(getApplicationContext(), getString(R.string.note_shared) + " " + note_url, Toast.LENGTH_LONG).show();
                                Log.d(LOGTAG, "Note shared with following URL: " + note_url);

                                removeDialog(DIALOG_PROGRESS);

                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onException(Exception exception) {
                                onError(exception, "Error sharing notes. ", R.string.error_sharing_note);
                            }
                        });
                    } catch (TTransportException exception){
                        onError(exception, "Error creating notestore. ", R.string.error_creating_notestore);
                    }
                }
                else {
                    try{
                        // If a selected note is shared, unshare it.
                        mEvernoteSession.getClientFactory().createNoteStoreClient().stopSharingNote(mGuid, new OnClientCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                notesObjects.get(mPosition).setSharedFlag(false);

                                Toast.makeText(getApplicationContext(), getString(R.string.note_unshared), Toast.LENGTH_LONG).show();
                                removeDialog(DIALOG_PROGRESS);

                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onException(Exception exception) {
                                onError(exception, "Error sharing notes. ", R.string.error_sharing_note);
                            }
                        });
                    } catch (TTransportException exception){
                        onError(exception, "Error creating notestore. ", R.string.error_creating_notestore);
                    }
                }
            }
        });

    }

    /**
     * Called the activity is called.
     * </p>
     * Search from all user's notebooks up to 10 notes,
     * display their titles on ListView in order of most recently updated,
     * and return result.
     */
    public void listNotesByUpdated() {
        int offset = 0;
        int pageSize = 10;

        NoteFilter filter = new NoteFilter();
        filter.setOrder(NoteSortOrder.UPDATED.getValue());
        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(true);
        spec.setIncludeAttributes(true);

        mAdapter.clear();

        showDialog(DIALOG_PROGRESS);
        try{
            mEvernoteSession.getClientFactory().createNoteStoreClient()
                    .findNotesMetadata(filter, offset, pageSize, spec, new OnClientCallback<NotesMetadataList>() {
                        @Override
                        public void onSuccess(NotesMetadataList data) {
                            removeDialog(DIALOG_PROGRESS);

                            if(data.getTotalNotes() == 0){
                                Toast.makeText(getApplicationContext(), R.string.msg_note_required, Toast.LENGTH_LONG).show();
                            }
                            else {
                                for(NoteMetadata note : data.getNotes()) {
                                    SharingListData item = new SharingListData();

                                    item.setTitleData(note.getTitle());
                                    item.setNoteGuidData(note.getGuid());

                                    // If both NoteAttributes and SetShareDate within it exists, setSharedFlag is true, otherwise false.
                                    item.setSharedFlag((note.isSetAttributes() && note.getAttributes().isSetShareDate()));

                                    notesObjects.add(item);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onException(Exception exception) {
                            onError(exception, "Error listing notes. ", R.string.error_listing_notes);
                        }
                    });
        } catch (TTransportException exception){
            onError(exception, "Error creating notestore. ", R.string.error_creating_notestore);
        }
    }

    /**
     * Show log and toast and remove a dialog on Exceptions
     *
     */
    public void onError(Exception exception, String logstr, int id){
        Log.e(LOGTAG, logstr + exception);
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
        removeDialog(DIALOG_PROGRESS);
    }

    /**
     * Send given string to Clipboard with compatibility.
     */
    public void copyToClipboard(String text) {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null)
            {
                clipboard.setText(text);
            }
        }
        else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                android.content.ClipData clip = android.content.ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(clip);
            }
        }
    }
}