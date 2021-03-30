package xyz.truenight.dynamic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;

import androidx.collection.ArrayMap;

import com.tyron.layouteditor.editor.WidgetFactory;
import com.tyron.layouteditor.models.Widget;

import java.lang.reflect.Constructor;
import java.util.Map;


public class EditorFactory implements DynamicLayoutInflater.Factory2 {

    WidgetFactory factory;


    private static final String LOG_TAG = EditorFactory.class.getSimpleName();

    private static final int[] sOnClickAttrs = new int[]{android.R.attr.onClick};

    private static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class};

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    private static final Map<String, Constructor<? extends View>> sConstructorMap
            = new ArrayMap<>();

    public EditorFactory(WidgetFactory factory){
        this.factory = factory;
    }
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs,
                             AttributeApplier attributeApplier) {
       return onCreateView(null, name, context, attrs, attributeApplier);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs, AttributeApplier attributeApplier) {
        View view = null;
        switch(name){
            case Widget.LINEAR_LAYOUT:
                view = factory.createWidget(new Widget(Widget.LINEAR_LAYOUT));
                break;
            case Widget.TEXTVIEW:
                view = factory.createWidget(new Widget(Widget.TEXTVIEW));
                break;
            case Widget.PROGRESSBAR:
                view = factory.createWidget(new Widget(Widget.PROGRESSBAR));
                break;
            case Widget.BUTTON:
                view = factory.createWidget(new Widget(Widget.BUTTON));
                break;
            case Widget.RELATIVE_LAYOUT:
                view = factory.createWidget(new Widget(Widget.FRAME_LAYOUT));
                break;
            case Widget.EDITTEXT:
                view = factory.createWidget(new Widget(Widget.EDITTEXT));
                break;
        }

        if(view == null){
            view = createViewFromTag(context, name, attrs);
        }

        if (view != null) {
            attributeApplier.applyAttrs(view, attrs);
        }

        return view;
    }

    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        try {
            if (-1 == name.indexOf('.')) {
                for (String classPrefix : sClassPrefixList) {
                    final View view = createView(context, name, classPrefix);
                    if (view != null) {
                        return view;
                    }
                }
                return null;
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        }
    }

    private View createView(Context context, String name, String prefix)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                Class<? extends View> clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(context);
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        }
    }
}
