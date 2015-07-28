package com.evernote.android.demo.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * @author rwondratschek
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        AbsListView listView = findListView(this);
        if (listView != null) {
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            return super.canChildScrollUp();
        }
    }

    private AbsListView findListView(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            if (child instanceof AbsListView) {
                return (AbsListView) child;
            }
            if (child instanceof ViewGroup) {
                AbsListView listView = findListView((ViewGroup) child);
                if (listView != null) {
                    return listView;
                }
            }
        }

        return null;
    }
}
