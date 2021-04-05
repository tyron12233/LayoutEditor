/**
 * Copyright (C) 2017 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.dynamic.adapter.attr;

import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

import com.tyron.layouteditor.editor.EditorContext;

import xyz.truenight.dynamic.AttrUtils;

final class TextViewClassAttrAdapter extends ClassMappedAttrAdapter<TextView> {

    public TextViewClassAttrAdapter() {
        super(TextView.class);
        put("android:textSize", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context, TextView view, String value) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, AttrUtils.getDimension(view.getContext(), value));
                return true;
            }
        });
        put("android:textColor", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context,TextView view, String value) {
                if (value.startsWith("#")) {
                    view.setTextColor(Color.parseColor(value));
                } else {
                    view.setTextColor(AttrUtils.getColor(view.getContext(), value));
                }
                return true;
            }
        });
        put("android:gravity", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context,TextView view, String value) {
                view.setGravity(AttrUtils.getGravity(value));
                return true;
            }
        });
        put("android:maxLines", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context,TextView view, String value) {
                view.setMaxLines(AttrUtils.parseInt(value));
                return true;
            }
        });
        put("android:ellipsize", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context,TextView view, String value) {
                view.setEllipsize(AttrUtils.getEllipsize(value));
                return true;
            }
        });
        put("android:textAllCaps", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context,TextView view, String value) {
                view.setAllCaps(AttrUtils.getBoolean(view.getContext(), value));
                return true;
            }
        });
        put("android:textStyle", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context,TextView view, String value) {
                view.setTypeface(view.getTypeface(), AttrUtils.getTextStyle(value));
                return true;
            }
        });
        put("android:text", new AttrAdapter<TextView>() {
            @Override
            public boolean apply(EditorContext context,TextView view, String value) {
                if (value.startsWith("@")) {
                    view.setText(AttrUtils.getResId(view.getContext(), value));
                } else {
                    view.setText(value);
                }
                return true;
            }
        });
    }
}
