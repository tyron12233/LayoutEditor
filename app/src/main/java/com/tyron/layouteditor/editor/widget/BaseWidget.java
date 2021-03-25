package com.tyron.layouteditor.editor.widget;

import android.view.View;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.SimpleIdGenerator;
import com.tyron.layouteditor.ViewManager;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;

import java.util.ArrayList;

public interface BaseWidget extends NotificationCenter.NotificationCenterDelegate {

    default void update(ArrayList<Attribute> data) {
        ViewManager.updateView(data, getAsView());
        getAsView().requestLayout();
    }

    default String getStringId() {
        return SimpleIdGenerator.getInstance().getString(getAsView().getId());
    }

    @NonNull
    ArrayList<Attribute> getAttributes();

    View getAsView();
}
