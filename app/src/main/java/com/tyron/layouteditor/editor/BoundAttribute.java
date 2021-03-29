package com.tyron.layouteditor.editor;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.ViewManager;
import com.tyron.layouteditor.values.Binding;
import com.tyron.layouteditor.values.ObjectValue;

/**
 * BoundAttribute holds the attribute id to binding pair
 * which is used in the update flow of a {@link com.tyron.layouteditor.editor.widget.BaseWidget}
 * which is executed when {@link ViewManager#update(ObjectValue)}
 * is invoked.
 *
 * @author kirankumar
 * @author adityasharat
 */
public class BoundAttribute {

    /**
     * The {@code int} attribute id of the pair.
     */
    public final int attributeId;

    /**
     * The {@link Binding} for the layout attributes value.
     */
    @NonNull
    public final Binding binding;

    public BoundAttribute(int attributeId, @NonNull Binding binding) {
        this.attributeId = attributeId;
        this.binding = binding;
    }
}