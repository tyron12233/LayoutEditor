package com.tyron.layouteditor.managers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.BoundAttribute;
import com.tyron.layouteditor.editor.DataContext;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.Value;
import com.tyron.layouteditor.values.ObjectValue;
import com.tyron.layouteditor.models.Attribute;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ViewManager implements BaseWidget.Manager {

    @NonNull
    protected final EditorContext context;

    @NonNull
    protected final View view;

    @NonNull
    protected final Layout layout;

    @NonNull
    protected final DataContext dataContext;

    @NonNull
    public final ViewTypeParser parser;

    @Nullable
    protected final List<BoundAttribute> boundAttributes;

    @Nullable
    protected Object extras;

    public ViewManager(@NonNull EditorContext context, @NonNull ViewTypeParser parser,
                       @NonNull View view, @NonNull Layout layout, @NonNull DataContext dataContext) {
        this.context = context;
        this.parser = parser;
        this.view = view;
        this.layout = layout;
        this.dataContext = dataContext;

        if (null != layout.attributes) {
            List<BoundAttribute> boundAttributes = new ArrayList<>();
            for (Layout.Attribute attribute : layout.attributes) {
                if (attribute.value.isBinding()) {
                    boundAttributes.add(new BoundAttribute(attribute.id, attribute.value.getAsBinding()));
                }
            }
            if (boundAttributes.size() > 0) {
                this.boundAttributes = boundAttributes;
            } else {
                this.boundAttributes = null;
            }
        } else {
            this.boundAttributes = null;
        }
    }

    @Override
    public void update(@Nullable ObjectValue data) {
        // update the data context so all child views can refer to new data
        if (data != null) {
            updateDataContext(data);
        }

        // update the bound attributes of this view
        if (this.boundAttributes != null) {
            for (BoundAttribute boundAttribute : this.boundAttributes) {
                this.handleBinding(boundAttribute);
            }
        }
    }

    @Nullable
    @Override
    public View findViewById(@NonNull String id) {
        return view.findViewById(context.getInflater().getUniqueViewId(id));
    }

    @NonNull
    @Override
    public EditorContext getContext() {
        return this.context;
    }

    @NonNull
    @Override
    public Layout getLayout() {
        return this.layout;
    }

    @NonNull
    public DataContext getDataContext() {
        return dataContext;
    }

    @Nullable
    @Override
    public Object getExtras() {
        return this.extras;
    }

    @NonNull
    public ViewTypeParser getParser() {
        return parser;
    }

    @Override
    public void setExtras(@Nullable Object extras) {
        this.extras = extras;
    }
	
	@Override
	public void updateAttributes(List<Attribute> attrs){
		
		LinkedHashSet<Layout.Attribute> attributeSet = new LinkedHashSet<>(this.layout.attributes);
		
		for(Attribute attr : attrs) {
			
			ViewTypeParser.AttributeSet.Attribute attribute = parser.getAttributeSet().getAttribute(attr.key);
			Value value = attr.value;
			attributeSet.add(new Layout.Attribute(attribute.id, value));
			
			parser.handleAttribute(view, attribute.id, value);
			
		}
		
		layout.attributes.clear();
		layout.attributes.addAll(attributeSet);
		
	}

    private void updateDataContext(ObjectValue data) {
        if (dataContext.hasOwnProperties()) {
            dataContext.update(context, data);
        } else {
            dataContext.setData(data);
        }
    }

    private void handleBinding(BoundAttribute boundAttribute) {
        //noinspection unchecked
        parser.handleAttribute(view, boundAttribute.attributeId, boundAttribute.binding);
    }
}