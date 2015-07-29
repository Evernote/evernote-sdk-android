package com.evernote.android.demo.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.evernote.android.demo.R;
import com.evernote.android.demo.fragment.note.NoteContainerFragment;

/**
 * @author rwondratschek
 */
public class SearchQueryDialogFragment extends DialogFragment {

    public static final String TAG = "SearchQueryDialogFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_note, null);
        final TextInputLayout titleView = (TextInputLayout) view.findViewById(R.id.textInputLayout_title);
        titleView.setHint(getString(R.string.query));

        view.findViewById(R.id.textInputLayout_content).setVisibility(View.GONE);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (getParentFragment() instanceof NoteContainerFragment) {
                            ((NoteContainerFragment) getParentFragment()).search(titleView.getEditText().getText().toString());
                        } else {
                            throw new IllegalStateException();
                        }
                        break;
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.search)
                .setView(view)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .setNegativeButton(android.R.string.cancel, onClickListener)
                .create();
    }
}
