package com.tyron.layouteditor.editor.widget;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.card.MaterialCardView;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.DataContext;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.ViewManager;
import com.tyron.layouteditor.editor.widget.viewgroup.FrameLayoutItem;
import com.tyron.layouteditor.editor.widget.viewgroup.LinearLayoutItem;
import com.tyron.layouteditor.editor.widget.viewgroup.RelativeLayoutItem;
import com.tyron.layouteditor.editor.widget.compat.CompatAttributes;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public interface BaseWidget extends NotificationCenter.NotificationCenterDelegate {

    default void update(ArrayList<Attribute> data) {
        ViewManager.updateView(data, getAsView());
        getAsView().requestLayout();
    }

    default String getStringId() {
        return getViewManager().getContext().getInflater().getIdGenerator().getString(getAsView().getId());
    }

    @NonNull
    default List<Attribute> getAttributes() {

        LinkedHashSet<Attribute> appliedAttributes = new LinkedHashSet<>();

        if(getAsView().getTag(R.id.attributes) != null){
            appliedAttributes.addAll((Collection<? extends Attribute>) getAsView().getTag(R.id.attributes));

            return new ArrayList<>(appliedAttributes);
        }

        throw new IllegalStateException("View doesn't have attributes");
    }

    default List<String> getAvailableAttributes(){
        LinkedHashSet<String> attrs = new LinkedHashSet<>();
        View view = getAsView();

        //View attributes
        attrs.add(Attributes.View.Id);
        attrs.add(Attributes.View.Width);
        attrs.add(Attributes.View.Height);
        attrs.add(Attributes.View.Background);
        attrs.add(Attributes.View.BackgroundTint);
        attrs.add(Attributes.View.Foreground);
        attrs.add(Attributes.View.ForegroundTint);

        attrs.add(Attributes.View.Padding);
        attrs.add(Attributes.View.PaddingLeft);
        attrs.add(Attributes.View.PaddingTop);
        attrs.add(Attributes.View.PaddingRight);
        attrs.add(Attributes.View.PaddingBottom);
        attrs.add(Attributes.View.PaddingStart);
        attrs.add(Attributes.View.PaddingEnd);

        attrs.add(Attributes.View.Margin);
        attrs.add(Attributes.View.MarginLeft);
        attrs.add(Attributes.View.MarginTop);
        attrs.add(Attributes.View.MarginRight);
        attrs.add(Attributes.View.MarginBottom);
        attrs.add(Attributes.View.MarginEnd);
        attrs.add(Attributes.View.MarginStart);

        if(AndroidUtilities.isAssignableFrom(LinearLayout.class.getName(), view)){
            attrs.add(Attributes.View.Gravity);
            attrs.add(Attributes.LinearLayout.Orientation);
        }

        if(AndroidUtilities.isAssignableFrom(TextView.class.getName(), view)){
            attrs.add(Attributes.TextView.Text);
            attrs.add(Attributes.TextView.TextColor);
            attrs.add(Attributes.TextView.TextSize);
            attrs.add(Attributes.TextView.Hint);
            attrs.add(Attributes.TextView.Gravity);
            attrs.add(Attributes.TextView.TextColorHint);
            attrs.add(Attributes.TextView.TextStyle);
        }

        if(AndroidUtilities.isAssignableFrom(ImageView.class.getName(), view)){
            attrs.add(Attributes.ImageView.ScaleType);
            attrs.add(Attributes.ImageView.AdjustViewBounds);
            attrs.add(Attributes.ImageView.Src);
        }

        if(AndroidUtilities.isAssignableFrom(MaterialCardView.class.getName(), view)){
            attrs.add(CompatAttributes.MaterialCardView.Checkable);
            attrs.add(CompatAttributes.MaterialCardView.CheckedIconSize);
            attrs.add(CompatAttributes.MaterialCardView.RippleColor);
            attrs.add(CompatAttributes.MaterialCardView.ForegroundColor);
            attrs.add(CompatAttributes.MaterialCardView.CheckedIcon);
            attrs.add(CompatAttributes.MaterialCardView.CheckedIconMargin);
        }
        if(AndroidUtilities.isAssignableFrom(CardView.class.getName(), view)){
            attrs.add(CompatAttributes.CardView.BackgroundColor);
            attrs.add(CompatAttributes.CardView.ContentPadding);
            attrs.add(CompatAttributes.CardView.ContentPaddingBottom);
            attrs.add(CompatAttributes.CardView.ContentPaddingLeft);
            attrs.add(CompatAttributes.CardView.ContentPaddingRight);
            attrs.add(CompatAttributes.CardView.ContentPaddingTop);
            attrs.add(CompatAttributes.CardView.CornerRadius);
            attrs.add(CompatAttributes.CardView.Elevation);
            attrs.add(CompatAttributes.CardView.MaxElevation);
            attrs.add(CompatAttributes.CardView.PreventCornerOverlap);
            attrs.add(CompatAttributes.CardView.UseCompatPadding);
        }

        //relative layout child attributes
        if(AndroidUtilities.isAssignableFrom(RelativeLayout.class.getName(), (View) view.getParent())){
            attrs.add(Attributes.View.ToLeftOf);
            attrs.add(Attributes.View.ToStartOf);
            attrs.add(Attributes.View.ToRightOf);
            attrs.add(Attributes.View.ToEndOf);
            attrs.add(Attributes.View.Above);
            attrs.add(Attributes.View.Below);
            attrs.add(Attributes.View.CenterHorizontal);
            attrs.add(Attributes.View.CenterInParent);
            attrs.add(Attributes.View.CenterVertical);
            attrs.add(Attributes.View.AlignBaseline);
            attrs.add(Attributes.View.AlignBottom);
            attrs.add(Attributes.View.AlignTop);
            attrs.add(Attributes.View.AlignLeft);
            attrs.add(Attributes.View.AlignStart);
            attrs.add(Attributes.View.AlignRight);
            attrs.add(Attributes.View.AlignEnd);
            attrs.add(Attributes.View.AlignParentBottom);
            attrs.add(Attributes.View.AlignParentTop);
            attrs.add(Attributes.View.AlignParentLeft);
            attrs.add(Attributes.View.AlignParentStart);
            attrs.add(Attributes.View.AlignParentRight);
            attrs.add(Attributes.View.AlignParentEnd);
        }

        if(AndroidUtilities.isChildOf(CoordinatorLayout.class.getName(), view)){
            attrs.add(CompatAttributes.CoordinatorLayout.Behavior);
            attrs.add(CompatAttributes.CoordinatorLayout.Hideable);
            attrs.add(CompatAttributes.CoordinatorLayout.PeekHeight);
        }

        if(AndroidUtilities.isAssignableFrom(LinearLayout.class.getName(), (View) view.getParent())){
            attrs.add(Attributes.View.Weight);
            attrs.add(Attributes.View.LayoutGravity);
        }

        if(AndroidUtilities.isAssignableFrom(FrameLayout.class.getName(), (View) view.getParent())){
            attrs.add(Attributes.View.LayoutGravity);
        }
		
		if(AndroidUtilities.isAssignableFrom(ConstraintLayout.class.getName(), (View) view.getParent())){
			attrs.add(CompatAttributes.ConstraintLayout.LeftToLeftOf);
			attrs.add(CompatAttributes.ConstraintLayout.LeftToRightOf);
			attrs.add(CompatAttributes.ConstraintLayout.RightToLeftOf);
			attrs.add(CompatAttributes.ConstraintLayout.RightToRightOf);

			attrs.add(CompatAttributes.ConstraintLayout.BottomToBottomOf);
			attrs.add(CompatAttributes.ConstraintLayout.BottomToTopOf);
			attrs.add(CompatAttributes.ConstraintLayout.TopToBottomOf);
			attrs.add(CompatAttributes.ConstraintLayout.TopToTopOf);
		}

        return new ArrayList<>(attrs);
    }

    @Override
    default void didReceivedNotification(int id, Object... args){
        if(id == NotificationCenter.didUpdateWidget && ((String)args[0]).equals(getStringId())){
            getViewManager().updateAttributes((List<Attribute>) args[1]);

            View view = getAsView();

            if(view.getTag(R.id.attributes) == null) {
                view.setTag(R.id.attributes, new LinkedHashSet<>((List<Attribute>) args[1]));
            }else{
                LinkedHashSet<Attribute> attributes = new LinkedHashSet<>((Collection<? extends Attribute>) args[1]);
                attributes.addAll((Collection<? extends Attribute>) view.getTag(R.id.attributes) );
                view.setTag(R.id.attributes, attributes);
            }
        }
    }

    @NonNull
    View getAsView();

    Manager getViewManager();

    void setViewManager(@NonNull Manager manager);

    public interface Manager {

        void update(@Nullable ObjectValue data);

        @Nullable
        View findViewById(@NonNull String id);

        EditorContext getContext();
		
		void updateAttributes(List<Attribute> attrs);
		
        @NonNull
        Layout getLayout();

        @Nullable
        Object getExtras();

        @NonNull
        DataContext getDataContext();

        void setExtras(@Nullable Object extras);
    }
}
