package com.evernote.android.demo.task;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.LinkedNotebook;

import java.util.List;

/**
 * @author rwondratschek
 */
public class FindLinkedNotebooksTask extends BaseTask<List<LinkedNotebook>> {

    @SuppressWarnings("unchecked")
    public FindLinkedNotebooksTask() {
        super((Class) List.class);
    }

    @Override
    protected List<LinkedNotebook> checkedExecute() throws Exception {
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        return noteStoreClient.listLinkedNotebooks();
    }
}
