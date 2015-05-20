package com.evernote.android.demo.task;

import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.User;

/**
 * @author rwondratschek
 */
public class GetUserTask extends BaseTask<User> {

    public GetUserTask() {
        super(User.class);
    }

    @Override
    protected User checkedExecute() throws Exception {
        return EvernoteSession.getInstance().getEvernoteClientFactory().getUserStoreClient().getUser();
    }
}
