package com.tyron.layouteditor.parser.custom;

import android.content.res.ColorStateList;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.compat.CardViewItem;
import com.tyron.layouteditor.editor.widget.compat.CompatAttributes;
import com.tyron.layouteditor.processor.BooleanAttributeProcessor;
import com.tyron.layouteditor.processor.ColorResourceProcessor;
import com.tyron.layouteditor.processor.DimensionAttributeProcessor;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class CardViewParser<T extends CardView> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "CardView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "FrameLayout";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new CardViewItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(CompatAttributes.CardView.BackgroundColor, new ColorResourceProcessor<T>() {
            @Override
            public void setColor(T view, int color) {
                view.setCardBackgroundColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setCardBackgroundColor(colors);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.CornerRadius, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setRadius(dimension);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.Elevation, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setCardElevation(dimension);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.MaxElevation, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setMaxCardElevation(dimension);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.PreventCornerOverlap, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setPreventCornerOverlap(value);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.UseCompatPadding, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setUseCompatPadding(value);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.ContentPadding, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setContentPadding((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.ContentPaddingBottom, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int t = view.getContentPaddingTop();
                int r = view.getContentPaddingRight();
                int l = view.getContentPaddingLeft();

                view.setContentPadding(l, t, r, (int) dimension);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.ContentPaddingLeft, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int t = view.getContentPaddingTop();
                int r = view.getContentPaddingRight();
                int b = view.getContentPaddingBottom();

                view.setContentPadding((int) dimension, t, r, b);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.ContentPaddingRight, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int t = view.getContentPaddingTop();
                int b = view.getContentPaddingBottom();
                int l = view.getContentPaddingLeft();

                view.setContentPadding(l, t, (int) dimension, b);
            }
        });

        addAttributeProcessor(CompatAttributes.CardView.ContentPaddingTop, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int r = view.getContentPaddingRight();
                int b = view.getContentPaddingBottom();
                int l = view.getContentPaddingLeft();

                view.setContentPadding(l, (int) dimension, r, b);
            }
        });

    }
}