package com.evernote.android.demo.fragment.note;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.evernote.edam.type.Note;

/**
 * @author rwondratschek
 */
public class NoteContentDialogFragment extends DialogFragment {

    public static final String TAG = "NoteContentDialogFragment";

    private static final String KEY_NOTE = "KEY_NOTE";

    public static NoteContentDialogFragment create(Note note) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_NOTE, note);

        NoteContentDialogFragment fragment = new NoteContentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Note mNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNote = (Note) getArguments().getSerializable(KEY_NOTE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setMessage(mNote.getContent())
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
