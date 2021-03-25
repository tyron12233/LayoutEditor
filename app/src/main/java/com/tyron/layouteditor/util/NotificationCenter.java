package com.tyron.layouteditor.util;

import android.os.SystemClock;
import android.util.SparseArray;

import androidx.annotation.UiThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NotificationCenter {

    private static int totalEvents = 1;
    public static final int stopAllHeavyOperations = totalEvents++;
    public static final int startAllHeavyOperations = totalEvents++;
    public static final int didUpdateWidget = totalEvents++;
    private static volatile NotificationCenter[] Instance = new NotificationCenter[3];
    private static volatile NotificationCenter globalInstance;
    private final HashMap<Integer, AllowedNotifications> allowedNotifications = new HashMap<>();

    HashSet<Integer> heavyOperationsCounter = new HashSet<>();

    private SparseArray<ArrayList<NotificationCenterDelegate>> observers = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> removeAfterBroadcast = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> addAfterBroadcast = new SparseArray<>();

    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);
    private ArrayList<Runnable> delayedRunnables = new ArrayList<>(10);
    private ArrayList<Runnable> delayedRunnablesTmp = new ArrayList<>(10);
    private ArrayList<DelayedPost> delayedPostsTmp = new ArrayList<>(10);
    private ArrayList<PostponeNotificationCallback> postponeCallbackList = new ArrayList<>(10);

    private Runnable checkForExpiredNotifications;

    private int broadcasting = 0;
    private int animationInProgressCount;
    private int animationInProgressPointer = 1;
    private int currentAccount;
    private int currentHeavyOperationFlags;

    public NotificationCenter(int account) {
        currentAccount = account;
    }

    @UiThread
    public static NotificationCenter getInstance(int num) {
        NotificationCenter localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new NotificationCenter(num);
                }
            }
        }
        return localInstance;
    }

    @UiThread
    public static NotificationCenter getInstance() {
        return getGlobalInstance();
    }

    @UiThread
    public static NotificationCenter getGlobalInstance() {
        NotificationCenter localInstance = globalInstance;
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = globalInstance;
                if (localInstance == null) {
                    globalInstance = localInstance = new NotificationCenter(-1);
                }
            }
        }
        return localInstance;
    }

    public int setAnimationInProgress(int oldIndex, int[] allowedNotifications) {
        return setAnimationInProgress(oldIndex, allowedNotifications, true);
    }

    public int setAnimationInProgress(int oldIndex, int[] allowedNotifications, boolean stopHeavyOperations) {
        onAnimationFinish(oldIndex);
        if (heavyOperationsCounter.isEmpty() && stopHeavyOperations) {
            getGlobalInstance().postNotificationName(stopAllHeavyOperations, 512);
        }

        animationInProgressCount++;
        animationInProgressPointer++;

        if (stopHeavyOperations) {
            heavyOperationsCounter.add(animationInProgressPointer);
        }
        AllowedNotifications notifications = new AllowedNotifications();
        notifications.allowedIds = allowedNotifications;
        this.allowedNotifications.put(animationInProgressPointer, notifications);
        if (checkForExpiredNotifications == null) {
            AndroidUtilities.runOnUIThread(checkForExpiredNotifications = this::checkForExpiredNotifications, 1017);
        }

        return animationInProgressPointer;
    }

    private void checkForExpiredNotifications() {
        checkForExpiredNotifications = null;
        if (this.allowedNotifications.isEmpty()) {
            return;
        }
        long minTime = Long.MAX_VALUE;
        long currentTime = SystemClock.elapsedRealtime();
        ArrayList<Integer> expiredIndices = null;
        for (HashMap.Entry<Integer, AllowedNotifications> entry : this.allowedNotifications.entrySet()) {
            AllowedNotifications allowedNotification = entry.getValue();
            if (currentTime - allowedNotification.time > 1000) {
                if (expiredIndices == null) {
                    expiredIndices = new ArrayList<>();
                }
                expiredIndices.add(entry.getKey());
            } else {
                minTime = Math.min(allowedNotification.time, minTime);
            }
        }
        if (expiredIndices != null) {
            for (int i = 0; i < expiredIndices.size(); i++) {
                onAnimationFinish(expiredIndices.get(i));
            }
        }
        if (minTime != Long.MAX_VALUE) {
            long time = 1017 - (currentTime - minTime);
            AndroidUtilities.runOnUIThread(() -> checkForExpiredNotifications = this::checkForExpiredNotifications, Math.max(17, time));
        }
    }

    public void updateAllowedNotifications(int transitionAnimationIndex, int[] allowedNotifications) {
        AllowedNotifications notifications = this.allowedNotifications.get(transitionAnimationIndex);
        if (notifications != null) {
            notifications.allowedIds = allowedNotifications;
        }
    }

    public void onAnimationFinish(int index) {
        AllowedNotifications allowed = allowedNotifications.remove(index);
        if (allowed != null) {
            animationInProgressCount--;
            if (!heavyOperationsCounter.isEmpty()) {
                heavyOperationsCounter.remove(index);
                if (heavyOperationsCounter.isEmpty()) {
                    NotificationCenter.getGlobalInstance().postNotificationName(startAllHeavyOperations, 512);
                }
            }
            if (animationInProgressCount == 0) {
                runDelayedNotifications();
            }
        }
        if (checkForExpiredNotifications != null && allowedNotifications.isEmpty()) {
            AndroidUtilities.cancelRunOnUIThread(checkForExpiredNotifications);
            checkForExpiredNotifications = null;
        }
    }

    public void runDelayedNotifications() {
        if (!delayedPosts.isEmpty()) {
            delayedPostsTmp.clear();
            delayedPostsTmp.addAll(delayedPosts);
            delayedPosts.clear();
            for (int a = 0; a < delayedPostsTmp.size(); a++) {
                DelayedPost delayedPost = delayedPostsTmp.get(a);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            delayedPostsTmp.clear();
        }

        if (!delayedRunnables.isEmpty()) {
            delayedRunnablesTmp.clear();
            delayedRunnablesTmp.addAll(delayedRunnables);
            delayedRunnables.clear();
            for (int a = 0; a < delayedRunnablesTmp.size(); a++) {
                delayedRunnablesTmp.get(a).run();
            }
            delayedRunnablesTmp.clear();
        }
    }

    public boolean isAnimationInProgress() {
        return animationInProgressCount > 0;
    }

    public int getCurrentHeavyOperationFlags() {
        return currentHeavyOperationFlags;
    }

    public void postNotificationName(int id, Object... args) {
        boolean allowDuringAnimation = id == startAllHeavyOperations || id == stopAllHeavyOperations;
        ArrayList<Integer> expiredIndices = null;
        if (!allowDuringAnimation && !allowedNotifications.isEmpty()) {
            int size = allowedNotifications.size();
            int allowedCount = 0;
            long currentTime = SystemClock.elapsedRealtime();
            for (HashMap.Entry<Integer, AllowedNotifications> entry : allowedNotifications.entrySet()) {
                AllowedNotifications allowedNotification = entry.getValue();
                if (currentTime - allowedNotification.time > 1000) {
                    if (expiredIndices == null) {
                        expiredIndices = new ArrayList<>();
                    }
                    expiredIndices.add(entry.getKey());
                }
                int[] allowed = allowedNotification.allowedIds;
                if (allowed != null) {
                    for (int a = 0; a < allowed.length; a++) {
                        if (allowed[a] == id) {
                            allowedCount++;
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            allowDuringAnimation = size == allowedCount;
        }
        if (id == startAllHeavyOperations) {
            Integer flags = (Integer) args[0];
            currentHeavyOperationFlags &= ~flags;
        } else if (id == stopAllHeavyOperations) {
            Integer flags = (Integer) args[0];
            currentHeavyOperationFlags |= flags;
        }
        postNotificationNameInternal(id, allowDuringAnimation, args);

        if (expiredIndices != null) {
            for (int i = 0; i < expiredIndices.size(); i++) {
                onAnimationFinish(expiredIndices.get(i));
            }
        }
    }

    @UiThread
    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        /*if (BuildVars.DEBUG_VERSION) {
            if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("postNotificationName allowed only from MAIN thread");
            }
        }*/
        if (!allowDuringAnimation && isAnimationInProgress()) {
            DelayedPost delayedPost = new DelayedPost(id, args);
            delayedPosts.add(delayedPost);
            /*if (BuildVars.LOGS_ENABLED) {
                FileLog.e("delay post notification " + id + " with args count = " + args.length);
            }*/
            return;
        }
        if (!postponeCallbackList.isEmpty()) {
            for (int i = 0; i < postponeCallbackList.size(); i++) {
                if (postponeCallbackList.get(i).needPostpone(id, currentAccount, args)) {
                    delayedPosts.add(new DelayedPost(id, args));
                    return;
                }
            }
        }
        broadcasting++;
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects != null && !objects.isEmpty()) {
            for (int a = 0; a < objects.size(); a++) {
                NotificationCenterDelegate obj = objects.get(a);
                obj.didReceivedNotification(id, args);
            }
        }
        broadcasting--;
        if (broadcasting == 0) {
            if (removeAfterBroadcast.size() != 0) {
                for (int a = 0; a < removeAfterBroadcast.size(); a++) {
                    int key = removeAfterBroadcast.keyAt(a);
                    ArrayList<NotificationCenterDelegate> arrayList = removeAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        removeObserver(arrayList.get(b), key);
                    }
                }
                removeAfterBroadcast.clear();
            }
            if (addAfterBroadcast.size() != 0) {
                for (int a = 0; a < addAfterBroadcast.size(); a++) {
                    int key = addAfterBroadcast.keyAt(a);
                    ArrayList<NotificationCenterDelegate> arrayList = addAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        addObserver(arrayList.get(b), key);
                    }
                }
                addAfterBroadcast.clear();
            }
        }
    }

    public void addObserver(NotificationCenterDelegate observer, int id) {
        /*if (BuildVars.DEBUG_VERSION) {
            if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("addObserver allowed only from MAIN thread");
            }
        }*/
        if (broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<>()));
        }
        if (objects.contains(observer)) {
            return;
        }
        objects.add(observer);
    }

    public void removeObserver(NotificationCenterDelegate observer, int id) {
        /*if (BuildVars.DEBUG_VERSION) {
            if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("removeObserver allowed only from MAIN thread");
            }
        }*/
        if (broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects != null) {
            objects.remove(observer);
        }
    }

    public boolean hasObservers(int id) {
        return observers.indexOfKey(id) >= 0;
    }

    public void addPostponeNotificationsCallback(PostponeNotificationCallback callback) {
        /*if (BuildVars.DEBUG_VERSION) {
            if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("PostponeNotificationsCallback allowed only from MAIN thread");
            }
        }*/
        if (!postponeCallbackList.contains(callback)) {
            postponeCallbackList.add(callback);
        }
    }

    public void removePostponeNotificationsCallback(PostponeNotificationCallback callback) {
        /*if (BuildVars.DEBUG_VERSION) {
            if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("removePostponeNotificationsCallback allowed only from MAIN thread");
            }
        }*/
        if (postponeCallbackList.remove(callback)) {
            runDelayedNotifications();
        }
    }

    public void doOnIdle(Runnable runnable) {
        if (isAnimationInProgress()) {
            delayedRunnables.add(runnable);
        } else {
            runnable.run();
        }
    }

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int id, Object... args);
    }

    public interface PostponeNotificationCallback {
        boolean needPostpone(int id, int currentAccount, Object[] args);
    }

    private static class DelayedPost {

        private int id;
        private Object[] args;
        private DelayedPost(int id, Object[] args) {
            this.id = id;
            this.args = args;
        }
    }

    private static class AllowedNotifications {

        final long time;
        int[] allowedIds;

        private AllowedNotifications() {
            time = SystemClock.elapsedRealtime();
        }
    }
}