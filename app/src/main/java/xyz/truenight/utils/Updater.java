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

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class Updater {

    private final long mDelay;
    private final Callable<Boolean> mRunnable;
    private AtomicReference<Boolean> mExecuteTask = new AtomicReference<>();

    public Updater(long delay, Callable<Boolean> runnable) {
        mDelay = delay;
        mRunnable = runnable;
    }

    public void cancel() {
        mExecuteTask.set(false);
    }

    public void start() {
        startUpdates();
    }

    private void startUpdates() {
        mExecuteTask.set(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long current = System.currentTimeMillis();

                    Boolean execute = mExecuteTask.get();
                    if (execute != null && execute) {
                        try {
                            Boolean continueExecution = mRunnable.call();
                            if (!continueExecution) {
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }

                    long delta = System.currentTimeMillis() - current;
                    try {
                        Thread.sleep(mDelay - delta);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}