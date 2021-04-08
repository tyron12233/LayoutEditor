package com.tyron.layouteditor.parser.compat;

import android.content.res.ColorStateList;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.EditorConstants;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.compat.CompatAttributes;
import com.tyron.layouteditor.editor.widget.compat.MaterialCardViewItem;
import com.tyron.layouteditor.processor.BooleanAttributeProcessor;
import com.tyron.layouteditor.processor.ColorResourceProcessor;
import com.tyron.layouteditor.processor.DimensionAttributeProcessor;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.values.Color;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class MaterialCardViewParser<V extends MaterialCardView> extends ViewTypeParser<V> {
    @NonNull
    @Override
    public String getType() {
        return "MaterialCardView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "CardView";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new MaterialCardViewItem(context);
    }

    @Override
    protected void addAttributeProcessors() {
        addAttributeProcessor(CompatAttributes.MaterialCardView.Checkable, new BooleanAttributeProcessor<V>() {
            @Override
            public void setBoolean(V view, boolean value) {
                view.setCheckable(value);
            }
        });

        addAttributeProcessor(CompatAttributes.MaterialCardView.CheckedIcon, new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                if(value.startsWith("@drawable/")){

                    if(view instanceof BaseWidget){
                        BaseWidget widget = (BaseWidget)view;

                        String path = widget.getViewManager()
                                .getContext()
                                .getEditorResources()
                                .getDrawable(value.replace("@drawable/", ""));

                        if(path.equals(EditorConstants.DATA_NULL)){
                            //use default image
                            AndroidUtilities.setBackground(view, R.drawable.ic_warning);
                        }else{
                            AndroidUtilities.setBackground(view, path);
                        }
                    }else{
                        throw new IllegalStateException("View is not an instance of BaseWidget");
                    }

                }
            }
        });

        addAttributeProcessor(CompatAttributes.MaterialCardView.CheckedIconMargin, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setCheckedIconMargin((int) dimension);
            }
        });

        addAttributeProcessor(CompatAttributes.MaterialCardView.CheckedIconSize, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setCheckedIconSize((int) dimension);
            }
        });

        addAttributeProcessor(CompatAttributes.MaterialCardView.RippleColor, new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                view.setRippleColor(ColorStateList.valueOf(color));
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setRippleColor(colors);
            }
        });
    }
}
