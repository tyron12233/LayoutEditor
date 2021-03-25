package com.tyron.layouteditor.editor.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.tyron.layouteditor.SimpleIdGenerator;
import com.tyron.layouteditor.editor.PropertiesView;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.values.Primitive;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class TextViewItem extends TextView implements BaseWidget, View.OnClickListener, View.OnLongClickListener {

    public TextViewItem(Context context) {
        super(context);

        setOnClickListener(this);
    }

    @NotNull
    @Override
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Attributes.View.Height, new Primitive(getLayoutParams().height)));
        attributes.add(new Attribute(Attributes.View.Width, new Primitive(getLayoutParams().width)));
        attributes.add(new Attribute(Attributes.TextView.Text, new Primitive(getText().toString())));

        if (getParent() instanceof RelativeLayoutItem) {
            attributes.addAll(Attributes.getRelativeLayoutChildAttributes((RelativeLayout.LayoutParams) getLayoutParams()));
        }

        return attributes;
    }

    @Override
    public void onClick(View v) {
        final PropertiesView dialog = PropertiesView.newInstance(getAttributes(), getStringId());
        dialog.show(AndroidUtilities.getActivity(getContext()).getSupportFragmentManager(), "");
        Toast.makeText(getContext(), SimpleIdGenerator.getInstance().getString(getId()), Toast.LENGTH_SHORT).show();
    }


    @Override
    public View getAsView() {
        return this;
    }


    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public boolean onLongClick(View v) {
        ViewCompat.startDragAndDrop(this, null, new DragShadowBuilder(this), this, 0);
        ((ViewGroup) getParent()).removeView(this);
        return true;
    }
}
