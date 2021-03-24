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
import android.os.Build;
import android.view.View;

import xyz.truenight.dynamic.AttrUtils;

final class ViewAttrAdapter implements TypedAttrAdapter {

    @Override
    public boolean isSuitable(View view) {
        return true;
    }

    @Override
    public boolean apply(View view, String name, String value) {
        switch (name) {
            //region for all views
            case "android:id":
                view.setId(AttrUtils.getResId(view.getContext(), value));
                return true;
            case "android:tag":
                view.setTag(value);
                return true;
            case "android:visibility":
                view.setVisibility(AttrUtils.getVisibility(value));
                return true;
            case "android:padding": {
                int padding = AttrUtils.getDimension(view.getContext(), value);
                view.setPadding(padding, padding, padding, padding);
                return true;
            }
            case "android:paddingTop":
                view.setPadding(view.getPaddingLeft(),
                        AttrUtils.getDimension(view.getContext(), value),
                        view.getPaddingRight(), view.getPaddingBottom());
                return true;
            case "android:paddingLeft":
                view.setPadding(AttrUtils.getDimension(view.getContext(), value),
                        view.getPaddingTop(),
                        view.getPaddingRight(),
                        view.getPaddingBottom());
                return true;
            case "android:paddingRight":
                view.setPadding(view.getPaddingLeft(),
                        view.getPaddingTop(),
                        AttrUtils.getDimension(view.getContext(), value),
                        view.getPaddingBottom());
                return true;
            case "android:paddingBottom":
                view.setPadding(view.getPaddingLeft(),
                        view.getPaddingTop(),
                        view.getPaddingRight(),
                        AttrUtils.getDimension(view.getContext(), value));
                return true;
            case "android:paddingStart":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setPaddingRelative(AttrUtils.getDimension(view.getContext(), value),
                            view.getPaddingTop(),
                            view.getPaddingEnd(),
                            view.getPaddingBottom());
                } else {
                    view.setPadding(AttrUtils.getDimension(view.getContext(), value),
                            view.getPaddingTop(),
                            view.getPaddingRight(),
                            view.getPaddingBottom());
                }
                return true;
            case "android:paddingEnd":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setPaddingRelative(view.getPaddingStart(),
                            view.getPaddingTop(),
                            AttrUtils.getDimension(view.getContext(), value),
                            view.getPaddingBottom());
                } else {
                    view.setPadding(view.getPaddingLeft(),
                            view.getPaddingTop(),
                            AttrUtils.getDimension(view.getContext(), value),
                            view.getPaddingBottom());
                }
                return true;
            case "android:background":
                return setBackground(view, value);
            case "android:fitsSystemWindows":
                view.setFitsSystemWindows(AttrUtils.getBoolean(view.getContext(), value));
                return true;
            //endregion for all views
        }

        return false;
    }

    public boolean setBackground(View view, String value) {
        if (value.startsWith("#")) {
            view.setBackgroundColor(Color.parseColor(value));
            return true;
        } else if (value.startsWith("@") || value.startsWith("?attr")) {
            view.setBackgroundResource(AttrUtils.getResId(view.getContext(), value));
            return true;
        }
        return false;
    }
}
