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

    public static void updateView(ArrayList<Attribute> attributes, View view){

        ViewGroup.LayoutParams params = view.getLayoutParams();

        for(Attribute attr : attributes){

            switch (attr.key) {
                case "layout_width":
                    view.setMinimumWidth(attr.value.getAsInt() >= 0 ? -1 : AndroidUtilities.dp(50));
                    params.width = (attr.value.getAsInt()) >= 0 ? AndroidUtilities.dp(attr.value.getAsInt()) : attr.value.getAsInt();
                    break;
                case "layout_height":
                    params.height = (attr.value.getAsInt()) >= 0 ? AndroidUtilities.dp(attr.value.getAsInt()) : attr.value.getAsInt();
                    break;
                case Attributes.LinearLayout.Orientation:
                    ((LinearLayout) view).setOrientation(attr.value.getAsInt());
                    break;
                case Attributes.View.Weight:
                    ((LinearLayout.LayoutParams) params).weight = attr.value.getAsInt();
                    break;
                case Attributes.View.AlignParentBottom :
                    if(attr.value.getAsBoolean()) {
                        ((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    }else{
                        ((RelativeLayout.LayoutParams)params).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    }
                    break;
            }
        }

    }
}
