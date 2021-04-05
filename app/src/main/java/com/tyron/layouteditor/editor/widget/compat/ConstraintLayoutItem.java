package com.tyron.layouteditor.editor.widget.compat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tyron.layouteditor.Theme;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.util.NotificationCenter;

public class ConstraintLayoutItem extends ConstraintLayout implements BaseWidget {

    private Manager manager;

    public ConstraintLayoutItem(@NonNull Context context) {
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
