package com.evernote.android.demo.task;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.type.NoteRef;
import com.squareup.okhttp.Response;

/**
 * @author rwondratschek
 */
public class GetNoteHtmlTask extends BaseTask<String> {

    private final NoteRef mNoteRef;

    public GetNoteHtmlTask(NoteRef noteRef) {
        super(String.class);
        mNoteRef = noteRef;
    }

    @Override
    protected String checkedExecute() throws Exception {
        EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

        EvernoteHtmlHelper htmlHelper;
        if (mNoteRef.isLinked()) {
            htmlHelper = clientFactory.getLinkedHtmlHelper(mNoteRef.loadLinkedNotebook());
        } else {
            htmlHelper = clientFactory.getHtmlHelperDefault();
        }

        Response response = htmlHelper.downloadNote(mNoteRef.getGuid());
        return htmlHelper.parseBody(response);
    }

    public NoteRef getNoteRef() {
        return mNoteRef;
    }
}
