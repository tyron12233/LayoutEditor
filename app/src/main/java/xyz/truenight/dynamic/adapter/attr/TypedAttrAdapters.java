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

import java.util.List;

import xyz.truenight.utils.Utils;

public final class TypedAttrAdapters {

    private TypedAttrAdapters() {
    }

    public static final TypedAttrAdapter VIEW_ADAPTER = new ViewAttrAdapter();
    public static final TypedAttrAdapter TEXT_VIEW_ADAPTER = new TextViewAttrAdapter();
    public static final TypedAttrAdapter LINEAR_LAYOUT_ADAPTER = new LinearLayoutAttrAdapter();
    public static final TypedAttrAdapter RELATIVE_LAYOUT_ADAPTER = new RelativeLayoutAttrAdapter();
    public static final TypedAttrAdapter IMAGE_VIEW_ADAPTER = new ImageViewAttrAdapter();

    public static List<TypedAttrAdapter> DEFAULT = Utils.add(
            TypedAttrAdapters.VIEW_ADAPTER,
            TypedAttrAdapters.TEXT_VIEW_ADAPTER,
            TypedAttrAdapters.IMAGE_VIEW_ADAPTER,
            TypedAttrAdapters.LINEAR_LAYOUT_ADAPTER,
            TypedAttrAdapters.RELATIVE_LAYOUT_ADAPTER
    );
}
