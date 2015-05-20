package com.evernote.android.demo.fragment.notebook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evernote.android.demo.R;
import com.evernote.android.demo.activity.NotesActivity;
import com.evernote.android.demo.util.ParcelableUtil;
import com.evernote.edam.type.Notebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rwondratschek
 */
@SuppressWarnings("FieldCanBeLocal")
public class NotebookListFragment extends Fragment {

    private static final String KEY_NOTEBOOK_LIST = "KEY_NOTEBOOK_LIST";

    public static NotebookListFragment create(List<Notebook> notebooks) {
        Bundle args = new Bundle();
        ParcelableUtil.putSerializableList(args, new ArrayList<Serializable>(notebooks), KEY_NOTEBOOK_LIST);

        NotebookListFragment fragment = new NotebookListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<Notebook> mNotebooks;

    private AbsListView mListView;
    private MyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotebooks = ParcelableUtil.getSerializableArrayList(getArguments(), KEY_NOTEBOOK_LIST);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        mListView = (AbsListView) view.findViewById(R.id.listView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(NotesActivity.createIntent(getActivity(), mNotebooks.get(position)));
            }
        });

        mAdapter = new MyAdapter();

        mListView.setAdapter(mAdapter);

//        registerForContextMenu(mListView);

        return view;
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        switch (v.getId()) {
//            case R.id.listView:
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
//                menu.setHeaderTitle(mNoteMetadataList.get(info.position).getTitle());
//
//                String[] menuItems = getResources().getStringArray(R.array.notes_context_menu);
//                for (int i = 0; i < menuItems.length; i++) {
//                    menu.add(Menu.NONE, i, i, menuItems[i]);
//                }
//                break;
//
//            default:
//                super.onCreateContextMenu(menu, v, menuInfo);
//                break;
//        }
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
//
//        switch (item.getItemId()) {
//            case 0:
//                new ShareNoteTask(mNoteMetadataList.get(info.position).getGuid()).start(this);
//                return true;
//
//            default:
//                return false;
//        }
//    }

//    @TaskResult
//    public void onNoteShared(String url) {
//        if (!TextUtils.isEmpty(url)) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//        } else {
//            Toast.makeText(getActivity(), "URL is null", Toast.LENGTH_SHORT).show();
//        }
//    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNotebooks.size();
        }

        @Override
        public Notebook getItem(int position) {
            return mNotebooks.get(position);
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

            Notebook notebook = getItem(position);
            viewHolder.mTextView1.setText(notebook.getName());

            return convertView;
        }
    }

    private static class ViewHolder {

        private final TextView mTextView1;

        public ViewHolder(View view) {
            mTextView1 = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}
