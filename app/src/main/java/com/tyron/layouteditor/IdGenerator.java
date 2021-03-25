package com.tyron.layouteditor;

import android.os.Parcelable;

public interface IdGenerator extends Parcelable {
    int getUnique(String id);
}