package com.tyron.layouteditor.parser.custom;

import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.DataContext;
import com.tyron.layouteditor.editor.EditorConstants;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.EditorLayoutInflater;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.viewgroup.AspectRatioFrameLayoutItem;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.managers.ViewGroupManager;
import com.tyron.layouteditor.processor.AttributeProcessor;
import com.tyron.layouteditor.processor.BooleanAttributeProcessor;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Binding;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.NestedBinding;
import com.tyron.layouteditor.values.ObjectValue;
import com.tyron.layouteditor.values.Value;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;

import java.util.Iterator;

public class ViewGroupParser<T extends ViewGroup> extends ViewTypeParser<T> {

    private static final String LAYOUT_MODE_CLIP_BOUNDS = "clipBounds";
    private static final String LAYOUT_MODE_OPTICAL_BOUNDS = "opticalBounds";

    @NonNull
    @Override
    public String getType() {
        return "ViewGroup";
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
        return new AspectRatioFrameLayoutItem(context);
    }

    @NonNull
    @Override
    public BaseWidget.Manager createViewManager(@NonNull EditorContext context, @NonNull BaseWidget view, @NonNull Layout layout,
                                                 @NonNull ObjectValue data, @Nullable ViewTypeParser caller, @Nullable ViewGroup parent,
                                                 int dataIndex) {
        DataContext dataContext = createDataContext(context, layout, data, parent, dataIndex);
        return new ViewGroupManager(context, null != caller ? caller : this, view.getAsView(), layout, dataContext);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.ViewGroup.ClipChildren, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setClipChildren(value);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.ClipToPadding, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setClipToPadding(value);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.LayoutMode, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if (LAYOUT_MODE_CLIP_BOUNDS.equals(value)) {
                    view.setLayoutMode(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS);
                } else if (LAYOUT_MODE_OPTICAL_BOUNDS.equals(value)) {
                    view.setLayoutMode(ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS);
                }
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.SplitMotionEvents, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setMotionEventSplittingEnabled(value);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.Children, new AttributeProcessor<T>() {
         /*   @Override
            public void handleBinding(T view, Binding value) {
                handleDataBoundChildren(view, value);
            }*/

            @Override
            public void handleValue(T view, Value value) {
                handleChildren(view, value);
            }

            @Override
            public void handleResource(T view, Resource resource) {
                 throw new IllegalArgumentException("children cannot be a resource");
            }

            @Override
            public void handleAttributeResource(T view, AttributeResource attribute) {
                throw new IllegalArgumentException("children cannot be a resource");
            }

            @Override
            public void handleStyleResource(T view, StyleResource style) {
                throw new IllegalArgumentException("children cannot be a style attribute");
            }
        });
    }

    @Override
    public boolean handleChildren(T view, Value children) {
        BaseWidget proteusView = ((BaseWidget) view);
        BaseWidget.Manager viewManager = proteusView.getViewManager();
        EditorLayoutInflater layoutInflater = viewManager.getContext().getInflater();
        ObjectValue data = viewManager.getDataContext().getData();
        int dataIndex = viewManager.getDataContext().getIndex();

        if (children.isArray()) {
            BaseWidget child;
            Iterator<Value> iterator = children.getAsArray().iterator();
            Value element;
            while (iterator.hasNext()) {
                element = iterator.next();
                if (!element.isLayout()) {
                    throw new InflateException("attribute  'children' must be an array of 'Layout' objects");
                }
                child = layoutInflater.inflate(element.getAsLayout(), data, view, dataIndex);
                addView(proteusView, child);
            }
        }

        return true;
    }

    protected void handleDataBoundChildren(T view, Binding value) {
        BaseWidget parent = ((BaseWidget) view);
        ViewGroupManager manager = (ViewGroupManager) parent.getViewManager();
        DataContext dataContext = manager.getDataContext();
        ObjectValue config = ((NestedBinding) value).getValue().getAsObject();

        Binding collection = config.getAsBinding(EditorConstants.COLLECTION);
        Layout layout = config.getAsLayout(EditorConstants  .LAYOUT);

        manager.hasDataBoundChildren = true;

        if (null == layout || null == collection) {
            throw new InflateException("'collection' and 'layout' are mandatory for attribute:'children'");
        }

        Value dataset = collection.getAsBinding().evaluate(view.getContext(), dataContext.getData(), dataContext.getIndex());
        if (dataset.isNull()) {
            return;
        }

        if (!dataset.isArray()) {
            throw new InflateException("'collection' in attribute:'children' must be NULL or Array");
        }

        int length = dataset.getAsArray().size();
        int count = view.getChildCount();
        ObjectValue data = dataContext.getData();
        EditorLayoutInflater inflater = manager.getContext().getInflater();
        BaseWidget child;
        View temp;

        if (count > length) {
            while (count > length) {
                count--;
                view.removeViewAt(count);
            }
        }

        for (int index = 0; index < length; index++) {
            if (index < count) {
                temp = view.getChildAt(index);
                if (temp instanceof BaseWidget) {
                    ((BaseWidget) temp).getViewManager().update(data);
                }
            } else {
                //noinspection ConstantConditions : We want to throw an exception if the layout is null
                child = inflater.inflate(layout, data, view, index);
                addView(parent, child);
            }
        }
    }

    @Override
    public boolean addView(BaseWidget parent, BaseWidget view) {
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).addView(view.getAsView());
            return true;
        }
        return false;
    }
}