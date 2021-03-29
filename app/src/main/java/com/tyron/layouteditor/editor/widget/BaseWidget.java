package com.tyron.layouteditor.editor.widget;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.DataContext;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.ViewManager;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

import java.util.ArrayList;
import java.util.List;

public interface BaseWidget extends NotificationCenter.NotificationCenterDelegate {

    default void update(ArrayList<Attribute> data) {
        ViewManager.updateView(data, getAsView());
        getAsView().requestLayout();
    }

    default String getStringId() {
        return getViewManager().getContext().getInflater().getIdGenerator().getString(getAsView().getId());
    }

    @NonNull
    ArrayList<Attribute> getAttributes();

    @NonNull
    View getAsView();

    Manager getViewManager();

    void setViewManager(@NonNull Manager manager);

    public interface Manager {

        void update(@Nullable ObjectValue data);

        @Nullable
        View findViewById(@NonNull String id);

        EditorContext getContext();
		
		void updateAttributes(List<Attribute> attrs);
		
        @NonNull
        Layout getLayout();

        @Nullable
        Object getExtras();

        @NonNull
        DataContext getDataContext();

        void setExtras(@Nullable Object extras);
    }
}
