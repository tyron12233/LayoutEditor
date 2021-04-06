package com.tyron.layouteditor.editor.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.util.NotificationCenter;

@SuppressLint("AppCompatCustomView")
public class ImageViewItem extends ImageView implements BaseWidget {

    private Manager manager;

    public ImageViewItem(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public View getAsView() {
        return this;
    }

    @Override
    public Manager getViewManager() {
        return manager;
    }

    @Override
    public void setViewManager(@NonNull Manager manager) {
        this.manager = manager;
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
