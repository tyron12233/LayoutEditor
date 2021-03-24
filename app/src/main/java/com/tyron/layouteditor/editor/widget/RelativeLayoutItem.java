package com.tyron.layouteditor.editor.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.tyron.layouteditor.EditorContext;
import com.tyron.layouteditor.WidgetFactory;
import com.tyron.layouteditor.editor.PropertiesView;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Primitive;

import java.util.ArrayList;

public class RelativeLayoutItem extends RelativeLayout implements BaseWidget, View.OnClickListener, View.OnLongClickListener {

    private View shadow;

    public RelativeLayoutItem(Context context) {
        super(context);
        init();
    }

    private void init(){
        setOnLongClickListener(this);
        setOnClickListener(this);
        setBackgroundColor(0xffffffff);

        shadow = new View(getContext());
        shadow.setBackgroundColor(0x52000000);
        shadow.setLayoutParams(new LinearLayout.LayoutParams(AndroidUtilities.dp(100), AndroidUtilities.dp(50)));
        shadow.setMinimumWidth(AndroidUtilities.dp(50));
        shadow.setMinimumHeight(AndroidUtilities.dp(50));

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(AndroidUtilities.dp(2), 0xfffe6262);
        setBackground(gd);

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.setDuration(180L);
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
        setLayoutTransition(layoutTransition);
    }

    @Override
    public boolean onLongClick(View v) {
        ViewCompat.startDragAndDrop(this, null, new DragShadowBuilder(this), this, 0);
        ((ViewGroup)getParent()).removeView(this);
        return true;
    }

    @Override
    public void onClick(View v) {
        PropertiesView d = PropertiesView.newInstance(getAttributes(), getStringId());
        d.show(AndroidUtilities.getActivity(getContext()).getSupportFragmentManager(), "null");
    }


    @Override
    public View getAsView(){
        return this;
    }


    @NonNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();

        attributes.add(new Attribute(Attributes.View.Height, new Primitive(getLayoutParams().height)));
        attributes.add(new Attribute(Attributes.View.Width, new Primitive(getLayoutParams().width)));

        if(getParent() instanceof RelativeLayoutItem){
            attributes.addAll(Attributes.getRelativeLayoutChildAttributes((RelativeLayout.LayoutParams)getLayoutParams()));
        }
        return attributes;
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
    public void didReceivedNotification(int id, Object... args){
        if(id == NotificationCenter.didUpdateWidget && ((String)args[0]).equals(getStringId())){
            update((ArrayList<Attribute>) args[1]);
        }
    }
}
