package com.tyron.layouteditor.toolbox;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

public class HidingLayoutBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    public HidingLayoutBehavior(){

    }

    public HidingLayoutBehavior(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationX() - dependency.getWidth());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        Log.d(HidingLayoutBehavior.class.getName(), "view class: "+ dependency.getClass().getName());
        return dependency instanceof CardView;
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull V child,  @NonNull View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        child.setTranslationY(0);
    }
}
