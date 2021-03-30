package com.tyron.layouteditor.parser.custom;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.view.ProgressBarItem;
import com.tyron.layouteditor.parser.ParseHelper;
import com.tyron.layouteditor.processor.AttributeProcessor;
import com.tyron.layouteditor.processor.ColorResourceProcessor;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

/**
 * @author Aditya Sharat
 */
public class ProgressBarParser<T extends ProgressBar> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "ProgressBar";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "View";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data,
                                 @Nullable ViewGroup parent, int dataIndex) {
        return new ProgressBarItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.ProgressBar.Max, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setMax((int) ParseHelper.parseDouble(value));
            }
        });
        addAttributeProcessor(Attributes.ProgressBar.Progress, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setProgress((int) ParseHelper.parseDouble(value));
            }
        });

        addAttributeProcessor(Attributes.ProgressBar.ProgressTint, new AttributeProcessor<T>() {
            @Override
            public void handleValue(T view, Value value) {
                if (!value.isObject()) {
                    return;
                }
                int background = Color.TRANSPARENT;
                int progress = Color.TRANSPARENT;
                ObjectValue object = value.getAsObject();
                String string = object.getAsString("background");
                if (string != null) {
                    background = ParseHelper.parseColor(string);
                }
                string = object.getAsString("progress");
                if (string != null) {
                    progress = ParseHelper.parseColor(string);
                }

                view.setProgressDrawable(getLayerDrawable(progress, background));
            }

            @Override
            public void handleResource(T view, Resource resource) {
                Drawable d = resource.getDrawable(view.getContext());
                view.setProgressDrawable(d);
            }

            @Override
            public void handleAttributeResource(T view, AttributeResource attribute) {
                TypedArray a = attribute.apply(view.getContext());
                view.setProgressDrawable(a.getDrawable(0));
            }

            @Override
            public void handleStyleResource(T view, StyleResource style) {
                TypedArray a = style.apply(view.getContext());
                view.setProgressDrawable(a.getDrawable(0));
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addAttributeProcessor(Attributes.ProgressBar.SecondaryProgressTint, new ColorResourceProcessor<T>() {
                @Override
                public void setColor(T view, int color) {

                }

                @Override
                public void setColor(T view, ColorStateList colors) {
                    //noinspection AndroidLintNewApi
                    view.setSecondaryProgressTintList(colors);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addAttributeProcessor(Attributes.ProgressBar.IndeterminateTint, new ColorResourceProcessor<T>() {
                @Override
                public void setColor(T view, int color) {

                }

                @Override
                public void setColor(T view, ColorStateList colors) {
                    view.setIndeterminateTintList(colors);
                }
            });
        }
    }

    Drawable getLayerDrawable(int progress, int background) {
        ShapeDrawable shape = new ShapeDrawable();
        shape.getPaint().setStyle(Paint.Style.FILL);
        shape.getPaint().setColor(background);

        ShapeDrawable shapeD = new ShapeDrawable();
        shapeD.getPaint().setStyle(Paint.Style.FILL);
        shapeD.getPaint().setColor(progress);
        ClipDrawable clipDrawable = new ClipDrawable(shapeD, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        return new LayerDrawable(new Drawable[]{shape, clipDrawable});
    }
}
