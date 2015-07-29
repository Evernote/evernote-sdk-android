package com.evernote.android.demo.util;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.demo.R;

/**
 * @author rwondratschek
 */
public final class ViewUtil {

    private ViewUtil() {

    }

    public static void showSnackbar(View view, @StringRes int stringRes) {
        showSnackbar(view, view.getContext().getString(stringRes));
    }

    public static void showSnackbar(View view, CharSequence text) {
        showSnackbar(view, text, Snackbar.LENGTH_LONG);
    }

    public static void showSnackbar(View view, CharSequence text, int length) {
        CoordinatorLayout coordinatorLayout = findFabCoordinator(view, R.id.coordinatorLayout);
        if (coordinatorLayout != null) {
            view = coordinatorLayout;
        }

        Snackbar.make(view, text, length).show();
    }

    public static CoordinatorLayout findFabCoordinator(View view, @IdRes int coordinatorId) {
        View coordinatorView = view.findViewById(coordinatorId);
        if (coordinatorView != null) {
            return (CoordinatorLayout) coordinatorView;
        }

        return findFabCoordinatorInParent(view, coordinatorId);
    }

    private static CoordinatorLayout findFabCoordinatorInParent(View view, @IdRes int coordinatorId) {
        if (view.getId() == android.R.id.content) {
            return null;
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.equals(view)) {
                continue;
            }

            View coordinatorView = child.findViewById(coordinatorId);
            if (coordinatorView != null) {
                return (CoordinatorLayout) coordinatorView;
            }
        }

        return findFabCoordinator(parent, coordinatorId);
    }
}
