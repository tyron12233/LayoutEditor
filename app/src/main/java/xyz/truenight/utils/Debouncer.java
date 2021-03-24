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

import java.util.concurrent.atomic.AtomicReference;

import xyz.truenight.utils.interfaces.Consumer;

public class Debouncer<T> {

    private final long mDelay;
    private final Consumer<T> mRunnable;
    private AtomicReference<T> mExecuteTask = new AtomicReference<>();
    private AtomicReference<Boolean> mListening = new AtomicReference<>();
    private AtomicReference<Long> mLastUpdated = new AtomicReference<>();

    public Debouncer(long delay, Consumer<T> runnable) {
        mDelay = delay;
        mRunnable = runnable;
    }

    public void update(T data) {
        mLastUpdated.set(System.currentTimeMillis());
        mExecuteTask.set(data);
        Boolean listening = mListening.get();
        if (listening == null || !listening) {
            startListenUpdates();
        }
    }

    private void startListenUpdates() {
        mListening.set(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLastUpdated.set(System.currentTimeMillis());
                while (true) {
                    long current = System.currentTimeMillis();
                    if (mDelay < current - mLastUpdated.get()) {
                        T execute = mExecuteTask.get();
                        if (execute != null) {
                            mExecuteTask.set(null);

                            mRunnable.accept(execute);
//                            mLastUpdated.set(current);
                        } else {
                            mListening.set(false);
                            return;
                        }
                    }
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}