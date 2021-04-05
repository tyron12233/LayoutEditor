package com.tyron.layouteditor.editor.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.viewgroup.RelativeLayoutItem;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Primitive;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class TextViewItem extends TextView implements BaseWidget {

    public TextViewItem(Context context) {
        super(context);
    }

    private Manager viewManager;

    @Override
    public @NonNull View getAsView() {
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
