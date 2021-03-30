package com.tyron.layouteditor.parser.custom;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.view.TextViewItem;
import com.tyron.layouteditor.parser.ParseHelper;
import com.tyron.layouteditor.processor.BooleanAttributeProcessor;
import com.tyron.layouteditor.processor.ColorResourceProcessor;
import com.tyron.layouteditor.processor.DimensionAttributeProcessor;
import com.tyron.layouteditor.processor.DrawableResourceProcessor;
import com.tyron.layouteditor.processor.GravityAttributeProcessor;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class TextViewParser<T extends TextView> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "TextView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "View";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data,
                                 @Nullable ViewGroup parent, int dataIndex) {
        return new TextViewItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.TextView.HTML, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    //noinspection deprecation
                    view.setText(Html.fromHtml(value));
                }
            }
        });
        addAttributeProcessor(Attributes.TextView.Text, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setText(value);
            }
        });

        addAttributeProcessor(Attributes.TextView.DrawablePadding, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setCompoundDrawablePadding((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextSize, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension);
            }
        });
        addAttributeProcessor(Attributes.TextView.Gravity, new GravityAttributeProcessor<T>() {
            @Override
            public void setGravity(T view, @GravityAttributeProcessor.Gravity int gravity) {
                view.setGravity(gravity);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColor, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setTextColor(colors);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColorHint, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setHintTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setHintTextColor(colors);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColorLink, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setLinkTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setLinkTextColor(colors);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColorHighLight, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setHighlightColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                //
            }
        });

        addAttributeProcessor(Attributes.TextView.DrawableLeft, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
            }
        });
        addAttributeProcessor(Attributes.TextView.DrawableTop, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3]);
            }
        });
        addAttributeProcessor(Attributes.TextView.DrawableRight, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], drawable, compoundDrawables[3]);
            }
        });
        addAttributeProcessor(Attributes.TextView.DrawableBottom, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], drawable);
            }
        });

        addAttributeProcessor(Attributes.TextView.MaxLines, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setMaxLines(ParseHelper.parseInt(value));
            }
        });

        addAttributeProcessor(Attributes.TextView.Ellipsize, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                Enum ellipsize = ParseHelper.parseEllipsize(value);
                view.setEllipsize((android.text.TextUtils.TruncateAt) ellipsize);
            }
        });

        addAttributeProcessor(Attributes.TextView.PaintFlags, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if (value.equals("strike"))
                    view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        addAttributeProcessor(Attributes.TextView.Prefix, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setText(value + view.getText());
            }
        });

        addAttributeProcessor(Attributes.TextView.Suffix, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setText(view.getText() + value);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextStyle, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                int typeface = ParseHelper.parseTextStyle(value);
                view.setTypeface(Typeface.defaultFromStyle(typeface));
            }
        });

        addAttributeProcessor(Attributes.TextView.SingleLine, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setSingleLine(value);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextAllCaps, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setAllCaps(value);
            }
        });
        addAttributeProcessor(Attributes.TextView.Hint, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setHint(value);
            }
        });
    }
}