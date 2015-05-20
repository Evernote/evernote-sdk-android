package com.evernote.android.demo.task;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Notebook;

import java.util.List;

/**
 * @author rwondratschek
 */
public class FindNotebooksTask extends BaseTask<List<Notebook>> {

    @SuppressWarnings("unchecked")
    public FindNotebooksTask() {
        super((Class) List.class);
    }

    @Override
    protected List<Notebook> checkedExecute() throws Exception {
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        return noteStoreClient.listNotebooks();
    }
}
