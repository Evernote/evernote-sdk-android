package com.evernote.android.demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.android.demo.R;

/**
 * @author rwondratschek
 */
public class EmptyFragment extends Fragment {

    private static final String KEY_TEXT = "KEY_TEXT";

    public static EmptyFragment create(String text) {
        Bundle args = new Bundle();
        args.putString(KEY_TEXT, text);
        EmptyFragment fragment = new EmptyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(getString(R.string.empty, getArguments().getString(KEY_TEXT)));
        return view;
    }
}
