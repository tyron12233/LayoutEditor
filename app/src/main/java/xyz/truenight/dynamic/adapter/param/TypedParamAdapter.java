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
import android.view.ViewGroup;

public interface TypedParamAdapter<T extends ViewGroup.LayoutParams> {

    String LAYOUT_WIDTH = "android:layout_width";
    String LAYOUT_HEIGHT = "android:layout_height";
    String LAYOUT_WEIGHT = "android:layout_weight";
    String LAYOUT_GRAVITY = "android:layout_gravity";

    /**
     * Return is adapter suitable for LayoutParams
     */
    boolean isSuitable(ViewGroup.LayoutParams params);

    /**
     * Apply param to LayoutParams
     *
     * @param context context
     * @param params  target
     * @param name    param name
     * @param value   param value
     * @return is param applied
     */
    boolean apply(Context context, T params, String name, String value);
}
