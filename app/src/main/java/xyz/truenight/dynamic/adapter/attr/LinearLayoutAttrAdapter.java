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

import android.view.View;
import android.widget.LinearLayout;

import xyz.truenight.dynamic.AttrUtils;

final class LinearLayoutAttrAdapter implements TypedAttrAdapter<LinearLayout> {
    @Override
    public boolean isSuitable(View view) {
        return view instanceof LinearLayout;
    }

    @Override
    public boolean apply(LinearLayout v, String name, String value) {
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
