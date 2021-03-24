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

package xyz.truenight.dynamic.adapter.param;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;

import xyz.truenight.dynamic.AttrUtils;

final class MarginParamAdapter implements TypedParamAdapter {
    @Override
    public boolean isSuitable(ViewGroup.LayoutParams params) {
        return params instanceof ViewGroup.MarginLayoutParams;
    }

    @Override
    public boolean apply(Context context, ViewGroup.LayoutParams p, String name, String value) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) p;
        switch (name) {
            case "android:layout_margin": {
                int margin = AttrUtils.getDimension(context, value);
                params.setMargins(margin, margin, margin, margin);
                return true;
            }
            case "android:layout_marginLeft":
                params.leftMargin = AttrUtils.getDimension(context, value);
                return true;
            case "android:layout_marginStart":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(AttrUtils.getDimension(context, value));
                } else {
                    params.leftMargin = AttrUtils.getDimension(context, value);
                }
                return true;
            case "android:layout_marginRight":
                params.rightMargin = AttrUtils.getDimension(context, value);
                return true;
            case "android:layout_marginEnd":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginEnd(AttrUtils.getDimension(context, value));
                } else {
                    params.rightMargin = AttrUtils.getDimension(context, value);
                }
                return true;
            case "android:layout_marginTop":
                params.topMargin = AttrUtils.getDimension(context, value);
                return true;
            case "android:layout_marginBottom":
                params.bottomMargin = AttrUtils.getDimension(context, value);
                return true;
        }

        return false;
    }
}
