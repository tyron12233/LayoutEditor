package com.tyron.layouteditor.editor.widget;

import android.view.View;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.ViewManager;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.values.Value;

import java.util.ArrayList;
import java.util.Map;

public interface BaseWidget {
	
    default void update(ArrayList<Attribute> data){
        ViewManager.updateView(data, getAsView());
        getAsView().requestLayout();
    };

    @NonNull
    ArrayList<Attribute> getAttributes();

    View getAsView();
}
