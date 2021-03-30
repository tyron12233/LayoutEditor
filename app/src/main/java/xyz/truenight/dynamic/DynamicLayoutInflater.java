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

package xyz.truenight.dynamic;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.ContextThemeWrapper;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.WidgetFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import xyz.truenight.dynamic.adapter.attr.AttrAdapter;
import xyz.truenight.dynamic.adapter.attr.ClassMappedAttrAdapter;
import xyz.truenight.dynamic.adapter.attr.TypedAttrAdapter;
import xyz.truenight.dynamic.adapter.param.ClassMappedParamAdapter;
import xyz.truenight.dynamic.adapter.param.ParamAdapter;
import xyz.truenight.dynamic.adapter.param.TypedParamAdapter;
import xyz.truenight.dynamic.annotation.Nullable;
import xyz.truenight.utils.Utils;

public abstract class DynamicLayoutInflater {

    private static final String TAG = DynamicLayoutInflater.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

    private static final String TAG_MERGE = "merge";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_1995 = "blink";
    private static final String TAG_REQUEST_FOCUS = "requestFocus";
    private static final String TAG_TAG = "tag";

    public static final String ATTR_LAYOUT = "layout";
    public static final String ATTR_THEME = "theme";
    public static final String ATTR_ID = "id";
    public static final String ATTR_VISIBILITY = "visibility";

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class};

    protected static WeakReference<DynamicLayoutInflater> mBase;

    final Object[] mConstructorArgs = new Object[2];

    private final EditorContext mContext;


    // these are optional, set by the caller
    private boolean mFactorySet;
    private Factory mFactory;
    private Factory2 mFactory2;
    private Factory2 mPrivateFactory;
    private Filter mFilter;

    private AttributeApplier mAttributeApplier;

    private HashMap<String, Boolean> mFilterMap;

    private TypedValue mTempValue;


    /**
     * Initializing of base {@link DynamicLayoutInflater}
     * <p>
     * Can be used for setting params which will be copied
     * to instances given by {@link DynamicLayoutInflater#from(EditorContext)}
     *
     * @param context Application context
     * @return Base inflater
     */
    public static Builder base(EditorContext context) {
        return new Builder(context);
    }

    /**
     * Obtains the {@link DynamicLayoutInflater} from the given context.
     */
    public static DynamicLayoutInflater from(EditorContext context) {
        DynamicLayoutInflater unwrap = Utils.unwrap(mBase);
        if (unwrap == null) {
            return new PhoneDynamicLayoutInflater(context);
        } else {
            return unwrap.cloneInContext(context);
        }
    }

    protected DynamicLayoutInflater(EditorContext context) {
        mContext = context;
        mAttributeApplier = new AttributeApplier(context);
    }

    protected WidgetFactory widgetFactory;

    protected DynamicLayoutInflater(DynamicLayoutInflater original, EditorContext newContext) {
        mContext = newContext;
        widgetFactory = original.widgetFactory;
        mFactory = original.mFactory;
        mFactory2 = original.mFactory2;
        mPrivateFactory = original.mPrivateFactory;
        mAttributeApplier = original.mAttributeApplier.clone();
        setFilter(original.mFilter);
    }

    /**
     * Return the context we are running in, for access to resources, class
     * loader, etc.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Return the current {@link AttributeApplier}.
     * This is called on each element to apply View attributes or LayoutParams.
     */
    protected AttributeApplier getAttributeApplier() {
        return mAttributeApplier;
    }

    protected void setAttributeApplier(AttributeApplier attributeApplier) {
        mAttributeApplier = attributeApplier;
    }

    /**
     * Return the current {@link Factory} (or null). This is called on each element
     * name. If the factory returns a View, add that to the hierarchy. If it
     * returns null, proceed to call onCreateView(name).
     */
    public final Factory getFactory() {
        return mFactory;
    }

    /**
     * Return the current {@link Factory2}.  Returns null if no factory is set
     * or the set factory does not implement the {@link Factory2} interface.
     * This is called on each element
     * name. If the factory returns a View, add that to the hierarchy. If it
     * returns null, proceed to call onCreateView(name).
     */
    public final Factory2 getFactory2() {
        return mFactory2;
    }

    /**
     * Attach a custom Factory interface for creating views while using
     * this DynamicLayoutInflater.  This must not be null, and can only be set once;
     * after setting, you can not change the factory.  This is
     * called on each element name as the xml is parsed. If the factory returns
     * a View, that is added to the hierarchy. If it returns null, the next
     * factory default {@link #onCreateView} method is called.
     * <p>
     * <p>If you have an existing
     * DynamicLayoutInflater and want to add your own factory to it, use
     * {@link #cloneInContext} to clone the existing instance and then you
     * can use this function (once) on the returned new instance.  This will
     * merge your own factory with whatever factory the original instance is
     * using.
     */
    public void setFactory(Factory factory) {
        if (mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this DynamicLayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        mFactorySet = true;
        if (mFactory == null) {
            mFactory = factory;
        } else {
            mFactory = new FactoryMerger(factory, null, mFactory, mFactory2);
        }
    }

    /**
     * Like {@link #setFactory}, but allows you to set a {@link Factory2}
     * interface.
     */
    public void setFactory2(Factory2 factory) {
        if (mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        mFactorySet = true;
        if (mFactory == null) {
            mFactory = mFactory2 = factory;
        } else {
            mFactory = mFactory2 = new FactoryMerger(factory, factory, mFactory, mFactory2);
        }
    }

    public void setPrivateFactory(Factory2 factory) {
        if (mPrivateFactory == null) {
            mPrivateFactory = factory;
        } else {
            mPrivateFactory = new FactoryMerger(factory, factory, mPrivateFactory, mPrivateFactory);
        }
    }

    /**
     * @return The {@link Filter} currently used by this DynamicLayoutInflater to restrict the set of Views
     * that are allowed to be inflated.
     */
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * Sets the {@link Filter} to by this DynamicLayoutInflater. If a view is attempted to be inflated
     * which is not allowed by the {@link Filter}, the {@link #inflate(String, ViewGroup)} call will
     * throw an {@link InflateException}. This filter will replace any previous filter set on this
     * LayoutInflater.
     *
     * @param filter The Filter which restricts the set of Views that are allowed to be inflated.
     *               This filter will replace any previous filter set on this DynamicLayoutInflater.
     */
    public void setFilter(Filter filter) {
        mFilter = filter;
        if (filter != null) {
            mFilterMap = new HashMap<String, Boolean>();
        }
    }

    /**
     * Create a copy of the existing DynamicLayoutInflater object, with the copy
     * pointing to a different Context than the original.  This is used by
     * {@link ContextThemeWrapper} to create a new DynamicLayoutInflater to go along
     * with the new Context theme.
     *
     * @param newContext The new Context to associate with the new DynamicLayoutInflater.
     *                   May be the same as the original Context if desired.
     * @return Returns a brand spanking new DynamicLayoutInflater object associated with
     * the given Context.
     */
    public abstract DynamicLayoutInflater cloneInContext(EditorContext newContext);

    /**
     * Inflate a new view hierarchy from the specified xml resource. Throws
     * {@link InflateException} if there is an error.
     *
     * @param resource XML layout resource to load
     * @param root     Optional view to be the parent of the generated hierarchy.
     * @return The root View of the inflated hierarchy. If root was supplied,
     * this is the root View; otherwise it is the root of the inflated
     * XML file.
     */
    public View inflate(String resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    /**
     * Inflate a new view hierarchy from the specified xml resource. Throws
     * {@link InflateException} if there is an error.
     *
     * @param resource     XML layout resource to load
     * @param root         Optional view to be the parent of the generated hierarchy (if
     *                     <em>attachToRoot</em> is true), or else simply an object that
     *                     provides a set of LayoutParams values for root of the returned
     *                     hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *                     the root parameter? If false, root is only used to create the
     *                     correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     * attachToRoot is true, this is root; otherwise it is the root of
     * the inflated XML file.
     */
    public View inflate(String resource, ViewGroup root, boolean attachToRoot) {
        XmlPullParser parser = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory
                    .newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(new StringReader(resource));
        } catch (XmlPullParserException e) {
            Log.e(TAG, "", e);
        }

        return parser == null ? null : inflate(parser, root, attachToRoot);
    }

    /**
     * Inflate a new view hierarchy from the specified xml node. Throws
     * {@link InflateException} if there is an error. *
     * <p>
     * <em><strong>Important</strong></em>&nbsp;&nbsp;&nbsp;For performance
     * reasons, view inflation relies heavily on pre-processing of XML files
     * that is done at build time. Therefore, it is not currently possible to
     * use DynamicLayoutInflater with an XmlPullParser over a plain XML file at runtime.
     *
     * @param parser XML dom node containing the description of the view
     *               hierarchy.
     * @param root   Optional view to be the parent of the generated hierarchy.
     * @return The root View of the inflated hierarchy. If root was supplied,
     * this is the root View; otherwise it is the root of the inflated
     * XML file.
     */
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    /**
     * Inflate a new view hierarchy from the specified XML node. Throws
     * {@link InflateException} if there is an error.
     * <p>
     * <em><strong>Important</strong></em>&nbsp;&nbsp;&nbsp;For performance
     * reasons, view inflation relies heavily on pre-processing of XML files
     * that is done at build time. Therefore, it is not currently possible to
     * use DynamicLayoutInflater with an XmlPullParser over a plain XML file at runtime.
     *
     * @param parser       XML dom node containing the description of the view
     *                     hierarchy.
     * @param root         Optional view to be the parent of the generated hierarchy (if
     *                     <em>attachToRoot</em> is true), or else simply an object that
     *                     provides a set of LayoutParams values for root of the returned
     *                     hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *                     the root parameter? If false, root is only used to create the
     *                     correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     * attachToRoot is true, this is root; otherwise it is the root of
     * the inflated XML file.
     */
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        synchronized (mConstructorArgs) {

            final Context inflaterContext = mContext;
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            Context lastContext = (Context) mConstructorArgs[0];
            mConstructorArgs[0] = inflaterContext;
            View result = root;

            try {
                // Look for the root node.
                int type;
                while ((type = parser.next()) != XmlPullParser.START_TAG &&
                        type != XmlPullParser.END_DOCUMENT) {
                    // Empty
                }

                if (type != XmlPullParser.START_TAG) {
                    throw new InflateException(parser.getPositionDescription()
                            + ": No start tag found!");
                }

                final String name = parser.getName();

                if (DEBUG) {
                    System.out.println("**************************");
                    System.out.println("Creating root view: "
                            + name);
                    System.out.println("**************************");
                }

                if (TAG_MERGE.equals(name)) {
                    if (root == null || !attachToRoot) {
                        throw new InflateException("<merge /> can be used only with a valid "
                                + "ViewGroup root and attachToRoot=true");
                    }

                    rInflate(parser, root, inflaterContext, attrs, false);
                } else {
                    // Temp is the root view that was found in the xml
                    final View temp = createViewFromTag(root, name, inflaterContext, attrs);

                    ViewGroup.LayoutParams params = null;

                    if (root != null) {
                        if (DEBUG) {
                            System.out.println("Creating params from root: " +
                                    root);
                        }
                        // Create layout params that match root, if supplied
                        params = mAttributeApplier.generateLayoutParams(root, attrs);
                        if (!attachToRoot) {
                            // Set the layout params for temp if we are not
                            // attaching. (If we are, we use addView, below)
                            temp.setLayoutParams(params);
                        }
                    }

                    if (DEBUG) {
                        System.out.println("-----> start inflating children");
                    }

                    // Inflate all children under temp against its context.
                    rInflateChildren(parser, temp, attrs, true);

                    if (DEBUG) {
                        System.out.println("-----> done inflating children");
                    }

                    // We are supposed to attach all the views we found (int temp)
                    // to root. Do that now.
                    if (root != null && attachToRoot) {
                        root.addView(temp, params);
                    }

                    // Decide whether to return the root that was passed in or the
                    // top view found in xml.
                    if (root == null || !attachToRoot) {
                        result = temp;
                    }
                }

            } catch (XmlPullParserException e) {
                final InflateException ie = new InflateException(e.getMessage(), e);
                ie.setStackTrace(EMPTY_STACK_TRACE);
                throw ie;
            } catch (Exception e) {
                final InflateException ie = new InflateException(parser.getPositionDescription()
                        + ": " + e.getMessage(), e);
                ie.setStackTrace(EMPTY_STACK_TRACE);
                throw ie;
            } finally {
                // Don't retain static reference on context.
                mConstructorArgs[0] = lastContext;
                mConstructorArgs[1] = null;
            }

            return result;
        }
    }

    /**
     * Recursive method used to inflate internal (non-root) children. This
     * method calls through to {@link #rInflate} using the parent context as
     * the inflation context.
     * <strong>Note:</strong> Default visibility so the BridgeInflater can
     * call it.
     */
    final void rInflateChildren(XmlPullParser parser, View parent, AttributeSet attrs,
                                boolean finishInflate) throws XmlPullParserException, IOException {
        rInflate(parser, parent, parent.getContext(), attrs, finishInflate);
    }

    /**
     * Recursive method used to descend down the xml hierarchy and instantiate
     * views, instantiate their children, and then call onFinishInflate().
     * <p>
     * <strong>Note:</strong> Default visibility so the BridgeInflater can
     * override it.
     */
    void rInflate(XmlPullParser parser, View parent, Context context,
                  AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {

        final int depth = parser.getDepth();
        int type;

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();

            if (TAG_REQUEST_FOCUS.equals(name)) {
                parseRequestFocus(parser, parent);
            } else if (TAG_TAG.equals(name)) {
                parseViewTag(parser, parent, attrs);
            } else if (TAG_INCLUDE.equals(name)) {
                if (parser.getDepth() == 0) {
                    throw new InflateException("<include /> cannot be the root element");
                }
                parseInclude(parser, context, parent, attrs);
            } else if (TAG_MERGE.equals(name)) {
                throw new InflateException("<merge /> must be the root element");
            } else {
                final View view = createViewFromTag(parent, name, context, attrs);
                final ViewGroup viewGroup = (ViewGroup) parent;
                final ViewGroup.LayoutParams params = mAttributeApplier.generateLayoutParams(viewGroup, attrs);
                rInflateChildren(parser, view, attrs, true);
                viewGroup.addView(view, params);
            }
        }

        if (finishInflate) {
//            parent.onFinishInflate();
            onFinishInflate(parent);
        }
    }

    private void onFinishInflate(View parent) {
        try {
            Method onFinishInflate = View.class.getDeclaredMethod("onFinishInflate");
            onFinishInflate.setAccessible(true);
            onFinishInflate.invoke(parent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convenience method for calling through to the five-arg createViewFromTag
     * method. This method passes {@code false} for the {@code ignoreThemeAttr}
     * argument and should be used for everything except {@code &gt;include>}
     * tag parsing.
     */
    private View createViewFromTag(View parent, String name, Context context, AttributeSet attrs) {
        return createViewFromTag(parent, name, context, attrs, false);
    }

    /**
     * Creates a view from a tag name using the supplied attribute set.
     * <p>
     * <strong>Note:</strong> Default visibility so the BridgeInflater can
     * override it.
     *
     * @param parent  the parent view, used to inflate layout params
     * @param name    the name of the XML tag used to define the view
     * @param context the inflation context for the view, typically the
     *                {@code parent} or base layout inflater context
     * @param attrs   the attribute set for the XML tag used to define the view
     */
    private View createViewFromTag(View parent, String name, Context context, AttributeSet attrs,
                                   boolean ignoreThemeAttr) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        // Apply a theme wrapper, if allowed and one is specified.
        if (!ignoreThemeAttr) {
            final int themeResId = AttrUtils.getAndroidResId(context, attrs, ATTR_THEME);
            if (themeResId != 0) {
                context = new ContextThemeWrapper(context, themeResId);
            }
        }

        if (name.equals(TAG_1995)) {
            // Let's party like it's 1995!
            return new BlinkLayout(context, attrs);
        }

        try {
            View view;
            if (mFactory2 != null) {
                view = mFactory2.onCreateView(parent, name, context, attrs, mAttributeApplier);
            } else if (mFactory != null) {
                view = mFactory.onCreateView(name, context, attrs, mAttributeApplier);
            } else {
                view = null;
            }

            if (view == null && mPrivateFactory != null) {
                view = mPrivateFactory.onCreateView(parent, name, context, attrs, mAttributeApplier);
            }

            if (view == null) {
                final Object lastContext = mConstructorArgs[0];
                mConstructorArgs[0] = context;
                try {
                    if (-1 == name.indexOf('.')) {
                        view = onCreateView(parent, name, attrs);
                    } else {
                        view = createView(name, null, attrs);
                    }
                } finally {
                    mConstructorArgs[0] = lastContext;
                }
            }

            return view;
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name, e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;

        } catch (Exception e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name, e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;
        }
    }

    /**
     * This routine is responsible for creating the correct subclass of View
     * given the xml element name. Override it to handle custom view objects. If
     * you override this in your subclass be sure to call through to
     * super.onCreateView(name) for names you do not recognize.
     *
     * @param name  The fully qualified class name of the View to be create.
     * @param attrs An AttributeSet of attributes to apply to the View.
     * @return View The View created.
     */
    protected View onCreateView(String name, AttributeSet attrs)
            throws ClassNotFoundException {
    return createView(name, "android.view.", attrs);
    }

    /**
     * Version of {@link #onCreateView(String, AttributeSet)} that also
     * takes the future parent of the view being constructed.  The default
     * implementation simply calls {@link #onCreateView(String, AttributeSet)}.
     *
     * @param parent The future parent of the returned view.  <em>Note that
     *               this may be null.</em>
     * @param name   The fully qualified class name of the View to be create.
     * @param attrs  An AttributeSet of attributes to apply to the View.
     * @return View The View created.
     */
    protected View onCreateView(View parent, String name, AttributeSet attrs)
            throws ClassNotFoundException {
        return onCreateView(name, attrs);
    }

    /**
     * Low-level function for instantiating a view by name. This attempts to
     * instantiate a view class of the given <var>name</var> found in this
     * DynamicLayoutInflater's ClassLoader.
     * <p>
     * <p>
     * There are two things that can happen in an error case: either the
     * exception describing the error will be thrown, or a null will be
     * returned. You must deal with both possibilities -- the former will happen
     * the first time createView() is called for a class of a particular name,
     * the latter every time there-after for that class name.
     *
     * @param name  The full name of the class to be instantiated.
     * @param attrs The XML attributes supplied for this instance.
     * @return View The newly instantiated view, or null.
     */
    protected final View createView(String name, String prefix, AttributeSet attrs)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor != null && !verifyClassLoader(constructor)) {
            constructor = null;
            sConstructorMap.remove(name);
        }
        Class<? extends View> clazz = null;

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                clazz = mContext.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);

                if (mFilter != null && clazz != null) {
                    boolean allowed = mFilter.onLoadClass(clazz);
                    if (!allowed) {
                        failNotAllowed(name, prefix, attrs);
                    }
                }
                constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name, constructor);
            } else {
                // If we have a filter, apply it to cached constructor
                if (mFilter != null) {
                    // Have we seen this name before?
                    Boolean allowedState = mFilterMap.get(name);
                    if (allowedState == null) {
                        // New class -- remember whether it is allowed
                        clazz = mContext.getClassLoader().loadClass(
                                prefix != null ? (prefix + name) : name).asSubclass(View.class);

                        boolean allowed = clazz != null && mFilter.onLoadClass(clazz);
                        mFilterMap.put(name, allowed);
                        if (!allowed) {
                            failNotAllowed(name, prefix, attrs);
                        }
                    } else if (allowedState.equals(Boolean.FALSE)) {
                        failNotAllowed(name, prefix, attrs);
                    }
                }
            }

            Object[] args = mConstructorArgs;
            args[1] = attrs;

            final View view = constructor.newInstance((Context) args[0]);
            mAttributeApplier.applyAttrs(view, attrs);

            if (view instanceof ViewStub) {
                // Use the same context when inflating ViewStub later.
                final ViewStub viewStub = (ViewStub) view;
                viewStub.setLayoutInflater(LayoutInflater.from((Context) args[0]));
            }
            return view;

        } catch (NoSuchMethodException e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + (prefix != null ? (prefix + name) : name), e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;

        } catch (ClassCastException e) {
            // If loaded class is not a View subclass
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Class is not a View " + (prefix != null ? (prefix + name) : name), e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;
        } catch (ClassNotFoundException e) {
            // If loadClass fails, we should propagate the exception.
            throw e;
        } catch (Exception e) {
            final InflateException ie = new InflateException(
                    attrs.getPositionDescription() + ": Error inflating class "
                            + (clazz == null ? "<unknown>" : clazz.getName()), e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;
        } finally {
        }
    }

    /**
     * Parses a <code>&lt;request-focus&gt;</code> element and requests focus on
     * the containing View.
     */
    private void parseRequestFocus(XmlPullParser parser, View view)
            throws XmlPullParserException, IOException {
        view.requestFocus();

        consumeChildElements(parser);
    }

    /**
     * Parses a <code>&lt;tag&gt;</code> element and sets a keyed tag on the
     * containing View.
     */
    private void parseViewTag(XmlPullParser parser, View view, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        final Context context = view.getContext();
        final int key = AttrUtils.getAndroidResId(context, attrs, ATTR_ID);
        final CharSequence value = AttrUtils.getText(context, AttrUtils.getAndroidAttribute(attrs, "value"));
        view.setTag(key, value);

        consumeChildElements(parser);
    }

    private void parseInclude(XmlPullParser parser, Context context, View parent,
                              AttributeSet attrs) throws XmlPullParserException, IOException {

        if (parent instanceof ViewGroup) {
            // If the layout is pointing to a theme attribute, we have to
            // massage the value to get a resource identifier out of it.
            String layoutValue = attrs.getAttributeValue(null, ATTR_LAYOUT);
            int layout = AttrUtils.getResId(context, layoutValue);
            if (layout == 0) {
                if (layoutValue == null || layoutValue.length() <= 0) {
                    throw new InflateException("You must specify a layout in the"
                            + " include tag: <include layout=\"@layout/layoutID\" />");
                }

                // Attempt to resolve the "?attr/name" string to an identifier.
                layout = context.getResources().getIdentifier(layoutValue.substring(1), null, null);
            }

            // The layout might be referencing a theme attribute.
            if (mTempValue == null) {
                mTempValue = new TypedValue();
            }
            if (layout != 0 && context.getTheme().resolveAttribute(layout, mTempValue, true)) {
                layout = mTempValue.resourceId;
            }

            if (layout == 0) {
                throw new InflateException("You must specify a valid layout "
                        + "reference. The layout ID " + layoutValue + " is not valid.");
            } else {
                // it is hack for recognition of merge
                int count = ((ViewGroup) parent).getChildCount();
                View child = LayoutInflater.from(context).inflate(layout, (ViewGroup) parent);
                int newCount = ((ViewGroup) parent).getChildCount();
                if ((newCount - count) > 1) {
                    // The <merge> tag doesn't support android:theme, so
                    // nothing special to do here.
                } else {
                    int id = AttrUtils.getAndroidResId(context, attrs, ATTR_ID);
                    if (id > 0) {
                        child.setId(id);
                    }
                    String visibilityValue = AttrUtils.getAndroidAttribute(attrs, ATTR_VISIBILITY);
                    if (visibilityValue != null) {
                        child.setVisibility(AttrUtils.getVisibility(visibilityValue));
                    }
                    // We try to load the layout params set in the <include /> tag.
                    // If the parent can't generate layout params (ex. missing width
                    // or height for the framework ViewGroups, though this is not
                    // necessarily true of all ViewGroups) then we expect it to throw
                    // a runtime exception.
                    // We catch this exception and set localParams accordingly: true
                    // means we successfully loaded layout params from the <include>
                    // tag, false means we need to rely on the included layout params.
                    try {
                        ViewGroup.LayoutParams params = mAttributeApplier.generateLayoutParams((ViewGroup) parent, attrs);
                        child.setLayoutParams(params);
                    } catch (RuntimeException ignored) {
                        // Ignore, just fail over to child attrs.
                    }
                }
            }
        } else {
            throw new InflateException("<include /> can only be used inside of a ViewGroup");
        }

        consumeChildElements(parser);
    }

    /**
     * <strong>Note:</strong> default visibility so that
     * LayoutInflater_Delegate can call it.
     */
    final static void consumeChildElements(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int type;
        final int currentDepth = parser.getDepth();
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > currentDepth) && type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }
    }


    private static final ClassLoader BOOT_CLASS_LOADER = DynamicLayoutInflater.class.getClassLoader();

    private final boolean verifyClassLoader(Constructor<? extends View> constructor) {
        final ClassLoader constructorLoader = constructor.getDeclaringClass().getClassLoader();
        if (constructorLoader == BOOT_CLASS_LOADER) {
            // fast path for boot class loader (most common case?) - always ok
            return true;
        }
        // in all normal cases (no dynamic code loading), we will exit the following loop on the
        // first iteration (i.e. when the declaring classloader is the contexts class loader).
        ClassLoader cl = mContext.getClassLoader();
        do {
            if (constructorLoader == cl) {
                return true;
            }
            cl = cl.getParent();
        } while (cl != null);
        return false;
    }

    /**
     * Throw an exception because the specified class is not allowed to be inflated.
     */
    private void failNotAllowed(String name, String prefix, AttributeSet attrs) {
        throw new InflateException(attrs.getPositionDescription()
                + ": Class not allowed to be inflated "
                + (prefix != null ? (prefix + name) : name));
    }


    /**
     * Hook to allow clients of the LayoutInflater to restrict the set of Views that are allowed
     * to be inflated.
     */
    public interface Filter {
        /**
         * Hook to allow clients of the LayoutInflater to restrict the set of Views
         * that are allowed to be inflated.
         *
         * @param clazz The class object for the View that is about to be inflated
         * @return True if this class is allowed to be inflated, or false otherwise
         */
        @SuppressWarnings("unchecked")
        boolean onLoadClass(Class clazz);
    }

    public interface Factory {
        /**
         * Hook you can supply that is called when inflating from a LayoutInflater.
         * You can use this to customize the tag names available in your XML
         * layout files.
         * <p>
         * <p>
         * Note that it is good practice to prefix these custom names with your
         * package (i.e., com.coolcompany.apps) to avoid conflicts with system
         * names.
         *
         * @param name    Tag name to be inflated.
         * @param context The context the view is being created in.
         * @param attrs   Inflation attributes as specified in XML file.
         * @return View Newly created view. Return null for the default
         * behavior.
         */
        public View onCreateView(String name, Context context, AttributeSet attrs, AttributeApplier attributeApplier);
    }

    public interface Factory2 extends Factory {
        /**
         * Version of {@link #onCreateView(String, Context, AttributeSet, AttributeApplier)}
         * that also supplies the parent that the view created view will be
         * placed in.
         *
         * @param parent  The parent that the created view will be placed
         *                in; <em>note that this may be null</em>.
         * @param name    Tag name to be inflated.
         * @param context The context the view is being created in.
         * @param attrs   Inflation attributes as specified in XML file.
         * @return View Newly created view. Return null for the default
         * behavior.
         */
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs, AttributeApplier attributeApplier);
    }

    private static class FactoryMerger implements Factory2 {
        private final Factory mF1, mF2;
        private final Factory2 mF12, mF22;

        FactoryMerger(Factory f1, Factory2 f12, Factory f2, Factory2 f22) {
            mF1 = f1;
            mF2 = f2;
            mF12 = f12;
            mF22 = f22;
        }

        public View onCreateView(String name, Context context, AttributeSet attrs, AttributeApplier attributeApplier) {
            View v = mF1.onCreateView(name, context, attrs, attributeApplier);
            if (v != null) return v;
            return mF2.onCreateView(name, context, attrs, attributeApplier);
        }

        public View onCreateView(View parent, String name, Context context, AttributeSet attrs, AttributeApplier attributeApplier) {
            View v = mF12 != null ? mF12.onCreateView(parent, name, context, attrs, attributeApplier)
                    : mF1.onCreateView(name, context, attrs, attributeApplier);
            if (v != null) return v;
            return mF22 != null ? mF22.onCreateView(parent, name, context, attrs, attributeApplier)
                    : mF2.onCreateView(name, context, attrs, attributeApplier);
        }
    }

    private static class BlinkLayout extends FrameLayout {
        private static final int MESSAGE_BLINK = 0x42;
        private static final int BLINK_DELAY = 500;

        private boolean mBlink;
        private boolean mBlinkState;
        private final Handler mHandler;

        public BlinkLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            mHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == MESSAGE_BLINK) {
                        if (mBlink) {
                            mBlinkState = !mBlinkState;
                            makeBlink();
                        }
                        invalidate();
                        return true;
                    }
                    return false;
                }
            });
        }

        private void makeBlink() {
            Message message = mHandler.obtainMessage(MESSAGE_BLINK);
            mHandler.sendMessageDelayed(message, BLINK_DELAY);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();

            mBlink = true;
            mBlinkState = true;

            makeBlink();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();

            mBlink = false;
            mBlinkState = true;

            mHandler.removeMessages(MESSAGE_BLINK);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (mBlinkState) {
                super.dispatchDraw(canvas);
            }
        }
    }

    public static class Builder {
        private EditorContext mContext;
        private WidgetFactory factory;
        private Factory mFactory;
        private Factory2 mFactory2;
        private Factory2 mPrivateFactory;
        private AttributeApplier mAttributeApplier;
        private Filter mFilter;
        private Map<Class, ClassMappedAttrAdapter> mAttrClsMap;
        private Map<Class, ClassMappedParamAdapter> mParamClsMap;


        protected Builder(EditorContext context) {
            mContext = context;
        }

        public Builder setFactory(Factory factory) {
            mFactory = factory;
            return this;
        }

        public Builder setFactory2(Factory2 factory2) {
            mFactory2 = factory2;
            return this;
        }

        public Builder setPrivateFactory(Factory2 privateFactory) {
            mPrivateFactory = privateFactory;
            return this;
        }

        public Builder registerAttrAdapter(TypedAttrAdapter adapter) {
            initAttributeApplier();
            mAttributeApplier.addAttrAdapter(adapter);
            return this;
        }

        public <T extends View> Builder registerAttrAdapter(Class<T> cls, String name, AttrAdapter<T> adapter) {
            initAttributeApplier();
            ClassMappedAttrAdapter<T> attrAdapter = null;
            if (mAttrClsMap == null) {
                mAttrClsMap = new HashMap<>();
            } else {
                //noinspection unchecked
                attrAdapter = mAttrClsMap.get(cls);
            }
            if (attrAdapter == null) {
                attrAdapter = new ClassMappedAttrAdapter<>(cls);
                mAttrClsMap.put(cls, attrAdapter);
                attrAdapter.put(name, adapter);
                mAttributeApplier.addAttrAdapter(attrAdapter);
            } else {
                attrAdapter.put(name, adapter);
            }
            return this;
        }

        public Builder registerParamAdapter(TypedParamAdapter adapter) {
            initAttributeApplier();
            mAttributeApplier.addParamAdapter(adapter);
            return this;
        }

        public <T extends ViewGroup.LayoutParams> Builder registerParamAdapter(Class<T> cls, String name, ParamAdapter<T> adapter) {
            initAttributeApplier();
            ClassMappedParamAdapter<T> paramAdapter = null;
            if (mParamClsMap == null) {
                mParamClsMap = new HashMap<>();
            } else {
                //noinspection unchecked
                paramAdapter = mParamClsMap.get(cls);
            }
            if (paramAdapter == null) {
                paramAdapter = new ClassMappedParamAdapter<>(cls);
                mParamClsMap.put(cls, paramAdapter);
                paramAdapter.put(name, adapter);
                mAttributeApplier.addParamAdapter(paramAdapter);
            } else {
                paramAdapter.put(name, adapter);
            }
            return this;
        }

        private void initAttributeApplier() {
            if (mAttributeApplier == null) {
                mAttributeApplier = newAttributeApplier();
            }
        }

        protected AttributeApplier newAttributeApplier() {
            return new AttributeApplier(mContext);
        }

        public Builder setFilter(Filter filter) {
            mFilter = filter;
            return this;
        }

        protected DynamicLayoutInflater instance(EditorContext context) {
            return new PhoneDynamicLayoutInflater(context);
        }

        private DynamicLayoutInflater apply(DynamicLayoutInflater inflater) {
            if (mFactory2 != null) {
                inflater.setFactory2(mFactory2);
            } else if (mFactory != null) {
                inflater.setFactory(mFactory);
            }
            if (mPrivateFactory != null) {
                inflater.setPrivateFactory(mPrivateFactory);
            }
            if(factory != null){
                inflater.setWidgetFactory(factory);
            }
            if (mAttributeApplier != null) {
                inflater.mAttributeApplier = mAttributeApplier;
            }
            inflater.setFilter(mFilter);

            return inflater;
        }

        public Builder setWidgetFactory(WidgetFactory factory){
            this.factory = factory;
            return this;
        }

        public final DynamicLayoutInflater create() {
            DynamicLayoutInflater instance = instance(mContext);
            mBase = new WeakReference<>(instance);
            return apply(instance);
        }
    }

    private void setWidgetFactory(WidgetFactory factory) {
        this.widgetFactory = factory;
    }
}
