package com.tyron.layouteditor.parser;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.EditorConstants;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.exceptions.DrawableNotFoundException;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.compat.CompatAttributes;
import com.tyron.layouteditor.editor.widget.view.ViewItem;
import com.tyron.layouteditor.processor.AttributeProcessor;
import com.tyron.layouteditor.processor.BooleanAttributeProcessor;
import com.tyron.layouteditor.processor.ColorResourceProcessor;
import com.tyron.layouteditor.processor.DimensionAttributeProcessor;
import com.tyron.layouteditor.processor.DrawableResourceProcessor;
import com.tyron.layouteditor.processor.GravityAttributeProcessor;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Color;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

import java.util.Map;

public class ViewParser<V extends View> extends ViewTypeParser<V> {

    private static final String TAG = "ViewParser";

    private static final String ID_STRING_START_PATTERN = "@+id/";
    private static final String ID_STRING_START_PATTERN1 = "@id/";
    private static final String ID_STRING_NORMALIZED_PATTERN = ":id/";

    @NonNull
    @Override
    public String getType() {
        return "View";
    }

    @Nullable
    @Override
    public String getParentType() {
        return null;
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ViewItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.View.Activated, new BooleanAttributeProcessor<V>() {
            @Override
            public void setBoolean(V view, boolean value) {
                view.setActivated(value);
            }
        });

        addAttributeProcessor(Attributes.View.Background, new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                if(value.startsWith("@drawable/")){

                    if(view instanceof BaseWidget){
                        BaseWidget widget = (BaseWidget)view;

                        String path = widget.getViewManager()
                                .getContext()
                                .getEditorResources()
                                .getDrawable(value.replace("@drawable/", ""));

                        if(path.equals(EditorConstants.DATA_NULL)){
                            //use default image
                            AndroidUtilities.setBackground(view, R.drawable.ic_warning);
                        }else{
                            AndroidUtilities.setBackground(view, path);
                        }
                    }else{
                        throw new IllegalStateException("View is not an instance of BaseWidget");
                    }

                }else{
                    //the background starts with "#"
                    if(Color.isColor(value)){
                      view.setBackgroundColor(Color.valueOf(value).apply(view.getContext()).color);
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.BackgroundTint, new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                view.setBackgroundTintList(ColorStateList.valueOf(color));
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setBackgroundTintList(colors);
            }
        });

