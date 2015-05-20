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
import com.evernote.edam.type.LinkedNotebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rwondratschek
 */
@SuppressWarnings("FieldCanBeLocal")
public class LinkedNotebookListFragment extends Fragment {

    private static final String KEY_LINKED_NOTEBOOK_LIST = "KEY_LINKED_NOTEBOOK_LIST";

    public static LinkedNotebookListFragment create(List<LinkedNotebook> notebooks) {
        Bundle args = new Bundle();
        ParcelableUtil.putSerializableList(args, new ArrayList<Serializable>(notebooks), KEY_LINKED_NOTEBOOK_LIST);

        LinkedNotebookListFragment fragment = new LinkedNotebookListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<LinkedNotebook> mNotebooks;

    private AbsListView mListView;
    private MyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotebooks = ParcelableUtil.getSerializableArrayList(getArguments(), KEY_LINKED_NOTEBOOK_LIST);
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

        return view;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNotebooks.size();
        }

        @Override
        public LinkedNotebook getItem(int position) {
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

            LinkedNotebook notebook = getItem(position);
            viewHolder.mTextView1.setText(notebook.getShareName());

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
