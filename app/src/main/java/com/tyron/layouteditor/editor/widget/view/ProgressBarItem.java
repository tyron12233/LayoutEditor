package com.tyron.layouteditor.editor.widget.view;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.viewgroup.RelativeLayoutItem;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Primitive;

import java.util.ArrayList;
import java.util.List;

public class ProgressBarItem extends ProgressBar implements BaseWidget {

    private Manager viewManager;

    public ProgressBarItem(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>(Attributes.getViewAttributes(this));

        attributes.add(new Attribute(Attributes.ProgressBar.Indeterminate, new Primitive(isIndeterminate())));
        attributes.add(new Attribute(Attributes.ProgressBar.Max, new Primitive(getMax())));

        if(getParent() instanceof RelativeLayoutItem){
            attributes.addAll(Attributes.getRelativeLayoutChildAttributes(this));
        }
        return attributes;
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

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == NotificationCenter.didUpdateWidget && args[0].equals(getStringId())){
            viewManager.updateAttributes( (List<Attribute>) args[1] );
        }
    }
}
