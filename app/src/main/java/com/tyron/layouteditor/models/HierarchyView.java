package com.tyron.layouteditor.models;

import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.toolbox.tree.LayoutItemType;
import com.tyron.layouteditor.values.Layout;

public class HierarchyView implements LayoutItemType {

    public BaseWidget widget;

    public HierarchyView(BaseWidget widget){
        this.widget = widget;
    }
    @Override
    public int getLayoutId() {
        return R.layout.hierarchy_view;
    }
}
