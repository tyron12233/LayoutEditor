package com.tyron.layouteditor.parser.custom;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.viewgroup.LinearLayoutItem;
import com.tyron.layouteditor.parser.ParseHelper;
import com.tyron.layouteditor.processor.DimensionAttributeProcessor;
import com.tyron.layouteditor.processor.DrawableResourceProcessor;
import com.tyron.layouteditor.processor.GravityAttributeProcessor;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class LinearLayoutParser<T extends LinearLayout> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "LinearLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "ViewGroup";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data,
                                 @Nullable ViewGroup parent, int dataIndex) {
        return new LinearLayoutItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.LinearLayout.Orientation, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if ("horizontal".equals(value)) {
                    view.setOrientation(LinearLayoutItem.HORIZONTAL);
                } else {
                    view.setOrientation(LinearLayoutItem.VERTICAL);
                }
            }
        });

        addAttributeProcessor(Attributes.View.Gravity, new GravityAttributeProcessor<T>() {
            @Override
            public void setGravity(T view, @Gravity int gravity) {
                view.setGravity(gravity);
            }
        });

        addAttributeProcessor(Attributes.LinearLayout.Divider, new DrawableResourceProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void setDrawable(T view, Drawable drawable) {

                view.setDividerDrawable(drawable);
            }
        });

        addAttributeProcessor(Attributes.LinearLayout.DividerPadding, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setDividerPadding((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.LinearLayout.ShowDividers, new StringAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void setString(T view, String value) {

                int dividerMode = ParseHelper.parseDividerMode(value);
                // noinspection ResourceType
                view.setShowDividers(dividerMode);
            }
        });

        addAttributeProcessor(Attributes.LinearLayout.WeightSum, new StringAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void setString(T view, String value) {
                view.setWeightSum(ParseHelper.parseFloat(value));
            }
        });
    }
}