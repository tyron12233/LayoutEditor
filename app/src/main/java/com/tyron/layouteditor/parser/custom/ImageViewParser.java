package com.tyron.layouteditor.parser.custom;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.EditorConstants;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.view.ImageViewItem;
import com.tyron.layouteditor.parser.ParseHelper;
import com.tyron.layouteditor.processor.DrawableResourceProcessor;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.Color;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class ImageViewParser<T extends ImageView> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "ImageView";
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
        return new ImageViewItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.ImageView.Src, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if(value.startsWith("@drawable/")) {

                    if (view instanceof BaseWidget) {
                        BaseWidget widget = (BaseWidget) view;

                        String path = widget.getViewManager()
                                .getContext()
                                .getEditorResources()
                                .getDrawable(value.replace("@drawable/", ""));

                        if (path.equals(EditorConstants.DATA_NULL)) {
                            //use default image
                            setBackground(view, R.drawable.ic_warning);
                        } else {
                            setBackground(view, path);
                        }
                    } else {
                        throw new IllegalStateException("View is not an instance of BaseWidget");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.ImageView.ScaleType, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                ImageViewItem.ScaleType scaleType;
                scaleType = ParseHelper.parseScaleType(value);
                if (scaleType != null)
                    view.setScaleType(scaleType);
            }
        });

        addAttributeProcessor(Attributes.ImageView.AdjustViewBounds, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if ("true".equals(value)) {
                    view.setAdjustViewBounds(true);
                } else {
                    view.setAdjustViewBounds(false);
                }
            }
        });
    }

    private void setBackground(View view, String path){
        Glide.with(view).load(path).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                view.setBackground(placeholder);
            }
        });
    }

    private void setBackground(View view, @DrawableRes int id){
        Glide.with(view).load(id).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                view.setBackground(placeholder);
            }
        });
    }
}