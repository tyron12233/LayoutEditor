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

import java.util.HashMap;
import java.util.Map;

public class ClassMappedParamAdapter<T extends ViewGroup.LayoutParams> implements TypedParamAdapter<T> {

    private Class<T> mCls;
    private Map<String, ParamAdapter<T>> mMap = new HashMap<>();

    public ClassMappedParamAdapter(Class<T> cls) {
        mCls = cls;
    }

    public ParamAdapter<T> put(String name, ParamAdapter<T> adapter) {
        return mMap.put(name, adapter);
    }

    @Override
    public boolean isSuitable(ViewGroup.LayoutParams params) {
        return mCls.isInstance(params);
    }

    @Override
    public boolean apply(Context context, T params, String name, String value) {
        ParamAdapter<T> adapter = mMap.get(name);
        return adapter != null && adapter.apply(params, value);
    }
}
