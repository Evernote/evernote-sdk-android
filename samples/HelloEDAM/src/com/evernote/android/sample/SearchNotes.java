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

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.thrift.transport.TTransportException;
import java.util.ArrayList;
/**
 * This sample shows how to search Evernote notebooks.
 * <p/>
 * class created by @
 */
public class SearchNotes extends ParentActivity {

    /**
     * *************************************************************************
     * The following values and code are simply part of the demo application.  *
     * *************************************************************************
     */

    private static final String LOGTAG = "SearchNotes";

    //Views
    private ListView mResultsListView;
    private EditText mSearchEditText;
    private LinearLayout mSearchResultsParentLayout;
    private SearchView mSearchView;

    private ArrayList<String> notesNames;
    private ArrayAdapter<String> mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_notes);

        // If User uses Android OS version 2.3 or earlier, implement a search form by editText
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){

            mSearchResultsParentLayout = (LinearLayout)findViewById(R.id.search_results_parent);
            LayoutInflater inf = getLayoutInflater();

            mSearchEditText = (EditText)inf.inflate(R.layout.search_form, null);
            mSearchResultsParentLayout.addView(mSearchEditText, 0);

            mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        findNotesByQuery(mSearchEditText.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

        }

        notesNames = new ArrayList();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notesNames);
        mResultsListView = (ListView)findViewById(R.id.list);
        mResultsListView.setAdapter(mAdapter);

    }

    // If User uses Android OS version 3.0 or later, implement a search form by SearchView on ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.options_menu, menu);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    findNotesByQuery(query);
                    return true;
                }
            };

            mSearchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
    }

    /**
     * Called when the user taps the "Search" key in the IME.
     * </p>
     * Search from all user's notebooks up to 10 notes,
     * display their titles on ListView in order of most recently updated.
     *
     */
    public void findNotesByQuery(String query) {
        int offset = 0;
        int pageSize = 10;

        NoteFilter filter = new NoteFilter();
        filter.setOrder(NoteSortOrder.UPDATED.getValue());
        filter.setWords(query);
        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(true);

        mAdapter.clear();

        showDialog(DIALOG_PROGRESS);
        try{
            mEvernoteSession.getClientFactory().createNoteStoreClient()
                    .findNotesMetadata(filter, offset, pageSize, spec, new OnClientCallback<NotesMetadataList>() {
                        @Override
                        public void onSuccess(NotesMetadataList data) {
                            Toast.makeText(getApplicationContext(), R.string.notes_searched, Toast.LENGTH_LONG).show();
                            removeDialog(DIALOG_PROGRESS);

                            for(NoteMetadata note : data.getNotes()) {
                                String title = note.getTitle();
                                notesNames.add(title);
                            }
                            mAdapter.notifyDataSetChanged();
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
}