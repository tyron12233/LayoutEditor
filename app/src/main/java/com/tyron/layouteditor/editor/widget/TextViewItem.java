package com.tyron.layouteditor.editor.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Dimension;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class TextViewItem extends TextView implements BaseWidget{

    public TextViewItem(Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Attributes.View.Height, Dimension.valueOf(getLayoutParams().height)));
        attributes.add(new Attribute(Attributes.View.Width, Dimension.valueOf(getLayoutParams().width)));
        attributes.add(new Attribute(Attributes.TextView.Text, new Primitive(getText().toString())));

        attributes.add(new Attribute(Attributes.View.Id, new Primitive(viewManager.getContext().getInflater().getIdGenerator().getString(getId()))));

        if (getParent() instanceof RelativeLayoutItem) {
            attributes.addAll(Attributes.getRelativeLayoutChildAttributes((RelativeLayout.LayoutParams) getLayoutParams(), viewManager.getContext().getInflater().getIdGenerator()));
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
