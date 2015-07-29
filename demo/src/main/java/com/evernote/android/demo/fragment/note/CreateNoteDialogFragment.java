package com.evernote.android.demo.fragment.note;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.evernote.android.demo.R;
import com.evernote.android.demo.task.CreateNewNoteTask;
import com.evernote.client.android.helper.Cat;

import net.vrallev.android.task.Task;
import net.vrallev.android.task.TaskExecutor;
import net.vrallev.android.task.TaskResult;

/**
 * @author rwondratschek
 */
public class CreateNoteDialogFragment extends DialogFragment {

    public static final int REQ_SELECT_IMAGE = 100;

    public static final String TAG = "CreateNoteDialogFragment";

    private static final Cat CAT = new Cat(TAG);

    private static final String KEY_IMAGE_DATA = "KEY_IMAGE_DATA";

    private CreateNewNoteTask.ImageData mImageData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mImageData = savedInstanceState.getParcelable(KEY_IMAGE_DATA);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_note, null);
        final TextInputLayout titleView = (TextInputLayout) view.findViewById(R.id.textInputLayout_title);
        final TextInputLayout contentView = (TextInputLayout) view.findViewById(R.id.textInputLayout_content);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (getParentFragment() instanceof NoteContainerFragment) {
                            ((NoteContainerFragment) getParentFragment()).createNewNote(titleView.getEditText().getText().toString(),
                                    contentView.getEditText().getText().toString(), mImageData);
                        } else {
                            throw new IllegalStateException();
                        }
                        break;
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_new_note)
                .setView(view)
                .setPositiveButton(R.string.create, onClickListener)
                .setNegativeButton(android.R.string.cancel, onClickListener)
                .setNeutralButton(R.string.attach_image, onClickListener)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            Button button = alertDialog.getButton(Dialog.BUTTON_NEUTRAL);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    getActivity().startActivityForResult(intent, REQ_SELECT_IMAGE);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageData != null) {
            outState.putParcelable(KEY_IMAGE_DATA, mImageData);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    TaskExecutor.getInstance().execute(new QueryImageTask(data, getActivity()), this);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @TaskResult
    public void onImageData(CreateNewNoteTask.ImageData imageData) {
        mImageData = imageData;
    }

    private static final class QueryImageTask extends Task<CreateNewNoteTask.ImageData> {

        private static final String[] QUERY_COLUMNS = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        private final Intent mIntent;
        private final Context mContext;

        private QueryImageTask(Intent intent, Context context) {
            mIntent = intent;
            mContext = context;
        }

        @Override
        protected CreateNewNoteTask.ImageData execute() {
            Uri selectedImage = mIntent.getData();

            Cursor cursor = null;

            try {
                cursor = mContext.getContentResolver().query(selectedImage, QUERY_COLUMNS, null, null, null);
                if (cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndex(QUERY_COLUMNS[1]));
                    String fileName = cursor.getString(cursor.getColumnIndex(QUERY_COLUMNS[3]));
                    String mimeType = cursor.getString(cursor.getColumnIndex(QUERY_COLUMNS[2]));
                    return new CreateNewNoteTask.ImageData(path, fileName, mimeType);
                }

            } catch (Exception e) {
                CAT.e(e);

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return null;
        }
    }
}
