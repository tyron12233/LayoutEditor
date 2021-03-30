package xyz.truenight.dynamic.compat.adapter.attr;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Attribute;

import java.util.Arrays;

import xyz.truenight.dynamic.AttrUtils;
import xyz.truenight.dynamic.adapter.attr.TypedAttrAdapter;

public class CompatTextViewAttrAdapter implements TypedAttrAdapter<AppCompatTextView> {
    @Override
    public boolean isSuitable(View view) {
        return view instanceof AppCompatTextView;
    }

    @Override
    public boolean apply(EditorContext context, AppCompatTextView view, String name, String value) {
        if(view instanceof BaseWidget){
           // ((BaseWidget)view).getViewManager().updateAttributes(Arrays.asList(new Attribute(name, context.getEditorResources().get)));
        }
        switch (name) {
            case "android:textSize":
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, AttrUtils.getDimension(view.getContext(), value));
                return true;
            case "android:textColor":
                if (value.startsWith("#")) {
                    view.setTextColor(Color.parseColor(value));
                } else {
                    view.setTextColor(AttrUtils.getColor(view.getContext(), value));
                }
                return true;
            case "android:gravity":
                view.setGravity(AttrUtils.getGravity(value));
                return true;
            case "android:maxLines":
                view.setMaxLines(AttrUtils.parseInt(value));
                return true;
            case "android:ellipsize":
                view.setEllipsize(AttrUtils.getEllipsize(value));
                return true;
            case "android:textAllCaps":
                view.setAllCaps(AttrUtils.getBoolean(view.getContext(), value));
                return true;
            case "android:textStyle":
                view.setTypeface(view.getTypeface(), AttrUtils.getTextStyle(value));
                return true;
            case "android:text":
                if (value.startsWith("@")) {
                    view.setText(AttrUtils.getResId(view.getContext(), value));
                } else {
                    view.setText(value);
                }
                return true;
        }
        return false;
    }
}
