package com.evernote.android.demo.util;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rwondratschek
 */
public final class ParcelableUtil {

    private ParcelableUtil() {
        // no op
    }

    public static void putSerializableList(Bundle bundle, ArrayList<? extends Serializable> list, String key) {
        if (list == null) {
            return;
        }
        bundle.putInt(key + "size", list.size());
        for (int i = 0; i < list.size(); i++) {
            bundle.putSerializable(key + i, list.get(i));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> getSerializableArrayList(Bundle bundle, String key) {
        int size = bundle.getInt(key + "size", -1);
        if (size < 0) {
            return null;
        }

        ArrayList<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add((T) bundle.getSerializable(key + i));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static void putParcelableList(Bundle bundle, List<? extends Parcelable> list, String key) {
        if (list == null) {
            return;
        }
        ArrayList<? extends Parcelable> arrayList;
        if (list instanceof ArrayList) {
            arrayList = (ArrayList<? extends Parcelable>) list;
        } else {
            arrayList = new ArrayList<>(list);
        }

        bundle.putParcelableArrayList(key, arrayList);
    }
}
