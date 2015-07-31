package com.evernote.android.demo.fragment.notebook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.demo.R;
import com.evernote.android.demo.fragment.AbstractContainerFragment;
import com.evernote.android.demo.fragment.EmptyFragment;
import com.evernote.android.demo.task.FindLinkedNotebooksTask;
import com.evernote.edam.type.LinkedNotebook;

import net.vrallev.android.task.TaskResult;

import java.util.List;

/**
 * @author rwondratschek
 */
public class LinkedNotebookContainerFragment extends AbstractContainerFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    protected void loadData() {
        new FindLinkedNotebooksTask().start(this, "linked");
    }

    @Override
    public void onFabClick() {
        // no op
    }

    @TaskResult(id = "linked")
    public void onFindLinkedNotebooks(List<LinkedNotebook> linkedNotebooks) {
        mSwipeRefreshLayout.setRefreshing(false);

        if (linkedNotebooks == null || linkedNotebooks.isEmpty()) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, EmptyFragment.create("linked notebooks"))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, LinkedNotebookListFragment.create(linkedNotebooks))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }
}
