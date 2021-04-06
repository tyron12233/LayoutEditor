package com.tyron.layouteditor.parser;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

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
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.compat.CompatAttributes;
import com.tyron.layouteditor.editor.widget.view.ViewItem;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.Color;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class CompatViewParser<V extends View> extends ViewTypeParser<V> {
    @NonNull
    @Override
    public String getType() {
        return "View";
    }

    @Nullable
    @Override
    public String getParentType() {
        return null;
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ViewItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(CompatAttributes.View.Background, new StringAttributeProcessor<V>() {
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
                            setBackground(view, R.drawable.ic_warning);
                        }else{
                            setBackground(view, path);
                        }
                    }else{
                        throw new IllegalStateException("View is not an instance of BaseWidget");
                    }

                }else{
                    //the background starts with "#"
                    if(Color.isColor(value)){
                        view.setBackgroundColor(Color.valueOf(value).apply(view.getContext()).color);
                    }
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
