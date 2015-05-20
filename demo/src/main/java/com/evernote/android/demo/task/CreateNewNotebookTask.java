package com.evernote.android.demo.task;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Notebook;

/**
 * @author rwondratschek
 */
public class CreateNewNotebookTask extends BaseTask<Notebook> {

    private final String mName;

    public CreateNewNotebookTask(String name) {
        super(Notebook.class);
        mName = name;
    }

    @Override
    protected Notebook checkedExecute() throws Exception {
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        Notebook notebook = new Notebook();
        notebook.setName(mName);

        return noteStoreClient.createNotebook(notebook);
    }
}
