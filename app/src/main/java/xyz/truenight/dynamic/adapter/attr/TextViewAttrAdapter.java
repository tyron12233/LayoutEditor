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
import android.view.View;
import android.widget.TextView;

import xyz.truenight.dynamic.AttrUtils;

final class TextViewAttrAdapter implements TypedAttrAdapter<TextView> {
    @Override
    public boolean isSuitable(View view) {
        return view instanceof TextView;
    }

    @Override
    public boolean apply(TextView view, String name, String value) {
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
