package com.tyron.layouteditor.editor.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.tyron.layouteditor.editor.PropertiesView;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Dimension;
import com.tyron.layouteditor.values.Primitive;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RelativeLayoutItem extends RelativeLayout implements BaseWidget{

    Paint backgroundPaint = new Paint();

    public RelativeLayoutItem(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        backgroundPaint.setColor(0xff757575);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setAntiAlias(true);

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.setDuration(180L);
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
        setLayoutTransition(layoutTransition);
    }

    private Manager viewManager;

    @Override
    public @NotNull View getAsView() {
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


    @NonNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();

        attributes.add(new Attribute(Attributes.View.Height, Dimension.valueOf(getLayoutParams().height)));
        attributes.add(new Attribute(Attributes.View.Width, Dimension.valueOf(getLayoutParams().width)));
        attributes.add(new Attribute(Attributes.View.Id, new Primitive(viewManager.getContext().getInflater().getIdGenerator().getString(getId()))));
        if (getParent() instanceof RelativeLayoutItem) {
            attributes.addAll(Attributes.getRelativeLayoutChildAttributes((RelativeLayout.LayoutParams) getLayoutParams(), viewManager.getContext().getInflater().getIdGenerator()));
        }

        return attributes;
    }

    Rect rect = new Rect();
    @Override
    public void onDraw(Canvas canvas){

        getLocalVisibleRect(rect);
        canvas.drawRect(rect, backgroundPaint);
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


    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didUpdateWidget && ((String) args[0]).equals(getStringId())) {
            viewManager.updateAttributes((List<Attribute>) args[1]);
        }
    }
}
