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

package xyz.truenight.dynamic.compat.adapter.attr;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import xyz.truenight.dynamic.AttrUtils;
import xyz.truenight.dynamic.adapter.attr.TypedAttrAdapter;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

public class CompatImageViewAttrAdapter implements TypedAttrAdapter<AppCompatImageView> {
    @Override
    public boolean isSuitable(View view) {
        return view instanceof AppCompatImageView;
    }

    @Override
    public boolean apply(AppCompatImageView v, String name, String value) {
        switch (name) {
            case "app:srcCompat":
                return setSrc(v, value);
        }

        return false;
    }

    public boolean setSrc(AppCompatImageView view, String value) {
        if (value.startsWith("@")) {
            view.setImageResource(AttrUtils.getResId(view.getContext(), value));
            return true;
        }
        return false;
    }
}
