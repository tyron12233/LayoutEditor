package com.tyron.layouteditor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.values.Primitive;

import java.util.ArrayList;

public class ViewManager {


    public static void updateView(ArrayList<Attribute> attributes, View view) {

        SimpleIdGenerator idGenerator = SimpleIdGenerator.getInstance();
        ViewGroup.LayoutParams params = view.getLayoutParams();


        for (Attribute attr : attributes) {

            //ViewGroup LayoutParams
            switch (attr.key) {
                case Attributes.View.Width:
                    //view.setMinimumWidth(attr.value.getAsInt() >= 0 ? -1 : AndroidUtilities.dp(50));
                    params.width = (int) attr.value.getAsDimension().apply(view.getContext());
                    break;
                case Attributes.View.Height:
                    params.height = (int) attr.value.getAsDimension().apply(view.getContext());
                    break;
            }

            if (params instanceof LinearLayout.LayoutParams) {
                applyLinearLayoutParams(attr, view, (LinearLayout.LayoutParams) params);
            } else if (params instanceof RelativeLayout.LayoutParams) {
                applyRelativeLayoutParams(attr, view, (RelativeLayout.LayoutParams) params);
            }
        }

    }

    /**
     * Handles LinearLayout.LayoutParams for view
     *
     * @param attr   Attribute to be added
     * @param view   Target view
     * @param params Layout params of view
     */
    public static void applyLinearLayoutParams(Attribute attr, View view, LinearLayout.LayoutParams params) {
        switch (attr.key) {
            case Attributes.LinearLayout.Orientation:
                ((LinearLayout) view).setOrientation(attr.value.getAsInt());
                break;
            case Attributes.View.Weight:
                params.weight = attr.value.getAsInt();
                break;
        }
    }

    public static void applyRelativeLayoutParams(Attribute attr, View view, RelativeLayout.LayoutParams rParams) {

        IdGenerator idGenerator = SimpleIdGenerator.getInstance();
        boolean layoutTarget = false;
        int layoutRule = 100;

        if (attr.value.isNull()) {
            attr.value = new Primitive(false);
        }
        switch (attr.key) {
            case Attributes.View.ToLeftOf:
                layoutRule = RelativeLayout.LEFT_OF;
                layoutTarget = true;
                break;
            case Attributes.View.ToRightOf:
                layoutRule = RelativeLayout.RIGHT_OF;
                layoutTarget = true;
                break;
            case Attributes.View.Above:
                layoutRule = RelativeLayout.ABOVE;
                layoutTarget = true;
                break;
            case Attributes.View.Below:
                layoutRule = RelativeLayout.BELOW;
                layoutTarget = true;
                break;
            case Attributes.View.AlignBaseline:
                layoutRule = RelativeLayout.ALIGN_BASELINE;
                layoutTarget = true;
                break;
            case Attributes.View.AlignLeft:
                layoutRule = RelativeLayout.ALIGN_LEFT;
                layoutTarget = true;
                break;
            case Attributes.View.AlignParentBottom:
                layoutRule = RelativeLayout.ALIGN_PARENT_BOTTOM;
                break;
            case Attributes.View.AlignParentTop:
                layoutRule = RelativeLayout.ALIGN_PARENT_TOP;
                break;
            case Attributes.View.AlignParentLeft:
            case Attributes.View.AlignParentStart:
                layoutRule = RelativeLayout.ALIGN_PARENT_START;
                break;
            case Attributes.View.AlignParentRight:
            case Attributes.View.AlignParentEnd:
                layoutRule = RelativeLayout.ALIGN_PARENT_END;
                break;
            case Attributes.View.CenterInParent:
                layoutRule = RelativeLayout.CENTER_IN_PARENT;
                break;
            case Attributes.View.CenterHorizontal:
                layoutRule = RelativeLayout.CENTER_HORIZONTAL;
                break;
            case Attributes.View.CenterVertical:
                layoutRule = RelativeLayout.CENTER_VERTICAL;
                break;

        }
        if (layoutRule != 100) {
            if (layoutTarget) {
                if (attr.value.isNull()) {
                    rParams.removeRule(layoutRule);
                } else {
                    int anchor = idGenerator.getUnique(attr.value.getAsString());
                    rParams.addRule(layoutRule, anchor);
                }
            } else {
                if (attr.value.isNull() || !attr.value.getAsBoolean()) {
                    rParams.removeRule(layoutRule);
                } else {
                    rParams.addRule(layoutRule);
                }
            }
        }
    }
}
