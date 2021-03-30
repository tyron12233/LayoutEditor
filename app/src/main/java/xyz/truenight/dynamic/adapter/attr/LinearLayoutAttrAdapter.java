package xyz.truenight.dynamic.adapter.attr;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.managers.ViewManager;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;

import java.util.Arrays;

import xyz.truenight.dynamic.AttrUtils;

final class LinearLayoutAttrAdapter implements TypedAttrAdapter<LinearLayout> {
    @Override
    public boolean isSuitable(View view) {
        return view instanceof LinearLayout;
    }

    @Override
    public boolean apply(EditorContext context, LinearLayout v, @NonNull String name, String value) {

        if(v instanceof BaseWidget){
            try {
                ViewTypeParser.AttributeSet.Attribute attribute = ((ViewManager) ((BaseWidget) v).getViewManager()).parser.getAttributeSet().getAttribute(name);
                Value val = attribute.processor.precompile(new Primitive(value), v.getContext(), ((BaseWidget) v).getViewManager().getContext().getFunctionManager());

                ((BaseWidget) v).getViewManager().updateAttributes(Arrays.asList(new Attribute(name, val)));
                return true;
            }catch(Exception ignore){

            }
        }
        switch (name) {
            case "android:orientation":
                switch (value) {
                    case "vertical":
                        v.setOrientation(LinearLayout.VERTICAL);
                        return true;
                    case "horizontal":
                        v.setOrientation(LinearLayout.HORIZONTAL);
                        return true;
                }
                break;
            case "android:gravity":
                v.setGravity(AttrUtils.getGravity(value));
                return true;
        }
        return false;
    }
}
