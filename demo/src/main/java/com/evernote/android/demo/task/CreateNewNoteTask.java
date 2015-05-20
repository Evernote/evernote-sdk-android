package com.evernote.android.demo.task;

import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteLinkedNotebookHelper;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.thrift.TException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author rwondratschek
 */
public class CreateNewNoteTask extends BaseTask<Note> {

    private final String mTitle;
    private final String mContent;
    private final ImageData mImageData;

    private final Notebook mNotebook;
    private final LinkedNotebook mLinkedNotebook;

    public CreateNewNoteTask(String title, String content, ImageData imageData, Notebook notebook, LinkedNotebook linkedNotebook) {
        super(Note.class);

        mTitle = title;
        mContent = content;
        mImageData = imageData;
        mNotebook = notebook;
        mLinkedNotebook = linkedNotebook;
    }

    @Override
    protected Note checkedExecute() throws Exception {
        Note note = new Note();
        note.setTitle(mTitle);

        if (mNotebook != null) {
            note.setNotebookGuid(mNotebook.getGuid());
        }

        if (mImageData == null) {
            note.setContent(EvernoteUtil.NOTE_PREFIX + mContent + EvernoteUtil.NOTE_SUFFIX);
            return createNote(note);
        }

        InputStream in = null;
        try {
            // Hash the data in the image file. The hash is used to reference the file in the ENML note content.
            in = new BufferedInputStream(new FileInputStream(mImageData.getPath()));
            FileData data = new FileData(EvernoteUtil.hash(in), new File(mImageData.getPath()));

            ResourceAttributes attributes = new ResourceAttributes();
            attributes.setFileName(mImageData.getFileName());

            // Create a new Resource
            Resource resource = new Resource();
            resource.setData(data);
            resource.setMime(mImageData.getMimeType());
            resource.setAttributes(attributes);

            note.addToResources(resource);

            // Set the note's ENML content
            String content = EvernoteUtil.NOTE_PREFIX
                    + mContent
                    + EvernoteUtil.createEnMediaTag(resource)
                    + EvernoteUtil.NOTE_SUFFIX;

            note.setContent(content);

            return createNote(note);

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    protected Note createNote(Note note) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        if (mNotebook == null && mLinkedNotebook != null) {
            EvernoteLinkedNotebookHelper linkedNotebookHelper = EvernoteSession.getInstance().getEvernoteClientFactory().getLinkedNotebookHelper(mLinkedNotebook);
            return linkedNotebookHelper.createNoteInLinkedNotebook(note);

        } else {
            EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
            return noteStoreClient.createNote(note);
        }
    }

    public static class ImageData implements Parcelable {

        private final String mPath;
        private final String mFileName;
        private final String mMimeType;

        public ImageData(String path, String fileName, String mimeType) {
            mPath = path;
            mFileName = fileName;
            mMimeType = mimeType;
        }

        public String getPath() {
            return mPath;
        }

        public String getFileName() {
            return mFileName;
        }

        public String getMimeType() {
            return mMimeType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mPath);
            dest.writeString(mFileName);
            dest.writeString(mMimeType);
        }

        public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
            @Override
            public ImageData createFromParcel(final Parcel source) {
                return new ImageData(source.readString(), source.readString(), source.readString());
            }

            @Override
            public ImageData[] newArray(final int size) {
                return new ImageData[size];
            }
        };
    }
}
