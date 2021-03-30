package com.tyron.layouteditor.editor;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleIdGenerator implements IdGenerator {
    public final static Parcelable.Creator<SimpleIdGenerator> CREATOR = new Creator<SimpleIdGenerator>() {
        @Override
        public SimpleIdGenerator createFromParcel(Parcel source) {
            return new SimpleIdGenerator(source);
        }

        @Override
        public SimpleIdGenerator[] newArray(int size) {
            return new SimpleIdGenerator[size];
        }
    };
    private static SimpleIdGenerator instance;
    private final HashMap<String, Integer> idMap = new HashMap<>();
    private final HashMap<Integer, String> keyMap = new HashMap<>();
    private final AtomicInteger sNextGeneratedId;

    public SimpleIdGenerator() {
        sNextGeneratedId = new AtomicInteger(1);
    }

    public SimpleIdGenerator(Parcel source) {
        sNextGeneratedId = new AtomicInteger(source.readInt());
        source.readMap(idMap, null);
    }

    public static SimpleIdGenerator getInstance() {
        if (instance == null) {
            instance = new SimpleIdGenerator();
        }
        return instance;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sNextGeneratedId.get());
        dest.writeMap(idMap);
    }

    /**
     * Generates and returns a unique id, for the given key.
     * If key exists, returns old value.
     * Ensure that all
     *
     * @param idKey
     * @return a unique ID integer for use with {@link android.view.View#setId(int)}.
     */
    @Override
    public synchronized int getUnique(String idKey) {
        Integer existingId = idMap.get(idKey);
        if (existingId == null) {
            int newId = generateViewId();
            idMap.put(idKey, newId);
            keyMap.put(newId, idKey);
            existingId = newId;
        }
        return existingId;
    }
    
    @Override
    public int replace(String oldId, String newId){
        Integer existingId = idMap.get(oldId);
        
        if(existingId != null){
            idMap.remove(oldId);
            keyMap.remove(existingId);
        }
        idMap.put(newId, existingId);
        keyMap.put(existingId, newId);
        
        return existingId;
        
     }
	
	@Override
    public synchronized boolean keyExists(String key) {
        return idMap.containsKey(key);
    }
	
	

    @Override
    public synchronized String getString(Integer id) {
        try {
            return keyMap.get(id);
        } catch (Exception e) {
            Log.e("LayoutEditor", "Id doesn't exist: " + id);
        }
        return "";
    }

    public synchronized ArrayList<String> getKeys() {
        return new ArrayList<String>(idMap.keySet());
    }

    /**
     * Taken from Android View Source code API 17+
     * <p/>
     * Generate a value suitable for use.
     * This value will not collide with ID values generated at inflate time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();

            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }
}
