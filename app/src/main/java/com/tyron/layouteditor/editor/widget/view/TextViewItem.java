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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class TextViewItem extends TextView implements BaseWidget {

    public TextViewItem(Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>(Attributes.getViewAttributes(this));

        attributes.add(new Attribute(Attributes.TextView.Text, new Primitive(getText().toString())));

        int textColor = getCurrentTextColor() - (16777215 + 1);

        attributes.add(new Attribute(Attributes.TextView.TextColor, new Primitive(String.format("#%06X", (0xFFFFFF & textColor)))));

        if (getParent() instanceof RelativeLayoutItem) {
            attributes.addAll(Attributes.getRelativeLayoutChildAttributes(this));
        }

        return attributes;
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
