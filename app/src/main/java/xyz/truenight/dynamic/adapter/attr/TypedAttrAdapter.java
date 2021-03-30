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

import com.tyron.layouteditor.editor.EditorContext;

public interface TypedAttrAdapter<T extends View> {
    /**
     * Return is adapter suitable for view
     */
    boolean isSuitable(View view);

    /**
     * Apply attribute to View
     *
     * @param view  target
     * @param name  attribute name
     * @param value attribute value
     * @return is attribute applied
     */
    boolean apply(EditorContext context, T view, String name, String value);
}
