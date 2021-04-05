package com.tyron.layouteditor.editor.widget;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.DataContext;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.ViewManager;
import com.tyron.layouteditor.editor.widget.viewgroup.LinearLayoutItem;
import com.tyron.layouteditor.editor.widget.viewgroup.RelativeLayoutItem;
import com.tyron.layouteditor.editor.widget.compat.CompatAttributes;
import com.tyron.layouteditor.models.Attribute;
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

        if(view instanceof TextView){
            attrs.add(Attributes.TextView.Text);
            attrs.add(Attributes.TextView.TextColor);
            attrs.add(Attributes.TextView.TextSize);
        }

        //relative layout child attributes
        if(view.getParent() instanceof RelativeLayoutItem){
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

        if(view.getParent() instanceof LinearLayoutItem){
            attrs.add(Attributes.View.Weight);
            attrs.add(Attributes.View.Gravity);
        }
		
		if(view.getParent() instanceof ConstraintLayout){
			attrs.add(CompatAttributes.ConstraintLayout.LeftToLeftOf);
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
