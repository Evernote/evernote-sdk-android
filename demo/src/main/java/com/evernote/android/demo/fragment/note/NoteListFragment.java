package com.evernote.android.demo.fragment.note;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evernote.android.demo.R;
import com.evernote.android.demo.activity.ViewHtmlActivity;
import com.evernote.android.demo.fragment.AbstractContainerFragment;
import com.evernote.android.demo.task.BaseTask;
import com.evernote.android.demo.task.DeleteNoteTask;
import com.evernote.android.demo.task.GetNoteContentTask;
import com.evernote.android.demo.task.GetNoteHtmlTask;
import com.evernote.android.demo.util.ParcelableUtil;
import com.evernote.android.demo.util.ViewUtil;
import com.evernote.android.intent.EvernoteIntent;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.Note;

import net.vrallev.android.task.TaskResult;

import java.util.List;

/**
 * @author rwondratschek
 */
@SuppressWarnings("FieldCanBeLocal")
public class NoteListFragment extends Fragment {

    private static final String KEY_NOTE_LIST = "KEY_NOTE_LIST";

    public static NoteListFragment create(List<NoteRef> noteRefList) {
        Bundle args = new Bundle();
        ParcelableUtil.putParcelableList(args, noteRefList, KEY_NOTE_LIST);

        NoteListFragment fragment = new NoteListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<NoteRef> mNoteRefList;

    private AbsListView mListView;
    private MyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNoteRefList = getArguments().getParcelableArrayList(KEY_NOTE_LIST);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        mListView = (AbsListView) view.findViewById(R.id.listView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new GetNoteHtmlTask(mNoteRefList.get(position)).start(NoteListFragment.this, "html");
            }
        });

        mAdapter = new MyAdapter();

        mListView.setAdapter(mAdapter);

        registerForContextMenu(mListView);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.listView:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

                NoteRef noteRef = mNoteRefList.get(info.position);
                boolean linked = noteRef.isLinked();
                menu.setHeaderTitle(noteRef.getTitle());

                String[] menuItems = getResources().getStringArray(R.array.notes_context_menu);
                for (int i = 0; i < menuItems.length; i++) {
                    if (linked && (i == 0 || i == 3)) {
                        // share public link and delete
                        continue;
                    }

                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
                break;

            default:
                super.onCreateContextMenu(menu, v, menuInfo);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        NoteRef noteRef = mNoteRefList.get(info.position);

        switch (item.getItemId()) {
            case 0:
                new ShareNoteTask(noteRef).start(this);
                return true;

            case 1:
                Intent intent = EvernoteIntent.viewNote()
                        .setNoteGuid(noteRef.getGuid())
                        .create();

                if (EvernoteIntent.isEvernoteInstalled(getActivity())) {
                    startActivity(intent);
                } else {
                    ViewUtil.showSnackbar(mListView, R.string.evernote_not_installed);
                }
                return true;

            case 2:
                new GetNoteContentTask(noteRef).start(this, "content");
                return true;

            case 3:
                new DeleteNoteTask(noteRef).start(this);
                return true;

            default:
                return false;
        }
    }

    @TaskResult
    public void onNoteShared(String url) {
        if (!TextUtils.isEmpty(url)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            ViewUtil.showSnackbar(mListView, "URL is null");
        }
    }

    @TaskResult
    public void onNoteDeleted(DeleteNoteTask.Result result) {
        if (result != null) {
            ((AbstractContainerFragment) getParentFragment()).refresh();
        } else {
            ViewUtil.showSnackbar(mListView, "Delete note failed");
        }
    }

    @TaskResult(id = "content")
    public void onGetNoteContent(Note note) {
        if (note != null) {
            NoteContentDialogFragment.create(note).show(getChildFragmentManager(), NoteContentDialogFragment.TAG);
        } else {
            ViewUtil.showSnackbar(mListView, "Get content failed");
        }
    }

    @TaskResult(id = "html")
    public void onGetNoteContentHtml(String html, GetNoteHtmlTask task) {
        startActivity(ViewHtmlActivity.createIntent(getActivity(), task.getNoteRef(), html));
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNoteRefList.size();
        }

        @Override
        public NoteRef getItem(int position) {
            return mNoteRefList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            NoteRef noteRef = getItem(position);
            viewHolder.mTextView1.setText(noteRef.getTitle());

            return convertView;
        }
    }

    private static class ViewHolder {

        private final TextView mTextView1;

        public ViewHolder(View view) {
            mTextView1 = (TextView) view.findViewById(android.R.id.text1);
        }
    }

    private static final class ShareNoteTask extends BaseTask<String> {

        private final NoteRef mNoteRef;

        private ShareNoteTask(NoteRef noteRef) {
            super(String.class);
            mNoteRef = noteRef;
        }

        @Override
        protected String checkedExecute() throws Exception {
            EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

            String shardId = clientFactory.getUserStoreClient().getUser().getShardId();
            String shareKey = clientFactory.getNoteStoreClient().shareNote(mNoteRef.getGuid());

            return "https://" + EvernoteSession.getInstance().getAuthenticationResult().getEvernoteHost()
                    + "/shard/" + shardId + "/sh/" + mNoteRef.getGuid() + "/" + shareKey;
        }
    }
}
