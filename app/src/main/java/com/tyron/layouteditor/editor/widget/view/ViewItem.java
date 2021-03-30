package com.tyron.layouteditor.editor.widget.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Attribute;

import java.util.ArrayList;

public class ViewItem extends View implements BaseWidget {

    Manager viewManager;

    public ViewItem(Context context) {
        super(context);
    }

    public ViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public Manager getViewManager() {
        return viewManager;
    }

    @Override
    public void setViewManager(@NonNull Manager manager) {
        this.viewManager = manager;
    }

    @NonNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        return null;
    }

    @NonNull
    @Override
    public View getAsView() {
        return this;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }
}
