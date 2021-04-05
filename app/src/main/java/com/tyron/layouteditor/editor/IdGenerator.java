package com.tyron.layouteditor.editor;

import android.os.Parcelable;

public interface IdGenerator extends Parcelable {
    int getUnique(String id);
	
	String getString(Integer id);
	
	boolean keyExists(String key);
	
	int replace(String old, String newId);
}