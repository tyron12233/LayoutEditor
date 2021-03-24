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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by true
 * date: 18/04/16
 * time: 14:39
 */
public class Palazzo {

    private static final String TAG = Palazzo.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final long PRIORITY_KEEP_ALIVE_TIME = 10000L;

    private final int mCoreSize;
    private final Updater mPriorityCheck;

    List<ThreadPoolExecutor> mExecutors = new Vector<>();
    List<ThreadPoolExecutor> mPriorityExecutors = new Vector<>();

    List<OnCompletionListener> mListeners = new Vector<>();

    Map<String, WeakReference<ThreadPoolExecutor>> mTagMap = new ConcurrentHashMap<>();
    Map<String, Integer> mTagCountMap = new ConcurrentHashMap<>();

    public Palazzo(int coreSize) {
        if (coreSize < 1) {
            throw new IllegalArgumentException("coreSize must be > 1");
        }
        mCoreSize = coreSize;

        mPriorityCheck = new Updater(15000L, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return checkPriority();
            }
        });
    }

    public synchronized void submit(Runnable runnable) {
        submit(false, runnable);
    }

    public synchronized void submit(boolean priority, Runnable runnable) {
        submitInternal(null, priority, new Task(null, runnable));
    }

    public synchronized void submit(Object tag, Runnable runnable) {
        submit(tag, false, runnable);
    }

    public synchronized void submit(Object tag, boolean priority, Runnable runnable) {
        submitInternal(Utils.toString(tag), priority, new Task(tag, runnable));
    }

    public synchronized void submit(Object tag, Object subTag, Runnable runnable) {
        submit(tag, subTag, false, runnable);
    }

    public synchronized void submit(Object tag, Object subTag, boolean priority, Runnable runnable) {
        submitInternal(Utils.toString(tag), priority, new Task(tag, subTag, runnable));
    }

    /**
     * Same as submit(String tag, String subTag, Runnable runnable) but task will NOT be added to queue IF there is same task in queue
     *
     * @param tag      queue tag
     * @param subTag   task tag
     * @param runnable task
     */
    public void submitSafe(Object tag, Object subTag, Runnable runnable) {
        submitSafe(tag, subTag, false, runnable);
    }

    /**
     * Same as submit(String tag, String subTag, Runnable runnable) but task will NOT be added to queue IF there is same task in queue
     *
     * @param tag      queue tag
     * @param subTag   task tag
     * @param runnable task
     */
    public synchronized void submitSafe(Object tag, Object subTag, boolean priority, Runnable runnable) {
        Task task = new Task(tag, subTag, runnable);
        if (!isContains(task)) {
            submitInternal(task.mTag, priority, task);
        } else if (priority) {
            remove(task);
            submitInternal(task.mTag, true, task);
        }
        if (DEBUG) {
            System.out.print(TAG + "/ Task NOT added: { TAG: " + tag + ", SUBTAG: " + subTag + " }");
        }
    }

    private void submitInternal(String tag, boolean priority, Task task) {
        try {
            submitInternalUnsafe(tag, priority, task);
        } catch (Exception e) {
            try {
                submitInternalUnsafe(tag, priority, task);
            } catch (Exception ignored) {
            }
        }
    }

    private void submitInternalUnsafe(String tag, boolean priority, Task task) {
        ThreadPoolExecutor executor = null;
        if (tag != null) {
            WeakReference<ThreadPoolExecutor> reference = mTagMap.get(tag);
            if (reference != null) {
                executor = reference.get();
            }
        }
        if (executor == null) {
            if (mExecutors.size() < mCoreSize) {
                executor = newExecutor();
                mExecutors.add(executor);
                //            tagExecutor(tag, executor);
            } else if (priority) {
                executor = getPriorityExecutor();
                //            tagExecutor(tag, executor);
            } else {
                executor = getOptimalExecutor();
                //            tagExecutor(tag, executor);
            }
        }

        executor.execute(task);
        tagExecutor(tag, executor);
    }

    private void tagExecutor(String tag, ThreadPoolExecutor executor) {
        if (tag != null) {
            Integer count = mTagCountMap.get(tag);
            if (count != null) {
                mTagCountMap.put(tag, ++count);
            } else {
                mTagCountMap.put(tag, 1);
                mTagMap.put(tag, new WeakReference<>(executor));
            }
        }
    }

    public boolean isContains(Object tag, Object subTag) {
        return isContains(new Task(tag, subTag));
    }

    private boolean isContains(Task task) {
        if (task.mTag != null) {
            ThreadPoolExecutor worker = getTaggedWorker(task.mTag);
            return worker != null && worker.getQueue().contains(task);
        } else {
            boolean contains = false;
            for (ThreadPoolExecutor executor : mExecutors) {
                contains = contains || executor.getQueue().contains(task);
            }
            return contains;
        }
    }

    public boolean remove(Object tag, Object subTag) {
        return remove(new Task(tag, subTag));
    }

    public synchronized boolean remove(Task task) {
        if (task.mTag != null) {
            ThreadPoolExecutor worker = getTaggedWorker(task.mTag);
            boolean removed = worker != null && worker.remove(task);
            if (removed) {
                decrease(task.mTag);
                if (DEBUG) {
                    System.out.print(TAG + "/ Task REMOVED: { TAG: " + task.mTag + ", SUBTAG: " + task.mSubTag + " }");
                }
            }
            return removed;
        } else {
            boolean removed = false;
            for (ThreadPoolExecutor executor : mExecutors) {
                removed = removed || executor.remove(task);
            }
            if (removed && DEBUG) {
                System.out.print(TAG + "/ Task REMOVED everyware: { TAG: " + task.mTag + ", SUBTAG: " + task.mSubTag + " }");
            }
            return removed;
        }
    }

    private synchronized void decrease(String tag) {
        if (tag != null) {
            Integer count = mTagCountMap.get(tag);
            if (count != null) {
                if (count > 1) {
                    mTagCountMap.put(tag, --count);
                } else {
                    mTagMap.remove(tag);
                    mTagCountMap.remove(tag);
                }
            }
        }
    }

    private ThreadPoolExecutor getTaggedWorker(String tag) {
        WeakReference<ThreadPoolExecutor> reference = mTagMap.get(tag);
        if (reference != null) {
            return reference.get();
        }
        return null;
    }

    private ThreadPoolExecutor getPriorityExecutor() {
        ThreadPoolExecutor executor;
        for (ThreadPoolExecutor threadPoolExecutor : mExecutors) {
            if (threadPoolExecutor.getQueue().size() == 0) {
                if (DEBUG) {
                    System.out.print(TAG + "/ Optimal POOL reused");
                }
                return threadPoolExecutor;
            }
        }
        for (ThreadPoolExecutor threadPoolExecutor : mPriorityExecutors) {
            if (threadPoolExecutor.getQueue().size() <= 1) {
                if (DEBUG) {
                    System.out.print(TAG + "/ Priority POOL reused");
                }
                return threadPoolExecutor;
            }
        }
        executor = newPriorityExecutor();
        mPriorityExecutors.add(executor);
        mPriorityCheck.start();
        if (DEBUG) {
            System.out.print(TAG + "/ Priority POOL created");
        }
        return executor;
    }

    private ThreadPoolExecutor getOptimalExecutor() {
        ThreadPoolExecutor executor;
        executor = mExecutors.iterator().next();
        int size = executor.getQueue().size();
        for (ThreadPoolExecutor threadPoolExecutor : mExecutors) {
            if (threadPoolExecutor.getQueue().size() < size) {
                executor = threadPoolExecutor;
            }
        }
        return executor;
    }

    public void shutdown() {
        for (ThreadPoolExecutor executor : mExecutors) {
            executor.shutdownNow();
        }
        for (ThreadPoolExecutor executor : mPriorityExecutors) {
            executor.shutdownNow();
        }
        mExecutors.clear();
        mPriorityExecutors.clear();
        mTagMap.clear();
        mTagCountMap.clear();
    }

    private static ThreadPoolExecutor newExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
    }

    private static ThreadPoolExecutor newPriorityExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 1, PRIORITY_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private synchronized void onComplete(String tag, String subTag) {
        decrease(tag);
        if (!mListeners.isEmpty()) {
            List<OnCompletionListener> temp = new ArrayList<>(mListeners);
            Iterator<OnCompletionListener> iterator = temp.iterator();
            while (iterator.hasNext()) {
                OnCompletionListener listener = iterator.next();

                if (Utils.equal(listener.mTag, tag) && listener.contains(subTag)) {
                    listener.onComplete();
                    try {
                        iterator.remove();
                    } catch (Exception e) {
                        System.out.print(TAG + "/ " + e.getMessage());
                    }
                }
            }
        }
        if (DEBUG) {
            System.out.print(TAG + "/Task COMPLETED: { TAG: " + tag + ", SUBTAG: " + subTag + " }");
        }
    }

    private boolean checkPriority() {
        Iterator<ThreadPoolExecutor> iterator = mPriorityExecutors.iterator();
        if (!iterator.hasNext()) {
            return false;
        }

        while (iterator.hasNext()) {
            ThreadPoolExecutor executor = iterator.next();
            if (executor.getPoolSize() == 0 && executor.getActiveCount() == 0) {
                executor.shutdown();
                iterator.remove();
                if (DEBUG) {
                    System.out.print(TAG + "/Priority POOL removed");
                }
            }
        }
        return true;
    }

    public void listen(OnCompletionListener onCompletionListener) {
        mListeners.add(onCompletionListener);
    }

    public static abstract class OnCompletionListener {
        public abstract void onComplete();

        private String mTag;

        private List<String> mSubTag;

        public boolean contains(Object subTag) {
            return mSubTag.isEmpty() || mSubTag.contains(Utils.toString(subTag));
        }

        public OnCompletionListener(Object tag, Object... subTags) {
            mTag = Utils.toString(tag);
            mSubTag = Utils.toString(subTags);
        }
    }

    private class Task implements Runnable {
        private final Runnable mRunnable;

        private String mTag;

        private String mSubTag;

        public Task(Object tag, Runnable runnable) {
            this(tag, null, runnable);
        }

        public Task(Object tag, Object subTag, Runnable runnable) {
            mTag = Utils.toString(tag);
            mSubTag = Utils.toString(subTag);
            if (runnable == null) {
                throw new NullPointerException();
            }
            mRunnable = runnable;
        }

        public Task(Object tag, Object subTag) {
            mTag = Utils.toString(tag);
            mSubTag = Utils.toString(subTag);
            mRunnable = null;
        }

        @Override
        public void run() {
            if (Thread.interrupted()) return;
            mRunnable.run();
            onComplete(mTag, mSubTag);
        }

        @Override
        public int hashCode() {
            return (mTag + mSubTag).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Task && hashCode() == o.hashCode();
        }
    }
}
