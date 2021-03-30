package com.tyron.layouteditor.parser.custom;

import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.view.EditTextItem;
import com.tyron.layouteditor.editor.widget.view.ViewItem;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

/**
 * Created by kirankumar on 25/11/14.
 */
public class EditTextParser<T extends EditText> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "EditText";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "TextView";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data,
                                 @Nullable ViewGroup parent, int dataIndex) {
        return new EditTextItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

    }
}