        addAttributeProcessor(Attributes.View.ForegroundTint, new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                view.setBackgroundTintList(ColorStateList.valueOf(color));
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setBackgroundTintList(colors);
            }
        });

        addAttributeProcessor(Attributes.View.Height, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = (int) dimension;
                    view.setLayoutParams(layoutParams);
					
					//throw new IllegalArgumentException("Called");
					
                }
            }
        });

        addAttributeProcessor(Attributes.View.Width, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = (int) dimension;
                    view.setLayoutParams(layoutParams);
                }
            }
        });
        addAttributeProcessor(Attributes.View.Weight, new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                LinearLayout.LayoutParams layoutParams;
                if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.weight = ParseHelper.parseFloat(value);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "'weight' is only supported for LinearLayouts");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.LayoutGravity, new GravityAttributeProcessor<V>() {
            @Override
            public void setGravity(V view, @Gravity int gravity) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
                    linearLayoutParams.gravity = gravity;
                    view.setLayoutParams(layoutParams);
                } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams linearLayoutParams = (FrameLayout.LayoutParams) layoutParams;
                    linearLayoutParams.gravity = gravity;
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "'layout_gravity' is only supported for LinearLayout and FrameLayout");
                    }
                }
            }
        });


        addAttributeProcessor(Attributes.View.Padding, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.PaddingLeft, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding((int) dimension, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });

        addAttributeProcessor(Attributes.View.PaddingTop, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding(view.getPaddingLeft(), (int) dimension, view.getPaddingRight(), view.getPaddingBottom());
            }
        });

        addAttributeProcessor(Attributes.View.PaddingRight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), (int) dimension, view.getPaddingBottom());
            }
        });

        addAttributeProcessor(Attributes.View.PaddingBottom, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), (int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.Margin, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins((int) dimension, (int) dimension, (int) dimension, (int) dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MarginLeft, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins((int) dimension, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MarginTop, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, (int) dimension, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MarginRight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, (int) dimension, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MarginBottom, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, (int) dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MarginStart, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMarginStart((int) dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MarginEnd, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMarginEnd((int) dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (EditorConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MinHeight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setMinimumHeight((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.MinWidth, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setMinimumWidth((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.Elevation, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElevation(dimension);
                }
            }
        });

        addAttributeProcessor(Attributes.View.Alpha, new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                view.setAlpha(ParseHelper.parseFloat(value));
            }
        });

        addAttributeProcessor(Attributes.View.Visibility, new AttributeProcessor<V>() {
            @Override
            public void handleValue(V view, Value value) {
                if (value.isPrimitive() && value.getAsPrimitive().isNumber()) {
                    // noinspection ResourceType
                    view.setVisibility(value.getAsInt());
                } else {
                    process(view, precompile(value, view.getContext(), ((EditorContext) view.getContext()).getFunctionManager()));
                }
            }

            @Override
            public void handleResource(V view, Resource resource) {
                Integer visibility = resource.getInteger(view.getContext());
                //noinspection WrongConstant
                view.setVisibility(null != visibility ? visibility : View.GONE);
            }

            @Override
            public void handleAttributeResource(V view, AttributeResource attribute) {
                TypedArray a = attribute.apply(view.getContext());
                //noinspection WrongConstant
                view.setVisibility(a.getInt(0, View.GONE));
            }

            @Override
            public void handleStyleResource(V view, StyleResource style) {
                TypedArray a = style.apply(view.getContext());
                //noinspection WrongConstant
                view.setVisibility(a.getInt(0, View.GONE));
            }

            @Override
            public Value compile(@Nullable Value value, Context context) {
                int visibility = ParseHelper.parseVisibility(value);
                return ParseHelper.getVisibility(visibility);
            }
        });

        addAttributeProcessor(Attributes.View.Id, new StringAttributeProcessor<V>() {
            @Override
            public void setString(final V view, String value) {
                if (view instanceof BaseWidget) {
                    
                    if(view.getId() == View.NO_ID){
                    view.setId(((BaseWidget) view).getViewManager().getContext().getInflater().getUniqueViewId(value));
                    }else{
                        view.setId(((BaseWidget) view).getViewManager().getContext().getInflater().getIdGenerator().replace( ((BaseWidget)view).getViewManager().getContext().getInflater().getIdGenerator().getString(view.getId()), value));
                    }
                }

                // set view id resource name
                final String resourceName = value;
                view.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                    @Override
                    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(host, info);
                        String normalizedResourceName;
                        if (!TextUtils.isEmpty(resourceName)) {
                            String id;
                            if (resourceName.startsWith(ID_STRING_START_PATTERN)) {
                                id = resourceName.substring(ID_STRING_START_PATTERN.length());
                            } else if (resourceName.startsWith(ID_STRING_START_PATTERN1)) {
                                id = resourceName.substring(ID_STRING_START_PATTERN1.length());
                            } else {
                                id = resourceName;
                            }
                            normalizedResourceName = view.getContext().getPackageName() + ID_STRING_NORMALIZED_PATTERN + id;
                        } else {
                            normalizedResourceName = "";
                        }
                        info.setViewIdResourceName(normalizedResourceName);
                    }
                });
				
				//android.widget.Toast.makeText(view.getContext(), "called: " + resourceName, android.widget.Toast.LENGTH_LONG).show();
            }
        });

        addAttributeProcessor(Attributes.View.Style, new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                BaseWidget.Manager viewManager = ((BaseWidget) view).getViewManager();
                EditorContext context = viewManager.getContext();
                Layout layout = viewManager.getLayout();

                ViewTypeParser handler = context.getInflater().getParser(layout.type);

                String[] styleSet = value.split(EditorConstants.STYLE_DELIMITER);
                for (String styleName : styleSet) {
                    Map<String, Value> style = context.getStyle(styleName);
                    if (null != style) {
                        process(context.getStyle(styleName), (BaseWidget) view, (handler != null ? handler : ViewParser.this));
                    }
                }
            }
            private void process(Map<String, Value> style, BaseWidget proteusView, ViewTypeParser handler) {
                for (Map.Entry<String, Value> entry : style.entrySet()) {
                    //noinspection unchecked
                    handler.handleAttribute(proteusView.getAsView(), handler.getAttributeId(entry.getKey()), entry.getValue());
                }
            }
        });

        addAttributeProcessor(CompatAttributes.CoordinatorLayout.Behavior, new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                if(!(value.startsWith("@"))){
                    ViewGroup.LayoutParams params = view.getLayoutParams();

                    if(params instanceof CoordinatorLayout.LayoutParams){
                        CoordinatorLayout.LayoutParams cParams = (CoordinatorLayout.LayoutParams)params;
                        cParams.setBehavior(AndroidUtilities.parseBehavior(view.getContext(), null, value));
                        view.setLayoutParams(cParams);
                    }else{
                        Log.e(TAG, "CoordinatorLayout behavior can only be set on coordinator layout childs");
                    }
                }
            }
        });

        addAttributeProcessor(CompatAttributes.CoordinatorLayout.Hideable, new BooleanAttributeProcessor<V>() {
            @Override
            public void setBoolean(V view, boolean value) {
                ViewGroup.LayoutParams params = view.getLayoutParams();

                if(params instanceof CoordinatorLayout.LayoutParams){
                    CoordinatorLayout.LayoutParams cParams = (CoordinatorLayout.LayoutParams)params;

                    CoordinatorLayout.Behavior<V> behavior = cParams.getBehavior();

                    if(behavior instanceof BottomSheetBehavior){
                        ((BottomSheetBehavior<V>) behavior).setHideable(value);
                    }
                }else{
                    Log.e(TAG, "CoordinatorLayout behavior can only be set on coordinator layout childs");
                }
            }
        });

        addAttributeProcessor(CompatAttributes.CoordinatorLayout.PeekHeight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                ViewGroup.LayoutParams params = view.getLayoutParams();

                if(params instanceof CoordinatorLayout.LayoutParams){
                    CoordinatorLayout.LayoutParams cParams = (CoordinatorLayout.LayoutParams)params;

                    CoordinatorLayout.Behavior<V> behavior = cParams.getBehavior();

                    if(behavior instanceof BottomSheetBehavior){
                        ((BottomSheetBehavior<V>) behavior).setPeekHeight((int) dimension, true);
                    }
                }else{
                    Log.e(TAG, "CoordinatorLayout behavior can only be set on coordinator layout childs");
                }
            }
        });

        addAttributeProcessor(Attributes.View.Above, createRelativeLayoutRuleProcessor(RelativeLayout.ABOVE));
        addAttributeProcessor(Attributes.View.AlignBaseline, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_BASELINE));
        addAttributeProcessor(Attributes.View.AlignBottom, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_BOTTOM));
        addAttributeProcessor(Attributes.View.AlignLeft, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_LEFT));
        addAttributeProcessor(Attributes.View.AlignRight, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_RIGHT));
        addAttributeProcessor(Attributes.View.AlignTop, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_TOP));
        addAttributeProcessor(Attributes.View.Below, createRelativeLayoutRuleProcessor(RelativeLayout.BELOW));
        addAttributeProcessor(Attributes.View.ToLeftOf, createRelativeLayoutRuleProcessor(RelativeLayout.LEFT_OF));
        addAttributeProcessor(Attributes.View.ToRightOf, createRelativeLayoutRuleProcessor(RelativeLayout.RIGHT_OF));
        addAttributeProcessor(Attributes.View.AlignEnd, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_END));
        addAttributeProcessor(Attributes.View.AlignStart, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_START));
        addAttributeProcessor(Attributes.View.ToEndOf, createRelativeLayoutRuleProcessor(RelativeLayout.END_OF));
        addAttributeProcessor(Attributes.View.ToStartOf, createRelativeLayoutRuleProcessor(RelativeLayout.START_OF));

        addAttributeProcessor(Attributes.View.AlignParentTop, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_TOP));
        addAttributeProcessor(Attributes.View.AlignParentRight, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_RIGHT));
        addAttributeProcessor(Attributes.View.AlignParentBottom, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_BOTTOM));
        addAttributeProcessor(Attributes.View.AlignParentLeft, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_LEFT));
        addAttributeProcessor(Attributes.View.CenterHorizontal, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_HORIZONTAL));
        addAttributeProcessor(Attributes.View.CenterVertical, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_VERTICAL));
        addAttributeProcessor(Attributes.View.CenterInParent, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_IN_PARENT));
        addAttributeProcessor(Attributes.View.AlignParentStart, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_START));
        addAttributeProcessor(Attributes.View.AlignParentEnd, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_END));

        //constraint layout
		addAttributeProcessor(CompatAttributes.ConstraintLayout.LeftToLeftOf, createConstraintLayoutRuleProcessor(ConstraintSet.LEFT, ConstraintSet.LEFT));
		addAttributeProcessor(CompatAttributes.ConstraintLayout.LeftToRightOf, createConstraintLayoutRuleProcessor(ConstraintSet.LEFT, ConstraintSet.RIGHT));
		addAttributeProcessor(CompatAttributes.ConstraintLayout.RightToLeftOf, createConstraintLayoutRuleProcessor(ConstraintSet.RIGHT, ConstraintSet.LEFT));
		addAttributeProcessor(CompatAttributes.ConstraintLayout.RightToRightOf, createConstraintLayoutRuleProcessor(ConstraintSet.RIGHT, ConstraintSet.RIGHT));

		addAttributeProcessor(CompatAttributes.ConstraintLayout.BottomToBottomOf, createConstraintLayoutRuleProcessor(ConstraintSet.BOTTOM, ConstraintSet.BOTTOM));
		addAttributeProcessor(CompatAttributes.ConstraintLayout.BottomToTopOf, createConstraintLayoutRuleProcessor(ConstraintSet.BOTTOM, ConstraintSet.TOP));
		addAttributeProcessor(CompatAttributes.ConstraintLayout.TopToBottomOf, createConstraintLayoutRuleProcessor(ConstraintSet.TOP, ConstraintSet.BOTTOM));
		addAttributeProcessor(CompatAttributes.ConstraintLayout.TopToTopOf, createConstraintLayoutRuleProcessor(ConstraintSet.TOP, ConstraintSet.TOP));

    }
	
	
	private AttributeProcessor<V> createConstraintLayoutRuleProcessor(int startSide, int endSide) {
		
		return new StringAttributeProcessor<V>() {
			@Override
			public void setString(V view, String value) {
				if(view.getParent() instanceof ConstraintLayout){
					
					ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
					ConstraintSet set = new ConstraintSet();

					int id;
					if(!value.equals("parent")) {
					    id = ((BaseWidget) view).getViewManager().getContext().getInflater().getUniqueViewId(value);
                    }else{
					    id = ConstraintSet.PARENT_ID;
                    }
					
					set.clone(constraintLayout);
					set.connect(view.getId(), startSide, id, endSide);
					set.applyTo(constraintLayout);
					
				}else{
				    if(EditorConstants.isLoggingEnabled()){
						Log.e(TAG, "ConstraintLayout attributes can only be used with ConstraintLayout childs");
					}
				}
			}
		};
	}


    private AttributeProcessor<V> createRelativeLayoutRuleProcessor(final int rule) {
        return new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                if (view instanceof BaseWidget) {
                    int id = ((BaseWidget) view).getViewManager().getContext().getInflater().getUniqueViewId(value);
                    ParseHelper.addRelativeLayoutRule(view, rule, id);
                }
				//android.widget.Toast.makeText(view.getContext(), value, android.widget.Toast.LENGTH_LONG).show();
            }
        };
    }

    private AttributeProcessor<V> createRelativeLayoutBooleanRuleProcessor(final int rule) {
        return new BooleanAttributeProcessor<V>() {
            @Override
            public void setBoolean(V view, boolean value) {
                int trueOrFalse = ParseHelper.parseRelativeLayoutBoolean(value);
                ParseHelper.addRelativeLayoutRule(view, rule, trueOrFalse);
				android.widget.Toast.makeText(view.getContext(), "rule: " + rule + " " + trueOrFalse, android.widget.Toast.LENGTH_LONG).show();
            }
        };
    }


}
