package com.evernote.android.demo.fragment.notebook;

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
import com.evernote.android.demo.task.CreateNewNotebookTask;

/**
 * @author rwondratschek
 */
public class CreateNotebookDialogFragment extends DialogFragment {

    public static final String TAG = "CreateNotebookDialogFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_note, null);
        final TextInputLayout titleView = (TextInputLayout) view.findViewById(R.id.textInputLayout_title);
        titleView.setHint(getString(R.string.notebook_title));

        view.findViewById(R.id.textInputLayout_content).setVisibility(View.GONE);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        new CreateNewNotebookTask(titleView.getEditText().getText().toString())
                                .start(getParentFragment());
                        break;
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_new_notebook)
                .setView(view)
                .setPositiveButton(R.string.create, onClickListener)
                .setNegativeButton(android.R.string.cancel, onClickListener)
                .create();
    }
}
