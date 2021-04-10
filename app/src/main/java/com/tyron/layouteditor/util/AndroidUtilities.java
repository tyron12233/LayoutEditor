package com.tyron.layouteditor.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tyron.layouteditor.ApplicationLoader;
import com.tyron.layouteditor.editor.widget.viewgroup.LinearLayoutItem;
import com.tyron.layouteditor.values.Array;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class AndroidUtilities {

    public static Point displaySize = new Point();
    public static float density;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();

    static final String WIDGET_PACKAGE_NAME;
    @SuppressWarnings("rawtypes")
    static final ThreadLocal<Map<String, Constructor<CoordinatorLayout.Behavior>>> sConstructors =
            new ThreadLocal<>();
    static final Class<?>[] CONSTRUCTOR_PARAMS = new Class<?>[] {
            Context.class
    };

    static {
        final Package pkg = CoordinatorLayout.class.getPackage();
        WIDGET_PACKAGE_NAME = pkg != null ? pkg.getName() : null;
    }
    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {
            float oldDensity = density;
            density = context.getResources().getDisplayMetrics().density;
            float newDensity = density;


            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }

            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    //screenRefreshRate = display.getRefreshRate();
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static int px(int dp) {
        if (dp == -1 || dp == -2) {
            return dp;
        }
        return (int) (dp / density);
    }

    public static AppCompatActivity getActivity(Context context) {

        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }


    /**
     * Counts the number of widgets for use with ids
     *
     * @param viewGroup The root editor view
     * @param clazz     widget to be counted
     * @return widget count
     */
    public static int countWidgets(ViewGroup viewGroup, Class<?> clazz) {
        int count = 0;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

//			if(child instanceof LinearLayoutItem && ((LinearLayoutItem)child).isRootView()){
//				count--;
//			}
            if (child instanceof ViewGroup) {
                if (((ViewGroup) child).getChildCount() > 0) {
                    count += countWidgets((ViewGroup) child, clazz);
                }
            }
            if (clazz.isAssignableFrom(child.getClass())) {
                count++;
            }
        }
        return count;
    }
   

    public static LinearLayoutItem getRootView(View view) {
        if (view instanceof LinearLayoutItem && ((LinearLayoutItem) view).isRootView()) {
            return (LinearLayoutItem) view;
        }
        return getRootView((ViewGroup) view.getParent());
    }

    /**
     * @param clazz class to be checked
     * @param view view to be checked
     * @return returns if view is assignable from the class provided
     * Note this is diferent from instanceOf
     */
    public static boolean isAssignableFrom(String clazz, View view){
        try {
            return Class.forName(clazz).isAssignableFrom(view.getClass());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    public static boolean isChildOf(String parentClass, View view){
        View parent = (View) view.getParent();
        try {
            while (parent != null && !(parent.getClass().isAssignableFrom(Class.forName(parentClass)))){
                parent = (View) parent.getParent();
            }
            if(parent == null){
                return false;
            }
            if(parent.getClass().isAssignableFrom(Class.forName(parentClass))){
                return true;
            }
        }catch(ClassNotFoundException e){
            return false;
        }

        return false;
    }

    @SuppressWarnings({"rawtypes"})
    public static CoordinatorLayout.Behavior parseBehavior(Context context, AttributeSet attrs, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        final String fullName;
        if (name.startsWith(".")) {
            // Relative to the app package. Prepend the app package name.
            fullName = context.getPackageName() + name;
        } else if (name.indexOf('.') >= 0) {
            // Fully qualified package name.
            fullName = name;
        } else {
            // Assume stock behavior in this package (if we have one)
            fullName = !TextUtils.isEmpty(WIDGET_PACKAGE_NAME)
                    ? (WIDGET_PACKAGE_NAME + '.' + name)
                    : name;
        }

        try {
            Map<String, Constructor<CoordinatorLayout.Behavior>> constructors = sConstructors.get();
            if (constructors == null) {
                constructors = new HashMap<>();
                sConstructors.set(constructors);
            }
            Constructor<CoordinatorLayout.Behavior> c = constructors.get(fullName);
            if (c == null) {
                final Class<CoordinatorLayout.Behavior> clazz =
                        (Class<CoordinatorLayout.Behavior>) Class.forName(fullName, false, context.getClassLoader());
                c = clazz.getConstructor();
                c.setAccessible(true);
                constructors.put(fullName, c);
            }
            return c.newInstance();
        } catch (Exception e) {
            Log.e("AndroidUtilities","Could not inflate Behavior subclass " + fullName, e);
            return null;
        }
    }

    public static void setBackground(View view, String path){
        Glide.with(view).load(path).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                view.setBackground(placeholder);
            }
        });
    }

    public static void setBackground(View view, @DrawableRes int id){
        Glide.with(view).load(id).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                view.setBackground(placeholder);
            }
        });
    }

    public static int getStatusBarHeight(Context context){
        int statusBarHeight = 0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
           statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }
}
