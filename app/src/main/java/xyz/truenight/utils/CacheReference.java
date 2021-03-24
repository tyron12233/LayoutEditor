/**
 * Copyright (C) 2016 Mikhail Frolov
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

package xyz.truenight.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class CacheReference<T> extends WeakReference<T> {
    public CacheReference(T referent) {
        super(referent);
    }

    public CacheReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    @Override
    public int hashCode() {
        return Utils.hashCode(get());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CacheReference && Utils.equal(get(), ((CacheReference) obj).get());
    }
}