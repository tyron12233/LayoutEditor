package com.tyron.layouteditor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;

import java.util.ArrayList;

public class ViewManager {


    public static void updateView(ArrayList<Attribute> attributes, View view) {

        SimpleIdGenerator idGenerator = SimpleIdGenerator.getInstance();
        ViewGroup.LayoutParams params = view.getLayoutParams();


        for (Attribute attr : attributes) {

            //ViewGroup LayoutParams
            switch (attr.key) {
                case "layout_width":
                    view.setMinimumWidth(attr.value.getAsInt() >= 0 ? -1 : AndroidUtilities.dp(50));
                    params.width = (attr.value.getAsInt()) >= 0 ? AndroidUtilities.dp(attr.value.getAsInt()) : attr.value.getAsInt();
                    break;
                case "layout_height":
                    params.height = (attr.value.getAsInt()) >= 0 ? AndroidUtilities.dp(attr.value.getAsInt()) : attr.value.getAsInt();
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
        switch (attr.key) {
            case Attributes.View.ToLeftOf:
                if (attr.value.isNull()) {
                    rParams.removeRule(RelativeLayout.LEFT_OF);
                } else {
                    rParams.addRule(RelativeLayout.LEFT_OF, idGenerator.getUnique(attr.value.getAsString()));
                }

                break;
            case Attributes.View.ToRightOf:
                if (attr.value.isNull()) {
                    rParams.removeRule(RelativeLayout.RIGHT_OF);
                } else {
                    rParams.addRule(RelativeLayout.RIGHT_OF, idGenerator.getUnique(attr.value.getAsString()));
                }
                break;
            case Attributes.View.Above:
                if (attr.value.isNull()) {
                    rParams.removeRule(RelativeLayout.ABOVE);
                } else {
                    rParams.addRule(RelativeLayout.ABOVE, idGenerator.getUnique(attr.value.getAsString()));
                }
                break;
            case Attributes.View.Below:
                if (attr.value.isNull()) {
                    rParams.removeRule(RelativeLayout.BELOW);
                } else {
                    rParams.addRule(RelativeLayout.BELOW, idGenerator.getUnique(attr.value.getAsString()));
                }
                break;
            case Attributes.View.AlignBaseline:
                if (attr.value.isNull()) {
                    rParams.removeRule(RelativeLayout.ALIGN_BASELINE);
                } else {
                    rParams.addRule(RelativeLayout.ALIGN_BASELINE, idGenerator.getUnique(attr.value.getAsString()));
                }
                break;
            case Attributes.View.AlignLeft:
                if (attr.value.isNull()) {
                    rParams.removeRule(RelativeLayout.ALIGN_LEFT);
                } else {
                    rParams.addRule(RelativeLayout.ALIGN_LEFT, idGenerator.getUnique(attr.value.getAsString()));
                }
                break;
            case Attributes.View.AlignParentBottom:
                if (!attr.value.isNull() && attr.value.getAsBoolean()) {
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                } else {
                    rParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }
                break;
            case Attributes.View.AlignParentTop:
                if (!attr.value.isNull() && attr.value.getAsBoolean()) {
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                } else {
                    rParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                }
                break;
            case Attributes.View.AlignParentStart:
                if (!attr.value.isNull() && attr.value.getAsBoolean()) {
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                } else {
                    rParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
                }
                break;
            case Attributes.View.AlignParentEnd:
                if (!attr.value.isNull() && attr.value.getAsBoolean()) {
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                } else {
                    rParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
                }
                break;
        }
    }
}
