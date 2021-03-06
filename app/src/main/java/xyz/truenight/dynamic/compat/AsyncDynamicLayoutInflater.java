package xyz.truenight.dynamic.compat;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.util.Pools;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tyron.layouteditor.editor.EditorContext;

import java.util.concurrent.ArrayBlockingQueue;

import xyz.truenight.dynamic.DynamicLayoutInflater;

/**
 * <p>Helper class for inflating layouts asynchronously. To use, construct
 * an instance of {@link AsyncDynamicLayoutInflater} on the UI thread and call
 * {@link #inflate(String, ViewGroup, OnInflateFinishedListener)}. The
 * {@link OnInflateFinishedListener} will be invoked on the UI thread
 * when the inflate request has completed.
 * <p>
 * <p>This is intended for parts of the UI that are created lazily or in
 * response to user interactions. This allows the UI thread to continue
 * to be responsive and animate while the relatively heavy inflate
 * is being performed.
 * <p>
 * <p>For a layout to be inflated asynchronously it needs to have a parent
 * whose {@link ViewGroup#generateLayoutParams(AttributeSet)} is thread-safe
 * and all the Views being constructed as part of inflation must not create
 * any {@link Handler}s or otherwise call {@link Looper#myLooper()}. If the
 * layout that is trying to be inflated cannot be constructed
 * asynchronously for whatever reason, {@link AsyncDynamicLayoutInflater} will
 * automatically fall back to inflating on the UI thread.
 * <p>
 * <p>NOTE that the inflated View hierarchy is NOT added to the parent. It is
 * equivalent to calling #inflate(String, ViewGroup, boolean)
 * with attachToRoot set to false. Callers will likely want to call
 * {@link ViewGroup#addView(View)} in the {@link OnInflateFinishedListener}
 * callback at a minimum.
 * <p>
 * <p>This inflater does not support setting a Factory
 * nor Factory2. Similarly it does not support inflating
 * layouts that contain fragments.
 */
public final class AsyncDynamicLayoutInflater {
    private static final String TAG = AsyncDynamicLayoutInflater.class.getSimpleName();

    DynamicLayoutInflater mInflater;
    Handler mHandler;
    InflateThread mInflateThread;

    public AsyncDynamicLayoutInflater(@NonNull EditorContext context) {
        mInflater = new BasicInflater(context);
        mHandler = new Handler(mHandlerCallback);
        mInflateThread = InflateThread.getInstance();
    }

    @UiThread
    public void inflate(String resource, @Nullable ViewGroup parent,
                        @NonNull OnInflateFinishedListener callback) {
        if (callback == null) {
            throw new NullPointerException("callback argument may not be null!");
        }
        InflateRequest request = mInflateThread.obtainRequest();
        request.inflater = this;
        request.resource = resource;
        request.parent = parent;
        request.callback = callback;
        mInflateThread.enqueue(request);
    }

    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            InflateRequest request = (InflateRequest) msg.obj;
            if (request.view == null) {
                request.view = mInflater.inflate(
                        request.resource, request.parent, false);
            }
            request.callback.onInflateFinished(
                    request.view, request.resource, request.parent);
            mInflateThread.releaseRequest(request);
            return true;
        }
    };

    public interface OnInflateFinishedListener {
        void onInflateFinished(View view, String resource, ViewGroup parent);
    }

    private static class InflateRequest {
        AsyncDynamicLayoutInflater inflater;
        ViewGroup parent;
        String resource;
        View view;
        OnInflateFinishedListener callback;

        InflateRequest() {
        }
    }

    private static class BasicInflater extends DynamicLayoutInflater {
        private static final String[] sClassPrefixList = {
                "android.widget.",
                "android.webkit.",
                "android.app."
        };

        BasicInflater(EditorContext context) {
            super(context);
        }

        @Override
        public DynamicLayoutInflater cloneInContext(EditorContext newContext) {
            return new BasicInflater(newContext);
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            for (String prefix : sClassPrefixList) {
                try {
                    View view = createView(name, prefix, attrs);
                    if (view != null) {
                        return view;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }

            return super.onCreateView(name, attrs);
        }
    }

    private static class InflateThread extends Thread {
        private static final InflateThread sInstance;

        static {
            sInstance = new InflateThread();
            sInstance.start();
        }

        public static InflateThread getInstance() {
            return sInstance;
        }

        private ArrayBlockingQueue<InflateRequest> mQueue = new ArrayBlockingQueue<>(10);
        private Pools.SynchronizedPool<InflateRequest> mRequestPool = new Pools.SynchronizedPool<>(10);

        @Override
        public void run() {
            while (true) {
                InflateRequest request;
                try {
                    request = mQueue.take();
                } catch (InterruptedException ex) {
                    // Odd, just continue
                    Log.w(TAG, ex);
                    continue;
                }

                try {
                    request.view = request.inflater.mInflater.inflate(
                            request.resource, request.parent, false);
                } catch (RuntimeException ex) {
                    // Probably a Looper failure, retry on the UI thread
                    Log.w(TAG, "Failed to inflate resource in the background! Retrying on the UI"
                            + " thread", ex);
                }
                Message.obtain(request.inflater.mHandler, 0, request)
                        .sendToTarget();
            }
        }

        public InflateRequest obtainRequest() {
            InflateRequest obj = mRequestPool.acquire();
            if (obj == null) {
                obj = new InflateRequest();
            }
            return obj;
        }

        public void releaseRequest(InflateRequest obj) {
            obj.callback = null;
            obj.inflater = null;
            obj.parent = null;
            obj.resource = null;
            obj.view = null;
            mRequestPool.release(obj);
        }

        public void enqueue(InflateRequest request) {
            try {
                mQueue.put(request);
            } catch (InterruptedException e) {
                throw new RuntimeException(
                        "Failed to enqueue async inflate request", e);
            }
        }
    }
}