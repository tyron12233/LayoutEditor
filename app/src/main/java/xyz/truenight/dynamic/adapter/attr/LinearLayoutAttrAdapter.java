package xyz.truenight.dynamic.adapter.attr;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.editor.widget.LinearLayoutItem;

import xyz.truenight.dynamic.AttrUtils;

final class LinearLayoutAttrAdapter implements TypedAttrAdapter<LinearLayout> {
    @Override
    public boolean isSuitable(View view) {
        return view instanceof LinearLayout;
    }

    @Override
    public boolean apply(LinearLayout v, @NonNull String name, String value) {
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
