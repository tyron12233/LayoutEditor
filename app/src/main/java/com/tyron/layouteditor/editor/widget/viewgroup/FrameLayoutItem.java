package com.tyron.layouteditor.editor.widget.viewgroup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.Theme;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;

import java.util.ArrayList;
import java.util.List;

public class FrameLayoutItem extends FrameLayout implements BaseWidget {

    private Manager viewManager;

    public FrameLayoutItem(Context context){
        super(context);
        setWillNotDraw(false);
    }

    Rect rect = new Rect();
    @Override
    public void onDraw(Canvas canvas){
        getLocalVisibleRect(rect);
        canvas.drawRect(rect, Theme.getViewBackgroundPaint());
    }

    @NonNull
    @Override
    public View getAsView() {
        return this;
    }

    @Override
    public Manager getViewManager() {
        return viewManager;
    }

    @Override
    public void setViewManager(@NonNull Manager manager) {
        this.viewManager = manager;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdateWidget);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdateWidget);
    }
}
