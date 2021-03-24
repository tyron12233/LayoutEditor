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

import java.util.HashMap;
import java.util.Map;

public class ClassMappedAttrAdapter<T extends View> implements TypedAttrAdapter<T> {

    private Class<T> mCls;
    private Map<String, AttrAdapter<T>> mMap = new HashMap<>();

    public ClassMappedAttrAdapter(Class<T> cls) {
        mCls = cls;
    }

    /**
     * Return is adapter suitable for view
     */
    public boolean isSuitable(View view) {
        return mCls.isInstance(view);
    }

    public AttrAdapter<T> put(String name, AttrAdapter<T> adapter) {
        return mMap.put(name, adapter);
    }

    /**
     * Apply attribute to View
     *
     * @param view  target
     * @param name  attribute name
     * @param value attribute value
     * @return is attribute applied
     */
    public boolean apply(T view, String name, String value) {
        AttrAdapter<T> adapter = mMap.get(name);
        return adapter != null && adapter.apply(view, value);
    }
}
