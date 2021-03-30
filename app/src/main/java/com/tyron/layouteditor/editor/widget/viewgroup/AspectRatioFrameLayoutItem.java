package com.tyron.layouteditor.editor.widget.viewgroup;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.custom.AspectRatioFrameLayout;
import com.tyron.layouteditor.models.Attribute;

import java.util.ArrayList;

public class AspectRatioFrameLayoutItem extends AspectRatioFrameLayout implements BaseWidget {

    private BaseWidget.Manager viewManager;

    public AspectRatioFrameLayoutItem(Context context) {
        super(context);
    }

    public AspectRatioFrameLayoutItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioFrameLayoutItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioFrameLayoutItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public BaseWidget.Manager getViewManager() {
        return viewManager;
    }

    @Override
    public void setViewManager(@NonNull BaseWidget.Manager manager) {
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